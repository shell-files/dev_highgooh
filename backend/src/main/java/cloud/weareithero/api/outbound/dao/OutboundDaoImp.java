package cloud.weareithero.api.outbound.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.outbound.dto.OutboundCarrierDTO;
import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundManifestDTO;
import cloud.weareithero.api.outbound.dto.OutboundPackingDTO;
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
    public List<OutboundPackingDTO> findAll(OutboundRequestDTO outboundRequestDTO) {
        return outboundMapper.findAll(outboundRequestDTO);
    }

    @Override
    public OutboundDTO findByOutboundId(int outboundId) {
        return outboundMapper.findByOutboundId(outboundId);
    }

    @Override
    public OutboundPackingDTO findByPackingId(int packingId) {
        return outboundMapper.findByPackingId(packingId);
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
        // @Options(useGeneratedKeys = true, keyProperty = "transportationId") 로 PK 자동
        // 주입
        return outboundVehicleAssignDTO.getTransportationId();
    }

    @Override
    public int updatePackingTransportation(int packingId, int transportationId, int shippingReadyCode) {
        return outboundMapper.updatePackingTransportation(packingId, transportationId, shippingReadyCode);
    }

    @Override
    public int updateInvoiceNumber(int packingId, String invoiceNumber, int invoiceStateCode) {
        return outboundMapper.updateInvoiceNumber(packingId, invoiceNumber, invoiceStateCode);
    }

    @Override
    public int updateTransportationConfirm(int transportationId, int stateCode) {
        return outboundMapper.updateTransportationConfirm(transportationId, stateCode);
    }

}