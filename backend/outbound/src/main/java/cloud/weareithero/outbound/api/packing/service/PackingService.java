package cloud.weareithero.outbound.api.packing.service;

import cloud.weareithero.outbound.api.packing.dto.PackingAddDTO;
import cloud.weareithero.outbound.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.outbound.dto.ResponseDTO;

public interface PackingService {
    
    public ResponseDTO findAll(PackingRequestDTO packingRequestDTO);

    public ResponseDTO findOne(int orderId);

    public ResponseDTO addPacking(PackingAddDTO PackingAddDTO);

    public ResponseDTO completePacking(String packingInvoiceNumber);
}
