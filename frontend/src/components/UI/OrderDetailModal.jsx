/**
 * OrderDetailModal.jsx
 *
 * [변경] AsnModal 구조 적용 결과, 등록/상세 모달이 OrderModal 하나로 통합됩니다.
 *
 * AsnModal은 modalMode('register'|'detail')로 등록/상세를 단일 컴포넌트에서 처리합니다.
 * 이 패턴을 그대로 따르면 OrderModal이 등록과 상세 두 역할을 모두 담당하므로
 * OrderDetailModal은 별도 파일로 유지할 필요가 없습니다.
 *
 * 따라서 이 파일은 OrderModal로 기능이 통합되었습니다.
 * OutboundOrder.jsx에서 OrderDetailModal import 및 렌더링을 제거하고
 * OrderModal 단독으로 사용하세요.
 *
 * 상세 조회 흐름:
 *   주문번호 클릭
 *   → dispatch(getOrderDetail({ outboundId }))
 *   → orderSlice: detailData 저장 + isModal=true + modalMode='detail'
 *   → OrderModal이 isDetail=true 상태로 렌더링 (읽기 전용)
 */

// 이 파일은 빈 상태로 유지하거나 삭제해도 됩니다.
// 상세 기능은 OrderModal.jsx (modalMode='detail')에서 처리합니다.

export default null;