package cloud.weareithero.api.inbound.dao;

import java.util.List;

import cloud.weareithero.api.inbound.dto.AsnDTO;
import cloud.weareithero.api.inbound.dto.AsnMaterialDTO;
import cloud.weareithero.api.inbound.dto.AsnOrderMaterialDTO;
import cloud.weareithero.api.inbound.dto.AsnRequestDTO;
import cloud.weareithero.api.inbound.dto.AsnSummaryDTO;
import cloud.weareithero.api.inbound.dto.AsnSupplierDTO;
import cloud.weareithero.api.inbound.dto.AsnWarehouseDTO;

public interface AsnDao {

  public AsnSummaryDTO findSummary(AsnRequestDTO asnRequestDTO);
  public List<AsnDTO> findAll(AsnRequestDTO asnRequestDTO);
  public AsnDTO findByAsnId(int asnId);
  public List<AsnOrderMaterialDTO> findOne(int asnId);
  public AsnDTO add(AsnDTO asnDTO);
  public int addOrderMaterial(AsnOrderMaterialDTO orderMaterialDTO);
  public List<AsnSupplierDTO> findBySupplier();
  public List<AsnWarehouseDTO> findByWarehouse();
  public List<AsnMaterialDTO> findByMaterial();
  
}
