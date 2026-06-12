package cloud.weareithero.api.carbonEmission.dao;

import cloud.weareithero.api.carbonEmission.dto.CarbonEmissionDTO;
import cloud.weareithero.api.carbonEmission.dto.CarbonEmissionRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CarbonEmissionDaoImp implements CarbonEmissionDao {
    private final CarbonEmissionMapper carbonEmissionMapper;

    @Override
    public List<CarbonEmissionDTO> selectDailyEmission(CarbonEmissionRequestDTO dto) { return carbonEmissionMapper.selectEmissionMonth(dto); }
    @Override
    public List<CarbonEmissionDTO> selectMonthlyEmissionByQuarter(CarbonEmissionRequestDTO dto) { return carbonEmissionMapper.selectEmissionQuarter(dto); }
    @Override
    public List<CarbonEmissionDTO> selectMonthlyEmissionByYear(CarbonEmissionRequestDTO dto) { return carbonEmissionMapper.selectEmissionYear(dto); }
}