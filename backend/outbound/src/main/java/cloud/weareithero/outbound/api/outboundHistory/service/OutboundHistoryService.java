package cloud.weareithero.outbound.api.outboundHistory.service;

import cloud.weareithero.outbound.api.outboundHistory.dto.OutboundHistoryRequestDTO;
import cloud.weareithero.outbound.dto.ResponseDTO;

public interface OutboundHistoryService {
  public ResponseDTO getDashboardStats(OutboundHistoryRequestDTO requestDTO);
  public ResponseDTO getOutboundDetail(Integer outboundId);
  public ResponseDTO getPartnerCompanies();
}