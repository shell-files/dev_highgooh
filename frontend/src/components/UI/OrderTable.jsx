import React from 'react';

/**
 * OrderTable
 * [변경] API 응답 필드명에 맞게 전체 수정:
 *   order.id              → order.outboundId
 *   order.customer        → order.partnerName
 *   order.amount          → order.totalPrice
 *   order.deliveryDeadline → order.deadline
 *   order.status          → order.stateCode
 *
 * [유지] UI 레이아웃, CSS 클래스, 컬럼 구조 완전 유지
 * [유지] onOrderClick props 방식 유지 (OutboundOrder에서 OpenOrderDetail 전달)
 * [유지] getStatusBadgeClass 함수명 및 분기 로직 유지
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
            // [변경] key: order.id → order.outboundId
            <tr key={order.outboundId}>
              <td
                className="text-center font-bold text-link"
                // [변경] onOrderClick에 order 전체 전달 (outboundId 포함)
                onClick={() => onOrderClick(order)}
                style={{ cursor: 'pointer' }}
              >
                {/* [변경] order.id → order.outboundId */}
                {order.outboundId}
              </td>
              {/* [변경] order.customer → order.partnerName */}
              <td>{order.partnerName}</td>
              <td className="text-center text-green">
                {/* [변경] order.amount → order.totalPrice */}
                {order.totalPrice != null ? Number(order.totalPrice).toLocaleString() : '-'}
              </td>
              {/* order.orderDate 유지 (필드명 일치) */}
              <td className="text-center">{order.orderDate}</td>
              {/* [변경] order.deliveryDeadline → order.deadline */}
              <td className="text-center">{order.deadline}</td>
              <td className="text-center">
                {/* [변경] order.status → order.stateCode */}
                <span className={`table-badge ${getStatusBadgeClass(order.stateCode)}`}>
                  {order.stateCode}
                </span>
              </td>
            </tr>
          ))}
          {orders.length === 0 && (
            <tr>
              <td colSpan="6" className="text-center" style={{ padding: '2rem' }}>
                데이터가 없습니다.
              </td>
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
    case '신규':   return 'badge-success';
    case '처리중': return 'badge-pending';
    case '완료':   return 'badge-rejected';
    default:       return '';
  }
};

export default OrderTable;