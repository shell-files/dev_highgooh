package cloud.weareithero.inbound.api.asn.service;

import cloud.weareithero.inbound.api.asn.dto.AsnAddDTO;
import cloud.weareithero.inbound.api.asn.dto.AsnCompleteDTO;
import cloud.weareithero.inbound.api.asn.dto.AsnRequestDTO;
import cloud.weareithero.inbound.dto.ResponseDTO;

public interface AsnService {

  public ResponseDTO findAll(AsnRequestDTO asnRequestDTO);

  public ResponseDTO findOne(int asnId);

  public ResponseDTO add(AsnAddDTO asnAddDTO);

  public ResponseDTO findAllAsn();

  public ResponseDTO completeInbound(AsnCompleteDTO asnCompleteDTO);

}
