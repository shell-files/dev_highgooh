package cloud.weareithero.api.outbound.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OutboundDaoImp implements OutboundDao {

    private final OutboundMapper outboundMapper;

    @Override
    public List<OutboundDTO> findAll(OutboundRequestDTO outboundRequestDTO) {
        return outboundMapper.findAll(outboundRequestDTO);
    }

    @Override
    public int countAll(OutboundRequestDTO outboundRequestDTO) {
        return outboundMapper.countAll(outboundRequestDTO);
    }

    @Override
    public List<OutboundProductDTO> findOne(int outboundId) {
        return outboundMapper.findOne(outboundId);
    }

    @Override
    public OutboundDTO findbyOutboundId(int outboundId) {
        return outboundMapper.findbyOutboundId(outboundId);
    }
}