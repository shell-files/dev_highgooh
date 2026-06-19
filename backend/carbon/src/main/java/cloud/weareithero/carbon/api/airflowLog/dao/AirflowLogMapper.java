package cloud.weareithero.carbon.api.airflowLog.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cloud.weareithero.carbon.api.airflowLog.dto.AirflowLogDTO;

@Mapper
public interface AirflowLogMapper {

    @Select("""
            SELECT al.id, al.target_day, al.summary, al.reasoning, al.recommendation, al.create_at
            FROM AIRFLOW_JOB AS aj
            INNER JOIN AIRFLOW_LOG AS al
            ON (aj.airflow_log_id = al.id AND aj.delete_yn = 1)
            WHERE aj.target_day = #{targetDay}
            ORDER BY al.target_day
            """)
    List<AirflowLogDTO> selectByTargetDay(@Param("targetDay") String targetDay);
}