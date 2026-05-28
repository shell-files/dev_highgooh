/**
 * AuthContext.jsx - 전역 인증 상태 관리 컨텍스트
 */

import { createContext, useContext, useEffect } from "react";
import { GET, POST, PUT, PATCH, DELETE } from "@utils/Network";
import { encodeJson, safeJsonParse } from "@utils/Base64";

import { useDispatch, useSelector } from "react-redux";
import { checkUser, logoutUser, loginUser } from '@stores/authSlice';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {

  const dispatch = useDispatch();

	// [변수] isAuthReady: localStorage 복원 완료 여부 (라우터 가드에서 활용)
	const isAuthReady = useSelector((state) => state.auth.isAuthReady);

  // [변수] 다이렉트 주소
	const redirectUrl = useSelector((state) => state.auth.redirectUrl);

	// [변수] companies: 해당 사용자의 전체 소속 회사 목록
	const companies = useSelector((state) => state.auth.companies);

  // [변수] isLoading: 로딩 상태 여부
	const isLoading = useSelector((state) => state.auth.loading);

	/**
   * [이펙트] 앱 진입 시 localStorage에서 이전 세션 복원
   */
  useEffect(() => { dispatch(checkUser()); }, []);

	/**
   * [함수] login: 로그인 API 응답 데이터를 받아 전역 상태 및 localStorage에 저장
   */
  const login = (data) => dispatch(loginUser(data));

  /**
   * [함수] logout: 로그아웃 API 응답 데이터를 받아 전역 상태 및 localStorage에 초기화
   */
  const logout = () => dispatch(logoutUser());

	// 전역 인증 상태 관리 컨텍스트에 필요한 값들을 객체로 묶어서 제공
	const authContextValue = {
    login,
    logout,
    redirectUrl,
    isLoading,
    isAuthReady,
    companies,
  };

	return (
		<AuthContext.Provider value={authContextValue}>
			{children}
		</AuthContext.Provider>
	);
};

export const useAuth = () => useContext(AuthContext);