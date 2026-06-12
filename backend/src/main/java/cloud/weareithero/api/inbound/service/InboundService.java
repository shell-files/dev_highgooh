package cloud.weareithero.api.inbound.service;

import cloud.weareithero.api.inbound.dto.InboundRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface InboundService {
    
    public ResponseDTO findAll(InboundRequestDTO inboundRequestDTO);
    
    public ResponseDTO findOne(int asnId);

}
