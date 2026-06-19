package cloud.weareithero.carbon.api.airflowLog.service;

import cloud.weareithero.carbon.api.airflowLog.dto.AirflowLogRequestDTO;
import cloud.weareithero.carbon.dto.ResponseDTO;

public interface AirflowLogService {

    ResponseDTO getAirflowLog(AirflowLogRequestDTO dto);
}