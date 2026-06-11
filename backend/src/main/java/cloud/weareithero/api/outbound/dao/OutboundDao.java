package cloud.weareithero.api.outbound.dao;

import java.util.List;

import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;

public interface OutboundDao {

    public List<OutboundDTO> findAll(OutboundRequestDTO outboundRequestDTO);

    int countAll(OutboundRequestDTO outboundRequestDTO);

    public List<OutboundProductDTO> findOne(int outboundId);

    public OutboundDTO findbyOutboundId(int outboundId);

}