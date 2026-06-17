package cloud.weareithero.api.airflowLog.dao;

import java.util.List;

import cloud.weareithero.api.airflowLog.dto.AirflowLogDTO;

public interface AirflowLogDao {

    List<AirflowLogDTO> selectByTargetDay(String targetDay);
}