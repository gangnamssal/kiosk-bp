package kr.co.bpservice.controller.kiosk;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.bpservice.service.kiosk.KBrollyReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Tag(name = "Kiosk Brolly Return", description = "키오스크에서 우산 반납 기능을 제공하기 위한 API")
@RequestMapping("/api/brolly/return")
@RestController
public class KBrollyReturnController {
    @Autowired
    private KBrollyReturnService kBrollyReturnService;

    // 반납 우산 정보 + 이미지를 받아서 저장하는 메소드 (반환값으로 키오스크에게 몇번 홀더를 열어야하는지 알려준다.)
    @PostMapping("")
    @Operation(description = "우산 반납을 담당하는 API (반납 로그, 이미지 등을 저장)")
    @Parameters({@Parameter(name = "brolly_id", description = "BROLLY_NAME")
            ,@Parameter(name = "img_data", description = "이미지 파일 데이터")
    })
    public ResponseEntity<?> returnBrolly(@RequestBody Map<String, String> requestMap) throws IOException {
        return new ResponseEntity<>(kBrollyReturnService.returnBrolly(
                Integer.parseInt(requestMap.get("caseId")),
                requestMap.get("brollyName"),
                requestMap.get("imgData")),
                HttpStatus.OK);
    }

//    // (부분) 환불처리를 진행하는 메소드 (반환값으로 우산 반납일시, 환불금액 등을 보내준다.)
//    @PostMapping("/refund")
//    @Operation(description = "반납 시 결제 환불 데이터를 받아 대여 로그 작성하는 기능")
//    @Parameters({@Parameter(name = "brollyId", description = "우산 QR 정보")
//            ,@Parameter(name = "caseId", description = "케이스 ID")
//    })
//    public ResponseEntity<?> refundMoney(@RequestBody Map<String, String> requestMap){
//        System.out.println(requestMap);
//        return new ResponseEntity<>(kBrollyReturnService.refundMoney(
//                requestMap.get("brollyId"),
//                Integer.parseInt(requestMap.get("caseId"))), HttpStatus.OK);
//    }
}
