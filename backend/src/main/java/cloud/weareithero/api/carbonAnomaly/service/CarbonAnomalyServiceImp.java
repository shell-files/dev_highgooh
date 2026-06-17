package cloud.weareithero.api.carbonAnomaly.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.weareithero.api.carbonAnomaly.dao.CarbonAnomalyDao;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyCountDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyStateUpdateDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyYesterdayDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarbonAnomalyServiceImp implements CarbonAnomalyService {

    private final CarbonAnomalyDao carbonAnomalyDao;

    /**
     * 상단 작일 공정 파이프라인 이상치 현황
     */
    @Override
    public ResponseDTO getYesterdayPipeline() {
        boolean isSuccess = false;
        String message = "데이터 조회가 완료되었습니다.";
        List<CarbonAnomalyYesterdayDTO> detailList;

        try {
            detailList = carbonAnomalyDao.getYesterdayPipeline();
            isSuccess = true;
        } catch (Exception e) {
            log.error("CarbonAnomalyServiceImp getYesterdayPipeline error", e);
            message = "데이터 조회 중 오류가 발생했습니다.";
            detailList = null;
        }

        return ResponseDTO.builder()
                .status(isSuccess)
                .data(detailList)
                .message(message)
                .build();
    }

    /**
     * 하단 기간별 공정 이상치 현황 (페이징 적용 버전)
     */
    @Override
    public ResponseDTO getAnomalyList(CarbonAnomalyRequestDTO dto) {
        boolean isSuccess = false;
        String message = "데이터 조회가 완료되었습니다.";
        Map<String, Object> resultMap = new HashMap<>();

        try {
            // 1. 페이징 처리 (DTO에 이미 year, month, quarter가 세팅되어 있음)
            if (dto.getPage() == null || dto.getPage() < 1) {
                dto.setPage(1);
            }
            dto.setOffset((dto.getPage() - 1) * dto.getLimit());

            // 2. DB 조회
            List<CarbonAnomalyDTO> detailList = carbonAnomalyDao.selectAnomalyList(dto);
            List<CarbonAnomalyCountDTO> stats = carbonAnomalyDao.selectAnomalyCount(dto);

            // 3. 결과 합산
            long totalCount = stats.stream().mapToLong(CarbonAnomalyCountDTO::getAnomaly_count).sum();

            // Map에 결과 담기
            resultMap.put("list", detailList);
            resultMap.put("stats", stats);
            resultMap.put("totalCount", totalCount);

            isSuccess = true;

        } catch (Exception e) {
            log.error("CarbonAnomalyServiceImp getAnomalyList error", e);
            message = "데이터 조회 중 오류가 발생했습니다.";
            return ResponseDTO.builder().status(false).message(message).build();
        }

        return ResponseDTO.builder()
                .status(isSuccess)
                .data(resultMap)
                .message(message)
                .build();
    }

    @Override
    @Transactional
    public ResponseDTO updateAnomalyStatus(CarbonAnomalyStateUpdateDTO dto) {
        try {
            // 1. DAO를 통해 업데이트 실행
            int updatedRows = carbonAnomalyDao.updateAnomalyStatus(dto);

            // 2. 결과에 따른 응답 설정
            if (updatedRows > 0) {
                return ResponseDTO.builder()
                        .status(true)
                        .message("상태값이 성공적으로 업데이트되었습니다.")
                        .build();
            } else {
                return ResponseDTO.builder()
                        .status(false)
                        .message("ID에 해당하는 데이터를 찾을 수 없습니다.")
                        .build();
            }
        } catch (Exception e) {
            log.error("CarbonAnomalyServiceImp updateAnomalyStatus error", e);
            return ResponseDTO.builder()
                    .status(false)
                    .message("업데이트 중 오류가 발생했습니다.")
                    .build();
        }
    }
}