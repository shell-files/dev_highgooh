package cloud.weareithero.carbon.api.carbonAnomaly.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyStateUpdateDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyYesterdayDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyCountDTO;

@Mapper
public interface CarbonAnomalyMapper {

    @Select("""
            <script>
                    SELECT
                        MAX(pd.process_start) AS process_start,
                        pm.process,
                        pm.proper_direct_emission * 24 AS proper_direct_emission,
                        COALESCE(SUM(pce.direct_emission), 0) AS sum_direct_emission,
                        pm.proper_indirect_emission * 24 AS proper_indirect_emission,
                        COALESCE(SUM(pce.indirect_emission), 0) AS sum_indirect_emission,
                        pm.proper_electricity_used * 24 AS proper_electricity_used,
                        COALESCE(SUM(pce.electricity_used), 0) AS sum_electricity_used,
                        COUNT(al.id) AS total_anomaly_count,
                        COUNT(CASE WHEN al.state_code != 17 THEN 1 END) AS unactioned_anomaly_count
                    FROM PROCESS_MASTER pm
                    LEFT JOIN PRODUCTION_DETAIL pd
                        ON pd.process_id = pm.id
                       AND pd.process_start LIKE CONCAT('%', #{yesterdayStr}, '%')
                    LEFT JOIN PRODUCT_CARBON_EMISSION pce
                        ON pd.id = pce.production_detail_id
                    LEFT JOIN ANOMALY_LOG al
                        ON al.product_carbon_emission_id = pce.id
                    GROUP BY
                        pm.id,
                        pm.process,
                        pm.proper_direct_emission,
                        pm.proper_indirect_emission
            </script>
                    """)
    List<CarbonAnomalyYesterdayDTO> getYesterdayPipeline(@Param("yesterdayStr") String yesterdayStr);

    @Select("""
    <script>
        <![CDATA[
            SELECT
                al.id, pm.process, al.anomaly_score AS anomaly_score,
                cc.name AS state, al.create_at AS create_at,
                pce.direct_emission AS direct_emission,
                pm.proper_direct_emission AS proper_direct_emission,
                pce.electricity_used AS electricity_used,
                pm.proper_electricity_used AS proper_electricity_used,
                pce.indirect_emission AS indirect_emission,
                pm.proper_indirect_emission AS proper_indirect_emission
            FROM ANOMALY_LOG al
            JOIN PRODUCT_CARBON_EMISSION pce ON al.product_carbon_emission_id = pce.id
            JOIN PRODUCTION_DETAIL pd ON pce.production_detail_id = pd.id
            JOIN PROCESS_MASTER pm ON pd.process_id = pm.id
            JOIN COMMON_CODE cc ON cc.id = al.state_code
            WHERE 1 = 1
        ]]>
        <if test="dto.year != null and dto.year != ''">
            AND YEAR(pd.process_start) = #{dto.year}
        </if>
        <if test="dto.month != null and dto.month != ''">
            AND MONTH(pd.process_start) = #{dto.month}
        </if>
        <if test="dto.quarter != null and dto.quarter != ''">
            AND QUARTER(pd.process_start) = #{dto.quarter}
        </if>
        <![CDATA[
            ORDER BY al.create_at DESC
            LIMIT #{dto.limit} OFFSET #{dto.offset}
        ]]>
    </script>
    """)
    List<CarbonAnomalyDTO> selectAnomalyList(@Param("dto") CarbonAnomalyRequestDTO dto);

    @Select("""
            <script>
                <![CDATA[
                    SELECT
                        pm.process AS process,
                        SUM(CASE WHEN cc.name = '조치완료' THEN 1 ELSE 0 END) +
                        SUM(CASE WHEN cc.name = '조치중' THEN 1 ELSE 0 END) +
                        SUM(CASE WHEN cc.name = '조치대기' THEN 1 ELSE 0 END) AS anomaly_count,
                        SUM(CASE WHEN cc.name = '조치완료' THEN 1 ELSE 0 END) AS actioned_count,
                        SUM(CASE WHEN cc.name = '조치중' THEN 1 ELSE 0 END) AS actioning_count,
                        SUM(CASE WHEN cc.name = '조치대기' THEN 1 ELSE 0 END) AS waiting_count
                    FROM ANOMALY_LOG al
                    JOIN PRODUCT_CARBON_EMISSION pce ON al.product_carbon_emission_id = pce.id
                    JOIN PRODUCTION_DETAIL pd ON pce.production_detail_id = pd.id
                    JOIN PROCESS_MASTER pm ON pd.process_id = pm.id
                    JOIN COMMON_CODE cc ON al.state_code = cc.id
                    WHERE 1 = 1

                ]]>
                <if test="dto.year != null and dto.year != ''">
                    AND YEAR(pd.process_start) = #{dto.year}
                </if>
                <if test="dto.month != null and dto.month != ''">
                    AND MONTH(pd.process_start) = #{dto.month}
                </if>
                <if test="dto.quarter != null and dto.quarter != ''">
                    AND QUARTER(pd.process_start) = #{dto.quarter}
                </if>
                <![CDATA[
                    GROUP BY pm.id, pm.process
                    ORDER BY pm.id ASC
                ]]>
            </script>
            """)

    List<CarbonAnomalyCountDTO> selectAnomalyCount(@Param("dto") CarbonAnomalyRequestDTO dto);

    @Update("""
                UPDATE ANOMALY_LOG
                SET state_code = #{dto.state_code}
                WHERE id = #{dto.id}
            """)
    int updateAnomalyStatus(@Param("dto") CarbonAnomalyStateUpdateDTO dto);

}