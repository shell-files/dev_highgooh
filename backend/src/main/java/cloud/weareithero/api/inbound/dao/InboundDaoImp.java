package cloud.weareithero.api.inbound.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.inbound.dto.InboundDTO;
import cloud.weareithero.api.inbound.dto.InboundItemDTO;
import cloud.weareithero.api.inbound.dto.InboundRequestDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InboundDaoImp implements InboundDao {

    private final InboundMapper inboundMapper;

    @Override
    public List<InboundDTO> findAll(InboundRequestDTO inboundRequestDTO) {
        return inboundMapper.findAll(inboundRequestDTO);
    }

    @Override
    public int countAll(InboundRequestDTO inboundRequestDTO) {
        return inboundMapper.countAll(inboundRequestDTO);
    }
    
    @Override
    public List<InboundItemDTO> findOne(int asnId) {
        return inboundMapper.findOne(asnId);
    }
    
    @Override
    public InboundDTO findbyAsnId(int asnId) {
        return inboundMapper.findbyAsnId(asnId);
    }
}
