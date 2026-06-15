import { useState, useEffect } from 'react';
import { GET, POST, PUT } from "@utils/Network";
import '@styles/asn.css';
import { useDispatch, useSelector } from "react-redux";
import { closeAsnModal, addAsnModal } from '@stores/asnSlice';
import { showDefaultAlert } from "@components/UI/ServiceAlert";

/**
 * AsnModal
 * @param {boolean}  isModal   - 모달 표시 여부
 * @param {Function} setModal  - 모달 상태 setter
 * @param {Function} getData   - 목록 갱신 콜백 (등록 후 호출)
 * @param {'register'|'detail'} mode - 'register': 신규 등록, 'detail': 상세 조회
 * @param {object|null} initialData  - mode='detail'일 때 서버에서 받아온 ASN 데이터
 */
const AsnModal = () => {
  const dispatch = useDispatch();

  const isModal = useSelector((state) => state.asn.isModal);
  const modalMode = useSelector((state) => state.asn.modalMode);
  const initialData = useSelector((state) => state.asn.detailData);

  const setModal = () => dispatch(closeAsnModal());

  const isDetail = modalMode === 'detail';

  const partnerCompany = useSelector((state) => state.asn.modal.partnerCompany);
  const warehouses = useSelector((state) => state.asn.modal.warehouses);
  const materials = useSelector((state) => state.asn.modal.materials);

  const [asn, setAsn] = useState({
    partnerCompany: 0,
    eta: '',
    warehouse: 0,
    vehicleNumber: ''
  });
  const [asnMaterials, setAsnMaterials] = useState([]);

  /* ── 상세 모드: initialData 로 폼 채우기 ── */
  useEffect(() => {
    if (isDetail && initialData) {
      const { asn, items } = initialData; // { asn: {...}, items: [...] }
      setAsn({
        partnerCompany: asn.partnerId ?? 0,
        eta: asn.eta ?? '',
        warehouse: asn.warehouseId ?? 0,
        vehicleNumber: asn.vehicleNumber ?? ''
      });
      setAsnMaterials(
        (items ?? []).map(item => ({
          itemNo: item.itemNo ?? 0,
          itemName: item.itemName ?? '',
          weight: item.weight ?? 0,
          diameter: item.diameter ?? 0
        }))
      );
    }
  }, [isDetail, initialData]);

  /* ── 이벤트 핸들러 ── */
  const onChangeEvent = (e) => {
    if (isDetail) return; // 상세 모드는 읽기 전용
    const { name, value } = e.target;
    setAsn(prev => ({ ...prev, [name]: value }));
  };

  const onChangeTableEvent = (e, index) => {
    if (isDetail) return;
    const { name, value } = e.target;
    setAsnMaterials(prev =>
      prev.map((item, i) => i === index ? { ...item, [name]: Number(value) } : item)
    );
  };

  const onDeleteRow = (index) => {
    if (isDetail) return;
    setAsnMaterials(prev => prev.filter((_, i) => i !== index));
  };

  const addAsnMaterial = () => {
    setAsnMaterials(prev => [...prev, { itemNo: 0, weight: 0, diameter: 0 }]);
  };

  /* ── 등록 제출 ── */
  const addAsn = () => {
    if (asn.partnerCompany === 0) { showDefaultAlert("오류", "공급사명을 선택하세요.", "error"); return; }
    if (asn.eta === '') { showDefaultAlert("오류", "입고 예정 날짜을 선택해주세요.", "error"); return; }
    if (asn.warehouse === 0) { showDefaultAlert("오류", "입고 창고을 선택해주세요.", "error"); return; }

    for (const material of asnMaterials) {
      if (material.itemNo === 0) { showdefaultAlert("오류", "품목을 선택해주세요.", "error"); return; }
      if (material.weight === 0) { showDefaultAlert("오류", "품목 무게을 입력해주세요.", "error"); return; }
      if (material.diameter === 0) { showDefaultAlert("오류", "지름을 입력해주세요.", "error"); return; }
    }

    if (asnMaterials.length === 0) { showDefaultAlert("오류", "입고 품목을 추가해주세요.", "error"); return; }

    const params = {
      partnerCompanyId: asn.partnerCompany,
      warehouseId: asn.warehouse,
      eta: asn.eta,
      items: asnMaterials
    };

    dispatch(addAsnModal(params));
  };

  /* ── 렌더링 ── */
  return (
    <>
      <div className={isModal ? 'modal-overlay active' : 'modal-overlay'} onClick={setModal} />
      <div className={isModal ? 'modal-overlay2 active' : 'modal-overlay'}>
        <div className="modal-window">
          {/* 헤더 */}
          <div className="modal-header">
            <h3>{isDetail ? '사전입고 통지(ASN) 상세' : '사전입고 통지(ASN) 등록'}</h3>
            <button className="modal-close-btn" onClick={setModal}>&times;</button>
          </div>

          {/* 바디 */}
          <div className="modal-body">
            <div className="popup-section">
              <h4 className="sub-title">기본 정보</h4>
              <div className="form-grid-4">
                <div className="input-box">
                  <label>공급사명</label>
                  {isDetail
                    ? <input type="text" className="filter-date-input" value={initialData?.asn?.partnerName ?? ''} readOnly />
                    : <select name="partnerCompany" value={asn.partnerCompany} onChange={onChangeEvent}>
                      <option value={0}>선택</option>
                      {partnerCompany?.map((v, i) => <option key={i} value={v.id}>{v.name}</option>)}
                    </select>
                  }
                </div>
                <div className="input-box">
                  <label>입고 예정일</label>
                  <input type="date" className="filter-date-input" name="eta" value={asn.eta} onChange={onChangeEvent} readOnly={isDetail} />
                </div>
              </div>
              <div className="input-box">
                <label>입고 창고</label>
                {isDetail
                  ? <input type="text" className="filter-date-input" value={initialData?.asn?.warehouseName ?? ''} readOnly />
                  : <select name="warehouse" value={asn.warehouse} onChange={onChangeEvent}>
                    <option value={0}>선택하세요</option>
                    {warehouses?.map((v, i) => <option key={i} value={v.id}>{v.name}</option>)}
                  </select>
                }
              </div>
            </div>

            <div className="popup-section" style={{ marginTop: '1.5rem' }}>
              <div className="section-header-flex">
                <h4 className="sub-title">품목 정보</h4>
                {!isDetail && (
                  <button type="button" className="btn-secondary-sm" onClick={addAsnMaterial}>+ 품목 추가</button>
                )}
              </div>
              <div className="table-responsive" style={{ maxHeight: '250px', overflowY: 'auto' }}>
                <table className="popup-grid-table">
                  <thead>
                    <tr>
                      <th>순번</th>
                      <th>품목</th>
                      <th>무게(kg)</th>
                      <th>지름(inch)</th>
                      {!isDetail && <th>삭제</th>}
                    </tr>
                  </thead>
                  <tbody>
                    {asnMaterials?.map((v, i) => (
                      <tr key={i}>
                        <td className="text-center">{i + 1}</td>
                        <td>
                          {isDetail
                            ? <input type="text" className="table-inner-input" value={v.itemName ?? ''} readOnly />
                            : <select className="table-inner-input highlight-field" name="itemNo" value={v.itemNo} onChange={e => onChangeTableEvent(e, i)} disabled={isDetail}>
                              <option value={0}>선택하세요</option>
                              {materials?.map((vv, ii) => <option key={ii} value={vv.id}>{vv.alloyType}</option>)}
                            </select>
                          }
                        </td>
                        <td>
                          <input type="number" className="table-inner-input text-right highlight-field" name="weight" value={v.weight} onChange={e => onChangeTableEvent(e, i)} readOnly={isDetail} />
                        </td>
                        <td>
                          <input type="number" className="table-inner-input highlight-field" name="diameter"
                            value={v.diameter} onChange={e => onChangeTableEvent(e, i)} readOnly={isDetail} />
                        </td>
                        {!isDetail && (
                          <td className="text-center">
                            <button type="button" className="btn-delete-row" onClick={() => onDeleteRow(i)}>&times;</button>
                          </td>
                        )}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          {/* 푸터 */}
          <div className="modal-footer">
            <button className="btn-pop-cancel" onClick={setModal}>
              {isDetail ? '닫기' : '취소'}
            </button>
            {!isDetail && (
              <button className="btn-pop-submit" onClick={addAsn}>ASN 등록</button>)}
          </div>
        </div>
      </div>
    </>
  );
};

export default AsnModal;