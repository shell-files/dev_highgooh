package cloud.weareithero.api.outboundHistory.service;

import cloud.weareithero.api.outboundHistory.dto.OutboundHistoryRequestDTO;
import cloud.weareithero.dto.ResponseDTO;

public interface OutboundHistoryService {
  public ResponseDTO getDashboardStats(OutboundHistoryRequestDTO requestDTO);
  public ResponseDTO getOutboundDetail(Integer outboundId);
  public ResponseDTO getPartnerCompanies();
}