package cloud.weareithero.inbound.api.asn.dao;

import java.time.LocalDateTime;
import java.util.List;

import cloud.weareithero.inbound.api.asn.dto.AsnDTO;
import cloud.weareithero.inbound.api.asn.dto.AsnMaterialDTO;
import cloud.weareithero.inbound.api.asn.dto.AsnOrderMaterialDTO;
import cloud.weareithero.inbound.api.asn.dto.AsnRequestDTO;
import cloud.weareithero.inbound.api.asn.dto.AsnSummaryDTO;
import cloud.weareithero.inbound.api.asn.dto.AsnSupplierDTO;
import cloud.weareithero.inbound.api.asn.dto.AsnWarehouseDTO;

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

  public int completeInbound(int asnId, LocalDateTime ata);

  public int findStateCode(int asnId);

}
