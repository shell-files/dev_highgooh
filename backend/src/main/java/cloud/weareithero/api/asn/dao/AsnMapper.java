package cloud.weareithero.api.asn.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cloud.weareithero.api.asn.dto.AsnDTO;
import cloud.weareithero.api.asn.dto.AsnMaterialDTO;
import cloud.weareithero.api.asn.dto.AsnOrderMaterialDTO;
import cloud.weareithero.api.asn.dto.AsnRequestDTO;
import cloud.weareithero.api.asn.dto.AsnSummaryDTO;
import cloud.weareithero.api.asn.dto.AsnSupplierDTO;
import cloud.weareithero.api.asn.dto.AsnWarehouseDTO;

@Mapper
public interface AsnMapper {

  @Select("<script>" +
      """
          SELECT
            COUNT(*) AS `total`,
            SUM(case when i.state_code = 4 then 1 ELSE 0 END) AS `expected`,
            SUM(case when i.state_code = 5 then 1 ELSE 0 END) AS `completed`
          from `INBOUND` `i`
          join `PARTNER_COMPANY_MASTER` `pcm`
          on(`i`.`partner_company_id` = `pcm`.`id`)
          join `WAREHOUSE_MASTER` `wm`
          on(`i`.`warehouse_id` = `wm`.`id`)
          join `COMMON_CODE` `cc`
          on(`i`.`state_code` = `cc`.`id`)
          """ +
      "<where>" +
      "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
      " AND `i`.`order_date` BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(#{orderEnd}, '%Y-%m-%d') " +
      "</if>" +
      "<if test='asnId != null and asnId != 0'>" +
      " AND `i`.`id` LIKE CONCAT('%', #{asnId}, '%') " +
      "</if>" +
      "</where> " +
      "</script>")
  public AsnSummaryDTO findSummary(AsnRequestDTO asnRequestDTO);

  @Select("<script>" +
      """
          SELECT
            `i`.`id` AS asnId,
            `pcm`.`id` AS partnerId,
            `pcm`.`name` AS partnerName,
            `wm`.`id` AS warehouseId,
            `wm`.`name` AS warehouseName,
            `i`.`vehicle_number` AS vehicleNumber,
            `i`.`product_count` AS itemCount,
            `i`.`eta` AS eta,
            `cc`.`name` AS step,
            `i`.`order_date` AS orderDate
          from `INBOUND` `i`
          join `PARTNER_COMPANY_MASTER` `pcm`
          on(`i`.`partner_company_id` = `pcm`.`id`)
          join `WAREHOUSE_MASTER` `wm`
          on(`i`.`warehouse_id` = `wm`.`id`)
          join `COMMON_CODE` `cc`
          on(`i`.`state_code` = `cc`.`id`)
          """ +
      "<where>" +
      "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
      " AND `i`.`order_date` BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(#{orderEnd}, '%Y-%m-%d') " +
      "</if>" +
      "<if test='asnId != null and asnId != 0'>" +
      " AND `i`.`id` LIKE CONCAT('%', #{asnId}, '%') " +
      "</if>" +
      "</where> " +
      "ORDER BY `i`.`id` DESC LIMIT #{offset}, #{size} " +
      "</script>")
  public List<AsnDTO> findAll(AsnRequestDTO asnRequestDTO);

  @Select("""
      SELECT
        om.`id` AS `no`,
        om.inbound_id as `inboundId`,
        om.`material_id` AS `itemNo`,
        mm.`name` AS `itemName`,
        om.diameter_inch AS `diameter`,
        om.weight_kg AS `weight`
      FROM `highgooh`.`ORDER_MATERIAL` AS om
      INNER JOIN `highgooh`.`MATERIAL_MASTER` AS mm
      ON (om.`material_id` = mm.`id`)
      WHERE om.`inbound_id` = #{asnId}
      """)
  public List<AsnOrderMaterialDTO> findOne(int asnId);

  @Select("""
      SELECT
        `i`.`id` AS asnId,
        `pcm`.`id` AS partnerId,
        `pcm`.`name` AS partnerName,
        `wm`.`id` AS warehouseId,
        `wm`.`name` AS warehouseName,
        `i`.`vehicle_number` AS vehicleNumber,
        `i`.`product_count` AS itemCount,
        `i`.`eta` AS eta,
        `cc`.`name` AS step,
        `i`.`order_date` AS orderDate
      from `INBOUND` `i`
      join `PARTNER_COMPANY_MASTER` `pcm`
      on(`i`.`partner_company_id` = `pcm`.`id`)
      join `WAREHOUSE_MASTER` `wm`
      on(`i`.`warehouse_id` = `wm`.`id`)
      join `COMMON_CODE` `cc`
      on(`i`.`state_code` = `cc`.`id`)
      WHERE `i`.`id` = #{asnId}
      """)
  public AsnDTO findByAsnId(int asnId);

  @Insert("""
      INSERT INTO `INBOUND` (
        `partner_company_id`,
        `warehouse_id`,
        `product_count`,
        `eta`
      ) VALUES (
        #{partnerId},
        #{warehouseId},
        #{itemCount},
        #{eta}
      )
      """)
  @Options(useGeneratedKeys = true, keyProperty = "asnId")
  public int add(AsnDTO asnDTO);

  @Insert("""
      INSERT INTO `ORDER_MATERIAL` (
        `inbound_id`,
        `material_id`,
        `weight_kg`,
        `diameter_inch`
      )
      VALUES (
        #{inboundId},
        #{itemNo},
        #{weight},
        #{diameter}
      )
      """)
  public int addOrderMaterial(AsnOrderMaterialDTO orderMaterialDTO);

  @Select("""
      SELECT
        `id`,
        `name`
      FROM `highgooh`.`PARTNER_COMPANY_MASTER`
      WHERE `supplier_yn_code` = 1
      """)
  public List<AsnSupplierDTO> findBySupplier();

  @Select("""
      SELECT
        `id`,
        `name`,
        `scale`,
        `type`
      FROM `highgooh`.`WAREHOUSE_MASTER`
      """)
  public List<AsnWarehouseDTO> findByWarehouse();

  @Select("""
      SELECT
        `id`,
        `name`,
        `alloy_type` AS alloyType
      FROM `highgooh`.`MATERIAL_MASTER`
      """)
  public List<AsnMaterialDTO> findByMaterial();

  // 입고 완료 처리 - state_code 변경, ata(실제입고시각) 기록
  @Update("""
      UPDATE `INBOUND` 
        SET `state_code` = 5, 
            `ata` = #{ata}
        WHERE `id` = #{asnId}
      """)
  public int completeInbound(@Param("asnId") int asnId, @Param("ata") LocalDateTime ata);

  // 입고 상태 검색
  @Select("SELECT `state_code` FROM `INBOUND` WHERE `id` = #{asnId}")
  public int findStateCode(int asnId);

}
