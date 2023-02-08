package kr.co.bpservice.service.kiosk;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Cancel;
import kr.co.bpservice.entity.brolly.*;
import kr.co.bpservice.repository.brolly.*;
import kr.co.bpservice.service.common.CommonService;
import kr.co.bpservice.util.HTTPUtils;
import kr.co.bpservice.util.image.ImageUtils;
import kr.co.bpservice.util.network.Get;
import kr.co.bpservice.util.network.Header;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KBrollyReturnService {

    private final BrollyRentLogRepository brollyRentLogRepository;
    private final BrollyRepository brollyRepository;
    private final BrollyHolderRepository brollyHolderRepository;
    private final BrollyCaseRepository brollyCaseRepository;
    private final BrollyPayLogRepository brollyPayLogRepository;

    @Value("${BootPay.applicationID}")
    public String applicationID;

    @Value("${BootPay.privateKey}")
    public String privateKey;

    //결제 환불 및 DB 변경
    @Transactional
    public Map<String,Object> refundMoney(String brollyName, int caseId){
        //QR 데이터를 이용한 RentLog 반환
        Optional<BrollyRentLog> optionalBrollyRentLog = brollyRentLogRepository.findBrollyRentLogForRefund(brollyName);
        if(optionalBrollyRentLog.isEmpty()){
            return CommonService.returnFail("우산 대여로그를 찾을 수 없습니다.");
        }
        BrollyRentLog brollyRentLog = optionalBrollyRentLog.get();

        //결제 취소할 데이터 가져오기
        Map<String,?> cancelDataMap = brollyPayLogRepository.findPayLogForRefund(brollyRentLog.getBrolly());
        String receiptId = cancelDataMap.get("receiptId").toString();
        String userId = cancelDataMap.get("userId").toString();
        double price = Double.parseDouble(cancelDataMap.get("price").toString());
        if(price <= 0.0){ //이 부분 환불할 필요없다는걸 알려줘야함
            return CommonService.returnFail("환불할 금액이 없습니다.");
        }
        try {
            Bootpay bootpay = new Bootpay(applicationID, privateKey);
            HashMap<String, Object> token = bootpay.getAccessToken();
            if(token.get("error_code") != null) { //failed
                return null;
            }
            Cancel cancel = new Cancel();
            cancel.receiptId = receiptId;
            cancel.cancelUsername = userId;
            cancel.cancelMessage = "우산 반납";
            cancel.cancelPrice = price;

            HashMap<String, Object>  res = bootpay.receiptCancel(cancel);
            if(res.get("error_code") == null) { //success
                System.out.println("receiptCancel success: " + res);
            } else {
                System.out.println("receiptCancel false: " + res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonService.returnFail("환불 진행 중 오류가 발생했습니다.");
        }
        LocalDateTime uptDt = LocalDateTime.now();
        int rentMoney = (int)(10000.0 - price);

        // DB에 있는 로그 업데이트
        BrollyPayLog brollyPayLog = brollyRentLog.getPay();
        brollyPayLog.setPrice(rentMoney);
        brollyPayLog.setUptDt(uptDt);
        brollyPayLog.setStatus("환불완료");
        brollyPayLogRepository.save(brollyPayLog);

        brollyRentLog.setRentMoney(rentMoney);
        brollyRentLog.setState(true);
        brollyRentLog.setUptDt(uptDt);
        brollyRentLogRepository.save(brollyRentLog);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", true);
        responseMap.put("message", "환불이 완료되었습니다.");
        responseMap.put("price", rentMoney);
        responseMap.put("uptDt",uptDt);

        //결제 취소 정보 반환
        return responseMap;
    }

    @Transactional
    public Map<String, Object> returnBrolly(Integer caseId, String brollyName, String imgUrl) throws IOException {
        Map<String, Object> responseMap = new HashMap<>();

        Optional<BrollyCase> optionalBrollyCase = brollyCaseRepository.findById(caseId);
        if(optionalBrollyCase.isEmpty()) {
            return CommonService.returnFail("존재하지 않는 우산 케이스 번호입니다.");
        }

        BrollyCase brollyCase = optionalBrollyCase.get();
        // 키오스크가 열어야 할 홀더 정보
        BrollyHolder brollyHolder = brollyHolderRepository.findFirstBrollyHolderByCase(brollyCase);

        Optional<Brolly> optionalBrolly = brollyRepository.findByName(brollyName);
        if(optionalBrolly.isEmpty()) {
            return CommonService.returnFail("우산 정보를 찾을 수 없습니다.");
        }

        // 이미지를 저장할 Rent Log를 불러오는 로직
        Brolly brolly = optionalBrolly.get();
        Optional<BrollyRentLog> optionalBrollyRentLog = brollyRentLogRepository.findTop1ByBrollyOrderByRegDtDesc(brolly);
        if(optionalBrollyRentLog.isEmpty()){
            return CommonService.returnFail("우산 대여로그를 찾을 수 없습니다.");
        }
        BrollyRentLog brollyRentLog = optionalBrollyRentLog.get();

        /// 이미지 저장 로직
        if(!imgSave(imgUrl, brollyRentLog)) {
            return CommonService.returnFail("이미지를 저장하는 도중 오류가 발생했습니다.");
        }

        String action = "return";
        Integer holderNum = brollyHolder.getNum();
        return requestOpenHolder(caseId, holderNum, brolly, action); // 홀더 오픈하고 환불 진행
    }

    private Map<String, Object> requestOpenHolder(Integer caseId, Integer holderNum, Brolly brolly, String action) {
        String url = String.format("http://rigizer2.iptime.org:8000/open?caseId=%d&holderNum=%d&action=%s", caseId, holderNum, action);

        Header header = new Header();
        header.append("User-Agent", HTTPUtils.USER_AGENT);
        header.append("Accept-Language", HTTPUtils.ACCEPT_LANGUAGE);
        header.append("Accept-Encoding", HTTPUtils.ACCEPT_ENCODING);
        header.append("Connection", HTTPUtils.CONNECTION);

        Get get = null;
        JSONObject resultJson = null;
        try {
            get = new Get(url, header);
            int responseCode = get.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("FastAPI Status code: " + responseCode);
                throw new RuntimeException("FastAPI: Http status 코드가 200이 아닙니다.");
            }
            String content = get.get();
            resultJson = new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IO Exception이 발생했습니다!");
        }

        boolean isSuccess = (Boolean) resultJson.get("brollyResult"); // 사용자가 우산을 넣었는지 체크
        if(isSuccess) { // 사용자가 우산을 넣었으면 오픈한 Holder에 우산 정보를 넣고 환불진행
            BrollyHolder brollyHolder = brollyHolderRepository.findByCaseIdAndHolderNum(caseId, holderNum);
            brollyHolder.setBrolly(brolly);
            brollyHolderRepository.save(brollyHolder);
            return this.refundMoney(brolly.getName(), caseId); // 환불 진행
        } else {
            return CommonService.returnFail("홀더에 우산을 넣지 않았습니다.");
        }
    }

    private boolean imgSave(String imgUrl, BrollyRentLog brollyRentLog) throws IOException {
        String binaryData = imgUrl;
        FileOutputStream stream = null;
        try {
            if (binaryData == null || binaryData.trim().equals("")) {
                throw new Exception();
            }

            binaryData = binaryData.replaceAll("data:image/png;base64,", "");
            byte[] file = Base64.decodeBase64(binaryData);

            // YOLO5
            // 이쯤에 들어가야함
            // 메소드(file) // 우산 라벨이 있는지 없는지에 따라서 나머지 로직 수행
            // 리턴 우산이 아닙니다

            String fileName = UUID.randomUUID().toString();
            String imgURL = ImageUtils.getImageUrl(fileName);

            // 파일스트림 저장
            stream = new FileOutputStream(imgURL);
            stream.write(file);
            stream.close();

            // 이미지 경로를 데이터베이스에 저장
            brollyRentLog.setImgName(fileName);
            brollyRentLogRepository.save(brollyRentLog);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("에러 발생");
            return false;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return true;
    }
    
}
