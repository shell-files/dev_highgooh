package cloud.weareithero.outbound.api.outboundHistory;

public interface OutboundHistoryResponseExamples {
  final String DASHBOARD_REQ_DEFAULT = """
      {
        "outboundId": 0,
        "orderStart": "",
        "orderEnd": "",
        "partnerCompanyId": 0,
        "transportCompanyId": 0,
        "page": 1,
        "size": 20
      }
      """;
}