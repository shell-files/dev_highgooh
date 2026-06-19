package cloud.weareithero.outbound.api.outboundHistory.dao;

import java.util.List;
import java.util.Map;

import cloud.weareithero.outbound.api.outboundHistory.dto.OutboundHistoryDashboardStatsDTO;
import cloud.weareithero.outbound.api.outboundHistory.dto.OutboundHistoryRequestDTO;

public interface OutboundHistoryDao {
  public OutboundHistoryDashboardStatsDTO getDashboardStats(OutboundHistoryRequestDTO requestDTO);
  public List<Map<String, Object>> getOutboundDetailList(Integer outboundId);
  public List<Map<String, Object>> getHistoryList(OutboundHistoryRequestDTO requestDTO);
  public long getHistoryListCount(OutboundHistoryRequestDTO requestDTO);
  public List<Map<String, Object>> getPartnerCompanies();
}