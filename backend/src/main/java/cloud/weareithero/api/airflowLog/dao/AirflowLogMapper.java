package cloud.weareithero.api.airflowLog.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cloud.weareithero.api.airflowLog.dto.AirflowLogDTO;

@Mapper
public interface AirflowLogMapper {

    @Select("""
            SELECT id, target_day, summary, reasoning, recommendation, create_at
            FROM AIRFLOW_LOG
            WHERE target_day = #{targetDay}
            ORDER BY target_day
            """)
    List<AirflowLogDTO> selectByTargetDay(@Param("targetDay") String targetDay);
}