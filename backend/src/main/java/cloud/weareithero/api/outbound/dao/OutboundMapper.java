package cloud.weareithero.api.outbound.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import cloud.weareithero.api.outbound.dto.OutboundDTO;
import cloud.weareithero.api.outbound.dto.OutboundProductDTO;
import cloud.weareithero.api.outbound.dto.OutboundRequestDTO;

@Mapper
public interface OutboundMapper {

    @Select("<script>" +
        """
        SELECT
            `o`.`atd` AS atd,
            `o`.`id` AS outboundId,
            `pcm`.`id` AS partnerId,
            `pcm`.`name` AS partnerName,
            `wm`.`id` AS warehouseId,
            `wm`.`name` AS warehouseName
        FROM `OUTBOUND` `o`
        JOIN `PARTNER_COMPANY_MASTER` `pcm`
            ON(`o`.`partner_company_id` = `pcm`.`id`)
        JOIN `WAREHOUSE_MASTER` `wm`
            ON(`o`.`warehouse_id` = `wm`.`id`)
        JOIN `COMMON_CODE` `cc`
            ON(`o`.`state_code` = `cc`.`id`)
        """ +
        "<where>" +
        " AND `cc`.`name` = '출고완료' " +
        "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
        " AND `o`.`atd` BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(#{orderEnd}, '%Y-%m-%d') " +
        "</if>" +
        "<if test='outboundId != null and outboundId != 0'>" +
        " AND `o`.`id` LIKE CONCAT('%', #{outboundId}, '%') " +
        "</if>" +
        "</where> " +
        "ORDER BY `o`.`id` DESC LIMIT #{offset}, #{size} " +
        "</script>")
    public List<OutboundDTO> findAll(OutboundRequestDTO outboundRequestDTO);

    @Select("<script>" +
        "SELECT COUNT(*) FROM `OUTBOUND` `o` " +
        "JOIN `COMMON_CODE` `cc` ON `o`.`state_code` = `cc`.`id` " +
        "WHERE `cc`.`name` = '출고완료' " +
        "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
        " AND `o`.`atd` BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d')" +
        " AND STR_TO_DATE(CONCAT(#{orderEnd}, ' 23:59:59'), '%Y-%m-%d %H:%i:%s') " +
        "</if>" +
        "<if test='outboundId != null and outboundId != 0'>" +
        " AND `o`.`id` = #{outboundId} " +
        "</if>" +
        "</script>")
    int countAll(OutboundRequestDTO outboundRequestDTO);

    @Select("""
        SELECT
            op.`id` AS `no`,
            op.`outbound_id` AS `outboundId`,
            op.`material_id` AS `itemNo`,
            mm.`name` AS `itemName`,
            mm.`alloy_type` AS `alloyType`,
            op.`weight_kg` AS `weight`
        FROM `highgooh`.`ORDER_PRODUCT` AS op
        INNER JOIN `highgooh`.`MATERIAL_MASTER` AS mm
        ON (op.`material_id` = mm.`id`)
        WHERE op.`outbound_id` = #{outboundId}
            """)
    public List<OutboundProductDTO> findOne(int outboundId);

    @Select("""
        SELECT
            `o`.`atd` AS atd,
            `o`.`id` AS outboundId,
            `pcm`.`id` AS partnerId,
            `pcm`.`name` AS partnerName,
            `wm`.`id` AS warehouseId,
            `wm`.`name` AS warehouseName
        FROM `OUTBOUND` `o`
        JOIN `PARTNER_COMPANY_MASTER` `pcm`
            ON(`o`.`partner_company_id` = `pcm`.`id`)
        JOIN `WAREHOUSE_MASTER` `wm`
            ON(`o`.`warehouse_id` = `wm`.`id`)
        JOIN `COMMON_CODE` `cc`
            ON(`o`.`state_code` = `cc`.`id`)
        WHERE `o`.`id` = #{outboundId}
        """)
    public OutboundDTO findbyOutboundId(int outboundId);

}