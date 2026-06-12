import { useState, useEffect, useRef } from 'react';
import { POST } from "@utils/Network";
import AsnModal from '@components/UI/AsnModal';
import { useDispatch, useSelector } from "react-redux";
import { getAsn, getAsnDetail, getAsnModal, openAsnModal, setPage } from '@stores/asnSlice';
 
const Asn = () => {
  const dispatch = useDispatch();

  const orderStartRef = useRef(null);
  const orderEndRef   = useRef(null);
  const asnRef        = useRef(null);

  const loading = useSelector((state) => state.asn.loading);

  const isModal = useSelector((state) => state.asn.isModal);
  const modalMode = useSelector((state) => state.asn.modalMode);

  const summary = useSelector((state) => state.asn.view.summary);
  const list = useSelector((state) => state.asn.view.list);
  const page = useSelector((state) => state.asn.view.page);
  const totalCount = useSelector((state) => state.asn.view.totalCount);
  const totalPages = useSelector((state) => state.asn.view.totalPages);
  const size = useSelector((state) => state.asn.view.size);
 
  const openAsnDetailModal = async (id) => {
    const params = { id };
    dispatch(getAsnDetail(params));
  };
 
  /* ── 검색 / 목록 조회 ── */
  const searchEvent = (e) => {
    e.preventDefault();
    getData();
  };
 
  const getData = () => {
    const params = { page, size };
 
    if (asnRef.current?.value)        params.asnId      = asnRef.current.value;
    if (orderStartRef.current?.value) params.orderStart = orderStartRef.current.value;
    if (orderEndRef.current?.value)   params.orderEnd   = orderEndRef.current.value;
 
    if (params.orderStart && !params.orderEnd) {
      alert("주문 기간이 필요합니다.");
      return;
    }

    dispatch(getAsn(params));
  };
 
  useEffect(() => { 
    if (!isModal) getData(); 
  }, [page, isModal]);
 
  if(loading) return <></>;

  /* ── 렌더링 ── */
  return (
    <div id="asn-page">
      <div id="asn-management-page" className="page-content active">
        <div className="page-header-flex">
          <h2 className="page-title">ASN 관리</h2>
          <button className="btn-main-action" onClick={() => {
            dispatch(getAsnModal());
            dispatch(openAsnModal());
          }}>+ ASN 등록 추가</button>
        </div>
 
        {/* 요약 카드 */}
        <div className="order-summary-grid">
          <div className="summary-card-item">
            <div className="card-info-left">
              <span className="summary-label">총 입고건수</span>
              <span className="summary-value">{summary.total}<small>건</small></span>
            </div>
            <div className="card-trend-right">
              <span className="status-badge bg-all-light text-muted">당월</span>
            </div>
          </div>
          
          <div className="summary-card-item">
            <div className="card-info-left">
              <span className="summary-label">입고완료</span>
              <span className="summary-value text-green">{summary.completed}<small>건</small></span>
            </div>
            <div className="card-trend-right">
              <span className="status-badge bg-green-light text-green">당월</span>
            </div>
          </div>

          <div className="summary-card-item">
            <div className="card-info-left">
              <span className="summary-label">입고예정</span>
              <span className="summary-value text-blue">{summary.expected}<small>건</small></span>
            </div>
            <div className="card-trend-right">
              <span className="status-badge bg-blue-light text-blue">당월</span>
            </div>
          </div>
        </div>
 
        {/* 검색 필터 */}
        <div className="filter-wrapper-card">
          <form className="search-filter-grid" onSubmit={searchEvent}>
            <div className="filter-group group-date-range">
              <label>주문 기간</label>
              <div className="date-range-container">
                <input type="date" className="filter-control" ref={orderStartRef} />
                <span className="date-separator">~</span>
                <input type="date" className="filter-control" ref={orderEndRef} />
              </div>
            </div>
            <div className="filter-group">
              <label>ASN 번호 검색</label>
              <div className="date-range-container">
                <input type="text" className="filter-control" ref={asnRef} />
              </div>
            </div>
            <div />
            <div className="filter-btn-group">
              <button type="reset" className="btn-filter-reset">초기화</button>
              <button type="submit" className="btn-filter-search">조회하기</button>
            </div>
          </form>
        </div>
 
        {/* 목록 테이블 */}
        <div className="content-card" style={{ marginTop: '1rem' }}>
          <div className="table-responsive">
            <button type="button" className="btn-filter-search refresh" onClick={getData}>새로고침</button>
            <table className="main-list-table">
              <thead>
                <tr>
                  <th>ASN 번호</th>
                  <th>공급사명</th>
                  <th>입고 창고</th>
                  <th>입고 예정일</th>
                  <th>품목 건수</th>
                  <th>진행 상태</th>
                </tr>
              </thead>
              <tbody>
                {list?.map(v => (
                  <tr key={v.asnId}>
                    <td>
                      <a className="text-link" style={{cursor: 'pointer'}} onClick={() => openAsnDetailModal(v.asnId)}>
                        {v.asnId}
                      </a>
                    </td>
                    <td>{v.partnerName}</td>
                    <td>{v.warehouseName}</td>
                    <td>{v.eta}</td>
                    <td>{v.itemCount}건</td>
                    <td>
                      <span className={v.step === '입고예정' ? 'status-badge ready' : 'status-badge complete'}>
                        {v.step}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
 
          {/* 페이지네이션 */}
          <div className="pagination-container">
            <div className="pagination-info">전체 <span>{totalCount}</span>건</div>
            <div className="pagination-buttons">
              <button className="btn-page first" onClick={() => dispatch(setPage(1))}          disabled={page <= 1}>&laquo;</button>
              <button className="btn-page prev"  onClick={() => dispatch(setPage(page - 1))} disabled={page <= 1}>&lsaquo;</button>
              {Array.from({ length: totalPages }, (_, i) => i + 1).map(index => (
                <button
                  key={index}
                  className={page === index ? 'btn-page-num active' : 'btn-page-num'}
                  onClick={() => dispatch(setPage(index))}
                >
                  {index}
                </button>
              ))}
              <button className="btn-page next" onClick={() => dispatch(setPage(page + 1))} disabled={page === totalPages}>&rsaquo;</button>
              <button className="btn-page last" onClick={() => dispatch(setPage(totalPages))}  disabled={page === totalPages}>&raquo;</button>
            </div>
            <div className="pagination-size-selector" />
          </div>
        </div>
      </div>
 
      {/* 공통 모달 - 등록/상세 모드 전환 */}
      {isModal && <AsnModal />}
    </div>
  );
};
 
export default Asn;