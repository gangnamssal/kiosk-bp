/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react'
import KioskHeader from '../components/KioskHeader'
import KioskHomeSection from '../components/KioskHomeSection'
import "bootstrap/dist/css/bootstrap.min.css";

const KioskHomeContainerStyle = css`
  box-sizing: border-box;
  /* border : 1px solid black; */
  width : 100vw;
  height : 100vh;

  background-color: #EEF1FF;
`

// 위에는 Emotion.js 입니다.
// 밑에는 JS 입니다.

const KioskHomeContainer = () => {

  return (
    <div css={KioskHomeContainerStyle}>
      <header>
        <KioskHeader />
      </header>
      <section>
        <KioskHomeSection />
      </section>
    </div>
  )
}

export default KioskHomeContainer;