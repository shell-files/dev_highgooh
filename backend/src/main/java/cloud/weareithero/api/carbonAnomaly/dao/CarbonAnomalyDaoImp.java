package cloud.weareithero.api.carbonAnomaly.dao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Repository;

import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyCountDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyYesterdayDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CarbonAnomalyDaoImp implements CarbonAnomalyDao {

    private final CarbonAnomalyMapper carbonAnomalyMapper;

    @Override
    public List<CarbonAnomalyYesterdayDTO> getYesterdayPipeline() {
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        return carbonAnomalyMapper.getYesterdayPipeline(yesterdayStr);
    }

    @Override
    public List<CarbonAnomalyDTO> selectAnomalyList(CarbonAnomalyRequestDTO dto) {
        return carbonAnomalyMapper.selectAnomalyList(dto);
    }

    @Override
    public List<CarbonAnomalyCountDTO> selectAnomalyCount(CarbonAnomalyRequestDTO dto) {
        // Mapper의 selectAnomalyStats를 호출하여 반환
        return carbonAnomalyMapper.selectAnomalyCount(dto);
    }
}