package cloud.weareithero.api.packing.service;

import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface PackingService {
    
    public ResponseDTO findAll(PackingRequestDTO packingRequestDTO);
}
