import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
// 내부 서브 컴포넌트들을 한 파일 안에 정의하거나 분리

const OutboundModal = ({ onClose }) => {
  const { modalMode, modalData } = useSelector((state) => state.outbound);

  // 모드에 따라 상단 타이틀 분기
  const getTitle = () => {
    switch (modalMode) {
      case 'register': return '출고 지시 등록';
      case 'detail': return '출고 상세 내역';
      case 'vehicle': return '배차 차량 배정';
      case 'invoice': return '송장 인쇄 및 마감';
      default: return '출고 관리';
    }
  };

  return (
    <div className="modal-overlay active">
      <div className="modal-container">
        <div className="modal-header">
          <h3>{getTitle()}</h3>
          <button className="modal-close-btn" onClick={onClose}>&times;</button>
        </div>

        <div className="modal-body">
          {/* 💡 modalMode에 따라 렌더링할 서브 화면 스위칭 */}
          {modalMode === 'register' && <RegisterForm />}
          {modalMode === 'detail' && <OutboundDetailView data={modalData} />}
          {modalMode === 'vehicle' && <VehicleAssignForm />}
          {modalMode === 'invoice' && <InvoicePrintView />}
        </div>
      </div>
    </div>
  );
};

export default OutboundModal;