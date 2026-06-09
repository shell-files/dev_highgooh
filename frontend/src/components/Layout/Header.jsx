import { useNavigate } from 'react-router';
import { useAuth } from '@hooks/AuthContext.jsx';

const Header = () => {
  const { logout, name, role } = useAuth();
  const navigate = useNavigate();
  return (
    <header className='home_header'>
      <div className="logo-area">
          <h1 className="logo-section" style={{cursor: 'pointer'}} onClick={()=>navigate("/home")}></h1>
      </div>
      <div className="header_right_area">
          <div className="user-section">
              <span>{name}/{role[0]}</span>
              <button className="bell_icon active"></button>
              <div className="btw_bar"></div>
              <button className="home_icon" onClick={()=>navigate("/")}></button>
              <div className="btw_bar"></div>
              <span className="logout-btn" onClick={logout}>로그아웃</span>
          </div>
      </div>
    </header>
  )
}

export default Header;