package cloud.weareithero.api.carbonEmission.service;

import java.util.List;
import org.springframework.stereotype.Service;
import cloud.weareithero.api.carbonEmission.dao.CarbonEmissionDao;
import cloud.weareithero.api.carbonEmission.dto.CarbonEmissionDTO;
import cloud.weareithero.api.carbonEmission.dto.CarbonEmissionRequestDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarbonEmissionServiceImp implements CarbonEmissionService {

    private final CarbonEmissionDao carbonEmissionDao;

    @Override
    public ResponseDTO getEmissionDetails(CarbonEmissionRequestDTO dto) {
        boolean isSuccess = false;
        String message = "데이터 조회가 완료되었습니다.";
        List<CarbonEmissionDTO> detailList;

        try {
            // 요청 기준에 따라 적절한 쿼리 분기
            if (!"".equals(dto.getMonth())) {
                detailList = carbonEmissionDao.selectDailyEmission(dto);
            } else if (!"".equals(dto.getQuarter())) {
                detailList = carbonEmissionDao.selectMonthlyEmissionByQuarter(dto);
            } else {
                detailList = carbonEmissionDao.selectMonthlyEmissionByYear(dto);
            }

            isSuccess = true;
        } catch (Exception e) {
            log.error("CarbonEmissionServiceImp getEmissionDetails error : {}", e.getMessage());
            message = "데이터 조회 중 오류가 발생했습니다.";
            detailList = null;
        }

        return ResponseDTO.builder()
                .status(isSuccess)
                .data(detailList) // 여기서 리스트 통째로 반환!
                .message(message)
                .build();
    }
}