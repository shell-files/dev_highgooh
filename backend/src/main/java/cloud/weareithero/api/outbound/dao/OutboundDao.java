package cloud.weareithero.api.outbound.dao;

import java.util.List;

import cloud.weareithero.api.outbound.dto.OutboundCarrierDTO;
import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundInvoiceDTO;
import cloud.weareithero.api.outbound.dto.OutboundManifestDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundSummaryDTO;
import cloud.weareithero.api.outbound.dto.OutboundTransportationVehicleDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;

public interface OutboundDao {

    public OutboundSummaryDTO findSummary(OutboundRequestDTO outboundRequestDTO);

    public List<OutboundDTO> findAll(OutboundRequestDTO outboundRequestDTO);

    public OutboundDTO findByOutboundId(int outboundId);

    public List<OutboundProductDTO> findProducts(int outboundId);

    public List<OutboundManifestDTO> findAllManifest(OutboundRequestDTO outboundRequestDTO);

    public List<OutboundCarrierDTO> findByCarrier();

    public List<OutboundTransportationVehicleDTO> findByVehicle();

    // 차량 배정: OUTBOUND_TRANSPORTATION INSERT → 생성된 PK 반환
    public int addTransportation(OutboundVehicleAssignDTO outboundVehicleAssignDTO);

    // 차량 배정: 개별 OUTBOUND에 transportationId 연결 UPDATE
    public int updateTransportationId(int outboundId, int transportationId);

    public int updateInvoice(OutboundInvoiceDTO outboundInvoiceDTO);

    // 출고 확정: state_code UPDATE
    public int updateStateCode(int outboundId, int stateCode);

}