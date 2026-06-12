package cloud.weareithero.api.carbonAnomaly.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;

@Mapper
public interface CarbonAnomalyMapper {

    @Select("""
        SELECT
            al.id,
            pm.process,
            al.anomaly_score AS anomalyScore,
            cc.name AS state,
            al.create_at AS createAt,
            pce.direct_emission AS directEmission,
            pm.proper_direct_emission AS properDirectEmission,
            pce.electricity_used AS electricityUsed,
            pm.proper_electricity_used AS properElectricityUsed,
            pce.indirect_emission AS indirectEmission,
            pm.proper_indirect_emission AS properIndirectEmission
        FROM ANOMALY_LOG al
        JOIN PRODUCT_CARBON_EMISSION pce ON al.product_carbon_emission_id = pce.id
        JOIN PRODUCTION_DETAIL pd ON pce.production_detail_id = pd.id
        JOIN PROCESS_MASTER pm ON pd.process_id = pm.id
        JOIN COMMON_CODE cc ON cc.id = al.state_code
        WHERE al.create_at >= DATE_SUB(CURDATE(), INTERVAL 1 DAY)
          AND al.create_at < CURDATE()
        ORDER BY al.anomaly_score DESC
        """)
    List<CarbonAnomalyDTO> selectYesterdayPipeline();


    @Select("""
<script>
        SELECT
            al.id,
            pm.process,
            al.anomaly_score AS anomaly_score,
            cc.name AS state,
            al.create_at AS create_at,
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
          AND pd.process_start <![CDATA[ >= ]]> #{dto.calculatedStartDate}
          AND pd.process_start <![CDATA[ < ]]> #{dto.calculatedEndDate}
        ORDER BY al.create_at DESC
        LIMIT #{dto.limit} OFFSET #{dto.offset}
</script>
        """)
    List<CarbonAnomalyDTO> selectAnomalyList(@Param("dto") CarbonAnomalyRequestDTO dto);
}