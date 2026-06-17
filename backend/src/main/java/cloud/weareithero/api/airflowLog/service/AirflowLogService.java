package cloud.weareithero.api.airflowLog.service;

import cloud.weareithero.api.airflowLog.dto.AirflowLogRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface AirflowLogService {

    ResponseDTO getAirflowLog(AirflowLogRequestDTO dto);
}