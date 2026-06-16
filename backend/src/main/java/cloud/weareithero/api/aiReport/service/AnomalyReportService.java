package cloud.weareithero.api.aiReport.service;

import cloud.weareithero.dto.ResponseDTO;

public interface AnomalyReportService {

    ResponseDTO getAiReport(Long id);
}