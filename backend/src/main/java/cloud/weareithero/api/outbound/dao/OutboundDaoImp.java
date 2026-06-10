package cloud.weareithero.api.outbound.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.outbound.dto.OutboundCarrierDTO;
import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundInvoiceDTO;
import cloud.weareithero.api.outbound.dto.OutboundManifestDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundSummaryDTO;
import cloud.weareithero.api.outbound.dto.OutboundTransportationVehicleDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OutboundDaoImp implements OutboundDao {

    private final OutboundMapper outboundMapper;

    @Override
    public OutboundSummaryDTO findSummary(OutboundRequestDTO outboundRequestDTO) {
        return outboundMapper.findSummary(outboundRequestDTO);
    }

    @Override
    public List<OutboundDTO> findAll(OutboundRequestDTO outboundRequestDTO) {
        return outboundMapper.findAll(outboundRequestDTO);
    }

    @Override
    public OutboundDTO findByOutboundId(int outboundId) {
        return outboundMapper.findByOutboundId(outboundId);
    }

    @Override
    public List<OutboundProductDTO> findProducts(int outboundId) {
        return outboundMapper.findProducts(outboundId);
    }

    @Override
    public List<OutboundManifestDTO> findAllManifest(OutboundRequestDTO outboundRequestDTO) {
        return outboundMapper.findAllManifest(outboundRequestDTO);
    }

    @Override
    public List<OutboundCarrierDTO> findByCarrier() {
        return outboundMapper.findByCarrier();
    }

    @Override
    public List<OutboundTransportationVehicleDTO> findByVehicle() {
        return outboundMapper.findByVehicle();
    }

    @Override
    public int addTransportation(OutboundVehicleAssignDTO outboundVehicleAssignDTO) {
        outboundMapper.addTransportation(outboundVehicleAssignDTO);
        // @Options(useGeneratedKeys = true, keyProperty = "transportationId") 로 PK 주입
        return outboundVehicleAssignDTO.getTransportationId();
    }

    @Override
    public int updateTransportationId(int outboundId, int transportationId) {
        return outboundMapper.updateTransportationId(outboundId, transportationId);
    }

    @Override
    public int updateInvoice(OutboundInvoiceDTO outboundInvoiceDTO) {
        return outboundMapper.updateInvoice(outboundInvoiceDTO);
    }

    @Override
    public int updateStateCode(int outboundId, int stateCode) {
        return outboundMapper.updateStateCode(outboundId, stateCode);
    }

}