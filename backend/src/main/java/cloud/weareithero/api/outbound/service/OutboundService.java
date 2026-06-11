package cloud.weareithero.api.outbound.service;

import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface OutboundService {

    /** 출고 목록 조회 (필터 + 페이지네이션) */
    public ResponseDTO findAll(OutboundRequestDTO outboundRequestDTO);

    /** 주문 상세 조회 (OUTBOUND 헤더 + ORDER_PRODUCT 목록) */
    public ResponseDTO findOne(int outboundId);

}