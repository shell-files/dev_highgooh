package cloud.weareithero.api.packing.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import cloud.weareithero.api.packing.dao.PackingDaoImp;
import cloud.weareithero.api.packing.dto.PackingAddDTO;
import cloud.weareithero.api.packing.dto.PackingCarrierDTO;
import cloud.weareithero.api.packing.dto.PackingDTO;
import cloud.weareithero.api.packing.dto.PackingInvoiceDTO;
import cloud.weareithero.api.packing.dto.PackingOrderProductDTO;
import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.api.packing.dto.PackingSummaryDTO;
import cloud.weareithero.dto.PaginationDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackingServiceImp implements PackingService {

    private final PackingDaoImp packingDao;
    
    @Override
    public ResponseDTO findAll(PackingRequestDTO packingRequestDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> data = new HashMap<>();

        try {
            PackingSummaryDTO summary = packingDao.findSummary(packingRequestDTO);
            List<PackingDTO> list = packingDao.findAll(packingRequestDTO);
            PaginationDTO pagination = PaginationDTO.builder()
                .page(packingRequestDTO.getPage())
                .totalCount(summary.getTotal())
                .totalPages((int) Math.ceil((double) summary.getTotal() / packingRequestDTO.getSize()))
                .build();
            data.put("summary", summary);
            data.put("list", list);
            data.put("pagination", pagination);
            isSuccess = true;
            message = "Packing 탭 // 주문 목록 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("PackingServiceImp findAll error : {}", e.getMessage());
            message = "Packing 탭 // 주문 목록 조회에 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(data)
            .message(message)
            .build();
    }

    @Override
    public ResponseDTO findOne(int orderId) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> data = new HashMap<>();
        try {
            PackingDTO order = packingDao.findOne(orderId);
            List<PackingOrderProductDTO> items = packingDao.findOrderProduct(orderId);
            List<PackingCarrierDTO> carrier = packingDao.findCarrierCompany();
            data.put("order", order);
            data.put("items", items);
            data.put("carrier", carrier);
            if (order.getStepCode() == 19 || order.getStepCode() == 20) {
            List<PackingInvoiceDTO> packingDetail = packingDao.findInvoice(orderId);
            data.put("packingDetail", packingDetail);
            }
            isSuccess = true;
            message = "Packing 탭 // 주문 상세 정보 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("PackingServiceImp findOne error : {}", e.getMessage());
            message = "Packing 탭 // 주문 상세 정보 조회가 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(data)
            .message(message)
            .build();
    }

    
    @Transactional
    @Override
    public ResponseDTO addPacking(PackingAddDTO PackingAddDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> data = new HashMap<>();
        try {
            // 1. OUTBOUND state_code 19로 변경
            int updated = packingDao.updateStateCode(PackingAddDTO.getOrderId());

            if (updated > 0) {
                // 2. OUTBOUND_Packing 행 추가
                int size = 0;
                for (PackingInvoiceDTO invoice : PackingAddDTO.getPackingInvoice()) {
                    size += packingDao.addInvoice(invoice);
                }
                if (size == PackingAddDTO.getPackingInvoice().size()) {
                    isSuccess = true;
                    message = "Packing 탭 // 송장 %d건 생성이 완료되었습니다.".formatted(size);
                } else {
                    isSuccess = false;
                    message = "Packing 탭 // 패킹 송장 생성 일부 실패했습니다.";
                }
            }
        } catch (Exception e) {
            log.info("PackingServiceImp InsertPacking error : {}", e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            message = "Packing 탭 // 패킹 송장 생성에 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(data)
            .message(message)
            .build();
    }

}