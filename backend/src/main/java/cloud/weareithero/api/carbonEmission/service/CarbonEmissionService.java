package cloud.weareithero.api.carbonEmission.service;

import cloud.weareithero.api.carbonEmission.dto.CarbonEmissionRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface CarbonEmissionService {
    ResponseDTO getEmissionDetails(CarbonEmissionRequestDTO dto);
}