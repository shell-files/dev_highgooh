import React from 'react';

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
            <tr key={order.id}>
              <td 
                className="text-center font-bold text-link"
                onClick={() => onOrderClick(order)}
                style={{ cursor: 'pointer' }}
              >
                {order.id}
              </td>
              <td>{order.customer}</td>
              <td className="text-center text-green">
                {order.amount.toLocaleString()}
              </td>
              <td className="text-center">{order.orderDate}</td>
              <td className="text-center">{order.deliveryDeadline}</td>
              <td className="text-center">
                <span className={`table-badge ${getStatusBadgeClass(order.status)}`}>
                  {order.status}
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

// Helper to determine badge class
const getStatusBadgeClass = (status) => {
  switch (status) {
    case '신규': return 'badge-success';
    case '처리중': return 'badge-pending';
    case '완료': return 'badge-rejected'; // Based on HTML mapping rejected -> 완료 in CSS classes usually
    default: return '';
  }
};

export default OrderTable;
