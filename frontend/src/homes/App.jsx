import { Routes, Route, Navigate, Outlet } from "react-router";
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
