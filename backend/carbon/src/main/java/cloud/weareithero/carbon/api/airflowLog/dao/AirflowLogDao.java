package cloud.weareithero.carbon.api.airflowLog.dao;

import java.util.List;

import cloud.weareithero.carbon.api.airflowLog.dto.AirflowLogDTO;

public interface AirflowLogDao {

    List<AirflowLogDTO> selectByTargetDay(String targetDay);
}