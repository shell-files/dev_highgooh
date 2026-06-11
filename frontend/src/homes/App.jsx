import { Routes, Route, Navigate, Outlet,useLocation } from "react-router";
// import '@styles/App.css';
import Gate from '@gates/Gate.jsx';
import NotFound from '@errors/NotFound.jsx';
import Login from '@logins/Login.jsx';
import Home from '@homes/main';
import { useAuth } from '@hooks/AuthContext.jsx';

const PublicRoute = () => {
  const { isAuthReady, isLoading, redirectUrl } = useAuth();
  if (isLoading) return <></>;
  return isAuthReady ? <Navigate to={redirectUrl} replace /> : <Outlet />;
};

const PrivateRoute = () => {
  const { isAuthReady, isLoading } = useAuth();
  if (isLoading) return <></>;
  return !isAuthReady ? <Navigate to="/login" replace /> : <Outlet />;
};

function App() {
const location = useLocation();
  
  // 주소가 바뀔 때마다 콘솔에 찍어봅니다.
  console.log(" 현재 브라우저 주소 위치:", location.pathname);
  return (
    <>
      <Routes>
        <Route path="/" element={ <Gate /> } />
        <Route element={<PublicRoute />}>        
          <Route path="/login" element={ <Login />} />
        </Route>
        <Route element={<PrivateRoute />}>
          <Route path="/home/*" element={ <Home /> } />
        </Route>
        <Route path="*" element={ <NotFound />} />
      </Routes>
    </>
  )
}

export default App
