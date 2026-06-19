package cloud.weareithero.inbound.api.inbound.service;

import cloud.weareithero.inbound.api.inbound.dto.InboundRequestDTO;
import cloud.weareithero.inbound.dto.ResponseDTO;

public interface InboundService {
    
    public ResponseDTO findAll(InboundRequestDTO inboundRequestDTO);
    
    public ResponseDTO findOne(int asnId);

}
