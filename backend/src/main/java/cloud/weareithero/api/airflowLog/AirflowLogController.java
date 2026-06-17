package cloud.weareithero.api.airflowLog;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.airflowLog.dto.AirflowLogRequestDTO;
import cloud.weareithero.api.airflowLog.service.AirflowLogService;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AirflowLogController implements AirflowLogControllerDocs {

    private final AirflowLogService airflowLogService;

    @Override
    @PostMapping("/airflow")
    public ResponseDTO getAirflowLog(@RequestBody AirflowLogRequestDTO dto) {

        return airflowLogService.getAirflowLog(dto);
    }
}