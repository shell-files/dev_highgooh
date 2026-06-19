package cloud.weareithero.inbound.api.inbound.dao;

import java.util.List;

import cloud.weareithero.inbound.api.inbound.dto.InboundDTO;
import cloud.weareithero.inbound.api.inbound.dto.InboundItemDTO;
import cloud.weareithero.inbound.api.inbound.dto.InboundRequestDTO;

public interface InboundDao {
    
    public List<InboundDTO> findAll(InboundRequestDTO inboundRequestDTO);
    
    public int countAll(InboundRequestDTO inboundRequestDTO);
    
    public List<InboundItemDTO> findOne(int asnId);

    public InboundDTO findbyAsnId(int asnId);

}
