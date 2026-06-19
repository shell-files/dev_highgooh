// CarbonAnomalyDao.java 인터페이스 파일 수정부 예시
package cloud.weareithero.carbon.api.carbonAnomaly.dao;

import java.util.List;

import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyCountDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyStateUpdateDTO;
import cloud.weareithero.carbon.api.carbonAnomaly.dto.CarbonAnomalyYesterdayDTO;

public interface CarbonAnomalyDao {

    List<CarbonAnomalyYesterdayDTO> getYesterdayPipeline();
    List<CarbonAnomalyDTO> selectAnomalyList(CarbonAnomalyRequestDTO dto);
    List<CarbonAnomalyCountDTO> selectAnomalyCount(CarbonAnomalyRequestDTO dto);
    int updateAnomalyStatus(CarbonAnomalyStateUpdateDTO dto);
}