import { Routes, Route } from "react-router";
import '@styles/App.css'
import Gate from '@gates/Gate.jsx';
import NotFound from '@errors/NotFound.jsx';
import Login from '@logins/Login.jsx';

function App() {

  return (
    <>
      <Routes>
         <Route path="/" element={ <Gate /> } />
         <Route path="/login" element={ <Login />} />
         <Route path="*" element={ <NotFound />} />
      </Routes>
    </>
  )
}

export default App
