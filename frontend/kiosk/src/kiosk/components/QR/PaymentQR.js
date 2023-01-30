import {QRCodeSVG} from 'qrcode.react';

const PaymentQR = () => {
  return (
    <div>
      <QRCodeSVG 
        value={"http://192.168.100.177:85/kiosk/lent/complete"}
        size={295}
        imageSettings={{ src:'http://192.168.100.177:85/kiosk/lent/complete', width: 10, height: 10 }}
        id="qr-gen"
        level={"H"}
        includeMargin={true}
        bgColor={"white"}
        fgColor={"black"}
      />,
    </div>
  )
}

export default PaymentQR