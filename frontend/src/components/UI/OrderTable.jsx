import React from 'react';

/**
 * OrderTable
 *   order.id              → order.outboundId
 *   order.customer        → order.partnerName
 *   order.amount          → order.totalPrice
 *   order.deliveryDeadline → order.deadline
 *   order.status          → order.stateCode
 */
const OrderTable = ({ orders, onOrderClick }) => {
  return (
    <div className="table-responsive">
      <table className="order-data-table">
        <thead>
          <tr>
            <th>주문번호</th>
            <th>고객사(고객사)</th>
            <th>총 공급가액</th>
            <th>주문일자</th>
            <th>출고마감</th>
            <th>진행상태</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((order) => (
            <tr key={order.outboundId}>
              <td className="text-center font-bold text-link" onClick={() => onOrderClick(order)} style={{ cursor: 'pointer' }}>{order.outboundId}</td>
              <td>{order.partnerName}</td>
              <td className="text-center text-green">{order.totalPrice != null ? Number(order.totalPrice).toLocaleString() : '-'}</td>
              <td className="text-center">{order.orderDate}</td>
              <td className="text-center">{order.deadline}</td>
              <td className="text-center">
                <span className={`table-badge ${getStatusBadgeClass(order.stateCode)}`}>{order.stateCode}</span>
              </td>
            </tr>
          ))}
          {orders.length === 0 && (
            <tr>
              <td colSpan="6" className="text-center" style={{ padding: '2rem' }}>데이터가 없습니다.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

// [유지] 함수명 및 분기 로직 유지
// [변경] 파라미터: status → stateCode (호출부와 일치)
const getStatusBadgeClass = (stateCode) => {
  switch (stateCode) {
    case '신규': return 'badge-success';
    case '처리중': return 'badge-danger';
    case '주문완료': return 'badge-pending';
    default: return '';
  }
};

export default OrderTable;