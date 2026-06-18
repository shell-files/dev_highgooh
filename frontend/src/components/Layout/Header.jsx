import { useNavigate } from 'react-router';
import { useAuth } from '@hooks/AuthContext.jsx';
import { useState, useRef, useEffect } from 'react';
import { Client } from '@stomp/stompjs';

const Header = () => {
  const { logout, name, role } = useAuth();
  const navigate = useNavigate();

  // 🔥 1. 알림 팝업 상태 및 Ref 정의
  const [isNotiOpen, setIsNotiOpen] = useState(false);
  const notiRef = useRef(null);
  const stompClientRef = useRef(null);

  // 가상의 최신 알림 데이터 리스트
  const [notifications, setNotifications] = useState([
    { id: 1, type: 'critical', message: '인발기(Machine-03) 공정 전력 이상치 탐지', time: '5분 전', isRead: false },
    { id: 2, type: 'warning', message: '가스 가공 파이프라인 압력 임계치 근접', time: '20분 전', isRead: false },
    { id: 3, type: 'info', message: '금일 AI 이상치 요약 보고서 발행 완료', time: '1시간 전', isRead: true },
  ]);

  useEffect(() => {
    // 1. STOMP 클라이언트 생성
    const client = new Client({
      brokerURL: `ws://${window.location.hostname}:8080/hg-websocket`,
      connectHeaders: {},

      onConnect: (frame) => {
        console.log('🟢 실시간 알림 채널 연결 성공:', frame);

        // ⭕ 2. 공통 알림 처리 헬퍼 함수
        const handleIncomingNotification = (response) => {
          try {
            const rawBody = response.body;
            let incomingNoti;

            try {
              const parsed = JSON.parse(rawBody);
              incomingNoti = {
                id: Date.now(),
                type: parsed.type || 'info',
                message: parsed.message,
                time: '방금 전',
                isRead: false
              };
            } catch {
              incomingNoti = {
                id: Date.now(),
                type: 'info',
                message: rawBody,
                time: '방금 전',
                isRead: false
              };
            }

            // 새로운 알림을 최상단에 추가
            setNotifications((prevNotis) => [incomingNoti, ...prevNotis]);
          } catch (error) {
            console.error('알림 데이터 처리 중 오류:', error);
          }
        };

        // ⭕ 3. 로그인한 유저의 권한(Role)에 따른 동적 채널 구독 조건문
        // role 변수가 배열이므로 포함 여부(includes)나 첫 번째 요소로 검사해
        if (role && role.includes('ADMIN')) {
          console.log('👑 [ADMIN] 권한 감지 - 관리자 전용 알림 채널을 구독합니다.');
          client.subscribe('/topic/role-ADMIN', handleIncomingNotification);
        }

        if (role && role.includes('MANAGER')) {
          console.log('💼 [MANAGER] 권한 감지 - 매니저 전용 알림 채널을 구독합니다.');
          client.subscribe('/topic/role-MANAGER', handleIncomingNotification);
        }

        // (옵션) 만약 권한 상관없이 본인에게 오는 1:1 알림도 같이 받고 싶다면 아래 줄 주석을 해제하면 돼
        // client.subscribe('/user/queue/notifications', handleIncomingNotification);
      },
      onWebSocketError: (error) => console.error('🔴 웹소켓 연결 실패:', error),
      onStompError: (frame) => console.error('STOMP 브로커 에러:', frame)
    });

    client.activate();
    stompClientRef.current = client;

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
        console.log('🔴 실시간 알림 채널 연결 종료');
      }
    };
  }, [role]); // ⭕ 중요: 로그아웃 후 다른 계정(다른 권한)으로 재로그인 시 채널 재구독을 위해 의존성 배열에 role 추가


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
        <h1 className="logo-section" style={{ cursor: 'pointer' }} onClick={() => navigate("/")}></h1>
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
          <button className="home_icon" onClick={() => navigate("/")}></button>
          <div className="btw_bar"></div>
          <span className="logout-btn" onClick={logout}>로그아웃</span>
        </div>
      </div>
    </header>
  )
}

export default Header;