package cloud.weareithero.api.packing.dao;

import java.util.List;

import cloud.weareithero.api.packing.dto.PackingDTO;
import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.api.packing.dto.PackingSummaryDTO;

public interface PackingDao {
    PackingSummaryDTO findSummary(PackingRequestDTO packingRequestDTO);
    List<PackingDTO> findAll(PackingRequestDTO packingRequestDTO);
}
