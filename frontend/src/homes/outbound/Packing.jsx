import React, { useState, useEffect } from 'react';
import { QRCodeSVG } from 'qrcode.react';
import '@styles/packing.css';

// ==========================================
// [MOCK DB] 서버 역할을 대행할 가상 데이터베이스
// ==========================================
const MOCK_SERVER_DB = {
  summary: { total: 42, completed: 34, new: 42, imminent: 5, overdue: 3 },
  
  orders: [
    { orderNo: 'PO-20260601-M04', customer: '대한알루미늄공업', totalQty: 17, orderDate: '2026-06-01', deliveryDate: '2026-06-20', timeLeft: '17분 남음', status: '신규' },
    { orderNo: 'PO-20260602-X01', customer: '(주)한성자재마트', totalQty: 21, orderDate: '2026-06-02', deliveryDate: '2026-06-25', timeLeft: '15분 초과', status: '처리중' },
    { orderNo: 'PO-20260602-X02', customer: '(주)한성자재마트', totalQty: 12, orderDate: '2026-06-02', deliveryDate: '2026-06-25', timeLeft: '15분 초과', status: '완료' }
  ],

  orderItemDetails: {
    'PO-20260601-M04': {
      customer: '대한알루미늄공업', status: '신규',
      products: [
        { product_id: 101, name: 'Al 시트레일 압출재 (6063-T5)', qty: 10, price: 1250000 },
        { product_id: 102, name: '조립용 고정 볼트 (M6)', qty: 7, price: 535000 }
      ]
    },
    'PO-20260602-X01': {
      customer: '(주)한성자재마트', status: '처리중',
      products: [
        { product_id: 201, name: 'Al 시트레일 압출재 (6063-T5)', qty: 15, price: 1500000 },
        { product_id: 202, name: '알루미늄 플레이트 (5052)', qty: 6, price: 600000 }
      ]
    },
    'PO-20260602-X02': {
      customer: '(주)한성자재마트', status: '완료',
      products: [
        { product_id: 301, name: '알루미늄 플레이트 (5052)', qty: 12, price: 600000 }
      ]
    }
  },

  packingInvoicesTable: []
};

// ==========================================
// 1. 하위 컴포넌트: 대시보드 요약 (PackingSummary)
// ==========================================
function PackingSummary({ summaryData }) {
  return (
    <div className="order-summary-grid">
      <div className="summary-card-item">
        <div className="card-info-left"><span className="summary-label">당월</span><span className="summary-value">{summaryData.total}<small>건</small></span></div>
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
        <div className="card-info-left"><span className="summary-label">처리중</span><span className="summary-value text-orange">{summaryData.imminent}<small>건</small></span></div>
        <div className="card-trend-right"><span className="status-badge bg-orange-light text-orange">당월</span></div>
      </div>
      {/* <div className="summary-card-item">
        <div className="card-info-left"><span className="summary-label">기한 초과</span><span className="summary-value text-red">{summaryData.overdue}<small>건</small></span></div>
        <div className="card-trend-right"><span className="status-badge bg-red-light text-red">당월</span></div>
      </div> */}
    </div>
  );
}

// ==========================================
// 2. 하위 컴포넌트: 검색 필터 바 (PackingFilter)
// ==========================================
function PackingFilter({ filters, setFilters, onSearch, onReset }) {
  const handleChange = (field, value) => {
    setFilters(prev => ({ ...prev, [field]: value }));
  };

  return (
    <div className="filter-wrapper-card">
      <form className="search-filter-grid" onSubmit={onSearch}>
        <div className="filter-group group-date-range">
          <label>주문 기간</label>
          <div className="date-range-container">
            <input type="date" className="filter-control" value={filters.start} onChange={(e) => handleChange('start', e.target.value)} />
            <span className="date-separator">~</span>
            <input type="date" className="filter-control" value={filters.end} onChange={(e) => handleChange('end', e.target.value)} />
          </div>
        </div>
        <div className="filter-group">
          <label>주문번호 검색</label>
          <input type="text" className="filter-control" value={filters.orderNo} onChange={(e) => handleChange('orderNo', e.target.value)} />
        </div>
        <div className="filter-group">
          <label>고객사 검색</label>
          <input type="text" className="filter-control" value={filters.customer} onChange={(e) => handleChange('customer', e.target.value)} />
        </div>
        <div className="filter-group">
          <label htmlFor="search_status">진행 상태</label>
          <select id="search_status" className="filter-control" value={filters.status} onChange={(e) => handleChange('status', e.target.value)}>
            <option value="">전체 상태</option>
            <option value="approved">신규</option>
            <option value="pending">처리중</option>
            <option value="rejected">완료</option>
          </select>
        </div>
        <div className="filter-btn-group">
          <button type="button" className="btn-filter-reset" onClick={onReset}>초기화</button>
          <button type="submit" className="btn-filter-search">조회하기</button>
        </div>
      </form>
    </div>
  );
}

// ==========================================
// 3. 하위 컴포넌트: 메인 테이블 리스트 (PackingTable)
// ==========================================
function PackingTable({ orders, onRowClick }) {
  const getStatusBadgeClass = (status) => {
    switch (status) {
      case '신규': return 'text-blue bg-blue-light';
      case '처리중': return 'text-red bg-red-light';
      case '완료': return 'text-green bg-green-light';
      default: return 'text-muted bg-all-light';
    }
  };

  return (
    <div className="content-card">
      <div className="table-responsive">
        <table className="order-data-table">
          <thead>
            <tr>
              <th>주문번호</th><th>고객사</th><th>총 물량(세트/개)</th><th>주문일자</th><th>진행상태</th>
            </tr>
          </thead>
          <tbody>
            {orders.length === 0 ? (
              <tr><td colSpan="7" className="text-center" style={{ padding: '2rem', color: 'var(--text-muted)' }}>조회된 패킹 내역이 없습니다.</td></tr>
            ) : (
              orders.map((order, index) => (
                <tr key={index} onClick={() => onRowClick(order.orderNo)} style={{ cursor: 'pointer' }}>
                  <td className="text-center font-bold text-link">{order.orderNo}</td>
                  <td>{order.customer}</td>
                  <td className="text-center text-green font-bold">{order.totalQty} 개</td>
                  <td className="text-center">{order.orderDate}</td>
                  <td className="text-center"><span className={`table-badge ${getStatusBadgeClass(order.status)}`}>{order.status}</span></td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// ==========================================
// 4. 하위 컴포넌트: 모달 내부 제품 테이블 (ProductTable)
// ==========================================
function ProductTable({ products }) {
  return (
    <div className="modal-table-responsive" style={{ maxHeight: '180px', marginBottom: '1rem' }}>
      <table className="modal-product-table">
        <thead>
          <tr>
            <th style={{ width: '60%' }}>제품명</th>
            <th style={{ width: '40%', textAlign: 'right' }}>총 주문 수량</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product, idx) => (
            <tr key={idx}>
              <td className="text-left" style={{ padding: '0.75rem 0.5rem', textAlign: 'left', color: 'var(--text-dark)' }}>{product.name}</td>
              <td className="text-right text-green" style={{ padding: '0.75rem 0.5rem', fontWeight: 600 }}>{product.qty} 개</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

// ==========================================
// 5. 하위 컴포넌트: 모달 내부 송장 슬라이더 + QR 시각화 (InvoiceSlider)
// ==========================================
function InvoiceSlider({ invoicePreviews, currentSlipIdx, prevSlip, nextSlip }) {
  if (invoicePreviews.length === 0) {
    return (
      <div className="invoice-preview-card">
        <div className="empty-invoice-msg">
          배송 물류업체 설정을 마친 후 하단의<br /><strong>[송장 생성 (전체 품목)]</strong> 버튼을 클릭해 주세요.
        </div>
      </div>
    );
  }

  const currentInvoice = invoicePreviews[currentSlipIdx];
  const qrUrl = `https://aigo.com?id=${currentInvoice.id}`; 

  return (
    <div className="invoice-preview-card">
      <div className="slider-wrapper-container">
        <button type="button" className="slider-nav-btn prev" onClick={prevSlip} disabled={currentSlipIdx === 0}>&#10094;</button>

        <div className="invoice-box-inner animated-fade">
          <div className="invoice-header-title">출고 거래 송장 (PACKING SLIP)</div>
          <table className="invoice-mini-table">
            <tbody>
              {/* <tr><th>고유키 (ID)</th><td><strong style={{ color: 'var(--primary-green)' }}>{currentInvoice.id}</strong></td></tr> */}
              <tr><th>패킹 송장번호</th><td>{currentInvoice.packing_invoice_number}</td></tr>
              <tr><th>출고 번호</th><td>{currentInvoice.outbound_id}</td></tr>
              {/* <tr><th>완제품명</th><td>{currentInvoice.outbound_product_id}</td></tr> */}
              <tr><th>품목명</th><td style={{ fontSize: '0.82rem', fontWeight: 600, color: '#2d3748' }}>{currentInvoice.productName}</td></tr>
              <tr><th>고객사</th><td><span className="text-green font-bold">대한</span></td></tr>
              <tr><th>운송사 ID</th><td>{currentInvoice.partner_company_id}</td></tr>
              {/* <tr><th>운송장번호</th><td><span style={{ color: '#aaa', fontStyle: 'italic' }}>{currentInvoice.invoice_number === null ? 'null (미배정)' : currentInvoice.invoice_number}</span></td></tr> */}
              {/* <tr><th>매니페스트 FK</th><td><span style={{ color: '#aaa', fontStyle: 'italic' }}>{currentInvoice.outbound_transportation_id === null ? 'null (미배정)' : currentInvoice.outbound_transportation_id}</span></td></tr> */}
              {/* <tr><th>상태 코드</th><td><span className="table-badge text-blue bg-blue-light">{currentInvoice.state_code}</span></td></tr> */}
            </tbody>
          </table>
          
          {/* QR 코드 시각화 영역 */}
          <div className="qr-visualization-zone" style={{ textAlign: 'center', margin: '12px 0', padding: '8px', background: '#f8fafc', borderRadius: '6px' }}>
            <div style={{ background: 'white', padding: '10px', display: 'inline-block', borderRadius: '4px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
              <QRCodeSVG
                value={qrUrl}
                size={105}           
                bgColor={"#ffffff"} 
                fgColor={"#000000"}  
                level={"H"}         
                includeMargin={false}
              />
            </div>
            <div style={{ fontSize: '0.7rem', marginTop: '6px', color: '#4a5568', wordBreak: 'break-all' }}>
              QR 링크: <span style={{ color: '#007bff' }}>{qrUrl}</span>
            </div>
          </div>
          
          {/* ★ 요구사항 반영: 개별 라벨 출력 버튼 영역 제거 */}
          <div style={{ height: '5px' }}></div>
        </div>

        <button type="button" className="slider-nav-btn next" onClick={nextSlip} disabled={currentSlipIdx === invoicePreviews.length - 1}>&#10095;</button>
      </div>
    </div>
  );
}

// ==========================================
// 6. 하위 컴포넌트: 주문 상세 모달 컨테이너 (PackingDetailModal)
// ==========================================
function PackingDetailModal({ isOpen, orderNo, orderData, carrier, setCarrier, invoicePreviews, currentSlipIdx, prevSlip, nextSlip, onGenerateInvoice, onPrintAllInvoices, onClose }) {
  if (!isOpen || !orderData) return null;

  return (
    <div className={`modal-overlay ${isOpen ? 'active' : ''}`}>
      <div className="modal-container" style={{ width: '980px', maxWidth: '95%' }}>
        <div className="modal-header">
          <h3>주문 계약 상세 내역</h3>
          <button className="modal-close-btn" onClick={onClose}>&times;</button>
        </div>

        <div className="modal-body" style={{ padding: '1rem' }}>
          <div className="modal-form-grid" style={{ gridTemplateColumns: 'repeat(3, 1fr)', marginBottom: '1rem', padding: '0 0.5rem' }}>
            <div className="form-group">
              <label>주문번호(ASN)</label>
              <input type="text" className="modal-input" value={orderNo} readOnly style={{ backgroundColor: '#f8fafc' }} />
            </div>
            <div className="form-group">
              <label>고객사</label>
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

          <div className="modal-split-layout">
            <div className="modal-split-left">
              <div className="modal-section-title" style={{ marginTop: 0 }}>
                <h4>1. 주문 제품 정보 <small style={{ color: 'var(--text-muted)' }}>(계약 품목 상세 목록)</small></h4>
              </div>
              
              <ProductTable products={orderData.products} />

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
                  <button type="button" className="btn-generate-invoice" onClick={onGenerateInvoice}>송장 생성 (전체 품목 낱개 발행)</button>
                </div>
              </div>
            </div>

            <div className="modal-split-right">
              <div className="modal-section-title" style={{ marginTop: 0 }}>
                <h4>3. 제품별 출고 송장 발행 결과 {invoicePreviews.length > 0 && `(${currentSlipIdx + 1} / ${invoicePreviews.length})`}</h4>
              </div>
              <InvoiceSlider invoicePreviews={invoicePreviews} currentSlipIdx={currentSlipIdx} prevSlip={prevSlip} nextSlip={nextSlip} />
            </div>
          </div>
        </div>

        {/* 모달 푸터 영역: 요구사항 반영 버튼 배치 조정 */}
        <div className="modal-footer" style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px' }}>
          {/* ★ 요구사항 반영: 닫기(취소) 버튼 왼쪽에 전체 송장 출력 버튼 배치 */}
          {invoicePreviews.length > 0 && (
            <button 
              type="button" 
              className="btn-filter-search" 
              style={{ backgroundColor: 'var(--primary-green, #10b981)', borderColor: 'var(--primary-green, #10b981)', padding: '0.5rem 1.25rem' }} 
              onClick={onPrintAllInvoices}
            >
              전체 송장 출력 ({invoicePreviews.length}장 일괄 인쇄)
            </button>
          )}
          <button type="button" className="btn-pop-cancel" onClick={onClose}>닫기</button>
        </div>
      </div>
    </div>
  );
}

// ==========================================
// 메인 페이지 컴포넌트 (기본 내보내기)
// ==========================================
export default function PackingPage() {
  const [summaryData, setSummaryData] = useState({ total: 0, completed: 0, new: 0, imminent: 0, overdue: 0 });
  const [orders, setOrders] = useState([]);
  const [filteredOrders, setFilteredOrders] = useState([]);
  const [filters, setFilters] = useState({ start: '', end: '', orderNo: '', customer: '', status: '' });

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedOrderNo, setSelectedOrderNo] = useState('');
  const [orderData, setOrderData] = useState(null);
  
  const [carrier, setCarrier] = useState('');
  const [invoicePreviews, setInvoicePreviews] = useState([]);
  const [currentSlipIdx, setCurrentSlipIdx] = useState(0);

  useEffect(() => {
    setTimeout(() => {
      setSummaryData(MOCK_SERVER_DB.summary);
      setOrders(MOCK_SERVER_DB.orders);
      setFilteredOrders(MOCK_SERVER_DB.orders);
    }, 100);
  }, []);

  const openOrderDetailModal = (orderNo) => {
    const fetchedDetail = MOCK_SERVER_DB.orderItemDetails[orderNo];
    if (!fetchedDetail) {
      alert('상세 제품 정보를 찾을 수 없습니다.');
      return;
    }
    setSelectedOrderNo(orderNo);
    setOrderData(fetchedDetail);
    setCarrier('');
    setInvoicePreviews([]);
    setCurrentSlipIdx(0);
    setIsModalOpen(true);
  };

  const closeOrderDetailModal = () => {
    setIsModalOpen(false);
    setOrderData(null);
    setSelectedOrderNo('');
    setInvoicePreviews([]);
    setCurrentSlipIdx(0);
  };

  const handleGenerateInvoice = () => {
    if (!carrier) {
      alert('배송을 담당할 택배사/물류업체를 선택해 주세요.');
      return;
    }
    if (!orderData?.products?.length) {
      alert('출고 처리를 진행할 제품 정보가 존재하지 않습니다.');
      return;
    }

    MOCK_SERVER_DB.packingInvoicesTable = MOCK_SERVER_DB.packingInvoicesTable.filter(
      row => row.outbound_id !== selectedOrderNo
    );

    orderData.products.forEach((product) => {
      for (let i = 1; i <= product.qty; i++) {
        const uniqueId = `EA-${selectedOrderNo.split('-')[2]}-${product.product_id}-${String(i).padStart(3, '0')}`;
        const packingInvNo = `PK-INV-${Date.now().toString().slice(-4)}-${product.product_id}${i}`;

        const newSingleRow = {
          id: uniqueId,
          outbound_id: selectedOrderNo,
          packing_invoice_number: packingInvNo,
          outbound_product_id: product.product_id,
          partner_company_id: carrier,
          invoice_number: null,
          outbound_transportation_id: null,
          state_code: '패킹진행',
          updated_at: new Date().toISOString(),
          productName: product.name,
          customer: orderData.customer
        };

        MOCK_SERVER_DB.packingInvoicesTable.push(newSingleRow);
      }
    });

    const reSelectedInvoices = MOCK_SERVER_DB.packingInvoicesTable.filter(
      row => row.outbound_id === selectedOrderNo
    );

    setInvoicePreviews(reSelectedInvoices);
    setCurrentSlipIdx(0);
    
    alert(`[낱개 분할 처리 완료] 총 주문 수량 ${reSelectedInvoices.length}개에 매핑되는 개별 QR 라벨 송장 기록이 생성되었습니다.`);
  };

  // ★ 추가 기능: 전체 송장 일괄 출력 핸들러 함수
  const handlePrintAllInvoices = () => {
    const totalCount = invoicePreviews.length;
    const keyListStr = invoicePreviews.map(inv => inv.id).join('\n - ');
    
    alert(`[바코드 스풀러 인쇄 명령 수신]\n\n총 ${totalCount}개의 낱개 송장라벨 출력을 시작합니다.\n\n[출력 대상 고유키 리스트]:\n - ${keyListStr}`);
  };

  const prevSlip = () => { if (currentSlipIdx > 0) setCurrentSlipIdx(currentSlipIdx - 1); };
  const nextSlip = () => { if (currentSlipIdx < invoicePreviews.length - 1) setCurrentSlipIdx(currentSlipIdx + 1); };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    const statusMap = { 'approved': '신규', 'pending': '처리중', 'rejected': '완료' };
    const result = orders.filter(order => {
      const matchOrderNo = filters.orderNo ? order.orderNo.toLowerCase().includes(filters.orderNo.toLowerCase()) : true;
      const matchCustomer = filters.customer ? order.customer.toLowerCase().includes(filters.customer.toLowerCase()) : true;
      const matchStatus = filters.status ? order.status === statusMap[filters.status] : true;
      const orderDate = new Date(order.orderDate);
      const matchStart = filters.start ? orderDate >= new Date(filters.start) : true;
      const matchEnd = filters.end ? orderDate <= new Date(filters.end) : true;
      return matchOrderNo && matchCustomer && matchStatus && matchStart && matchEnd;
    });
    setFilteredOrders(result);
  };

  const handleResetFilter = () => {
    setFilters({ start: '', end: '', orderNo: '', customer: '', status: '' });
    setFilteredOrders(orders);
  };

  return (
    <div id="packing-page">
      <div className="page-header-flex"><h2 className="page-title">패킹</h2></div>
      <PackingSummary summaryData={summaryData} />
      <PackingFilter filters={filters} setFilters={setFilters} onSearch={handleSearchSubmit} onReset={handleResetFilter} />
      <PackingTable orders={filteredOrders} onRowClick={openOrderDetailModal} />
      <PackingDetailModal 
        isOpen={isModalOpen} orderNo={selectedOrderNo} orderData={orderData} carrier={carrier} setCarrier={setCarrier}
        invoicePreviews={invoicePreviews} currentSlipIdx={currentSlipIdx} prevSlip={prevSlip} nextSlip={nextSlip}
        onGenerateInvoice={handleGenerateInvoice} onPrintAllInvoices={handlePrintAllInvoices} onClose={closeOrderDetailModal}
      />
    </div>
  );
}