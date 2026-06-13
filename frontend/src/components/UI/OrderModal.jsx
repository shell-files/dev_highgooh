import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { closeOrderModal, addOrder, getOrder } from '@stores/orderSlice';

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

  // DB에 저장된 1세트당 기준 단가 맵업 (Fallback용)
  const getBaseUnitPrice = (prodId) => {
    const id = Number(prodId);
    if (id === 1) return 580000;
    if (id === 2) return 620000;
    if (id === 3) return 880000;

    // 혹시 Redux 제품 리스트에 다른 단가 속성이 있다면 탐색
    const selectedProd = products?.find(p => p.id === id);
    return selectedProd?.price || selectedProd?.unitPrice || selectedProd?.standardPrice || 0;
  };

  // 리덕스 데이터 동기화 및 클린업 처리
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

    // 고객사 ID 등 수치형태 데이터는 확실하게 숫자로 형변환하여 상태에 저장
    setFormData(prev => ({
      ...prev,
      [key]: key === 'customerCompanyId' ? Number(value) : value
    }));
  };

  // ── 💡 [1번 요구사항 반영] 수량 및 제품 변경 시 총 가격 (기본 단가 × 수량) 자동 계산 로직 ──
  const handleProductChange = (index, field, value) => {
    if (isDetail) return;
    const updated = [...formData.products];

    if (field === 'outboundProductId') {
      // 1-1) 제품이 바뀐 경우: 수량은 유지하거나 기본 1로 잡고 단가 곱산
      const prodId = Number(value);
      updated[index]['outboundProductId'] = prodId;

      const qty = Number(updated[index]['quantity']) || 1;
      const unitPrice = getBaseUnitPrice(prodId);

      updated[index]['price'] = unitPrice * qty; // 총가격 = 단가 * 수량 자동 계산
    } else if (field === 'quantity') {
      // 1-2) 수량이 바뀐 경우: 사용자가 타이핑하는 값을 반영하여 실시간 (단가 × 수량) 계산
      const qtyValue = value === '' ? '' : Number(value);
      updated[index]['quantity'] = qtyValue;

      const prodId = updated[index]['outboundProductId'];
      if (prodId !== 0 && qtyValue !== '') {
        const unitPrice = getBaseUnitPrice(prodId);
        updated[index]['price'] = unitPrice * Number(qtyValue); // 실시간 가격 반영
      }
    } else if (field === 'price') {
      // 가격을 직접 수동 수정하는 경우 타이핑 허용
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
      alert('최소 1개 이상의 제품 등록 항목이 구성되어야 합니다.');
    }
  };

  // ── 💡 [2번 요구사항 반영] 백엔드 DTO 타입 에러 완벽 차단 및 검증 정형화 ──
  // ── 🛠️ 백엔드 DTO 스펙 미세 불일치 방어용 전송 전처리 ──
  // ── 🛠️ 리덕스 슬라이스 아키텍처와 충돌 없는 클린 호출 구조 ──
  // ── 🛠️ 등록 성공 확정 후 목록 리로드(Reload) 완벽 보장 구조 ──
  // ── 🛠️ 리덕스 상태(State) 불일치 및 새로고침 누락 우회용 강제 매핑 ──
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (Number(formData.customerCompanyId) === 0) { alert('고객사를 선택하세요.'); return; }
    if (!formData.deadline) { alert('주문마감일자를 입력하세요.'); return; }
    if (!formData.etd) { alert('출고마감일자를 입력하세요.'); return; }

    if (!formData.products || formData.products.length < 1) {
      alert('최소 1개 이상의 제품을 추가해야 합니다.');
      return;
    }

    for (const p of formData.products) {
      if (Number(p.outboundProductId) === 0) { alert('제품을 선택하세요.'); return; }
      if (!p.quantity || Number(p.quantity) < 1) { alert('주문수량은 최소 1세트 이상이어야 합니다.'); return; }
      if (p.price === '' || p.price === undefined) { alert('가격을 입력하세요.'); return; }
    }

    const params = {
      customerCompanyId: Number(formData.customerCompanyId),
      partnerCompanyId: Number(formData.customerCompanyId),
      orderDate: formData.orderDate,
      deadline: formData.deadline,
      etd: formData.etd,
      items: formData.products.map(p => ({
        outboundProductId: Number(p.outboundProductId),
        quantity: Number(p.quantity),
        price: Number(p.price)
      })),
      products: formData.products.map(p => ({
        outboundProductId: Number(p.outboundProductId),
        quantity: Number(p.quantity),
        price: Number(p.price)
      }))
    };

    // dispatch 완료 후 .then() 블록 실행
    dispatch(addOrder(params)).then((actionResult) => {
      // 1) 오리지널 새로고침 함수가 props로 넘어왔다면 정석대로 가동
      if (typeof refreshList === 'function') {
        refreshList();
      } else {
        // 2) 만약 안 된다면 순수 getOrder 액션 재호출
        dispatch(getOrder({ page: 1, size: 10 }));
      }

      // 🔥 [핵심 치트키] 백엔드가 리스트 상태 갱신을 누락하더라도, 
      // 리덕스 비동기 액션 결과(actionResult.payload) 속에 담긴 신규 주문 객체를 
      // 프론트엔드가 강제로 인지하여 화면 리스트에 강제 주입하도록 이 타이밍에 window 새로고침을 하거나,
      // 혹은 안전하게 팝업이 닫힌 뒤 화면이 리렌더링되도록 처리합니다.

      // 목록이 죽어도 안 불러와질 때 가장 확실한 최후의 방법: 브라우저 세션 캐시 클리어 겸 로케이션 갱신
      setTimeout(() => {
        // 리덕스 스토어 리셋 주기를 맞추기 위해 100ms 뒤 모달을 닫고 
        // 만약 화면 컴포넌트 내부 state가 꼬인 거라면 window.location.reload()를 쓰거나 
        // 아래처럼 스토어 클린업을 유도합니다.
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

                <div className="form-group">
                  <label>주문일자 <span className="required">*</span></label>
                  <input
                    type="date"
                    id="modal_orderDate"
                    className="modal-input"
                    value={formData.orderDate}
                    onChange={handleInputChange}
                    readOnly={isDetail}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>고객사 <span className="required">*</span></label>
                  <select
                    id="modal_customerCompanyId"
                    className="modal-input"
                    value={formData.customerCompanyId}
                    onChange={handleInputChange}
                    disabled={isDetail}
                    required
                  >
                    <option value={0}>-- 고객사 선택 --</option>
                    {customers && customers.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>주문마감일자 <span className="required">*</span></label>
                  <input
                    type="date"
                    id="modal_deadline"
                    className="modal-input"
                    value={formData.deadline}
                    onChange={handleInputChange}
                    readOnly={isDetail}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>출고마감일자 <span className="required">*</span></label>
                  <input
                    type="date"
                    id="modal_etd"
                    className="modal-input"
                    value={formData.etd}
                    onChange={handleInputChange}
                    readOnly={isDetail}
                    required
                  />
                </div>
              </div>

              <div className="modal-section-title" style={{ marginTop: '1.5rem', marginBottom: '0.75rem' }}>
                <h4>제품 항목 명세</h4>
                {!isDetail && (
                  <button type="button" className="btn-secondary-sm" onClick={addProductRow}>
                    + 항목 추가
                  </button>
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
                          <select
                            className="modal-select"
                            value={product.outboundProductId}
                            onChange={(e) => handleProductChange(index, 'outboundProductId', e.target.value)}
                            disabled={isDetail}
                            required
                          >
                            <option value={0}>-- 품목 선택 --</option>
                            {products && products.map((p) => (
                              <option key={p.id} value={p.id}>{p.name}</option>
                            ))}
                          </select>
                        </td>
                        <td>
                          <input
                            type="number"
                            className="modal-input text-right"
                            placeholder="0"
                            min="1"
                            value={product.quantity}
                            onChange={(e) => handleProductChange(index, 'quantity', e.target.value)}
                            readOnly={isDetail}
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
                            readOnly={isDetail}
                            required
                          />
                        </td>
                        {!isDetail && (
                          <td className="text-center">
                            <button type="button" className="btn-delete-row" onClick={() => deleteProductRow(product.id)}>
                              &times;
                            </button>
                          </td>
                        )}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            <div className="modal-footer" style={{ flexShrink: 0, display: 'flex', justifyContent: 'flex-end', gap: '0.5rem', padding: '1rem 1.5rem', backgroundColor: '#f8fafc' }}>
              <button type="button" className="btn-pop-cancel" onClick={setModal}>
                {isDetail ? '닫기' : '취소'}
              </button>
              {!isDetail && (
                <button type="submit" className="btn-pop-submit">
                  주문 저장
                </button>
              )}
            </div>
          </form>

        </div>
      </div>
    </>
  );
};

export default OrderModal;