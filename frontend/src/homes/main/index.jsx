import { Routes, Route } from 'react-router';
import Header from '@components/Layout/Header.jsx';
import Sidebar from '@components/Layout/Sidebar.jsx';
import Main from '@homes/Main/Main.jsx';
import Asn from '@homes/inbound/Asn.jsx';
import '@styles/common.css';
import Anomaly from '@homes/anomaly/Anomaly.jsx';

const Home = () => {
  return (
    <>
      <Header />
      <div className="main-wrapper">
          <Sidebar />
          <div className="content-area">
            <Routes>
              <Route path='/' element={<Main />}/>
              <Route path='/asn' element={<Asn />}/>
              <Route path='/anomaly' element={<Anomaly />}/>
            </Routes>
          </div>
      </div>
    </>
  )
}
export default Home;