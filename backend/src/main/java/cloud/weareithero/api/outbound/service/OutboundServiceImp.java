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
import cloud.weareithero.api.outbound.dto.OutboundPackingDTO;
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
     * - 기준 테이블: OUTBOUND_PACKING (박스 = 패킹 단위)
     * - summary: OUTBOUND 기준 집계 5종
     * - list: OUTBOUND_PACKING 목록
     * - pagination: OUTBOUND 전체 건수 기준
     */
    @Override
    public ResponseDTO findAll(OutboundRequestDTO outboundRequestDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            OutboundSummaryDTO summary = outboundDao.findSummary(outboundRequestDTO);
            List<OutboundPackingDTO> packingList = outboundDao.findAll(outboundRequestDTO);
            PaginationDTO pagination = PaginationDTO.builder()
                    .page(outboundRequestDTO.getPage())
                    .totalCount(summary.getTotal())
                    .totalPages((int) Math.ceil((double) summary.getTotal() / outboundRequestDTO.getSize()))
                    .build();
            request.put("summary", summary);
            request.put("list", packingList);
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
     * 주문 상세 조회
     * - OUTBOUND 헤더 (고객사, 주문일, 마감일, 상태)
     * - ORDER_PRODUCT 목록 (제품명, 수량, 금액)
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
            message = "출고 상세 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("OutboundServiceImp findOne error : {}", e.getMessage());
            message = "출고 상세 조회에 실패했습니다.";
        }
        return ResponseDTO.builder()
                .status(isSuccess)
                .data(request)
                .message(message)
                .build();
    }

    /**
     * 매니페스트 뷰 목록 조회
     * - 기준 테이블: OUTBOUND_TRANSPORTATION
     * - 박스 수: OUTBOUND_PACKING COUNT 집계
     * ※ totalCount는 별도 COUNT 없이 list.size() 사용 (규모 고려 시 별도 쿼리 추가 권장)
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
     * 폼 데이터 조회
     * - 운송사: PARTNER_COMPANY_MASTER WHERE carrier_yn_code = 1
     * - 차량: TRANSPORTATION_VEHICLE_MASTER 전체
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
     *
     * 처리 흐름:
     * 1) OUTBOUND_TRANSPORTATION INSERT
     * - useGeneratedKeys 로 transportationId 획득
     * 2) 선택된 packingIds[] 각각 OUTBOUND_PACKING UPDATE
     * - outbound_transportation_id = transportationId
     * - state_code = 차량배정 상태코드
     * ※ 차량배정 state_code 값 COMMON_CODE 확인 필요 (주석 참고)
     * 3) Inbound 패턴 동일 - 부분 실패 감지
     *
     * ※ 박스 탭에서 체크박스로 선택한 packing_id 목록을 수신
     * ※ 선택된 박스들은 동일 운송사 소속이어야 함 (프론트 selectedCarrier 가드로 제어 중)
     */
    @Override
    public ResponseDTO assignVehicle(OutboundVehicleAssignDTO outboundVehicleAssignDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            // 1. OUTBOUND_TRANSPORTATION INSERT (lpn, driver, carrier, vehicle, etd)
            outboundDao.addTransportation(outboundVehicleAssignDTO);
            int transportationId = outboundVehicleAssignDTO.getTransportationId();

            if (transportationId > 0) {
                List<Integer> packingIds = outboundVehicleAssignDTO.getPackingIds();
                int updatedCount = 0;

                // 🟢 [수정 1] COMMON_CODE 테이블에 정의된 '차량배정완료' 상태 코드 번호를 변수에 담습니다.
                // (만약 디비에 등록된 실제 출고대기/차량배정완료 id 코드가 21번이 아니라 다른 숫자라면 그 숫자를 적어주세요!)
                int shippingReadyCode = outboundVehicleAssignDTO.getStateCode();

                // 2. 선택된 OUTBOUND_PACKING 각각 UPDATE
                for (Integer packingId : packingIds) {
                    updatedCount += outboundDao.updatePackingTransportation(packingId, transportationId,
                            shippingReadyCode);
                }

                // 3. 부분 실패 감지 (Inbound 패턴 동일)
                if (updatedCount == packingIds.size()) {
                    isSuccess = true;
                    message = "차량 배정이 완료되었습니다.";
                } else {
                    message = "차량 배정이 일부 실패했습니다.";
                }
            } else {
                message = "운송 차량 등록에 실패했습니다.";
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
     *
     * 처리 흐름:
     * 1) OUTBOUND_PACKING 상태 가드: 차량배정 완료 상태인지 확인
     * ※ 차량배정 완료 state_code 값 COMMON_CODE 확인 필요
     * 2) invoice_number 채번 → DTO에 주입
     * - 형식: INV-YYYYMMDD-NNN (ServiceImp 레이어에서 채번 권장)
     * ※ 동시성 처리 방식 팀 결정 필요 (현재: DB의 MAX+1 방식 사용 금지 권장)
     * 3) OUTBOUND_PACKING UPDATE (invoice_number, state_code 변경)
     *
     * ※ packingIds 배열로 복수 박스 일괄 발급 지원
     * JSX 송장 모달에서 activeInvoiceBoxes(박스번호 목록) 기준으로 일괄 발급 가능
     */
    @Override
    public ResponseDTO issueInvoice(OutboundInvoiceDTO outboundInvoiceDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            List<Integer> packingIds = outboundInvoiceDTO.getPackingIds();
            int updatedCount = 0;

            for (Integer packingId : packingIds) {
                // 상태 가드: 개별 OUTBOUND_PACKING의 state_code 확인
                OutboundPackingDTO current = outboundDao.findByPackingId(packingId);

                // 🟢 [수정] COMMON_CODE 테이블의 실제 id 값으로 교체합니다.
                final int STATE_VEHICLE_ASSIGNED = 21; // 차량배정 (21)
                final int STATE_OUTBOUND_WAITING = 22; // 출고대기 (22)

                if (current == null) {
                    log.info("OutboundServiceImp issueInvoice - packingId {} not found", packingId);
                    continue;
                }

                // 🟢 [수정] 현재 패킹 상태가 '차량배정(21)'도 아니고 '출고대기(22)'도 아닐 때만 튕겨내도록 조건을 변경합니다.
                if (current.getStateCode() != STATE_VEHICLE_ASSIGNED
                        && current.getStateCode() != STATE_OUTBOUND_WAITING) {
                    log.info("OutboundServiceImp issueInvoice - packingId {} state not valid: {}", packingId,
                            current.getStateCode());
                    continue;
                }

                // invoice_number를 DTO에 주입 (채번 로직 팀 결정 후 교체)
                // 현재: packingId 기반 임시 채번 - 실제 운영 전 동시성 안전한 방식으로 변경 필요
                // TODO: 채번 방식 결정 필요 (예: DB 시퀀스 테이블, UUID, 날짜+AUTO_INCREMENT 등)
                outboundInvoiceDTO.setPackingId(packingId);
                outboundInvoiceDTO.setStateCode(current.getStateCode());
                updatedCount += outboundDao.updateInvoiceNumber(outboundInvoiceDTO);
            }

            if (updatedCount == packingIds.size()) {
                isSuccess = true;
                message = "송장 발급이 완료되었습니다.";
            } else if (updatedCount > 0) {
                message = "송장 발급이 일부 완료되었습니다.";
            } else {
                message = "송장 발급에 실패했습니다. 차량 배정 완료 여부를 확인해주세요.";
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
     *
     * 처리 흐름:
     * - JSX 기준: 매니페스트 탭에서 체크박스 선택 후 [출고 확정] 버튼 클릭
     * - 단위: OUTBOUND_TRANSPORTATION (매니페스트) 단위 확정
     * - transportationIds[] 각각 state_code → 출고완료, atd = NOW() UPDATE
     * - 부분 실패 감지 (Inbound 패턴 동일)
     *
     * ※ 출고완료 state_code 값 COMMON_CODE 확인 필요
     * ※ OUTBOUND_PACKING의 state_code도 연동 변경이 필요한지 팀 협의 필요
     * (현재는 OUTBOUND_TRANSPORTATION 만 변경)
     */
    @Override
    public ResponseDTO confirmShipment(OutboundConfirmDTO outboundConfirmDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            List<Integer> transportationIds = outboundConfirmDTO.getTransportationIds();

            // TODO: COMMON_CODE에서 "출고완료" state_code 값 확인 후 교체 필요
            final int STATE_CONFIRMED = 0; // ← COMMON_CODE 확인 필요

            int updatedCount = 0;
            for (Integer transportationId : transportationIds) {
                updatedCount += outboundDao.updateTransportationConfirm(transportationId, STATE_CONFIRMED);
            }

            if (updatedCount == transportationIds.size()) {
                isSuccess = true;
                message = "출고 확정이 완료되었습니다.";
            } else if (updatedCount > 0) {
                message = "출고 확정이 일부 실패했습니다.";
            } else {
                message = "출고 확정에 실패했습니다.";
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