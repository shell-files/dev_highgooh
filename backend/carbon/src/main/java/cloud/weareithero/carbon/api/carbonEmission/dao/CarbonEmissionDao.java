package cloud.weareithero.carbon.api.carbonEmission.dao;

import cloud.weareithero.carbon.api.carbonEmission.dto.CarbonEmissionDTO;
import cloud.weareithero.carbon.api.carbonEmission.dto.CarbonEmissionRequestDTO;
import java.util.List;

public interface CarbonEmissionDao {
    // 배출량 조회 메서드 추가
    List<CarbonEmissionDTO> selectDailyEmission(CarbonEmissionRequestDTO dto);
    List<CarbonEmissionDTO> selectMonthlyEmissionByQuarter(CarbonEmissionRequestDTO dto);
    List<CarbonEmissionDTO> selectMonthlyEmissionByYear(CarbonEmissionRequestDTO dto);

}