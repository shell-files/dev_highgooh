import { useNavigate } from 'react-router';
import { useAuth } from '@hooks/AuthContext.jsx';
import { useState, useRef, useEffect } from 'react'; // 🔥 useState, useRef, useEffect 추가

const Header = () => {
  const { logout, name, role } = useAuth();
  const navigate = useNavigate();

  // 🔥 1. 알림 팝업 상태 및 Ref 정의
  const [isNotiOpen, setIsNotiOpen] = useState(false);
  const notiRef = useRef(null);

  // 가상의 최신 알림 데이터 리스트
  const [notifications, setNotifications] = useState([
    { id: 1, type: 'critical', message: '인발기(Machine-03) 공정 전력 이상치 탐지', time: '5분 전', isRead: false },
    { id: 2, type: 'warning', message: '가스 가공 파이프라인 압력 임계치 근접', time: '20분 전', isRead: false },
    { id: 3, type: 'info', message: '금일 AI 이상치 요약 보고서 발행 완료', time: '1시간 전', isRead: true },
    { id: 3, type: 'info', message: '금일 AI 이상치 요약 보고서 발행 완료', time: '1시간 전', isRead: true },
    { id: 3, type: 'info', message: '금일 AI 이상치 요약 보고서 발행 완료', time: '1시간 전', isRead: true },
    { id: 3, type: 'info', message: '금일 AI 이상치 요약 보고서 발행 완료', time: '1시간 전', isRead: true },
  ]);

  // 🔥 2. 알림창 바깥 영역 클릭 시 자동으로 닫히는 로직
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (notiRef.current && !notiRef.current.contains(event.target)) {
        setIsNotiOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <header className='home_header'>
      <div className="logo-area">
          <h1 className="logo-section" style={{cursor: 'pointer'}} onClick={()=>navigate("/")}></h1>
      </div>
      <div className="header_right_area">
          {/* 🔥 Ref 바인딩을 위해 wrapper 역할의 div에 relative-container 클래스 추가 */}
          <div className="user-section" style={{ position: 'relative' }} ref={notiRef}>
              <span>{name}/{role[0]}</span>
              
              {/* 🔥 3. 알림 토글 버튼 이벤트 매핑 및 카운트 배지 스타일 분기 */}
              <button 
                type="button"
                className={`bell_icon ${notifications.some(n => !n.isRead) ? 'active' : ''}`}
                onClick={() => setIsNotiOpen(!isNotiOpen)}
              >
                {/* 읽지 않은 알림이 있다면 작은 빨간 점 배지 노출 */}
                {notifications.some(n => !n.isRead) && <span className="noti-badge-dot"></span>}
              </button>

              {/* 🔥 4. 알림 팝업 창 드롭다운 마크업 */}
              {isNotiOpen && (
                <div className="header-noti-dropdown">
                  <div className="noti-dropdown-header">
                    <span>실시간 시스템 알림</span>
                    {notifications.some(n => !n.isRead) && (
                      <button 
                        className="btn-all-read"
                        onClick={() => setNotifications(notifications.map(n => ({ ...n, isRead: true })))}
                      >
                        모두 읽음 처리
                      </button>
                    )}
                  </div>
                  <div className="noti-dropdown-body">
                    {notifications.length > 0 ? (
                      <ul className="noti-list">
                        {notifications.map((noti) => (
                          <li key={noti.id} className={`noti-item ${noti.isRead ? 'read' : 'unread'}`}>
                            <div className="noti-item-title-row">
                              <span className={`noti-tag-icon ${noti.type}`}></span>
                              <p className="noti-message">{noti.message}</p>
                            </div>
                            <span className="noti-time">{noti.time}</span>
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <div className="noti-empty-state">
                        <span className="empty-bell">🔔</span>
                        <p>새로운 알림이 없습니다.</p>
                      </div>
                    )}
                  </div>
                </div>
              )}

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