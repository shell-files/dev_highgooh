package cloud.weareithero.api.outbound.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cloud.weareithero.api.outbound.dao.OutboundDao;
import cloud.weareithero.api.outbound.dto.OutboundCarrierDTO;
import cloud.weareithero.api.outbound.dto.OutboundConfirmDTO;
import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundInvoiceDTO;
import cloud.weareithero.api.outbound.dto.OutboundManifestDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundSummaryDTO;
import cloud.weareithero.api.outbound.dto.OutboundTransportationVehicleDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;
import cloud.weareithero.dto.PaginationDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboundServiceImp implements OutboundService {

    private final OutboundDao outboundDao;

    /**
     * 박스 뷰 목록 조회
     * - summary(상단 카드 5종) + list(박스 테이블) + pagination
     */
    @Override
    public ResponseDTO findAll(OutboundRequestDTO outboundRequestDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            OutboundSummaryDTO summary = outboundDao.findSummary(outboundRequestDTO);
            List<OutboundDTO> outboundList = outboundDao.findAll(outboundRequestDTO);
            PaginationDTO pagination = PaginationDTO.builder()
                .page(outboundRequestDTO.getPage())
                .totalCount(summary.getTotal())
                .totalPages((int) Math.ceil((double) summary.getTotal() / outboundRequestDTO.getSize()))
                .build();
            request.put("summary", summary);
            request.put("list", outboundList);
            request.put("pagination", pagination);
            isSuccess = true;
            message = "출고 목록 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("OutboundServiceImp findAll error : {}", e.getMessage());
            message = "출고 목록 조회에 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(request)
            .message(message)
            .build();
    }

    /**
     * 주문 상세 내역 조회
     * - 헤더(주문번호·고객사·상태) + 제품 목록
     * ※ outboundId는 .id 기준으로 조회
     */
    @Override
    public ResponseDTO findOne(int outboundId) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            OutboundDTO outbound = outboundDao.findByOutboundId(outboundId);
            List<OutboundProductDTO> products = outboundDao.findProducts(outboundId);
            request.put("outbound", outbound);
            request.put("products", products);
            isSuccess = true;
            message = "출고 상세 정보 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("OutboundServiceImp findOne error : {}", e.getMessage());
            message = "출고 상세 정보 조회에 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(request)
            .message(message)
            .build();
    }

    /**
     * 매니페스트 뷰 목록 조회
     * - OUTBOUND_TRANSPORTATION 기준으로 운송 단위 집계
     */
    @Override
    public ResponseDTO findAllManifest(OutboundRequestDTO outboundRequestDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            List<OutboundManifestDTO> manifestList = outboundDao.findAllManifest(outboundRequestDTO);
            PaginationDTO pagination = PaginationDTO.builder()
                .page(outboundRequestDTO.getPage())
                .totalCount(manifestList.size())
                .totalPages((int) Math.ceil((double) manifestList.size() / outboundRequestDTO.getSize()))
                .build();
            request.put("list", manifestList);
            request.put("pagination", pagination);
            isSuccess = true;
            message = "매니페스트 목록 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("OutboundServiceImp findAllManifest error : {}", e.getMessage());
            message = "매니페스트 목록 조회에 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(request)
            .message(message)
            .build();
    }

    /**
     * 폼 데이터 조회 (차량 배정 모달 드롭다운용)
     * - 운송사(carriers) + 차량 목록(vehicles)
     */
    @Override
    public ResponseDTO findAllOutbound() {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            List<OutboundCarrierDTO> carriers = outboundDao.findByCarrier();
            List<OutboundTransportationVehicleDTO> vehicles = outboundDao.findByVehicle();
            request.put("carriers", carriers);
            request.put("vehicles", vehicles);
            isSuccess = true;
            message = "출고 폼 데이터 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("OutboundServiceImp findAllOutbound error : {}", e.getMessage());
            message = "출고 폼 데이터 조회에 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(request)
            .message(message)
            .build();
    }

    /**
     * 차량 배정
     * 처리 순서:
     * 1. OUTBOUND_TRANSPORTATION INSERT → PK(transportationId) 획득
     * 2. 선택된 outboundIds[] 각각에 transportationId UPDATE
     * 3. 부분 실패 감지 (Inbound addOrderMaterial 패턴 동일 적용)
     *
     * ※ 상태 가드: 대상 OUTBOUND의 state_code가 "미배정" 상태인지 확인 필요
     *   (현재 state_code 값 체계는 COMMON_CODE 테이블 확인 필요 — ERD 확인 항목)
     */
    @Override
    public ResponseDTO assignVehicle(OutboundVehicleAssignDTO outboundVehicleAssignDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            // 1. OUTBOUND_TRANSPORTATION 헤더 INSERT
            int transportationId = outboundDao.addTransportation(outboundVehicleAssignDTO);

            if (transportationId > 0) {
                List<Integer> outboundIds = outboundVehicleAssignDTO.getOutboundIds();
                int updatedCount = 0;

                // 2. 선택된 박스 각각에 transportationId 연결 UPDATE
                for (Integer outboundId : outboundIds) {
                    updatedCount += outboundDao.updateTransportationId(outboundId, transportationId);
                }

                // 3. 부분 실패 감지 (Inbound 패턴 동일)
                if (updatedCount == outboundIds.size()) {
                    isSuccess = true;
                    message = "차량 배정이 완료되었습니다.";
                } else {
                    message = "차량 배정이 일부 실패했습니다.";
                }
            } else {
                message = "차량 배정 등록에 실패했습니다.";
            }
        } catch (Exception e) {
            log.info("OutboundServiceImp assignVehicle error : {}", e.getMessage());
            message = "차량 배정에 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(request)
            .message(message)
            .build();
    }

    /**
     * 송장 발급
     * 처리 순서:
     * 1. 상태 가드: 해당 OUTBOUND의 state_code가 "차량배정" 완료 상태인지 검증
     * 2. 송장번호 채번 (현재 MAX 방식 사용 — 동시성 처리는 추가 검토 필요)
     * 3. OUTBOUND UPDATE (invoice_no 저장, state_code 변경)
     *
     * ※ invoice_no 컬럼 존재 여부 ERD 확인 필요
     * ※ "차량배정 완료" state_code 값 COMMON_CODE 확인 필요
     */
    @Override
    public ResponseDTO issueInvoice(OutboundInvoiceDTO outboundInvoiceDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            // 1. 상태 가드: 차량 배정 완료 여부 확인
            OutboundDTO current = outboundDao.findByOutboundId(outboundInvoiceDTO.getOutboundId());

            // TODO: COMMON_CODE에서 "차량배정완료" state_code 값 확인 후 아래 상수 교체 필요
            // 현재 Inbound 기준 state_code 4 = 예정, 5 = 완료 → Outbound 값은 별도 확인
            final int STATE_VEHICLE_ASSIGNED = 0; // ← ERD/COMMON_CODE 확인 필요

            if (current == null) {
                message = "해당 출고 정보를 찾을 수 없습니다.";
            } else if (current.getStateCode() != STATE_VEHICLE_ASSIGNED) {
                message = "차량 배정 완료 후 송장 발급이 가능합니다.";
            } else {
                // 2. 송장번호 채번 후 UPDATE
                int result = outboundDao.updateInvoice(outboundInvoiceDTO);
                if (result > 0) {
                    isSuccess = true;
                    message = "송장 발급이 완료되었습니다.";
                } else {
                    message = "송장 발급에 실패했습니다.";
                }
            }
        } catch (Exception e) {
            log.info("OutboundServiceImp issueInvoice error : {}", e.getMessage());
            message = "송장 발급에 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(request)
            .message(message)
            .build();
    }

    /**
     * 출고 확정
     * 처리 순서:
     * 1. 상태 가드: 해당 OUTBOUND의 state_code가 "송장발급" 완료 상태인지 검증
     * 2. 선택된 outboundIds[] 각각 state_code → "출고확정" UPDATE
     * 3. 부분 실패 감지 (Inbound 패턴 동일 적용)
     *
     * ※ "송장발급 완료" state_code 값 COMMON_CODE 확인 필요
     * ※ "출고확정" state_code 값 COMMON_CODE 확인 필요
     */
    @Override
    public ResponseDTO confirmShipment(OutboundConfirmDTO outboundConfirmDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            List<Integer> outboundIds = outboundConfirmDTO.getOutboundIds();

            // TODO: COMMON_CODE에서 "송장발급완료" state_code 값 확인 후 교체 필요
            final int STATE_INVOICE_ISSUED = 0;  // ← ERD/COMMON_CODE 확인 필요
            final int STATE_CONFIRMED      = 0;  // ← ERD/COMMON_CODE 확인 필요

            int updatedCount = 0;
            for (Integer outboundId : outboundIds) {
                // 상태 가드: 개별 건 단위로 송장발급 완료 여부 확인 후 UPDATE
                OutboundDTO current = outboundDao.findByOutboundId(outboundId);
                if (current != null && current.getStateCode() == STATE_INVOICE_ISSUED) {
                    updatedCount += outboundDao.updateStateCode(outboundId, STATE_CONFIRMED);
                }
            }

            if (updatedCount == outboundIds.size()) {
                isSuccess = true;
                message = "출고 확정이 완료되었습니다.";
            } else if (updatedCount > 0) {
                message = "출고 확정이 일부 실패했습니다.";
            } else {
                message = "출고 확정에 실패했습니다. 송장 발급 완료 여부를 확인해주세요.";
            }
        } catch (Exception e) {
            log.info("OutboundServiceImp confirmShipment error : {}", e.getMessage());
            message = "출고 확정에 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(request)
            .message(message)
            .build();
    }

}