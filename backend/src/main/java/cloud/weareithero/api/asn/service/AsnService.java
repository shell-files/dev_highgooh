package cloud.weareithero.api.asn.service;

import cloud.weareithero.api.asn.dto.AsnAddDTO;
import cloud.weareithero.api.asn.dto.AsnRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface AsnService {

  public ResponseDTO findAll(AsnRequestDTO asnRequestDTO);

  public ResponseDTO findOne(int asnId);

  public ResponseDTO add(AsnAddDTO asnAddDTO);

  public ResponseDTO findAllAsn();

}
