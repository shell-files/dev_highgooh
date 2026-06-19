package cloud.weareithero.carbon.api.airflowLog;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.carbon.api.airflowLog.dto.AirflowLogRequestDTO;
import cloud.weareithero.carbon.api.airflowLog.service.AirflowLogService;
import cloud.weareithero.carbon.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AirflowLogController implements AirflowLogControllerDocs {

    private final AirflowLogService airflowLogService;

    @PreAuthorize("isAuthenticated()")
    @Override
    @PostMapping("/airflow")
    public ResponseDTO getAirflowLog(@RequestBody AirflowLogRequestDTO dto) {

        return airflowLogService.getAirflowLog(dto);
    }
}