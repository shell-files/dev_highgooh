// CarbonAnomalyDao.java 인터페이스 파일 수정부 예시
package cloud.weareithero.api.carbonAnomaly.dao;

import java.util.List;

import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyCountDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyStateUpdateDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyYesterdayDTO;

public interface CarbonAnomalyDao {

    List<CarbonAnomalyYesterdayDTO> getYesterdayPipeline();
    List<CarbonAnomalyDTO> selectAnomalyList(CarbonAnomalyRequestDTO dto);
    List<CarbonAnomalyCountDTO> selectAnomalyCount(CarbonAnomalyRequestDTO dto);
    int updateAnomalyStatus(CarbonAnomalyStateUpdateDTO dto);
}