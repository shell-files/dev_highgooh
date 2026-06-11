package cloud.weareithero.api.outbound.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cloud.weareithero.api.outbound.dao.OutboundDao;
import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.dto.PaginationDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboundServiceImp implements OutboundService {

    private final OutboundDao outboundDao;

    @Override
    public ResponseDTO findAll(OutboundRequestDTO outboundRequestDTO) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            int total = outboundDao.countAll(outboundRequestDTO);
            PaginationDTO paginationDTO = new PaginationDTO(outboundRequestDTO.getPage(), total, 0);

            List<OutboundDTO> list = outboundDao.findAll(outboundRequestDTO);

            request.put("list", list);
            request.put("page", paginationDTO);
            isSuccess = true;
            message = "출고 목록 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("OutboundServiceImp findAll error : {}", e.getMessage());
            message = "출고 목록 조회에 실패했습니다.";
        }
        return ResponseDTO.builder()
                .status(isSuccess)
                .message(message)
                .data(request)
                .build();
    }

    @Override
    public ResponseDTO findOne(int outboundId) {
        boolean isSuccess = false;
        String message = null;
        Map<String, Object> request = new HashMap<>();
        try {
            OutboundDTO outboundHeader = outboundDao.findbyOutboundId(outboundId);
            List<OutboundProductDTO> list = outboundDao.findOne(outboundId);

            request.put("outboundHeader", outboundHeader);
            request.put("list", list);
            isSuccess = true;
            message = "주문 상세 조회가 완료되었습니다.";
        } catch (Exception e) {
            log.info("OutboundServiceImp findOne error : {}", e.getMessage());
            message = "주문 상세 조회에 실패했습니다.";
        }
        return ResponseDTO.builder()
                .status(isSuccess)
                .message(message)
                .data(request)
                .build();
    }
}