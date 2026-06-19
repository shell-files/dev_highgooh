package cloud.weareithero.carbon.api.carbonEmission.dao;

import cloud.weareithero.carbon.api.carbonEmission.dto.CarbonEmissionDTO;
import cloud.weareithero.carbon.api.carbonEmission.dto.CarbonEmissionRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CarbonEmissionMapper {
    // 1. 월별 조회
    @Select("SELECT DATE_FORMAT(pd.process_start, '%Y-%m-%d') AS date, " +
            "       pm.process AS process, " +
            "       SUM(pce.indirect_emission) AS indirect_emission, " +
            "       SUM(pce.direct_emission) AS direct_emission, " +
            "       SUM(pce.total_embedded_emission) AS total_emission " +
            "FROM PRODUCT_CARBON_EMISSION pce " +
            "JOIN PRODUCTION_DETAIL pd ON pce.production_detail_id = pd.id " +
            "JOIN PROCESS_MASTER pm ON pd.process_id = pm.id " +
            "WHERE YEAR(pd.process_start) = #{year} AND MONTH(pd.process_start) = #{month} " +
            "GROUP BY DATE_FORMAT(pd.process_start, '%Y-%m-%d'), pm.process " +
            "ORDER BY `date` ASC, pd.process_id ASC")
    List<CarbonEmissionDTO> selectEmissionMonth(CarbonEmissionRequestDTO dto);

    // 2. 분기별 조회
    @Select("SELECT MONTH(pd.process_start) AS date, " +
            "       pm.process AS process, " +
            "       SUM(pce.indirect_emission) AS indirect_emission, " +
            "       SUM(pce.direct_emission) AS direct_emission, " +
            "       SUM(pce.total_embedded_emission) AS total_emission " +
            "FROM PRODUCT_CARBON_EMISSION pce " +
            "JOIN PRODUCTION_DETAIL pd ON pce.production_detail_id = pd.id " +
            "JOIN PROCESS_MASTER pm ON pd.process_id = pm.id " +
            "WHERE YEAR(pd.process_start) = #{year} AND QUARTER(pd.process_start) = #{quarter} " +
            "GROUP BY MONTH(pd.process_start), pm.process " +
            "ORDER BY pd.process_id ASC")
    List<CarbonEmissionDTO> selectEmissionQuarter(CarbonEmissionRequestDTO dto);

    // 3. 연간 조회

    @Select("<script>" +
    // 연도 선택이 없으면 YEAR(pd.process_start)를, 있으면 MONTH(pd.process_start)를 반환
            "SELECT " +
            "  <if test='year == null or year == &quot;&quot;'> YEAR(pd.process_start) AS date, </if>" +
            "  <if test='year != null and year != &quot;&quot;'> MONTH(pd.process_start) AS date, </if>" +
            "  pm.process AS process, " +
            "  SUM(pce.indirect_emission) AS indirect_emission, " +
            "  SUM(pce.direct_emission) AS direct_emission, " +
            "  SUM(pce.total_embedded_emission) AS total_emission " +
            "FROM PRODUCT_CARBON_EMISSION pce " +
            "JOIN PRODUCTION_DETAIL pd ON pce.production_detail_id = pd.id " +
            "JOIN PROCESS_MASTER pm ON pd.process_id = pm.id " +
            "<where>" +
            "  <if test='year != null and year != &quot;&quot;'>" +
            "    AND YEAR(pd.process_start) = #{year} " +
            "  </if>" +
            "</where>" +
            "GROUP BY date, pm.process " +
            "ORDER BY date ASC, pm.id ASC" +
            "</script>")
    List<CarbonEmissionDTO> selectEmissionYear(CarbonEmissionRequestDTO dto);
}