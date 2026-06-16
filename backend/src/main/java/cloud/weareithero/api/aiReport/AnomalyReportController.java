package cloud.weareithero.api.aiReport;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.aiReport.service.AnomalyReportService;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnomalyReportController {

    private final AnomalyReportService anomalyReportService;

    @GetMapping("/anomaly/{id}/ai-report")
    public ResponseDTO getAiReport(
            @PathVariable Long id) {

        return anomalyReportService.getAiReport(id);
    }
}