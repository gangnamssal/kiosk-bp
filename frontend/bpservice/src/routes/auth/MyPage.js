/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";
import { Suspense } from "react";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import Footer from "../../components/Footer";
import LoadingPage from "../../components/LoadingPage";
import Nav from "../../components/Nav";
import { deleteUser, getUserInfo } from "../../modules/mypage";

import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import Box from "@mui/material/Box";
import Divider from "@mui/material/Divider";
import PersonIcon from "@mui/icons-material/Person";

const myPageArea = css`
  width: 100%;
  height: 69vh;
`;

const title = css`
  flex-direction: row;
  justify-content: space-around;
  margin-left: 5vw;
  margin-bottom: 1vh;
`;

const firstModalStyle = css`
  width: 95vw;
  margin-left: 2.5vw;
  margin-bottom: 2.5vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border-radius: 10px;
  align-items: center;
  background-color: rgba(249, 250, 251, 0.9);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12), 0 2px 4px rgba(0, 0, 0, 0.24);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  .card-1:hover {
    box-shadow: 0 14px 28px rgba(0, 0, 0, 0.55), 0 10px 10px rgba(0, 0, 0, 0.52);
  }
  input::placeholder {
    color: transparent;
  }
  input:placeholder-shown + label {
    color: #aaa;
    font-size: 14pt;
    top: 15px;
  }
  input:focus + label,
  label {
    color: #8aa1a1;
    font-size: 10pt;
    pointer-events: none;
    position: absolute;
    left: 0px;
    top: 0px;
    transition: all 0.2s ease;
    -webkit-transition: all 0.2s ease;
    -moz-transition: all 0.2s ease;
    -o-transition: all 0.2s ease;
  }
  input:focus,
  input:not(:placeholder-shown) {
    border-bottom: solid 1px #8aa1a1;
    outline: none;
  }
`;

const secondModalStyle = css`
  width: 95vw;
  margin-left: 2.5vw;
  display: flex;
  margin-bottom: 2.5vh;
  flex-direction: column;
  justify-content: center;
  border-radius: 10px;
  align-items: center;
  background-color: rgba(249, 250, 251, 0.9);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12), 0 2px 4px rgba(0, 0, 0, 0.24);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  .card-1:hover {
    box-shadow: 0 14px 28px rgba(0, 0, 0, 0.55), 0 10px 10px rgba(0, 0, 0, 0.52);
  }
  input::placeholder {
    color: transparent;
  }
  input:placeholder-shown + label {
    color: #aaa;
    font-size: 14pt;
    top: 15px;
  }
  input:focus + label,
  label {
    color: #8aa1a1;
    font-size: 10pt;
    pointer-events: none;
    position: absolute;
    left: 0px;
    top: 0px;
    transition: all 0.2s ease;
    -webkit-transition: all 0.2s ease;
    -moz-transition: all 0.2s ease;
    -o-transition: all 0.2s ease;
  }
  input:focus,
  input:not(:placeholder-shown) {
    border-bottom: solid 1px #8aa1a1;
    outline: none;
  }
`;

const thirdModalStyle = css`
  width: 95vw;
  margin-left: 2.5vw;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border-radius: 10px;
  align-items: center;
  background-color: rgba(249, 250, 251, 0.9);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12), 0 2px 4px rgba(0, 0, 0, 0.24);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  .card-1:hover {
    box-shadow: 0 14px 28px rgba(0, 0, 0, 0.55), 0 10px 10px rgba(0, 0, 0, 0.52);
  }
  input::placeholder {
    color: transparent;
  }
  input:placeholder-shown + label {
    color: #aaa;
    font-size: 14pt;
    top: 15px;
  }
  input:focus + label,
  label {
    color: #8aa1a1;
    font-size: 10pt;
    pointer-events: none;
    position: absolute;
    left: 0px;
    top: 0px;
    transition: all 0.2s ease;
    -webkit-transition: all 0.2s ease;
    -moz-transition: all 0.2s ease;
    -o-transition: all 0.2s ease;
  }
  input:focus,
  input:not(:placeholder-shown) {
    border-bottom: solid 1px #8aa1a1;
    outline: none;
  }
`;

const sectionModalPosition = css`
  height: 72vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

const userInfoModal = css`
  display: flex;
  justify-content: space-around;
`;

const contentModal = css`
  width: 80vw;
  height: 40vh;
  border: 1px black solid;
  margin-bottom: 10vh;
`;

const content = css`
  text-align: center;
`;

function MyPage() {
  const navigation = useNavigate();
  const dispatch = useDispatch();
  const { userInfo = "" } = useSelector(({ mypageReducer }) => mypageReducer);

  const { userName, sido, sigungu, dong } = userInfo;

  const goToModifyInfo = () => {
    navigation(`/bp/modify/user`);
  };

  const goToModifyPwd = () => {
    navigation(`/bp/modify/pwd`);
  };

  const goToDeleteUser = () => {
    if (!window.confirm("탈퇴를 하시겠습니까?")) {
      return;
    } else {
      dispatch(deleteUser());
    }
  };

  // 회원정보 - 아직 구현 X
  useEffect(() => {
    dispatch(getUserInfo());
  }, [dispatch]);

  return (
    <div>
      <header>
        <Nav />
      </header>

      <Suspense fallback={<LoadingPage />}>
        <div css={myPageArea}>
          <h1 css={title}>My B.P</h1>

          {/* 유저 정보 */}
          <div css={firstModalStyle}>
            <div css={userInfoModal}>
              <div>
                <PersonIcon></PersonIcon>
              </div>
              <div>
                <h3>{userName}</h3>
              </div>
              <div className="userInfoItem">
                <span>{sido} </span>
                <span>{sigungu} </span>
                <span>{dong}</span>
              </div>
            </div>
          </div>

          {/* 유저 정보 */}
          <div css={secondModalStyle}>
            <div css={content}>
              <h1>마이 페이지</h1>
            </div>
          </div>

          {/* 회원 버튼 */}
          <div css={thirdModalStyle}>
            <Box
              sx={{
                width: "100%",
                maxWidth: 360,
                bgcolor: "background.paper",
              }}
            >
              <nav aria-label="secondary mailbox folders">
                <List>
                  {/* 회원 정보 수정 */}
                  <ListItem disablePadding>
                    <ListItemButton onClick={goToModifyInfo}>
                      <ListItemText primary="회원 정보 수정" />
                    </ListItemButton>
                  </ListItem>

                  <Divider />

                  <ListItem disablePadding>
                    {/* 비밀번호 변경 */}
                    <ListItemButton onClick={goToModifyPwd}>
                      <ListItemText primary="비밀번호 변경" />
                    </ListItemButton>
                  </ListItem>

                  <Divider />

                  <ListItem disablePadding>
                    {/* 회원 탈퇴 */}
                    <ListItemButton onClick={goToDeleteUser}>
                      <ListItemText primary="회원 탈퇴" />
                    </ListItemButton>
                  </ListItem>
                </List>
              </nav>
            </Box>
          </div>
        </div>
      </Suspense>

      <footer>
        <Footer />
      </footer>
    </div>
  );
}

export default MyPage;
