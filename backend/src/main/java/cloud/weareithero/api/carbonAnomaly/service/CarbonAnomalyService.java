package cloud.weareithero.api.carbonAnomaly.service;

import java.util.List;

import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyStateUpdateDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyYesterdayDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface CarbonAnomalyService {

    ResponseDTO getYesterdayPipeline();

    ResponseDTO getAnomalyList(CarbonAnomalyRequestDTO dto);

    ResponseDTO updateAnomalyStatus(CarbonAnomalyStateUpdateDTO dto);

}