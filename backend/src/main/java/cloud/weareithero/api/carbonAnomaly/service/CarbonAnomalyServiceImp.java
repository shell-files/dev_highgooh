package cloud.weareithero.api.carbonAnomaly.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cloud.weareithero.api.carbonAnomaly.dao.CarbonAnomalyDao;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyCountDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
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
<<<<<<< Updated upstream
            int year = Integer.parseInt(dto.getYear());
            LocalDateTime start;
            LocalDateTime end;

            // 기간 계산 로직 (월/분기/연도)
            if (dto.getMonth() != null && !dto.getMonth().isBlank()) {
                int month = Integer.parseInt(dto.getMonth());
                start = LocalDate.of(year, month, 1).atStartOfDay();
                end = start.plusMonths(1);
            } else if (dto.getQuarter() != null && !dto.getQuarter().isBlank()) {
                int quarter = Integer.parseInt(dto.getQuarter());
                int startMonth = (quarter - 1) * 3 + 1;
                start = LocalDate.of(year, startMonth, 1).atStartOfDay();
                end = start.plusMonths(3);
            } else {
                start = LocalDate.of(year, 1, 1).atStartOfDay();
                end = start.plusYears(1);
            }

            if (dto.getPage() == null || dto.getPage() < 1) dto.setPage(1);
            dto.setOffset((dto.getPage() - 1) * dto.getLimit());
            dto.setCalculatedStartDate(start);
            dto.setCalculatedEndDate(end);

            // DB 조회 요청
            List<CarbonAnomalyDTO> detailList = carbonAnomalyDao.selectAnomalyList(dto);
            List<CarbonAnomalyCountDTO> stats = carbonAnomalyDao.selectAnomalyCount(dto);
        
            // 전체 카운트 계산
=======
            // 1. 페이징 처리 (DTO에 이미 year, month, quarter가 세팅되어 있음)
            if (dto.getPage() == null || dto.getPage() < 1) {
                dto.setPage(1);
            }
            dto.setOffset((dto.getPage() - 1) * dto.getLimit());

            // 2. DB 조회
            List<CarbonAnomalyDTO> detailList = carbonAnomalyDao.selectAnomalyList(dto);
            List<CarbonAnomalyCountDTO> stats = carbonAnomalyDao.selectAnomalyCount(dto);

            // 3. 결과 합산
>>>>>>> Stashed changes
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
}