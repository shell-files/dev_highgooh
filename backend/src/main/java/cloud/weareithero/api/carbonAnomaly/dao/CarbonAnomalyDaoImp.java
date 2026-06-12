package cloud.weareithero.api.carbonAnomaly.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyDTO;
import cloud.weareithero.api.carbonAnomaly.dto.CarbonAnomalyRequestDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CarbonAnomalyDaoImp implements CarbonAnomalyDao {

    private final CarbonAnomalyMapper carbonAnomalyMapper;

    @Override
    public List<CarbonAnomalyDTO> selectYesterdayPipeline() {
        return carbonAnomalyMapper.selectYesterdayPipeline();
    }

    @Override
    public List<CarbonAnomalyDTO> selectAnomalyList(
            CarbonAnomalyRequestDTO dto) {

        return carbonAnomalyMapper.selectAnomalyList(dto);
    }
}