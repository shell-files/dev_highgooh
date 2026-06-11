package cloud.weareithero.api.packing.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.packing.dto.PackingDTO;
import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.api.packing.dto.PackingSummaryDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PackingDaoImp implements PackingDao {

    private final PackingMapper packingMapper;

    @Override
    public PackingSummaryDTO findSummary(PackingRequestDTO packingRequestDTO) {
        return packingMapper.findSummary(packingRequestDTO);
    }

    @Override
    public List<PackingDTO> findAll(PackingRequestDTO packingRequestDTO) {
        return packingMapper.findAll(packingRequestDTO);
    }
    
    
}
