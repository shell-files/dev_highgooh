package cloud.weareithero.api.packing.service;

import cloud.weareithero.api.packing.dto.PackingAddDTO;
import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface PackingService {
    
    public ResponseDTO findAll(PackingRequestDTO packingRequestDTO);

    public ResponseDTO findOne(int orderId);

    public ResponseDTO addPacking(PackingAddDTO PackingAddDTO);

    public ResponseDTO completePacking(int invoiceId);
}
