package cloud.weareithero.api.outbound;

public interface OutboundResponseExamples {

    final String FIND_ALL_DEFAULT = """
        {
          "outboundId": 0,
          "orderNo": "",
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
          "orderNo": "",
          "customerName": "",
          "stateCode": 6,
          "orderStart": "2026-05-20",
          "orderEnd": "2026-06-25",
          "page": 1,
          "size": 20
        }
        """;

    final String FIND_ALL_BY_ORDER = """
        {
          "outboundId": 0,
          "orderNo": "PO-20260602",
          "customerName": "",
          "stateCode": 0,
          "orderStart": "",
          "orderEnd": "",
          "page": 1,
          "size": 20
        }
        """;

}