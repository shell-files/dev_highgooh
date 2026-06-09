package cloud.weareithero.api.inbound.service;

import cloud.weareithero.dto.ResponseDTO;

public interface InboundService {
    
    public ResponseDTO findAll();
    
    public ResponseDTO findOne(int asnId);

}
