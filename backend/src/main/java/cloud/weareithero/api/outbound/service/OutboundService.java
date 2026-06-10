package cloud.weareithero.api.outbound.service;

import cloud.weareithero.api.outbound.dto.OutboundConfirmDTO;
import cloud.weareithero.api.outbound.dto.OutboundInvoiceDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;
import cloud.weareithero.api.outbound.dto.OutboundVehicleAssignDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface OutboundService {

    public ResponseDTO findAll(OutboundRequestDTO outboundRequestDTO);

    public ResponseDTO findOne(int outboundId);

    public ResponseDTO findAllManifest(OutboundRequestDTO outboundRequestDTO);

    public ResponseDTO findAllOutbound();

    public ResponseDTO assignVehicle(OutboundVehicleAssignDTO outboundVehicleAssignDTO);

    public ResponseDTO issueInvoice(OutboundInvoiceDTO outboundInvoiceDTO);

    public ResponseDTO confirmShipment(OutboundConfirmDTO outboundConfirmDTO);

}