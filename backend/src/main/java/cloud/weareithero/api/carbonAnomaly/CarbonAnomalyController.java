package cloud.weareithero.api.carbonAnomaly;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.api.carbonAnomaly.service.CarbonAnomalyService;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CarbonAnomalyController
        implements CarbonAnomalyControllerDocs {

    private final CarbonAnomalyService carbonAnomalyService;

    @Override
    @PostMapping("/anomaly/yesterday")
    public ResponseDTO getYesterdayPipeline() {

        return carbonAnomalyService.getYesterdayPipeline();
    }

    @Override
    @PostMapping("/anomaly")
    public ResponseDTO getAnomalyList(
            @RequestBody CarbonAnomalyRequestDTO dto) {

        return carbonAnomalyService.getAnomalyList(dto);
    }
}