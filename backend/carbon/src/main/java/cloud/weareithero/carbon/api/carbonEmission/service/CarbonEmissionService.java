package cloud.weareithero.carbon.api.carbonEmission.service;

import cloud.weareithero.carbon.api.carbonEmission.dto.CarbonEmissionRequestDTO;
import cloud.weareithero.carbon.dto.ResponseDTO;

public interface CarbonEmissionService {
    ResponseDTO getEmissionDetails(CarbonEmissionRequestDTO dto);
}