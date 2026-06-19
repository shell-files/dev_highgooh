package cloud.weareithero.outbound.api.outboundHistory.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cloud.weareithero.outbound.api.outboundHistory.dto.OutboundHistoryDashboardStatsDTO;
import cloud.weareithero.outbound.api.outboundHistory.dto.OutboundHistoryRequestDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OutboundHistoryDaoImp implements OutboundHistoryDao {
    private final OutboundHistoryMapper outboundHistoryMapper;

    @Override
    public OutboundHistoryDashboardStatsDTO getDashboardStats(OutboundHistoryRequestDTO requestDTO) {
        return outboundHistoryMapper.getSummaryStats(requestDTO);
    }
    
    @Override
    public List<Map<String, Object>> getHistoryList(OutboundHistoryRequestDTO requestDTO) {
        return outboundHistoryMapper.getHistoryList(requestDTO);
    }

    @Override
    public long getHistoryListCount(OutboundHistoryRequestDTO requestDTO) {
        return outboundHistoryMapper.getHistoryListCount(requestDTO);
    }

    @Override
    public List<Map<String, Object>> getOutboundDetailList(Integer outboundId) {
        return outboundHistoryMapper.getOutboundDetailList(outboundId);
    }
    @Override
    public List<Map<String, Object>> getPartnerCompanies() {
      return outboundHistoryMapper.getPartnerCompanies();
  }
}