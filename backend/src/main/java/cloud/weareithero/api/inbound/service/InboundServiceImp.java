package cloud.weareithero.api.inbound.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cloud.weareithero.api.inbound.dao.InboundDao;
import cloud.weareithero.api.inbound.dto.InboundDTO;
import cloud.weareithero.api.inbound.dto.InboundItemDTO;
import cloud.weareithero.api.inbound.dto.InboundRequestDTO;
import cloud.weareithero.dto.PaginationDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboundServiceImp implements InboundService {

    private final InboundDao inboundDao;

    @Override
    public ResponseDTO findAll(InboundRequestDTO inboundRequestDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();

        try {
            List<InboundDTO> inboundList = inboundDao.findAll(inboundRequestDTO);
            int totalCount = inboundDao.countAll(inboundRequestDTO);
            PaginationDTO pagination = PaginationDTO.builder()
                .page(inboundRequestDTO.getPage())
                .totalCount(totalCount)
                .totalPages((int) Math.ceil((double) totalCount / inboundRequestDTO.getSize()))
                .build();
            request.put("list", inboundList);
            request.put("pagination", pagination);
            isSuccess = true;
            message = "Inbound 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("InboundServiceImp findAll error : {}", e.getMessage());
            message = "Inbound 조회에 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(request)
            .message(message)
            .build();
    }

    @Override
    public ResponseDTO findOne(int asnId) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();

        try {
            List<InboundItemDTO> items = inboundDao.findOne(asnId);
            InboundDTO inbound = inboundDao.findbyAsnId(asnId);
            request.put("inbound", inbound);
            request.put("items", items);            
            isSuccess = true;
            message = "Inbound 상세 정보 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("InboundServiceImp findOne error : {}", e.getMessage());
            message = "Inbound 상세 정보 조회가 실패했습니다.";
        }
        return ResponseDTO.builder()
            .status(isSuccess)
            .data(request)
            .message(message)
            .build()
        ;
    }
    
}
