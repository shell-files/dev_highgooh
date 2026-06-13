package cloud.weareithero.api.packing.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import cloud.weareithero.api.packing.dto.PackingCarrierDTO;
import cloud.weareithero.api.packing.dto.PackingDTO;
import cloud.weareithero.api.packing.dto.PackingInvoiceDTO;
import cloud.weareithero.api.packing.dto.PackingOrderProductDTO;
import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.api.packing.dto.PackingSummaryDTO;

@Mapper
public interface PackingMapper {
    
    // 패킹 화면 Summary sql
    @Select("<script>" +
        """
            SELECT
                COUNT(*) AS 'total',
                SUM(CASE WHEN `o`.`state_code` = 9  THEN 1 ELSE 0 END) AS 'newpacking',
                SUM(CASE WHEN `o`.`state_code` = 19 THEN 1 ELSE 0 END) AS 'onpacking',
                SUM(CASE WHEN `o`.`state_code` = 20 THEN 1 ELSE 0 END) AS 'completed'
            FROM `OUTBOUND` `o`
            JOIN `COMMON_CODE` `cc`
                ON(`o`.`state_code` = `cc`.`id`)
            WHERE `o`.`state_code` IN (9, 19, 20)
        """ +
        "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
        " AND `o`.`order_date` BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(#{orderEnd}, '%Y-%m-%d') " +
        "</if>" +
        "<if test='orderId != null and orderId != 0'>" +
        " AND `o`.`id` LIKE CONCAT('%', #{orderId}, '%') " +
        "</if>" +
        "</script>")
    public PackingSummaryDTO findSummary(PackingRequestDTO packingRequestDTO);


    // 패킹 화면 목록 출력을 위한 주문목록 조회
    @Select("<script>" +
        """
            SELECT
                `o`.`id`                    AS 'orderId',
                `o`.`partner_company_id`    AS 'partnerId',
                `pcm`.`name`                AS 'partnerName',
                `o`.`total_quantity`        AS 'totalSets',
                `o`.`order_date`            AS 'orderDate',
                `o`.`deadline`,
                `o`.`etd`,
                `o`.`state_code`            AS 'stepCode',
                `cc`.`name`                 AS 'step'
            FROM `OUTBOUND` `o`
            JOIN `PARTNER_COMPANY_MASTER` `pcm`
                ON(`o`.`partner_company_id` = `pcm`.`id`)
            JOIN `COMMON_CODE` `cc`
                ON(`o`.`state_code` = `cc`.`id`)
            WHERE `o`.`state_code` IN (9, 19, 20)
        """ +
        "<if test='orderStart != null and orderStart != \"\" and orderEnd != null and orderEnd != \"\"'>" +
        " AND `o`.`order_date` BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(#{orderEnd}, '%Y-%m-%d') " +
        "</if>" +
        "<if test='orderId != null and orderId != 0'>" +
        " AND `o`.`id` LIKE CONCAT('%', #{orderId}, '%') " +
        "</if>" +
        "ORDER BY `o`.`id` DESC LIMIT #{offset}, #{size} " +
        "</script>")
    public List<PackingDTO> findAll(PackingRequestDTO packingRequestDTO);

    // 주문 단건 조회
    @Select("""
            SELECT
                `o`.`id`                    AS 'orderId',
                `o`.`partner_company_id`    AS 'partnerId',
                `pcm`.`name`                AS 'partnerName',
                `o`.`total_quantity`        AS 'totalSets',
                `o`.`order_date`            AS 'orderDate',
                `o`.`deadline`,
                `o`.`etd`,
                `o`.`state_code`            AS 'stepCode',
                `cc`.`name`                 AS 'step'
            FROM `OUTBOUND` `o`
            JOIN `PARTNER_COMPANY_MASTER` `pcm`
                ON(`o`.`partner_company_id` = `pcm`.`id`)
            JOIN `COMMON_CODE` `cc`
                ON(`o`.`state_code` = `cc`.`id`)
            WHERE `o`.`id` = #{orderId}
        """)
    public PackingDTO findOne(int orderId);

    // 주문 상품 ORDER_PRODUCT 목록 조회
    @Select("""
            SELECT
                `o`.id	                    AS 'orderProductNo',
                `o`.outbound_product_id     AS 'productId',
                `op`.`name`                 AS 'productName',
                `o`.`quantity`,
                `o`.`price`,
                `o`.`total_price`           AS 'totalPrice'
            FROM `ORDER_PRODUCT` o
            JOIN `OUTBOUND_PRODUCT_MASTER` op
                ON(`o`.`outbound_product_id` = `op`.`id`)
            WHERE `o`.`outbound_id` = #{orderId}
        """)
    public List<PackingOrderProductDTO> findOrderProduct(int orderId);

    // 협력사(운송사) 목록
    @Select("""
            SELECT
                `id` AS 'id',
                `name` AS 'name'
            FROM `PARTNER_COMPANY_MASTER`
            WHERE `carrier_yn_code` = 1
            """)
    public List<PackingCarrierDTO> findCarrierCompany();

    // 주문 상태가 패킹중(19), 패킹완료(20) 이면 패킹 테이블에서 패킹 정보 불러오기
    @Select("""
            SELECT
                `p`.`id`,
                `p`.`outbound_id`					AS 'orderId',
                `p`.`packing_invoice_number`	    AS 'packingInvoiceNumber',
                `p`.`outbound_product_id`		    AS 'productId',
                `opm`.`name`						AS 'productName',
                `p`.`partner_company_id`		    AS 'carrierId',
                `pcm`.`name`						AS 'carrierName'
                FROM `OUTBOUND_PACKING` p
                JOIN `OUTBOUND_PRODUCT_MASTER` opm
                    ON(`p`.`outbound_product_id` = `opm`.`id`)
                JOIN `PARTNER_COMPANY_MASTER` pcm
                    ON(`p`.`partner_company_id` = `pcm`.id)
                WHERE `p`.`outbound_id` = #{orderId}
            """)
    public List<PackingInvoiceDTO> findInvoice(int orderId);
                

}
