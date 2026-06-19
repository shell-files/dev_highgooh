package cloud.weareithero.carbon.api.carbonAnomaly.service;

import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyStateUpdateDTO;
import cloud.weareithero.carbon.dto.ResponseDTO;

public interface CarbonAnomalyService {

    ResponseDTO getYesterdayPipeline();

    ResponseDTO getAnomalyList(CarbonAnomalyRequestDTO dto);

    ResponseDTO updateAnomalyStatus(CarbonAnomalyStateUpdateDTO dto);

}