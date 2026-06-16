import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { closeOrderModal, addOrder, getOrder } from '@stores/orderSlice';
import { showDefaultAlert } from "@components/UI/ServiceAlert";
import { getToday } from '@stores/date';

const OrderModal = () => {
  const dispatch = useDispatch();

  // Redux state
  const isModal = useSelector((state) => state.order.isModal);
  const modalMode = useSelector((state) => state.order.modalMode);
  const initialData = useSelector((state) => state.order.detailData);
  const customers = useSelector((state) => state.order.modal.customers);
  const products = useSelector((state) => state.order.modal.products);

  const currentPage = useSelector((state) => state.order.view.page);
  const size = useSelector((state) => state.order.view.size);

  // 모달 모드 판별
  const isDetail = modalMode === 'detail';
  const isAdd = modalMode === 'add' || modalMode === 'register';

  // 모달 닫기
  const setModal = () => dispatch(closeOrderModal());

  // 컴포넌트 로컬 상태 선언
  const [formData, setFormData] = useState({
    orderDate: new Date().toISOString().substring(0, 10),
    customerCompanyId: 0,
    deadline: '',
    etd: '',
    products: [{ id: Date.now(), outboundProductId: 0, quantity: 1, price: 0 }]
  });

  // ETD 입력 모달 상태
  const [isEtdModal, setIsEtdModal] = useState(false);
  const [etdInput, setEtdInput] = useState('');

  // DB에 저장된 1세트당 기준 단가 맵업 (Fallback용)
  const getBaseUnitPrice = (prodId) => {
    const id = Number(prodId);
    if (id === 1) return 580000;
    if (id === 2) return 620000;
    if (id === 3) return 880000;
    const selectedProd = products?.find(p => p.id === id);
    return selectedProd?.price || selectedProd?.unitPrice || selectedProd?.standardPrice || 0;
  };

  // 리덕스 데이터 동기화
  useEffect(() => {
    if (!isModal) return;

    if (isDetail && initialData) {
      const { order, items } = initialData;
      setFormData({
        orderDate: order.orderDate ?? '',
        customerCompanyId: Number(order.partnerCompanyId) ?? 0,
        deadline: order.deadline ?? '',
        etd: order.etd ?? '',
        products: (items ?? []).map((item, idx) => ({
          id: item.no ?? idx,
          outboundProductId: Number(item.outboundProductId) ?? 0,
          productName: item.productName ?? '',
          quantity: Number(item.quantity) ?? 0,
          price: Number(item.price) ?? 0
        }))
      });
    } else if (isAdd) {
      setFormData({
        orderDate: new Date().toISOString().substring(0, 10),
        customerCompanyId: 0,
        deadline: '',
        etd: '',
        products: [{ id: Date.now(), outboundProductId: 0, quantity: 1, price: 0 }]
      });
    }
  }, [isModal, modalMode, initialData, isDetail, isAdd]);

  // 상위 마스터 정보 핸들러
  const handleInputChange = (e) => {
    if (isDetail) return;
    const { id, value } = e.target;
    const key = id.startsWith('modal_') ? id.replace('modal_', '') : id;
    setFormData(prev => ({
      ...prev,
      [key]: key === 'customerCompanyId' ? Number(value) : value
    }));
  };

  // 수량 및 제품 변경 시 총 가격 자동 계산
  const handleProductChange = (index, field, value) => {
    if (isDetail) return;
    const updated = [...formData.products];

    if (field === 'outboundProductId') {
      const prodId = Number(value);
      updated[index]['outboundProductId'] = prodId;
      const qty = Number(updated[index]['quantity']) || 1;
      const unitPrice = getBaseUnitPrice(prodId);
      updated[index]['price'] = unitPrice * qty;
    } else if (field === 'quantity') {
      const qtyValue = value === '' ? '' : Number(value);
      updated[index]['quantity'] = qtyValue;
      const prodId = updated[index]['outboundProductId'];
      if (prodId !== 0 && qtyValue !== '') {
        const unitPrice = getBaseUnitPrice(prodId);
        updated[index]['price'] = unitPrice * Number(qtyValue);
      }
    } else if (field === 'price') {
      updated[index][field] = value === '' ? '' : Number(value);
    } else {
      updated[index][field] = value;
    }

    setFormData(prev => ({ ...prev, products: updated }));
  };

  const addProductRow = () => {
    setFormData(prev => ({
      ...prev,
      products: [...prev.products, { id: Date.now(), outboundProductId: 0, quantity: 1, price: 0 }]
    }));
  };

  const deleteProductRow = (id) => {
    if (formData.products.length > 1) {
      setFormData(prev => ({ ...prev, products: prev.products.filter(p => p.id !== id) }));
    } else {
      showDefaultAlert('오류', "최소 1개 이상의 제품 등록 항목이 구성되어야 합니다.", "error");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (Number(formData.customerCompanyId) === 0) { showDefaultAlert('필수 입력', '고객사를 선택해주세요', 'error'); return; }
    if (!formData.deadline) { showDefaultAlert('필수 입력', '주문마감일자를 입력해야 합니다.', 'error'); return; }
    if (!formData.products || formData.products.length < 1) {
      showDefaultAlert('필수 입력', '최소 1개 이상의 제품을 추가해야 합니다.', 'error');
      return;
    }
    for (const p of formData.products) {
      if (Number(p.outboundProductId) === 0) { showDefaultAlert('오류', '제품을 선택해주세요', 'error'); return; }
      if (!p.quantity || Number(p.quantity) < 1) { showDefaultAlert('오류', '수량을 입력해야 합니다.', 'error'); return; }
      if (p.price === '' || Number(p.price === 0)) { showDefaultAlert('오류', '가격을 입력하세요.', 'error'); return; }
    }

    const params = {
      customerCompanyId: Number(formData.customerCompanyId),
      deadline: formData.deadline,
      items: formData.products.map(p => ({
        outboundProductId: Number(p.outboundProductId),
        quantity: Number(p.quantity),
        price: Number(p.price)
      })),
    };

    dispatch(addOrder(params)).then(() => {
      dispatch(getOrder({ page: 1, size: 10 }));
      setTimeout(() => {
        setModal();
        window.location.reload();
      }, 100);
    });
  };

  if (!isModal) return null;

  return (
    <>
      <div className="modal-overlay active" onClick={setModal}>
        <div className="modal-container" onClick={(e) => e.stopPropagation()} style={{ display: 'flex', flexDirection: 'column', height: '580px', maxHeight: '85vh', overflow: 'hidden' }}>

          <div className="modal-header" style={{ flexShrink: 0 }}>
            <h3>{isDetail ? '주문 계약 상세 내역' : '신규 주문 계약 등록'}</h3>
            <button className="modal-close-btn" onClick={setModal}>&times;</button>
          </div>

          <form id="orderForm" onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', flex: 1, overflow: 'hidden', margin: 0 }}>
            <div className="modal-body" style={{ flex: 1, overflowY: 'auto', padding: '1.5rem' }}>
              <div className="modal-form-grid">

                {/* 고객사 */}
                <div className="form-group">
                  <label>고객사 {!isDetail && <span className="required">*</span>}</label>
                  {isDetail
                    ? <input type="text" className="modal-input" value={initialData?.order?.partnerName ?? ''} readOnly />
                    : <select id="modal_customerCompanyId" className="modal-input" value={formData.customerCompanyId} onChange={handleInputChange}>
                      <option value={0}>-- 고객사 선택 --</option>
                      {customers && customers.map((c) => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                  }
                </div>

                {/* 주문마감일자 */}
                <div className="form-group">
                  <label>주문마감일자 {!isDetail && <span className="required">*</span>}</label>
                  <input type="date" id="modal_deadline" className="modal-input" value={formData.deadline} onChange={handleInputChange} readOnly={isDetail} />
                </div>

                {/* 상세 모드일 때만 표시 */}
                {isDetail && (
                  <>
                    <div className="form-group">
                      <label>주문일자</label>
                      <input type="date" className="modal-input" value={formData.orderDate} readOnly />
                    </div>

                    <div className="form-group">
                      <label>진행상태</label>
                      <input type="text" className="modal-input" value={initialData?.order?.stateCode ?? ''} readOnly />
                    </div>

                    <div className="form-group">
                      <label>출고 예정일자</label>
                      <input type="date" className="modal-input" value={formData.etd ?? ''} readOnly />
                    </div>
                  </>
                )}
              </div>

              {/* 제품 항목 명세 */}
              <div className="modal-section-title" style={{ marginTop: '1.5rem', marginBottom: '0.75rem' }}>
                <h4>제품 항목 명세</h4>
                {!isDetail && (
                  <button type="button" className="btn-secondary-sm" onClick={addProductRow}>+ 항목 추가</button>
                )}
              </div>

              <div className="modal-table-responsive" style={{ maxHeight: '180px', overflowY: 'auto', border: '1px solid #cbd5e0' }}>
                <table className="modal-product-table">
                  <thead>
                    <tr>
                      <th style={{ width: '45%' }}>제품 선택</th>
                      <th style={{ width: '20%' }}>주문수량 (세트)</th>
                      <th style={{ width: '25%' }}>가격 (원)</th>
                      {!isDetail && <th style={{ width: '10%' }}>삭제</th>}
                    </tr>
                  </thead>
                  <tbody>
                    {formData.products.map((product, index) => (
                      <tr key={product.id ?? index}>
                        <td>
                          {isDetail
                            ? <input type="text" className="modal-input" value={product.productName ?? ''} readOnly />
                            : <select className="modal-select" value={product.outboundProductId} onChange={(e) => handleProductChange(index, 'outboundProductId', e.target.value)} required>
                              <option value={0}>-- 품목 선택 --</option>
                              {products && products.map((p) => (
                                <option key={p.productId} value={p.productId}>{p.productName}</option>
                              ))}
                            </select>
                          }
                        </td>
                        <td>
                          <input type="number" className="modal-input text-right" placeholder="0" value={product.quantity} onChange={(e) => handleProductChange(index, 'quantity', e.target.value)} readOnly={isDetail} required />
                        </td>
                        <td>
                          <input type="number" className="modal-input text-right" placeholder="0" value={product.price} onChange={(e) => handleProductChange(index, 'price', e.target.value)} readOnly={isDetail} required />
                        </td>
                        {!isDetail && (
                          <td className="text-center">
                            <button type="button" className="btn-delete-row" onClick={() => deleteProductRow(product.id)}>&times;</button>
                          </td>
                        )}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* 푸터 */}
            <div className="modal-footer" style={{ flexShrink: 0, display: 'flex', justifyContent: 'flex-end', gap: '0.5rem', padding: '1rem 1.5rem', backgroundColor: '#f8fafc' }}>
              <button type="button" className="btn-pop-cancel" onClick={setModal}>{isDetail ? '닫기' : '취소'}</button>
              {/* 신규일 때만 출고 예정일 설정 버튼 노출 */}
              {isDetail && initialData?.order?.stateCode === '신규' && (
                <button type="button" className="btn-pop-submit" onClick={() => {
                  setEtdInput(getToday());
                  setIsEtdModal(true);
                }}>
                  출고 예정일 설정
                </button>
              )}
              {!isDetail && (
                <button type="submit" className="btn-pop-submit">주문 저장</button>
              )}
            </div>
          </form>
        </div>
      </div>

      {/* ETD 입력 모달 */}
      {isEtdModal && (
        <div className="modal-overlay active" onClick={() => setIsEtdModal(false)}>
          <div className="modal-container" style={{ width: '400px', height: 'auto' }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>출고 예정일 설정</h3>
              <button className="modal-close-btn" onClick={() => setIsEtdModal(false)}>&times;</button>
            </div>
            <div className="modal-body" style={{ padding: '1.5rem' }}>
              <div className="form-group">
                <label>출고 예정일자</label>
                <input type="date" className="modal-input" value={etdInput} onChange={(e) => setEtdInput(e.target.value)} />
              </div>
            </div>
            <div className="modal-footer" style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem', padding: '1rem 1.5rem' }}>
              <button type="button" className="btn-pop-cancel" onClick={() => setIsEtdModal(false)}>취소</button>
              <button type="button" className="btn-pop-submit" onClick={() => {
                if (!etdInput) {
                  showDefaultAlert("오류", "출고 예정일을 입력해주세요.", "error");
                  return;
                }
                dispatch(processOrder({ outboundId: initialData.order.outboundId, etd: etdInput }));
                setIsEtdModal(false);
              }}>확인</button>
            </div>
          </div>
        </div>
      )}
    </>


  );
};

export default OrderModal;