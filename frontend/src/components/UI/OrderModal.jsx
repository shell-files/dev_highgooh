import React, { useState } from 'react';

const OrderModal = ({ onClose, onSave }) => {
  const [formData, setFormData] = useState({
    orderDate: new Date().toISOString().substring(0, 10),
    customer: '',
    orderDeadline: '',
    deliveryDeadline: '',
    products: [{ id: Date.now(), name: '', qty: 0, price: 0 }]
  });

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setFormData({ ...formData, [id.replace('modal_', '')]: value });
  };

  const handleProductChange = (index, field, value) => {
    const updatedProducts = [...formData.products];
    updatedProducts[index][field] = value;
    setFormData({ ...formData, products: updatedProducts });
  };

  const addProductRow = () => {
    setFormData({
      ...formData,
      products: [...formData.products, { id: Date.now(), name: '', qty: 0, price: 0 }]
    });
  };

  const deleteProductRow = (id) => {
    if (formData.products.length > 1) {
      setFormData({
        ...formData,
        products: formData.products.filter(p => p.id !== id)
      });
    } else {
      alert('최소 1개 이상의 제품 등록 항목이 구성되어야 합니다.');
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const totalAmount = formData.products.reduce((sum, p) => sum + (Number(p.qty) * Number(p.price)), 0);
    onSave({
      ...formData,
      amount: totalAmount,
      status: '신규'
    });
    alert('신규 주문이 등록되었습니다.');
  };

  return (
    <div className="modal-overlay active" id="orderModal">
      <div className="modal-container">
        <div className="modal-header">
          <h3>신규 주문 계약 등록</h3>
          <button className="modal-close-btn" onClick={onClose}>&times;</button>
        </div>

        <div className="modal-body">
          <form id="orderForm" onSubmit={handleSubmit}>
            <div className="modal-form-grid">
              <div className="form-group">
                <label>주문일자 <span className="required">*</span></label>
                <input 
                  type="date" 
                  id="modal_orderDate" 
                  className="modal-input" 
                  value={formData.orderDate}
                  onChange={handleInputChange}
                  required 
                />
              </div>
              <div className="form-group">
                <label>고객사 <span className="required">*</span></label>
                <input 
                  type="text" 
                  id="modal_customer" 
                  className="modal-input" 
                  placeholder="고객사명 입력" 
                  value={formData.customer}
                  onChange={handleInputChange}
                  required 
                />
              </div>
              <div className="form-group">
                <label>주문마감일자 <span className="required">*</span></label>
                <input 
                  type="date" 
                  id="modal_orderDeadline" 
                  className="modal-input" 
                  value={formData.orderDeadline}
                  onChange={handleInputChange}
                  required 
                />
              </div>
              <div className="form-group">
                <label>출고마감일자 <span className="required">*</span></label>
                <input 
                  type="date" 
                  id="modal_deliveryDeadline" 
                  className="modal-input" 
                  value={formData.deliveryDeadline}
                  onChange={handleInputChange}
                  required 
                />
              </div>
            </div>

            <div className="modal-section-title">
              <h4>제품 항목 명세</h4>
              <button type="button" className="btn-secondary-sm" onClick={addProductRow}>+ 항목 추가</button>
            </div>

            <div className="modal-table-responsive">
              <table className="modal-product-table">
                <thead>
                  <tr>
                    <th style={{ width: '45%' }}>제품 선택</th>
                    <th style={{ width: '20%' }}>주문수량 (세트)</th>
                    <th style={{ width: '25%' }}>가격 (원)</th>
                    <th style={{ width: '10%' }}>삭제</th>
                  </tr>
                </thead>
                <tbody>
                  {formData.products.map((product, index) => (
                    <tr key={product.id}>
                      <td>
                        <select 
                          className="modal-select" 
                          value={product.name}
                          onChange={(e) => handleProductChange(index, 'name', e.target.value)}
                          required
                        >
                          <option value="">-- 품목 선택 --</option>
                          <option value="RM-AL-S01">Al 시트레일 압출재 (6063-T5)</option>
                          <option value="RM-AL-P02">알루미늄 플레이트 (5052)</option>
                          <option value="RM-ST-B01">조립용 고정 볼트 (M6)</option>
                        </select>
                      </td>
                      <td>
                        <input 
                          type="number" 
                          className="modal-input text-right" 
                          placeholder="0" 
                          min="1"
                          value={product.qty}
                          onChange={(e) => handleProductChange(index, 'qty', e.target.value)}
                          required 
                        />
                      </td>
                      <td>
                        <input 
                          type="number" 
                          className="modal-input text-right" 
                          placeholder="0" 
                          min="0"
                          value={product.price}
                          onChange={(e) => handleProductChange(index, 'price', e.target.value)}
                          required 
                        />
                      </td>
                      <td className="text-center">
                        <button 
                          type="button" 
                          className="btn-delete-row"
                          onClick={() => deleteProductRow(product.id)}
                        >&times;</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </form>
        </div>

        <div className="modal-footer">
          <button type="button" className="btn-pop-cancel" onClick={onClose}>취소</button>
          <button type="submit" form="orderForm" className="btn-pop-submit">주문 저장</button>
        </div>
      </div>
    </div>
  );
};

export default OrderModal;
