package cloud.weareithero.api.asn.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.asn.dto.AsnDTO;
import cloud.weareithero.api.asn.dto.AsnMaterialDTO;
import cloud.weareithero.api.asn.dto.AsnOrderMaterialDTO;
import cloud.weareithero.api.asn.dto.AsnRequestDTO;
import cloud.weareithero.api.asn.dto.AsnSummaryDTO;
import cloud.weareithero.api.asn.dto.AsnSupplierDTO;
import cloud.weareithero.api.asn.dto.AsnWarehouseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AsnDaoImp implements AsnDao {

  private final AsnMapper asnMapper;

  @Override
  public AsnSummaryDTO findSummary(AsnRequestDTO asnRequestDTO) {
    return asnMapper.findSummary(asnRequestDTO);
  }

  @Override
  public List<AsnDTO> findAll(AsnRequestDTO asnRequestDTO) {
    return asnMapper.findAll(asnRequestDTO);
  }

  @Override
  public AsnDTO findByAsnId(int asnId) {
    return asnMapper.findByAsnId(asnId);
  }

  @Override
  public List<AsnOrderMaterialDTO> findOne(int asnId) {
    return asnMapper.findOne(asnId);
  }

  @Override
  public AsnDTO add(AsnDTO asnDTO) {
    asnMapper.add(asnDTO);
    return asnDTO;
  }

  @Override
  public int addOrderMaterial(AsnOrderMaterialDTO orderMaterialDTO) {
    return asnMapper.addOrderMaterial(orderMaterialDTO);
  }

  @Override
  public List<AsnSupplierDTO> findBySupplier() {
    return asnMapper.findBySupplier();
  }

  @Override
  public List<AsnWarehouseDTO> findByWarehouse() {
    return asnMapper.findByWarehouse();
  }

  @Override
  public List<AsnMaterialDTO> findByMaterial() {
    return asnMapper.findByMaterial();
  }

  @Override
  public int completeInbound(int asnId, LocalDateTime ata) {
    return asnMapper.completeInbound(asnId, ata);
  }

  @Override
  public int findStateCode(int asnId) {
    return asnMapper.findStateCode(asnId);
  }
}
