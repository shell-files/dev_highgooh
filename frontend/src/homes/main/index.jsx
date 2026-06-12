// @homes/Main/Home.jsx (또는 Home 컴포넌트 파일)
import { Routes, Route } from 'react-router';
import '@styles/common.css';
import Header from '@components/Layout/Header.jsx';
import Sidebar from '@components/Layout/Sidebar.jsx';
import Main from '@homes/Main/Main.jsx';
import Asn from '@homes/inbound/Asn.jsx';
import Inbound from '@homes/inbound/Inbound.jsx';
import OutboundOrder from '@homes/outbound/OutboundOrder.jsx';
import Outbound from '@homes/outbound/Outbound.jsx';
import OutHistory from '@homes/outbound/OutHistory.jsx';
import Anomaly from '@homes/carbonEmission/Anomaly.jsx';
import CarbonDashboard from '@homes/carbonEmission/CarbonDashboard.jsx';
import Packing from '@homes/outbound/Packing.jsx';

const Home = () => {
  return (
    <>
      <Header />
      <div className="main-wrapper">
          <Sidebar />
          <div className="content-area">
            <Routes>
              {/* ⚠️ 맨 앞의 슬래시(/)를 모두 제거했습니다. */}
              <Route path='' element={<Main />}/>
              <Route path='asn' element={<Asn />}/>
              <Route path='inbound' element={<Inbound />}/>
              <Route path='order' element={<OutboundOrder />}/>
              <Route path='outbound' element={<Outbound />}/>
              <Route path='outhistory' element={<OutHistory />}/>
              <Route path='anomaly' element={<Anomaly />}/>
              <Route path='carbonemission' element={<CarbonDashboard />}/>
              <Route path='packing' element={<Packing />}/>
            </Routes>
          </div>
      </div>
    </>
  )
}
export default Home;