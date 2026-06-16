package cloud.weareithero.api.aiReport.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import cloud.weareithero.api.aiReport.dao.AnomalyReportMapper;
import cloud.weareithero.api.aiReport.dto.AnomalyReportContextDTO;
import cloud.weareithero.api.aiReport.dto.AnomalyReportResponseDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnomalyReportServiceImp implements AnomalyReportService {

    private final AnomalyReportMapper anomalyReportMapper;

    @Override
    public ResponseDTO getAiReport(Long id) {

        try {

            CarbonAnomalyDTO anomalyData = null;

            if (anomalyData == null) {

                return ResponseDTO.builder()
                        .status(false)
                        .message("AI 분석 대상 데이터를 찾을 수 없습니다.")
                        .build();
            }

            AnomalyReportContextDTO context = AnomalyReportContextDTO.builder().build();

            // TODO Ollama 연동
            // context 정보를 이용하여 LLM 분석 수행

            AnomalyReportResponseDTO response =
                    AnomalyReportResponseDTO.builder()
                            .anomalyScore(context.getAnomalyScore())
                            .summary(
                                    "기준 대비 전력 사용량 및 탄소 배출량이 증가한 이상치가 감지되었습니다.")
                            .estimatedCause(
                                    "설비 효율 저하 또는 센서 측정 오류 가능성이 있습니다.")
                            .impactScope(
                                    "해당 공정의 탄소 배출 KPI에 영향을 줄 수 있습니다.")
                            .recommendation(
                                    "설비 점검 및 데이터 계측 장비 검증이 필요합니다.")
                            .generatedAt(LocalDateTime.now())
                            .build();

            return ResponseDTO.builder()
                    .status(true)
                    .data(response)
                    .message("AI 분석 리포트 조회가 완료되었습니다.")
                    .build();

        } catch (Exception e) {

            log.error(
                    "AnomalyReportServiceImpl getAiReport error",
                    e);

            return ResponseDTO.builder()
                    .status(false)
                    .message("AI 리포트 생성 중 오류가 발생했습니다.")
                    .build();
        }
    }

    private AnomalyReportContextDTO toContextDTO(
            CarbonAnomalyDTO dto) {

        return AnomalyReportContextDTO.builder()
                .anomalyLogId(dto.getId())
                .process(dto.getProcess())
                .anomalyScore(dto.getAnomaly_score())
                .state(dto.getState())
                .detectedAt(dto.getCreate_at())
                .directEmission(dto.getDirect_emission())
                .properDirectEmission(dto.getProper_direct_emission())
                .indirectEmission(dto.getIndirect_emission())
                .properIndirectEmission(dto.getProper_indirect_emission())
                .electricityUsed(dto.getElectricity_used())
                .properElectricityUsed(dto.getProper_electricity_used())
                .build();
    }
}