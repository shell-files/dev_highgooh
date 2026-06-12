package cloud.weareithero.api.carbonAnomaly.dao;

import java.util.List;

import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;

public interface CarbonAnomalyDao {

    List<CarbonAnomalyDTO> selectYesterdayPipeline();

    List<CarbonAnomalyDTO> selectAnomalyList(
            CarbonAnomalyRequestDTO dto);
}