import { useState, useEffect, useRef } from 'react';
import { POST } from "@utils/Network";
import AsnModal from '@components/UI/AsnModal';

const Asn = () => {
  const orderStartRef = useRef(null);
  const orderEndRef = useRef(null);
  const asnRef = useRef(null);

  const [isModal, setModal] = useState(false);
  const [modalMode, setModalMode] = useState('register');       // 'register' | 'detail'
  const [detailData, setDetailData] = useState(null);

  const [summary, setSummary] = useState({ completed: 0, expected: 0, total: 0 });
  const [list, setList] = useState([]);
  const [page, setPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [size] = useState(10);

  const [firstDate, setFirstDate] = useState(null);
  const [endDate, setEndDate] = useState(null);

  const getFirstDay = () => {
    const today = new Date();
    // 이번 달 1일 구하기 ("YYYY-MM-01")
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const firstDayStr = `${year}-${month}-01`;
    return firstDayStr;
  }

  const getLastDayOfMonth = () => {
    const today = new Date();
    const year = today.getFullYear();
    // today.getMonth() + 1 은 '다음 달'의 인덱스가 됩니다.
    // 일(Day) 자리에 0을 주면 '이번 달의 마지막 날' 객체가 생성됩니다.
    const lastDay = new Date(year, today.getMonth() + 1, 0);

    const lastYear = lastDay.getFullYear();
    const lastMonth = String(lastDay.getMonth() + 1).padStart(2, '0');
    const lastDate = String(lastDay.getDate()).padStart(2, '0');

    const lastDayStr = `${lastYear}-${lastMonth}-${lastDate}`;
    return lastDayStr; // 예: "2026-06-30" 또는 "2026-02-28" 등 자동 계산
  };

  // 오늘 날짜 구하기 ("YYYY-MM-DD")


  // ✨ 1. 화면이 처음 켜질 때 날짜 초기값 세팅 (이번달 1일 & 오늘)
  useEffect(() => {

    // input 엘리먼트에 초기값 주입
    if (orderStartRef.current) orderStartRef.current.value = getFirstDay();
    if (orderEndRef.current) orderEndRef.current.value = getLastDayOfMonth();

    // 초기 데이터 로드 호출
    getData();
  }, []); // 중요: 첫 마운트 시점에만 실행

  const resetResearch = () => {
    if (orderStartRef.current) orderStartRef.current.value = getFirstDay();
    if (orderEndRef.current) orderEndRef.current.value = getLastDayOfMonth();
    if (asnRef.current) asnRef.current.value = null;
    // 초기 데이터 로드 호출
    getData();
  }

  // ✨ 2. 날짜에 하루를 더해주는 순수 함수로 수정
  const addOneDay = (dateStr) => {
    if (!dateStr) return "";

    const date = new Date(dateStr);
    date.setDate(date.getDate() + 1); // 하루 더하기

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  };


  /* ── 모달 열기 ── */
  const openAsnModal = () => {
    setModalMode('register');
    setDetailData(null);
    setModal(true);
  };

  const openAsnDetailModal = (id) => {
    POST(`/asn/${id}`).then(res => {
      if (res.status === true) {
        setDetailData(res.data);
        setModalMode('detail');
        setModal(true);
      }
    });
  };

  /* ── 검색 / 목록 조회 ── */
  const searchEvent = (e) => {
    e.preventDefault();
    getData();
  };

  // ✨ 3. 서버로 데이터를 보낼 때 하루 더해서 가공하는 로직 추가
  const getData = () => {
    const params = { page, size };

    if (asnRef.current?.value) params.asnId = asnRef.current.value;
    if (orderStartRef.current?.value) {
      params.orderStart = orderStartRef.current.value;
      setFirstDate(params.orderStart)
    }

    // 💡 종료일이 존재할 때만 addOneDay를 거쳐 하루 더한 값을 params에 매핑합니다.
    if (orderEndRef.current?.value) {
      params.orderEnd = addOneDay(orderEndRef.current.value);
      setEndDate(orderEndRef.current.value)
    }

    if (params.orderStart && !params.orderEnd) {
      alert("주문 기간이 필요합니다.");
      return;
    }

    POST("/asn", params).then(res => {
      if (res.status === true) {
        setSummary(res.data.summary);
        setList(res.data.list);
        setPage(res.data.pagination.page);
        setTotalCount(res.data.pagination.totalCount);
        setTotalPages(res.data.pagination.totalPages);
      }
    });
  };

  // 페이지가 변경될 때만 호출하도록 분리 (초기 빈 배열 마운트 이펙트와 중복 호출 방지)
  useEffect(() => {
    // 첫 로딩 시점(page가 1이고 list가 비어있을 때)의 중복 호출 방지용 안전 장치
    if (list.length > 0) {
      getData();
    }
  }, [page]);


  /* ── 렌더링 ── */
  return (
    <div id="asn-page">
      <div id="asn-management-page" className="page-content active">
        <div className="page-header-flex">
          <h2 className="page-title">ASN 관리</h2>
          <button className="btn-main-action" onClick={openAsnModal}>+ ASN 등록 추가</button>
        </div>

        {/* 요약 카드 */}
        <div className="order-summary-grid">
          <div className="summary-card-item">
            <div className="card-info-left">
              <span className="summary-label">총 입고건수</span>
              <span className="summary-value">{summary.total}<small>건</small></span>
            </div>
            <div className="card-trend-right">
              <span className="status-badge bg-all-light text-muted">{
                getFirstDay() === firstDate && getLastDayOfMonth() === endDate ? "당월" : "선택"
              }</span>
            </div>
          </div>
          <div className="summary-card-item">
            <div className="card-info-left">
              <span className="summary-label">입고예정</span>
              <span className="summary-value text-green">{summary.expected}<small>건</small></span>
            </div>
            <div className="card-trend-right">
              <span className="status-badge bg-green-light text-green">{
                getFirstDay() === firstDate && getLastDayOfMonth() === endDate ? "당월" : "선택"
              }</span>
            </div>
          </div>
          <div className="summary-card-item">
            <div className="card-info-left">
              <span className="summary-label">입고완료</span>
              <span className="summary-value text-blue">{summary.completed}<small>건</small></span>
            </div>
            <div className="card-trend-right">
              <span className="status-badge bg-blue-light text-blue">{
                getFirstDay() === firstDate && getLastDayOfMonth() === endDate ? "당월" : "선택"
              }</span>
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
              <button type="button" onClick={()=>resetResearch()} className="btn-filter-reset">초기화</button>
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
                  <th>주문 일자</th>
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
                    <td>{v.orderDate}</td>
                    <td>
                      <a href="#" className="text-link" onClick={() => openAsnDetailModal(v.asnId)}>
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
              <button className="btn-page first" onClick={() => setPage(1)} disabled={page <= 1}>&laquo;</button>
              <button className="btn-page prev" onClick={() => setPage(p => p - 1)} disabled={page <= 1}>&lsaquo;</button>
              {Array.from({ length: totalPages }, (_, i) => i + 1).map(index => (
                <button
                  key={index}
                  className={page === index ? 'btn-page-num active' : 'btn-page-num'}
                  onClick={() => setPage(index)}
                >
                  {index}
                </button>
              ))}
              <button className="btn-page next" onClick={() => setPage(p => p + 1)} disabled={page === totalPages}>&rsaquo;</button>
              <button className="btn-page last" onClick={() => setPage(totalPages)} disabled={page === totalPages}>&raquo;</button>
            </div>
            <div className="pagination-size-selector" />
          </div>
        </div>
      </div>

      {/* 공통 모달 - 등록/상세 모드 전환 */}
      {isModal && (
        <AsnModal
          isModal={isModal}
          setModal={setModal}
          getData={getData}
          mode={modalMode}
          initialData={detailData}
        />
      )}
    </div>
  );
};

export default Asn;