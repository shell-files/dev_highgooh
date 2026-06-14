package cloud.weareithero.api.outboundHistory.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import cloud.weareithero.api.outboundHistory.dto.OutboundHistoryDashboardStatsDTO;
import cloud.weareithero.api.outboundHistory.dto.OutboundHistoryRequestDTO;

@Mapper
public interface OutboundHistoryMapper {

  /**
   * 1. 요약 카드용 통계 계산 (검색 필터 동적 연동)
   */
  @Select("""
    <script>
        WITH total_completed AS (
            SELECT P.outbound_id
            FROM OUTBOUND_PACKING AS P
            INNER JOIN OUTBOUND AS O ON P.outbound_id = O.id
            <where>
                <if test='orderStart != null and orderStart != "" and orderEnd != null and orderEnd != ""'>
                    AND P.updated_at BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(CONCAT(#{orderEnd}, ' 23:59:59'), '%Y-%m-%d %H:%i:%s')
                </if>
                <if test='outboundId != null and outboundId != 0'>
                    AND P.outbound_id = #{outboundId}
                </if>
                <if test='partnerCompanyId != null and partnerCompanyId != 0'>
                    AND O.partner_company_id = #{partnerCompanyId}
                </if>
                <if test='transportCompanyId != null and transportCompanyId != 0'>
                    AND P.partner_company_id = #{transportCompanyId}
                </if>
            </where>
            GROUP BY P.outbound_id
            HAVING COUNT(*) = COUNT(CASE WHEN P.state_code = 23 THEN 1 END)
        ),
        on_time_completed AS (
            SELECT P.outbound_id
            FROM OUTBOUND_PACKING AS P
            INNER JOIN OUTBOUND AS O ON P.outbound_id = O.id
            <where>
                <if test='orderStart != null and orderStart != "" and orderEnd != null and orderEnd != ""'>
                    AND P.updated_at BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(CONCAT(#{orderEnd}, ' 23:59:59'), '%Y-%m-%d %H:%i:%s')
                </if>
                <if test='outboundId != null and outboundId != 0'>
                    AND P.outbound_id = #{outboundId}
                </if>
                <if test='partnerCompanyId != null and partnerCompanyId != 0'>
                    AND O.partner_company_id = #{partnerCompanyId}
                </if>
                <if test='transportCompanyId != null and transportCompanyId != 0'>
                    AND P.partner_company_id = #{transportCompanyId}
                </if>
            </where>
            GROUP BY P.outbound_id
            HAVING COUNT(*) = COUNT(CASE WHEN P.state_code = 23 AND P.updated_at &lt;= O.etd THEN 1 END)
        )
        SELECT 
            (SELECT COUNT(*) FROM total_completed) AS totalCompletedCount,
            (SELECT COUNT(*) FROM on_time_completed) AS onTimeCompletedCount,
            ((SELECT COUNT(*) FROM total_completed) - (SELECT COUNT(*) FROM on_time_completed)) AS delayedCompletedCount
    </script>
  """)
  OutboundHistoryDashboardStatsDTO getSummaryStats(OutboundHistoryRequestDTO requestDTO);

  /**
   * 2. 하단 리스트 뷰 페이징 및 동적 조회용 데이터 쿼리
   */
  @Select("""
    <script>
        SELECT 
            MAX(o.updated_at) AS updated_at, 
            o.outbound_id, 
            b.partner, 
            p.`name` AS trans, 
            COUNT(o.outbound_id) AS cnt,
            CASE 
                WHEN MAX(o.updated_at) &lt;= MAX(b.etd) THEN '기한달성'
                ELSE '기한초과'
            END AS delivery_status
        FROM OUTBOUND_PACKING AS o
        INNER JOIN (
            SELECT 
                b.id AS outbound_id, 
                b.etd,
                b.partner_company_id,
                p.`name` AS partner 
            FROM OUTBOUND AS b
            INNER JOIN PARTNER_COMPANY_MASTER AS p ON b.partner_company_id = p.id
        ) AS b ON o.outbound_id = b.outbound_id
        INNER JOIN PARTNER_COMPANY_MASTER AS p ON o.partner_company_id = p.id
        <where>
            <if test='orderStart != null and orderStart != "" and orderEnd != null and orderEnd != ""'>
                AND o.updated_at BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(CONCAT(#{orderEnd}, ' 23:59:59'), '%Y-%m-%d %H:%i:%s')
            </if>
            <if test='outboundId != null and outboundId != 0'>
                AND o.outbound_id = #{outboundId}
            </if>
            <if test='partnerCompanyId != null and partnerCompanyId != 0'>
                AND b.partner_company_id = #{partnerCompanyId}
            </if>
            <if test='transportCompanyId != null and transportCompanyId != 0'>
                AND o.partner_company_id = #{transportCompanyId}
            </if>
            <if test='partnerCompanyId != null and partnerCompanyId != 0'>
                AND b.partner_company_id = #{partnerCompanyId}
            </if>
            <if test='transportCompanyId != null and transportCompanyId != 0'>
                AND o.partner_company_id = #{transportCompanyId}
            </if>
        </where>
        GROUP BY o.outbound_id
        HAVING COUNT(*) = COUNT(CASE WHEN o.state_code = 23 THEN 1 END)
        ORDER BY updated_at DESC
        LIMIT #{skip}, #{size}
    </script>
  """)
  List<Map<String, Object>> getHistoryList(OutboundHistoryRequestDTO requestDTO);

  /**
   * 3. 현재 필터링 조건 하에서의 총 목록 개수 카운트 (페이징 연산 원천용)
   */
  @Select("""
    <script>
        SELECT COUNT(*) FROM (
            SELECT o.outbound_id
            FROM OUTBOUND_PACKING AS o
            INNER JOIN OUTBOUND AS b ON o.outbound_id = b.id
            <where>
                <if test='orderStart != null and orderStart != "" and orderEnd != null and orderEnd != ""'>
                    AND o.updated_at BETWEEN STR_TO_DATE(#{orderStart}, '%Y-%m-%d') AND STR_TO_DATE(CONCAT(#{orderEnd}, ' 23:59:59'), '%Y-%m-%d %H:%i:%s')
                </if>
                <if test='outboundId != null and outboundId != 0'>
                    AND o.outbound_id = #{outboundId}
                </if>
                <if test='partnerCompanyId != null and partnerCompanyId != 0'>
                    AND b.partner_company_id = #{partnerCompanyId}
                </if>
                <if test='transportCompanyId != null and transportCompanyId != 0'>
                    AND o.partner_company_id = #{transportCompanyId}
                </if>
                <if test='partnerCompanyId != null and partnerCompanyId != 0'>
                    AND b.partner_company_id = #{partnerCompanyId}
                </if>
                <if test='transportCompanyId != null and transportCompanyId != 0'>
                    AND o.partner_company_id = #{transportCompanyId}
                </if>
            </where>
            GROUP BY o.outbound_id
            HAVING COUNT(*) = COUNT(CASE WHEN o.state_code = 23 THEN 1 END)
        ) AS temp_count
    </script>
  """)
  long getHistoryListCount(OutboundHistoryRequestDTO requestDTO);

  @Select("SELECT id, `name`, customer_yn_code, carrier_yn_code FROM PARTNER_COMPANY_MASTER")
    List<Map<String, Object>> getPartnerCompanies();

  @Select("""
    SELECT 
        p.outbound_id,
        p.packing_invoice_number,
        m.`name`, 
        p.invoice_number, 
        CASE 
            WHEN MAX(p.updated_at) <= MAX(b.etd) THEN '기한달성'
            ELSE '기한초과'
        END AS delivery_status
    FROM OUTBOUND_PACKING AS p
    INNER JOIN OUTBOUND_PRODUCT_MASTER AS m ON p.outbound_product_id = m.id
    INNER JOIN OUTBOUND AS b ON p.outbound_id = b.id
    WHERE p.outbound_id = #{outboundId}
    GROUP BY p.packing_invoice_number, m.`name`, p.invoice_number, p.outbound_id
""")
    List<Map<String, Object>> getOutboundDetailList(Integer outboundId);
}