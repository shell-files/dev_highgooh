package cloud.weareithero.carbon.api.airflowLog.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import cloud.weareithero.carbon.api.airflowLog.dao.AirflowLogDao;
import cloud.weareithero.carbon.api.airflowLog.dto.AirflowLogDTO;
import cloud.weareithero.carbon.api.airflowLog.dto.AirflowLogRequestDTO;
import cloud.weareithero.carbon.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirflowLogServiceImp implements AirflowLogService {

    private final AirflowLogDao airflowLogDao;

    @Override
    public ResponseDTO getAirflowLog(AirflowLogRequestDTO dto) {
        boolean isSuccess = false;
        String message = "데이터 조회가 완료되었습니다.";
        List<AirflowLogDTO> detailList;

        try {
            String targetDay = dto.getTargetDay();
            if (targetDay == null || targetDay.isEmpty()) {
                targetDay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            detailList = airflowLogDao.selectByTargetDay(targetDay);
            isSuccess = true;
        } catch (Exception e) {
            log.error("AirflowLogServiceImp getAirflowLog error", e);
            message = "데이터 조회 중 오류가 발생했습니다.";
            detailList = null;
        }

        return ResponseDTO.builder()
                .status(isSuccess)
                .data(detailList)
                .message(message)
                .build();
    }
}