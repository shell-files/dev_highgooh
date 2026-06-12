package cloud.weareithero.api.carbonAnomaly.service;

import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface CarbonAnomalyService {

    ResponseDTO getYesterdayPipeline();

    ResponseDTO getAnomalyList(CarbonAnomalyRequestDTO dto);

}