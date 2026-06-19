package cloud.weareithero.outbound.api.outbound;

public interface OutboundResponseExamples {

    final String FIND_ALL_DEFAULT = """
        {
          "outboundId": 0,
          "customerName": "",
          "stateCode": 0,
          "orderStart": "",
          "orderEnd": "",
          "page": 1,
          "size": 20
        }
        """;

    final String FIND_ALL_FILTER = """
        {
          "outboundId": 0,
          "customerName": "",
          "stateCode": 1,
          "orderStart": "2026-06-01",
          "orderEnd": "2026-06-30",
          "page": 1,
          "size": 20
        }
        """;

    final String FIND_ALL_BY_OUTBOUND = """
        {
          "outboundId": 1,
          "customerName": "",
          "stateCode": 0,
          "orderStart": "",
          "orderEnd": "",
          "page": 1,
          "size": 20
        }
        """;

}