package cloud.weareithero.carbon.api.carbonAnomaly;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyStateUpdateDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.service.CarbonAnomalyService;
import cloud.weareithero.carbon.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CarbonAnomalyController
        implements CarbonAnomalyControllerDocs {

    private final CarbonAnomalyService carbonAnomalyService;

    @PreAuthorize("isAuthenticated()")
    @Override
    @PostMapping("/anomaly/yesterday")
    public ResponseDTO getYesterdayPipeline() {

        return carbonAnomalyService.getYesterdayPipeline();
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    @PostMapping("/anomaly")
    public ResponseDTO getAnomalyList(
            @RequestBody CarbonAnomalyRequestDTO dto) {

        return carbonAnomalyService.getAnomalyList(dto);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/anomaly")
    public ResponseDTO updateAnomalyStatus(@RequestBody CarbonAnomalyStateUpdateDTO dto) {
        log.info("이상치 상태 변경 요청: {}", dto);
        return carbonAnomalyService.updateAnomalyStatus(dto);
    }
    
}