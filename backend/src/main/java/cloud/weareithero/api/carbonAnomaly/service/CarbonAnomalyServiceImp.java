package cloud.weareithero.api.carbonAnomaly.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import cloud.weareithero.api.carbonAnomaly.dao.CarbonAnomalyDao;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
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
        List<CarbonAnomalyDTO> detailList;

        try {
            detailList = carbonAnomalyDao.selectYesterdayPipeline();
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
     * 하단 기간별 공정 이상치 현황
     */
    /**
     * 하단 기간별 공정 이상치 현황 (페이징 적용 버전)
     */
    @Override
    public ResponseDTO getAnomalyList(CarbonAnomalyRequestDTO dto) {
        boolean isSuccess = false;
        String message = "데이터 조회가 완료되었습니다.";
        List<CarbonAnomalyDTO> detailList;

        try {
            int year = Integer.parseInt(dto.getYear());
            LocalDateTime start;
            LocalDateTime end;

            // 1. 월 선택
            if (dto.getMonth() != null && !dto.getMonth().isBlank()) {
                int month = Integer.parseInt(dto.getMonth());
                start = LocalDate.of(year, month, 1).atStartOfDay();
                end = start.plusMonths(1);
            }
            // 2. 분기 선택
            else if (dto.getQuarter() != null && !dto.getQuarter().isBlank()) {
                int quarter = Integer.parseInt(dto.getQuarter());
                int startMonth = (quarter - 1) * 3 + 1;
                start = LocalDate.of(year, startMonth, 1).atStartOfDay();
                end = start.plusMonths(3);
            }
            // 3. 연도만 선택
            else {
                start = LocalDate.of(year, 1, 1).atStartOfDay();
                end = start.plusYears(1);
            }

            // [페이징 계산 추가] 
            // 혹시라도 프론트에서 page 값을 누락하거나 0 이하를 보내면 1페이지로 방어코드 적용
            if (dto.getPage() == null || dto.getPage() < 1) {
                dto.setPage(1);
            }
            // 건너뛸 개수(Offset) 계산
            int offset = (dto.getPage() - 1) * dto.getLimit();
            dto.setOffset(offset);

            dto.setCalculatedStartDate(start);
            dto.setCalculatedEndDate(end);

            // DB 조회 요청
            detailList = carbonAnomalyDao.selectAnomalyList(dto);
            isSuccess = true;

        } catch (Exception e) {
            log.error("CarbonAnomalyServiceImp getAnomalyList error", e);
            message = "데이터 조회 중 오류가 발생했습니다.";
            detailList = null;
        }

        return ResponseDTO.builder()
                .status(isSuccess)
                .data(detailList)
                .message(message)
                .build();
    }
}