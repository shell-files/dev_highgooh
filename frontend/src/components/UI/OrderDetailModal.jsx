import React from 'react';

const OrderDetailModal = ({ order, onClose }) => {
  if (!order) return null;

  return (
    <div className="modal-overlay active" id="orderDetailModal">
      <div className="modal-container">
        <div className="modal-header">
          <h3>주문 계약 상세 내역</h3>
          <button className="modal-close-btn" onClick={onClose}>&times;</button>
        </div>

        <div className="modal-body">
          <div className="modal-form-grid">
            <div className="form-group">
              <label>주문번호</label>
              <input 
                type="text" 
                className="modal-input" 
                value={order.id} 
                readOnly 
                style={{ backgroundColor: '#f8fafc', cursor: 'default' }} 
              />
            </div>
            <div className="form-group">
              <label>고객사</label>
              <input 
                type="text" 
                className="modal-input" 
                value={order.customer} 
                readOnly 
              />
            </div>
          </div>

          <div className="modal-section-title" style={{ marginTop: '1.5rem' }}>
            <h4>주문 제품 상세 명세</h4>
          </div>

          <div className="modal-table-responsive">
            <table className="modal-product-table">
              <thead>
                <tr>
                  <th style={{ width: '50%' }}>제품명</th>
                  <th style={{ width: '20%', textAlign: 'right' }}>수량 (세트)</th>
                  <th style={{ width: '30%', textAlign: 'right' }}>가격 (원)</th>
                </tr>
              </thead>
              <tbody>
                {order.products && order.products.map((product, index) => (
                  <tr key={index}>
                    <td style={{ padding: '0.75rem 0.5rem', color: 'var(--text-dark)' }}>
                      {product.name}
                    </td>
                    <td className="text-right" style={{ padding: '0.75rem 0.5rem', fontWeight: 600 }}>
                      {product.qty.toLocaleString()}
                    </td>
                    <td className="text-right text-green" style={{ padding: '0.75rem 0.5rem', fontWeight: 600 }}>
                      {product.price.toLocaleString()} 원
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="modal-footer">
          <button type="button" className="btn-pop-submit">수정</button>
          <button type="button" className="btn-pop-cancel" onClick={onClose}>취소</button>
        </div>
      </div>
    </div>
  );
};

export default OrderDetailModal;
