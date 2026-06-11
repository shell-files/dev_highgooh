package cloud.weareithero.api.packing.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cloud.weareithero.api.packing.dao.PackingDaoImp;
import cloud.weareithero.api.packing.dto.PackingDTO;
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
            data.put("order", order);
            data.put("items", items);
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

}