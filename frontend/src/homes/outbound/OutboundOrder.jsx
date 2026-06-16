import { useState, useEffect, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import OrderTable from '@components/UI/OrderTable';
import OrderModal from '@components/UI/OrderModal';
import { getOrder, getOrderDetail, getOrderModal, openOrderModal, setOrderPage } from '@stores/orderSlice';
import { getFirstDay, getLastDayOfMonth, addOneDay } from '@stores/date';
import '@styles/order.css';
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
  const isModal = useSelector((state) => state.order.isModal);

  // ── 검색용 useRef ──
  const startDateRef = useRef(null);
  const endDateRef = useRef(null);
  const orderIdRef = useRef(null);
  const customerIdRef = useRef(null);
  const stepRef = useRef(null);

  // ── 단일 데이터 조회 함수 (Ref 데이터 직접 수집) ──
  const getData = () => {
    const startVal = startDateRef.current?.value || "";
    const endVal = endDateRef.current?.value || "";

    if ((startVal !== "" && endVal === "") || (startVal === "" && endVal !== "")) {
      showDefaultAlert("오류", "주문 기간 검색을 완성하거나 초기화 후 검색해주세요.", "error");
      return;
    }

    const params = { page, size };

    if (orderIdRef.current?.value) params.outboundId = orderIdRef.current.value;
    if (customerIdRef.current?.value) params.customerName = customerIdRef.current.value;
    if (stepRef.current?.value) params.status = stepRef.current.value;

    if (startVal !== "") {
      params.orderStart = startVal;
    }
    if (endVal !== "") {
      params.orderEnd = addOneDay(endVal);
    }

    dispatch(getOrder(params));
  };

  // ── 선언적 생명주기 관리: 페이지 변경 및 모달이 닫힐 때 자동 리로드 ──
  useEffect(() => {
    if (!isModal) getData();
  }, [page, isModal]);

  // ── 검색 폼 제출 핸들러 ──
  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (page === 1) getData(); else dispatch(setOrderPage(1));
  };

  // ── 초기화 버튼 핸들러 (입력값 복원 및 데이터 동기화) ──
  const handleResetFilter = () => {
    if (startDateRef.current) startDateRef.current.value = getFirstDay();
    if (endDateRef.current) endDateRef.current.value = getLastDayOfMonth();
    if (orderIdRef.current) orderIdRef.current.value = "";
    if (customerIdRef.current) customerIdRef.current.value = "";
    if (stepRef.current) stepRef.current.value = "";

    if (page === 1) getData(); else dispatch(setOrderPage(1));
  };

  // ── 신규 주문 등록 모달 열기 ──
  const OpenOrderModal = () => {
    dispatch(getOrderModal()); // 기초 마스터 데이터 로드
    dispatch(openOrderModal());
  };

  // ── 주문 행 클릭 → 상세 조회 ──
  const OpenOrderDetail = (order) => {
    dispatch(getOrderDetail({ outboundId: order.outboundId }));
  };

  return (
    <div id="order-page">
      <div className="page-header-flex">
        <h2 className="page-title">주문 계약 및 접수 현황 관리</h2>
        <div className="header-action-group">
          <button className="btn-main-action" onClick={OpenOrderModal}>+ 신규 주문 등록</button>
        </div>
      </div>

      {/* 대시보드 요약 카드 영역 */}
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
            <span className="summary-label">주문완료</span>
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
        {/* <div className="summary-card-item">
          <div className="card-info-left">
            <span className="summary-label">처리중</span>
            <span className="summary-value text-orange">{summary.inProgress}<small>건</small></span>
          </div>
          <div className="card-trend-right">
            <span className="status-badge bg-orange-light text-orange">당월</span>
          </div>
        </div> */}
      </div>

      {/* 검색 필터 영역 (공통 date 유틸 기본값 바인딩) */}
      <div className="filter-wrapper-card">
        <form className="search-filter-grid" onSubmit={handleSearchSubmit}>
          <div className="filter-group group-date-range">
            <label>주문 기간</label>
            <div className="date-range-container">
              <input type="date" className="filter-control" ref={startDateRef} defaultValue={getFirstDay()} />
              <span className="date-separator">~</span>
              <input type="date" className="filter-control" ref={endDateRef} defaultValue={getLastDayOfMonth()} />
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
            <select className="filter-control" ref={stepRef} defaultValue="">
              <option value="">전체</option>
              <option value="신규">신규</option>
              {/* <option value="처리중">처리중</option> */}
              <option value="주문완료">주문완료</option>
            </select>
          </div>
          <div className="filter-btn-group">
            <button type="button" className="btn-filter-reset" onClick={handleResetFilter}>초기화</button>
            <button type="submit" className="btn-filter-search">조회하기</button>
          </div>
        </form>
      </div>

      {/* 메인 데이터 테이블 카드 */}
      <div className="content-card">
        {loading && <div className="text-center" style={{ padding: '2rem' }}>조회 중...</div>}
        {error && !loading && <div className="text-center" style={{ padding: '1rem', color: 'red' }}>{error}</div>}

        {!loading && (
          <OrderTable orders={list} onOrderClick={OpenOrderDetail} />
        )}

        {/* 페이지네이션 */}
        <div className="pagination-container">
          <div className="pagination-info">전체 <span>{totalCount}</span>건</div>
          <div className="pagination-buttons">
            <button type="button" className="btn-page first" onClick={() => dispatch(setOrderPage(1))} disabled={page <= 1}>&laquo;</button>
            <button type="button" className="btn-page prev" onClick={() => dispatch(setOrderPage(page - 1))} disabled={page <= 1}>&lsaquo;</button>
            {Array.from({ length: totalPages }, (_, i) => i + 1).map((num) => (
              <button key={num} type="button" className={`btn-page-num${num === page ? ' active' : ''}`} onClick={() => dispatch(setOrderPage(num))}>{num}</button>
            ))}
            <button type="button" className="btn-page next" onClick={() => dispatch(setOrderPage(page + 1))} disabled={page === totalPages}>&rsaquo;</button>
            <button type="button" className="btn-page last" onClick={() => dispatch(setOrderPage(totalPages))} disabled={page === totalPages}>&raquo;</button>
          </div>
          <div className="pagination-size-selector" />
        </div>
      </div>

      <OrderModal />
    </div>
  );
};

export default OutboundOrder;