package cloud.weareithero.api.inbound.service;

import cloud.weareithero.api.inbound.dto.AsnAddDTO;
import cloud.weareithero.api.inbound.dto.AsnRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface AsnService {
  
  public ResponseDTO findAll(AsnRequestDTO asnRequestDTO);
  public ResponseDTO findOne(int asnId);
  public ResponseDTO add(AsnAddDTO asnAddDTO);
  public ResponseDTO findAllAsn();

}
