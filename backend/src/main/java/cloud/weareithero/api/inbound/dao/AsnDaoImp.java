package cloud.weareithero.api.inbound.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.inbound.dto.AsnDTO;
import cloud.weareithero.api.inbound.dto.AsnMaterialDTO;
import cloud.weareithero.api.inbound.dto.AsnOrderMaterialDTO;
import cloud.weareithero.api.inbound.dto.AsnRequestDTO;
import cloud.weareithero.api.inbound.dto.AsnSummaryDTO;
import cloud.weareithero.api.inbound.dto.AsnSupplierDTO;
import cloud.weareithero.api.inbound.dto.AsnWarehouseDTO;
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
  
}
