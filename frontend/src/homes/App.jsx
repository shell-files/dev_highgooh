import { Routes, Route, useLocation } from "react-router";
// import '@styles/App.css';
import Gate from '@gates/Gate.jsx';
import NotFound from '@errors/NotFound.jsx';
import Login from '@logins/Login.jsx';
import Home from '@homes/main';
import { useAuth } from '@hooks/AuthContext.jsx';

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

function App() {
  const location = useLocation();
  const { isAuthReady, isLoading, redirectUrl } = useAuth();
  console.log(" 현재 브라우저 주소 위치:", location.pathname);
  if (isLoading) return <></>;
  if(!isAuthReady) {
    return (
      <Routes>
        <Route path="/" element={ <Gate /> } />
        <Route path="/login" element={ <Login />} />
        <Route path="*" element={ <NotFound />} />
      </Routes>
    )
  }
  // 주소가 바뀔 때마다 콘솔에 찍어봅니다.
  return (
    <>
      <Header />
      <div className="main-wrapper">
          <Sidebar />
          <div className="content-area">
            <Routes>
              {/* ⚠️ 맨 앞의 슬래시(/)를 모두 제거했습니다. */}
              <Route path='/' element={<Main />}/>
              <Route path='/asn' element={<Asn />}/>
              <Route path='/inbound' element={<Inbound />}/>
              <Route path='/order' element={<OutboundOrder />}/>
              <Route path='/outbound' element={<Outbound />}/>
              <Route path='/outhistory' element={<OutHistory />}/>
              <Route path='/anomaly' element={<Anomaly />}/>
              <Route path='/carbonemission' element={<CarbonDashboard />}/>
              <Route path='/packing' element={<Packing />}/>
              <Route path="*" element={ <NotFound />} />
            </Routes>
          </div>
      </div>
    </>
  )
}

export default App
