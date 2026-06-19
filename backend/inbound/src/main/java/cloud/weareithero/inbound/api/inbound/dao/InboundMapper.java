package cloud.weareithero.inbound.api.inbound.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import cloud.weareithero.inbound.api.inbound.dto.InboundDTO;
import cloud.weareithero.inbound.api.inbound.dto.InboundItemDTO;
import cloud.weareithero.inbound.api.inbound.dto.InboundRequestDTO;

@Mapper
public interface InboundMapper {
    
    @Select("<script>" +
        """
        SELECT
            `i`.`ata` AS ata,
            `i`.`id` AS asnId,
            `pcm`.`id` AS partnerId,
            `pcm`.`name` AS partnerName,
            `wm`.`id` AS warehouseId,
            `wm`.`name` AS warehouseName
        FROM `INBOUND` `i`
        JOIN `PARTNER_COMPANY_MASTER` `pcm`
            ON(`i`.`partner_company_id` = `pcm`.`id`)
        JOIN `WAREHOUSE_MASTER` `wm`
            ON(`i`.`warehouse_id` = `wm`.`id`)
        JOIN `COMMON_CODE` `cc`
            ON(`i`.`state_code` = `cc`.`id`)
        """ +
        "<where>" +
        " AND `cc`.`name` = '입고완료' " +
        "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
        " AND `i`.`ata` BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(#{orderEnd}, '%Y-%m-%d') " +
        "</if>" +
        "<if test='asnId != null and asnId != 0'>" +
        " AND `i`.`id` LIKE CONCAT('%', #{asnId}, '%') " +
        "</if>" +
        "</where> " +
        "ORDER BY `i`.`id` DESC LIMIT #{offset}, #{size} " +
        "</script>")
    public List<InboundDTO> findAll(InboundRequestDTO inboundRequestDTO);

    @Select("<script>" +
        "SELECT COUNT(*) FROM `INBOUND` `i` " +
        "JOIN `COMMON_CODE` `cc` ON `i`.`state_code` = `cc`.`id` " +
        "WHERE `cc`.`name` = '입고완료' " +
        "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
        " AND `i`.`ata` BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d')" +
        " AND STR_TO_DATE(CONCAT(#{orderEnd}, ' 23:59:59'), '%Y-%m-%d %H:%i:%s') " +
        "</if>" +
        "<if test='asnId != null and asnId != 0'>" +
        " AND `i`.`id` = #{asnId} " +
        "</if>" +
        "</script>")
    int countAll(InboundRequestDTO inboundRequestDTO);

    @Select("""
        SELECT
            om.`id` AS `no`,
            om.inbound_id as `inboundId`,
            om.`material_id` AS `itemNo`,
            mm.`name` AS `itemName`,
            mm.`alloy_type` AS `alloyType`,
            om.weight_kg AS `weight`
        FROM `highgooh`.`ORDER_MATERIAL` AS om
        INNER JOIN `highgooh`.`MATERIAL_MASTER` AS mm
        ON (om.`material_id` = mm.`id`)
        WHERE om.`inbound_id` = #{asnId}
            """)
    public List<InboundItemDTO> findOne(int asnId);

    @Select("""
        SELECT
            `i`.`ata` AS ata,
            `i`.`id` AS asnId,
            `pcm`.`id` AS partnerId,
            `pcm`.`name` AS partnerName,
            `wm`.`id` AS warehouseId,
            `wm`.`name` AS warehouseName
        FROM `INBOUND` `i`
        JOIN `PARTNER_COMPANY_MASTER` `pcm`
            ON(`i`.`partner_company_id` = `pcm`.`id`)
        JOIN `WAREHOUSE_MASTER` `wm`
            ON(`i`.`warehouse_id` = `wm`.`id`)
        JOIN `COMMON_CODE` `cc`
            ON(`i`.`state_code` = `cc`.`id`)
        WHERE `i`.`id` = #{asnId}
        """)
    public InboundDTO findbyAsnId(int asnId);




}
