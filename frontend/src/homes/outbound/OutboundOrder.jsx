import { useState, useEffect, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import OrderTable from '@components/UI/OrderTable';
import OrderModal from '@components/UI/OrderModal';
import { getOrder, getOrderDetail, getOrderModal, openOrderModal, setOrderPage } from '@stores/orderSlice';
import '@styles/order.css';
import { getFirstDay, getLastDayOfMonth, addOneDay } from '@stores/date';
import { showDefaultAlert } from "@components/UI/ServiceAlert";

const OutboundOrder = () => {
  const dispatch = useDispatch();

  // ── Redux state ──
  const loading = useSelector((state) => state.order.loading);
  const error = useSelector((state) => state.order.error);
  const summary = useSelector((state) => state.order.view.summary);
  const list = useSelector((state) => state.order.view.list);
  const page = useSelector((state) => state.order.view.page);
  const totalCount = useSelector((state) => state.order.view.totalCount);
  const totalPages = useSelector((state) => state.order.view.totalPages);
  const size = useSelector((state) => state.order.view.size);

  // ── 검색 ref  ──
  const startDateRef = useRef(null);
  const endDateRef = useRef(null);
  const orderIdRef = useRef(null);
  const customerIdRef = useRef(null);
  const stepRef = useRef(null);

  const getData = (targetPage = page) => {
    const params = { page: targetPage, size };

    if (startDateRef.current?.value) params.orderStart = startDateRef.current.value;
    if (endDateRef.current?.value) params.orderEnd = endDateRef.current.value;
    if (orderIdRef.current?.value) params.outboundId = orderIdRef.current.value;
    if (customerIdRef.current?.value) params.customerName = customerIdRef.current.value;
    if (stepRef.current?.value) params.status = stepRef.current.value;

    if (params.orderStart && !params.orderEnd) {
      showDefaultAlert("에러","주문 기간 검색을 완성하거나 초기화 후 검색해주세요.", "error");
      return;
    }

    dispatch(getOrder(params));
  };

  // ── 검색 폼 제출 (기존 searchEvent 유지) ──
  const searchEvent = (e) => {
    e.preventDefault();
    getData(1);   // 검색 시 1페이지부터
  };

  // ── 초기 마운트 시 목록 조회 ──
  useEffect(() => {
    const today = new Date();
    const year = today.getFullYear();
    // 월은 0부터 시작하므로 +1, 두 자릿수 포맷팅(06)
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    // 당월 시작일 (예: 2026-06-01)
    const firstDayOfMonth = `${year}-${month}-01`;
    // 오늘 날짜 (예: 2026-06-13)
    const todayDate = `${year}-${month}-${day}`;

    // ref에 기본값(defaultValue) 주입
    if (startDateRef.current) startDateRef.current.value = firstDayOfMonth;
    if (endDateRef.current) endDateRef.current.value = todayDate;

    // 날짜가 세팅된 상태에서 최초 데이터 로드 수행
    getData(1);
  }, []);

  // ── 페이지 변경 핸들러 ──
  // 페이지네이션 버튼에 핸들러 연결
  const handlePage = (targetPage) => {
    if (targetPage < 1 || targetPage > totalPages) return;
    dispatch(setOrderPage(targetPage));
    getData(targetPage);
  };

  // ── 신규 주문 등록 모달 열기 ──
  // dispatch(openOrderModal()) + 기초 데이터 로드
  const OpenOrderModal = () => {
    dispatch(getOrderModal());   // customers, products 로드
    dispatch(openOrderModal());
  };

  // ── 주문 행 클릭 → 상세 조회 API 호출 ──
  const OpenOrderDetail = (order) => {
    dispatch(getOrderDetail({ outboundId: order.outboundId }));
  };

  // ── 페이지 번호 배열 생성 (최대 5개) ──
  const getPageNumbers = () => {
    const current = page;
    const total = totalPages;
    if (total <= 1) return [1];
    const start = Math.max(1, current - 2);
    const end = Math.min(total, start + 4);
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  };

  return (
    <div id="order-page">

      <div className="page-header-flex">
        <h2 className="page-title">주문 계약 및 접수 현황 관리</h2>
        <div className="header-action-group">
          <button className="btn-main-action" onClick={OpenOrderModal}>+ 신규 주문 등록</button>
        </div>
      </div>

      {/* 요약 카드 — [변경] 하드코딩 숫자 → Redux summary 연동 */}
      <div className="order-summary-grid">
        <div className="summary-card-item">
          <div className="card-info-left">
            <span className="summary-label">전체 주문 접수</span>
            <span className="summary-value">{summary.total}<small>건</small></span>
          </div>
          <div className="card-trend-right">
            <span className="status-badge bg-all-light text-muted">당월</span>
          </div>
        </div>
        <div className="summary-card-item">
          <div className="card-info-left">
            <span className="summary-label">출고완료</span>
            <span className="summary-value text-green">{summary.completed}<small>건</small></span>
          </div>
          <div className="card-trend-right">
            <span className="status-badge bg-green-light text-green">당월</span>
          </div>
        </div>
        <div className="summary-card-item">
          <div className="card-info-left">
            <span className="summary-label">신규</span>
            <span className="summary-value text-blue">{summary.newOrder}<small>건</small></span>
          </div>
          <div className="card-trend-right">
            <span className="status-badge bg-blue-light text-blue">당월</span>
          </div>
        </div>
        <div className="summary-card-item">
          <div className="card-info-left">
            <span className="summary-label">처리중</span>
            <span className="summary-value text-orange">{summary.inProgress}<small>건</small></span>
          </div>
          <div className="card-trend-right">
            <span className="status-badge bg-orange-light text-orange">당월</span>
          </div>
        </div>
      </div>

      {/* 검색 영역 — 기존 UI 유지 */}
      <div className="filter-wrapper-card">
        <form className="search-filter-grid" onSubmit={searchEvent}>
          <div className="filter-group group-date-range">
            <label>주문 기간</label>
            <div className="date-range-container">
              <input type="date" className="filter-control" ref={startDateRef} />
              <span className="date-separator">~</span>
              <input type="date" className="filter-control" ref={endDateRef} />
            </div>
          </div>
          <div className="filter-group">
            <label>주문번호 검색</label>
            <input type="text" className="filter-control" ref={orderIdRef} />
          </div>
          <div className="filter-group">
            <label>고객사 검색</label>
            <input type="text" className="filter-control" ref={customerIdRef} />
          </div>
          <div className="filter-group">
            <label>진행 상태</label>
            {/* [변경] option value: "approved"/"pending"/"rejected" → ""/"신규"/"처리중"/"완료" */}
            <select className="filter-control" ref={stepRef}>
              <option value="">전체</option>
              <option value="신규">신규</option>
              <option value="처리중">처리중</option>
              <option value="완료">완료</option>
            </select>
          </div>
          <div className="filter-btn-group">
            <button type="reset" className="btn-filter-reset">초기화</button>
            <button type="submit" className="btn-filter-search">조회하기</button>
          </div>
        </form>
      </div>

      {/* 테이블 영역 */}
      <div className="content-card">

        {/* loading / error 처리 */}
        {loading && (
          <div className="text-center" style={{ padding: '2rem' }}>조회 중...</div>
        )}
        {error && !loading && (
          <div className="text-center" style={{ padding: '1rem', color: 'red' }}>{error}</div>
        )}

        {/* Redux list 연결 */}
        {!loading && (
          <OrderTable orders={list} onOrderClick={OpenOrderDetail} />
        )}

        <div className="pagination-container">
          {/* totalCount (Redux) */}
          <div className="pagination-info">
            전체 <span>{totalCount}</span>건
          </div>

          {/* 페이지네이션 — 버튼 핸들러 연결 */}
          <div className="pagination-buttons">
            <button type="button" className="btn-page" onClick={() => handlePage(1)}>&lt;&lt;</button>
            <button type="button" className="btn-page" onClick={() => handlePage(page - 1)}>&lt;</button>
            {getPageNumbers().map((num) => (
              <button key={num} type="button" className={`btn-page-num${num === page ? ' active' : ''}`} onClick={() => handlePage(num)}>{num}</button>
            ))}
            <button type="button" className="btn-page" onClick={() => handlePage(page + 1)}>&gt;</button>
            <button type="button" className="btn-page" onClick={() => handlePage(totalPages)}>&gt;&gt;</button>
          </div>
        </div>
      </div>

      {/* 모달 — OrderModal 단독 렌더링 (등록/상세 통합) */}
      {/* OrderModal 내부에서 isModal Redux state를 직접 구독하므로 조건부 렌더링 불필요 */}
      <OrderModal />

    </div>
  );
};

export default OutboundOrder;