import React, { useState, useEffect } from 'react';
import '@styles/packing.css';

// ==========================================
// DB 연동 전 사용할 초기 더미 데이터 세트
// ==========================================
const INITIAL_DUMMY_DATA = {
  // 1. 대시보드 요약 수치
  summary: {
    total: 42,
    completed: 34,
    new: 42,
    imminent: 5,
    overdue: 3
  },
  // 2. 메인 테이블 리스트 (주문 마스터 정보)
  orderList: [
    { orderNo: 'PO-20260601-M04', customer: '대한알루미늄공업', totalQty: 17, orderDate: '2026-06-01', deliveryDate: '2026-06-20', timeLeft: '17분 남음', status: '신규' },
    { orderNo: 'PO-20260602-X01', customer: '(주)한성자재마트', totalQty: 21, orderDate: '2026-06-02', deliveryDate: '2026-06-25', timeLeft: '15분 초과', status: '처리중' },
    { orderNo: 'PO-20260602-X02', customer: '(주)한성자재마트', totalQty: 21, orderDate: '2026-06-02', deliveryDate: '2026-06-25', timeLeft: '15분 초과', status: '완료' }
  ],
  // 3. 개별 DB 테이블에서 조회해 온 형식의 주문별 제품 상세 데이터 목록
  orderDetails: {
    'PO-20260601-M04': {
      customer: '대한알루미늄공업',
      status: '신규',
      // DB에 낱개 행으로 분리되어 저장되어 있는 품목 리스트
      products: [
        { id: 101, name: 'Al 시트레일 압출재 (6063-T5)', qty: 1000, price: 12500000, invoiceNo: 'IVC-20260601-004A', boxQty: 10 },
        { id: 102, name: '조립용 고정 볼트 (M6)', qty: 5000, price: 5350000, invoiceNo: 'IVC-20260601-004B', boxQty: 2 }
      ]
    },
    'PO-20260602-X01': {
      customer: '(주)한성자재마트',
      status: '처리중',
      products: [
        { id: 201, name: 'Al 시트레일 압출재 (6063-T5)', qty: 1200, price: 15000000, invoiceNo: 'IVC-20260602-001A', boxQty: 12 },
        { id: 202, name: '알루미늄 플레이트 (5052)', qty: 450, price: 6000000, invoiceNo: 'IVC-20260602-001B', boxQty: 5 }
      ]
    },
    'PO-20260602-X02': {
      customer: '(주)한성자재마트',
      status: '완료',
      products: [
        { id: 301, name: '알루미늄 플레이트 (5052)', qty: 450, price: 6000000, invoiceNo: 'IVC-20260602-002A', boxQty: 5 }
      ]
    }
  }
};

export default function PackingPage() {
  // 메인 대시보드 및 리스트 상태
  const [summaryData, setSummaryData] = useState(INITIAL_DUMMY_DATA.summary);
  const [orders, setOrders] = useState(INITIAL_DUMMY_DATA.orderList);
  const [filteredOrders, setFilteredOrders] = useState(INITIAL_DUMMY_DATA.orderList);

  // 모달 및 상세 조회 상태
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedOrderNo, setSelectedOrderNo] = useState('');
  const [orderData, setOrderData] = useState(null);
  
  // 배송 운송사 설정 및 발행된 송장 목록 슬라이더 상태
  const [carrier, setCarrier] = useState('');
  const [invoicePreviews, setInvoicePreviews] = useState([]);
  const [currentSlipIdx, setCurrentSlipIdx] = useState(0);

  // 검색 필터 폼 상태
  const [filterStart, setFilterStart] = useState('');
  const [filterEnd, setFilterEnd] = useState('');
  const [filterOrderNo, setFilterOrderNo] = useState('');
  const [filterCustomer, setFilterCustomer] = useState('');
  const [filterStatus, setFilterStatus] = useState('');

  // 컴포넌트 마운트 시 데이터 조회 (DB 연동 지점)
  useEffect(() => {
    /* const fetchDashboardData = async () => {
      try {
        const response = await fetch('/api/packing/dashboard');
        const data = await response.json();
        setSummaryData(data.summary);
        setOrders(data.orders);
        setFilteredOrders(data.orders);
      } catch (err) { console.error(err); }
    };
    fetchDashboardData();
    */
  }, []);

  // 메인 테이블 행 클릭 시 팝업 오픈 및 상세 품목 조회
  const openOrderDetailModal = (orderNo) => {
    const data = INITIAL_DUMMY_DATA.orderDetails[orderNo];
    
    /* // 실제 DB 연동 시: 낱개로 쪼개져 저장된 제품 정보 목록을 다시 불러오는 API 예시
    try {
      const response = await fetch(`/api/packing/orders/${orderNo}/products`);
      const data = await response.json(); // { customer, status, products: [...] }
    } catch (err) { ... }
    */

    if (!data) {
      alert('해당 주문의 상세 내역 정보를 찾을 수 없습니다.');
      return;
    }

    setSelectedOrderNo(orderNo);
    setOrderData(data);
    setCarrier('');
    setInvoicePreviews([]);
    setCurrentSlipIdx(0);
    setIsModalOpen(true);
  };

  // 모달 초기화 및 닫기
  const closeOrderDetailModal = () => {
    setIsModalOpen(false);
    setOrderData(null);
    setSelectedOrderNo('');
    setInvoicePreviews([]);
    setCurrentSlipIdx(0);
  };

  // [수정] 운송사 선택 후 송장 생성 시, DB에 적재된 각각의 낱개 제품별 송장 슬라이더 빌드
  const handleGenerateInvoice = () => {
    if (!carrier) {
      alert('배송을 담당할 택배사/물류업체를 선택해 주세요.');
      return;
    }
    if (!orderData || !orderData.products || orderData.products.length === 0) {
      alert('출고 처리를 진행할 제품 정보가 존재하지 않습니다.');
      return;
    }

    /*
    // 실제 시나리오 가이드 코드:
    // 1단계: 프론트에서 [송장 생성] 클릭 시 운송사 정보 등을 매핑하여 백엔드 DB 저장 API 호출 가능
    // await fetch(`/api/packing/orders/${selectedOrderNo}/invoice/save`, { method: 'POST', body: ... });
    // 2단계: 저장 완료 후 개별 고유 송장 번호가 발행된 데이터를 새로 State에 세팅
    */

    // DB에 개별 낱개 저장되어 불러와진 각 제품 데이터들을 기반으로 개별 송장 데이터 맵핑
    const generatedSlips = orderData.products.map((product) => ({
      invoiceNo: product.invoiceNo, // DB 개별 식별 번호 기반 송장 코드
      customer: orderData.customer,
      productName: product.name,
      qty: product.qty,
      boxQty: product.boxQty,
      carrier: carrier
    }));

    setInvoicePreviews(generatedSlips);
    setCurrentSlipIdx(0); // 생성 직후 첫 번째 품목 송장 화면 노출
  };

  // 슬라이더 이전 / 다음 내비게이션 (Outbound.jsx 스타일 방식)
  const prevSlip = () => {
    if (currentSlipIdx > 0) setCurrentSlipIdx(currentSlipIdx - 1);
  };

  const nextSlip = () => {
    if (currentSlipIdx < invoicePreviews.length - 1) setCurrentSlipIdx(currentSlipIdx + 1);
  };

  // 메인 조건 검색 핸들러
  const handleSearchSubmit = (e) => {
    e.preventDefault();
    const statusMap = { 'approved': '신규', 'pending': '처리중', 'rejected': '완료' };

    const result = orders.filter(order => {
      const matchOrderNo = filterOrderNo ? order.orderNo.toLowerCase().includes(filterOrderNo.toLowerCase()) : true;
      const matchCustomer = filterCustomer ? order.customer.toLowerCase().includes(filterCustomer.toLowerCase()) : true;
      const matchStatus = filterStatus ? order.status === statusMap[filterStatus] : true;
      
      const orderDate = new Date(order.orderDate);
      const matchStart = filterStart ? orderDate >= new Date(filterStart) : true;
      const matchEnd = filterEnd ? orderDate <= new Date(filterEnd) : true;

      return matchOrderNo && matchCustomer && matchStatus && matchStart && matchEnd;
    });

    setFilteredOrders(result);
  };

  // 필터 검색 조건 초기화
  const handleResetFilter = () => {
    setFilterStart(''); setFilterEnd(''); setFilterOrderNo(''); setFilterCustomer(''); setFilterStatus('');
    setFilteredOrders(orders);
  };

  // 상태 배지 CSS 클래스 매핑
  const getStatusBadgeClass = (status) => {
    switch (status) {
      case '신규': return 'text-blue bg-blue-light';
      case '처리중': return 'text-red bg-red-light';
      case '완료': return 'text-green bg-green-light';
      default: return 'text-muted bg-all-light';
    }
  };

  return (
    <div id="packing-page">
      <div className="page-header-flex">
        <h2 className="page-title">패킹</h2>
      </div>

      {/* 5칸 대시보드 요약 */}
      <div className="order-summary-grid">
        <div className="summary-card-item">
          <div className="card-info-left"><span className="summary-label">당월 전체</span><span className="summary-value">{summaryData.total}<small>건</small></span></div>
          <div className="card-trend-right"><span className="status-badge bg-all-light text-muted">당월</span></div>
        </div>
        <div className="summary-card-item">
          <div className="card-info-left"><span className="summary-label">패킹 완료</span><span className="summary-value text-green">{summaryData.completed}<small>건</small></span></div>
          <div className="card-trend-right"><span className="status-badge bg-green-light text-green">당월</span></div>
        </div>
        <div className="summary-card-item">
          <div className="card-info-left"><span className="summary-label">신규</span><span className="summary-value text-blue">{summaryData.new}<small>건</small></span></div>
          <div className="card-trend-right"><span className="status-badge bg-blue-light text-blue">당월</span></div>
        </div>
        <div className="summary-card-item">
          <div className="card-info-left"><span className="summary-label">기한 임박</span><span className="summary-value text-orange">{summaryData.imminent}<small>건</small></span></div>
          <div className="card-trend-right"><span className="status-badge bg-orange-light text-orange">당월</span></div>
        </div>
        <div className="summary-card-item">
          <div className="card-info-left"><span className="summary-label">기한 초과</span><span className="summary-value text-red">{summaryData.overdue}<small>건</small></span></div>
          <div className="card-trend-right"><span className="status-badge bg-red-light text-red">당월</span></div>
        </div>
      </div>

      {/* 검색 필터 바 */}
      <div className="filter-wrapper-card">
        <form className="search-filter-grid" onSubmit={handleSearchSubmit}>
          <div className="filter-group group-date-range">
            <label>주문 기간</label>
            <div className="date-range-container">
              <input type="date" className="filter-control" value={filterStart} onChange={(e) => setFilterStart(e.target.value)} />
              <span className="date-separator">~</span>
              <input type="date" className="filter-control" value={filterEnd} onChange={(e) => setFilterEnd(e.target.value)} />
            </div>
          </div>
          <div className="filter-group">
            <label>주문번호 검색</label>
            <input type="text" className="filter-control" value={filterOrderNo} onChange={(e) => setFilterOrderNo(e.target.value)} />
          </div>
          <div className="filter-group">
            <label>고객사 검색</label>
            <input type="text" className="filter-control" value={filterCustomer} onChange={(e) => setFilterCustomer(e.target.value)} />
          </div>
          <div className="filter-group">
            <label htmlFor="search_status">진행 상태</label>
            <select id="search_status" className="filter-control" value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
              <option value="">전체 상태</option>
              <option value="approved">신규</option>
              <option value="pending">처리중</option>
              <option value="rejected">완료</option>
            </select>
          </div>
          <div className="filter-btn-group">
            <button type="button" className="btn-filter-reset" onClick={handleResetFilter}>초기화</button>
            <button type="submit" className="btn-filter-search">조회하기</button>
          </div>
        </form>
      </div>

      {/* 메인 리스트 테이블 */}
      <div className="content-card">
        <div className="table-responsive">
          <table className="order-data-table">
            <thead>
              <tr>
                <th>주문번호</th><th>발주처(고객사)</th><th>총 물량(세트)</th><th>주문일자</th><th>납기요청일</th><th>기한</th><th>진행상태</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.length === 0 ? (
                <tr><td colSpan="7" className="text-center" style={{ padding: '2rem', color: 'var(--text-muted)' }}>조회된 패킹 내역이 없습니다.</td></tr>
              ) : (
                filteredOrders.map((order, index) => (
                  <tr key={index} onClick={() => openOrderDetailModal(order.orderNo)} style={{ cursor: 'pointer' }}>
                    <td className="text-center font-bold text-link">{order.orderNo}</td>
                    <td>{order.customer}</td>
                    <td className="text-center text-green">{order.totalQty}</td>
                    <td className="text-center">{order.orderDate}</td>
                    <td className="text-center">{order.deliveryDate}</td>
                    <td className="text-center">{order.timeLeft}</td>
                    <td className="text-center"><span className={`table-badge ${getStatusBadgeClass(order.status)}`}>{order.status}</span></td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* 주문 상세 계약 내역 모달 */}
      <div className={`modal-overlay ${isModalOpen ? 'active' : ''}`}>
        <div className="modal-container" style={{ width: '980px', maxWidth: '95%' }}>
          <div className="modal-header">
            <h3>주문 계약 상세 내역</h3>
            <button className="modal-close-btn" onClick={closeOrderDetailModal}>&times;</button>
          </div>

          {orderData && (
            <div className="modal-body" style={{ padding: '1rem' }}>
              {/* 마스터 필수 인풋 그룹 */}
              <div className="modal-form-grid" style={{ gridTemplateColumns: 'repeat(3, 1fr)', marginBottom: '1rem', padding: '0 0.5rem' }}>
                <div className="form-group">
                  <label>주문번호(ASN)</label>
                  <input type="text" className="modal-input" value={selectedOrderNo} readOnly style={{ backgroundColor: '#f8fafc' }} />
                </div>
                <div className="form-group">
                  <label>발주처 (고객사)</label>
                  <input type="text" className="modal-input" value={orderData.customer} readOnly />
                </div>
                <div className="form-group">
                  <label>진행상태</label>
                  <select className="modal-input" value={orderData.status || '신규'} disabled style={{ height: '38px', padding: '0.5rem' }}>
                    <option value="신규">신규</option>
                    <option value="처리중">처리중</option>
                    <option value="완료">완료</option>
                  </select>
                </div>
              </div>

              {/* 2분할 메인 콘텐츠 레이아웃 */}
              <div className="modal-split-layout">
                {/* 좌측: 제품 정보 리스트 및 배송사 등록 */}
                <div className="modal-split-left">
                  <div className="modal-section-title" style={{ marginTop: 0 }}>
                    <h4>1. 주문 제품 정보 <small style={{ color: 'var(--text-muted)' }}>(계약 품목 상세 목록)</small></h4>
                  </div>
                  <div className="modal-table-responsive" style={{ maxHeight: '180px', marginBottom: '1rem' }}>
                    {/* [수정] 행 단위 클릭 이벤트 및 스타일 효과 제거 (단순 정보 출력 전용) */}
                    <table className="modal-product-table">
                      <thead>
                        <tr>
                          <th style={{ width: '55%' }}>제품명</th>
                          <th style={{ width: '20%', textAlign: 'right' }}>수량(세트)</th>
                          <th style={{ width: '25%', textAlign: 'right' }}>가격(원)</th>
                        </tr>
                      </thead>
                      <tbody>
                        {orderData.products.map((product, idx) => (
                          <tr key={idx}>
                            <td className="text-left" style={{ padding: '0.75rem 0.5rem', textAlign: 'left', color: 'var(--text-dark)' }}>{product.name}</td>
                            <td className="text-right" style={{ padding: '0.75rem 0.5rem', fontWeight: 600 }}>{product.qty.toLocaleString()}</td>
                            <td className="text-right text-green" style={{ padding: '0.75rem 0.5rem', fontWeight: 600 }}>{product.price.toLocaleString()} 원</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  <div className="delivery-config-zone">
                    <div className="modal-section-title" style={{ marginTop: 0, marginBottom: '0.75rem' }}>
                      <h4>2. 배송 및 차량 정보 설정</h4>
                    </div>
                    <div className="config-form-row">
                      <div className="form-group">
                        <label htmlFor="select_carrier">택배사/물류업체</label>
                        <select id="select_carrier" className="modal-input select-styled" value={carrier} onChange={(e) => setCarrier(e.target.value)}>
                          <option value="">-- 택배사 선택 --</option>
                          <option value="CJ대한통운">CJ대한통운</option>
                          <option value="경동택배">경동택배 (대형물류)</option>
                          <option value="한진택배">한진택배</option>
                          <option value="용달화물">자체 배송 (용달화물)</option>
                        </select>
                      </div>
                    </div>

                    <div className="action-btn-wrap">
                      <button type="button" className="btn-generate-invoice" onClick={handleGenerateInvoice}>
                        송장 생성 (전체 품목)
                      </button>
                    </div>
                  </div>
                </div>

                {/* 우측: Outbound.jsx 연동 슬라이더 형태 송장 프리뷰 결과창 */}
                <div className="modal-split-right">
                  <div className="modal-section-title" style={{ marginTop: 0 }}>
                    <h4>3. 제품별 출고 송장 발행 결과 {invoicePreviews.length > 0 && `(${currentSlipIdx + 1} / ${invoicePreviews.length})`}</h4>
                  </div>
                  
                  <div className="invoice-preview-card">
                    {invoicePreviews.length === 0 ? (
                      <div className="empty-invoice-msg">
                        배송 물류업체 설정을 마친 후 하단의<br /><strong>[송장 생성 (전체 품목)]</strong> 버튼을 클릭해 주세요.
                      </div>
                    ) : (
                      <div className="slider-wrapper-container">
                        {/* 왼쪽 슬라이드 이동 버튼 */}
                        <button 
                          type="button"
                          className="slider-nav-btn prev" 
                          onClick={prevSlip} 
                          disabled={currentSlipIdx === 0}
                        >
                          &#10094;
                        </button>

                        {/* 개별 낱개 제품 매핑 완료된 독립 송장 카드 */}
                        <div className="invoice-box-inner animated-fade">
                          <div className="invoice-header-title">출고 거래 송장 (PACKING SLIP)</div>
                          <table className="invoice-mini-table">
                            <tbody>
                              <tr>
                                <th>송장 번호</th>
                                <td><strong style={{ color: 'var(--primary-green)' }}>{invoicePreviews[currentSlipIdx].invoiceNo}</strong></td>
                              </tr>
                              <tr>
                                <th>받는 사람</th>
                                <td>{invoicePreviews[currentSlipIdx].customer}</td>
                              </tr>
                              <tr>
                                <th>주소</th>
                                <td>서울특별시 강남구 테헤란로 123</td>
                              </tr>
                              <tr>
                                <th>보내는 사람</th>
                                <td>하이고 금속</td>
                              </tr>
                              <tr>
                                <th>출고 물품</th>
                                <td style={{ fontSize: '0.85rem', fontWeight: 600, color: '#1a202c' }}>{invoicePreviews[currentSlipIdx].productName}</td>
                              </tr>
                              <tr>
                                <th>확정 수량</th>
                                <td>{invoicePreviews[currentSlipIdx].qty.toLocaleString()} 세트 ({invoicePreviews[currentSlipIdx].boxQty} Box)</td>
                              </tr>
                              <tr>
                                <th>배송 업체</th>
                                <td>{invoicePreviews[currentSlipIdx].carrier}</td>
                              </tr>
                            </tbody>
                          </table>
                          
                          {/* 개별 제품 전용 바코드 */}
                          <div className="barcode-wrapper">
                            <div className="barcode-lines">
                              <span className="b-w-3"></span><span className="b-w-1"></span><span className="b-w-2"></span><span className="b-w-4"></span>
                              <span className="b-w-1"></span><span className="b-w-3"></span><span className="b-w-2"></span><span className="b-w-1"></span>
                              <span className="b-w-4"></span><span className="b-w-2"></span><span className="b-w-1"></span><span className="b-w-3"></span>
                            </div>
                            <div className="barcode-text">*{invoicePreviews[currentSlipIdx].invoiceNo}*</div>
                          </div>
                          
                          <div className="invoice-footer-action">
                            <button 
                              type="button" 
                              className="btn-secondary-sm" 
                              onClick={() => alert(`${invoicePreviews[currentSlipIdx].invoiceNo} 인쇄 명령이 시스템으로 전송되었습니다.`)}
                            >
                              현재 송장 인쇄
                            </button>
                          </div>
                        </div>

                        {/* 오른쪽 슬라이드 이동 버튼 */}
                        <button 
                          type="button"
                          className="slider-nav-btn next" 
                          onClick={nextSlip} 
                          disabled={currentSlipIdx === invoicePreviews.length - 1}
                        >
                          &#10095;
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          )}

          <div className="modal-footer">
            <button type="button" className="btn-pop-cancel" onClick={closeOrderDetailModal}>닫기</button>
          </div>
        </div>
      </div>
    </div>
  );
}