import React, { useState, useEffect, useRef } from 'react';
import OrderTable from './OrderTable';
import OrderModal from './OrderModal';
import OrderDetailModal from './OrderDetailModal';
import '@styles/order.css';

const OutboundOrder = () => {


  const [orders, setOrders] = useState([
    {
      id: 'PO-20260602-X01',
      customer: '(주)한성자재마트',
      amount: 21000000,
      orderDate: '2026-06-02',
      deliveryDeadline: '2026-06-25',
      status: '신규',
      products: [
        { name: 'Al 시트레일 압출재 (6063-T5)', qty: 1200, price: 15000000 },
        { name: '알루미늄 플레이트 (5052)', qty: 450, price: 6000000 }
      ]
    },
    {
      id: 'PO-20260601-M04',
      customer: '대한알루미늄공업',
      amount: 17850000,
      orderDate: '2026-06-01',
      deliveryDeadline: '2026-06-20',
      status: '처리중',
      products: [
        { name: 'Al 시트레일 압출재 (6063-T5)', qty: 1000, price: 12500000 },
        { name: '조립용 고정 볼트 (M6)', qty: 5000, price: 5350000 }
      ]
    }
  ]);

  // 모달 상태
    const [isOrderModalOpen, setIsOrderModalOpen] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);

    // 핸들러
    const OpenOrderModal = () => setIsOrderModalOpen(true);
    const CloseOrderModal = () => setIsOrderModalOpen(false);

    const OpenOrderDetail = (order) => {
        setSelectedOrder(order);
        setIsDetailModalOpen(true);
    };
    const CloseOrderDetail = () => {
        setSelectedOrder(null);
        setIsDetailModalOpen(false);
    };

    const AddOrder = (newOrder) => {
        setOrders([...orders, { ...newOrder, id: `PO-${new Date().toISOString().slice(0, 10).replace(/-/g, '')}-N${orders.length + 1}` }]);
        CloseOrderModal();
    };

    /* ── 검색 / 목록 조회 ── */
    const [page, setPage] = useState(1);
    const size = 10;
    const startDateRef = useRef(null);
    const endDateRef   = useRef(null);
    const orderIdRef       = useRef(null);
    const customerIdRef    = useRef(null);
    const stepRef        = useRef(null);

 
    const getData = () => {
    const params = { page, size };
 
    if (startDateRef.current?.value) params.startDate = startDateRef.current.value;
    if (endDateRef.current?.value)   params.endDate   = endDateRef.current.value;
    if (orderIdRef.current?.value)   params.orderId   = orderIdRef.current.value;
    if (customerIdRef.current?.value)   params.customerId   = customerIdRef.current.value;
    if (stepRef.current?.value)   params.step   = stepRef.current.value;
 
    if (params.startDate && !params.endDate) {
      alert("주문 기간이 필요합니다.");
      return;
    }}

    const searchEvent = (e) => {
    e.preventDefault();
    getData();
    };
  


  return (
    <div className="main-wrapper">
      <div className="content-area">
        <div className="page-header-flex">
          <h2 className="page-title">주문 계약 및 접수 현황 관리</h2>
          <div className="header-action-group">
            <button className="btn-main-action" onClick={OpenOrderModal}>+ 신규 주문 등록</button>
          </div>
        </div>

        {/* 합계 영역 */}
        <div className="order-summary-grid">
          <div className="summary-card-item">
            <div className="card-info-left">
              <span className="summary-label">전체 주문 접수</span>
              <span className="summary-value">42<small>건</small></span>
            </div>
            <div className="card-trend-right">
              <span className="status-badge bg-all-light text-muted">당월</span>
            </div>
          </div>
          <div className="summary-card-item">
            <div className="card-info-left">
              <span className="summary-label">신규</span>
              <span className="summary-value text-green">34<small>건</small></span>
            </div>
            <div className="card-trend-right">
              <span className="status-badge bg-green-light text-green">당월</span>
            </div>
          </div>
          <div className="summary-card-item">
            <div className="card-info-left">
              <span className="summary-label">처리중</span>
              <span className="summary-value text-orange">5<small>건</small></span>
            </div>
            <div className="card-trend-right">
              <span className="status-badge bg-orange-light text-orange">당월</span>
            </div>
          </div>
          <div className="summary-card-item">
            <div className="card-info-left">
              <span className="summary-label">출고완료</span>
              <span className="summary-value text-blue">3<small>건</small></span>
            </div>
            <div className="card-trend-right">
              <span className="status-badge bg-blue-light text-blue">당월</span>
            </div>
          </div>
        </div>

        {/* 검색 영역 */}
        <div className="filter-wrapper-card">
          <form className="search-filter-grid" onSubmit = {searchEvent}>
            <div className="filter-group group-date-range">
              <label>주문 기간</label>
              <div className="date-range-container">
                <input type="date" className="filter-control" ref={startDateRef}/>
                <span className="date-separator">~</span>
                <input type="date" className="filter-control" ref={endDateRef}/>
              </div>
            </div>
            <div className="filter-group">
              <label>주문번호 검색</label>
              <input type="text" className="filter-control" ref={orderIdRef}/>
            </div>
            <div className="filter-group">
              <label>고객사 검색</label>
              <input type="text" className="filter-control" ref={customerIdRef}/>
            </div>
            <div className="filter-group">
              <label>진행 상태</label>
              <select className="filter-control" ref={stepRef}>
                <option value="">전체</option>
                <option value="approved">신규</option>
                <option value="pending">처리중</option>
                <option value="rejected">완료</option>
              </select>
            </div>
            <div className="filter-btn-group">
              <button type="reset" className="btn-filter-reset">초기화</button>
              <button type="submit" className="btn-filter-search">조회하기</button>
            </div>
          </form>
        </div>

        {/* 테이블영역 */}
        <div className="content-card">
          <OrderTable orders={orders} onOrderClick={OpenOrderDetail} />
          
          <div className="pagination-container">
            <div className="pagination-info">
              전체 <span>{orders.length}</span>건
            </div>

        {/* 페이지네이션 */}
            <div className="pagination-buttons">
              <button type="button" className="btn-page" >&lt;&lt;</button>
              <button type="button" className="btn-page" >&lt;</button>
              <button type="button" className="btn-page-num active">1</button>
              <button type="button" className="btn-page" >&gt;</button>
              <button type="button" className="btn-page" >&gt;&gt;</button>
            </div>
          </div>
        </div>
      </div>

      {/* 모달 */}
      {isOrderModalOpen && (
        <OrderModal 
          onClose={CloseOrderModal} 
          onSave={AddOrder} 
        />
      )}
      
      {isDetailModalOpen && selectedOrder && (
        <OrderDetailModal 
          order={selectedOrder} 
          onClose={CloseOrderDetail} 
        />
      )}
    </div>
  );
};

export default OutboundOrder;
