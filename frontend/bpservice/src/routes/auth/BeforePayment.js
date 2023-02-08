import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import styled from "../../style/Receipt.module.css";

function BeforePayment() {
  const navigation = useNavigate();
  const location = useLocation();

  const queryParams = new URLSearchParams(
    `?${location.pathname.split("?")[1]}`
  );
  const term = queryParams.get("kioskID");

  const onClick = () => {
    navigation("/bp/payment", { state: { kioskId: term } });
  };

  const objString = localStorage.getItem("login-token");

  useEffect(() => {
    if (!objString) {
      navigation("/bp/login");
    }
  }, [objString, navigation]);

  return (
    <div>
      <div className={styled.container}>
        <div className={styled.tab}></div>

        <div className={styled.receipt}>
          <div className={styled.paper}>
            {/* <div className={styled.title}>Receipt</div> */}
            <div className={styled.date}>날짜: 2023/02/04</div>
            <table>
              <tbody>
                <tr>
                  <td
                    style={{
                      fontSize: "1rem",
                      fontWeight: "bolder",
                      color: "green",
                    }}
                  >
                    우산 번호
                  </td>
                  <td
                    className={styled.right}
                    style={{
                      fontSize: "0.8rem",
                      color: "green",
                    }}
                  >
                    금액
                  </td>
                </tr>

                <tr>
                  <td>312321312</td>
                  <td className={styled.right}>10000원</td>
                </tr>

                <tr>
                  <td colSpan="2" className={styled.center}>
                    <input type="button" value="결제하기" onClick={onClick} />
                  </td>
                </tr>
              </tbody>
            </table>
            <div className={(styled.sign, styled.center)}>
              <div className={styled.barcode}></div>
              <br />
              312321312
              <br />
              <div className={styled.thankyou}>이용해주셔서 감사합니다.</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default BeforePayment;
