package cloud.weareithero.api.order;

public interface OrderResponseExamples {

  // 1. 전체 조회
  final String SUCCESS1_DEFAULT = """
    {
      "outboundId": "",
      "orderStart": "",
      "orderEnd": "",
      "customerName": "",
      "status": "",
      "page": 1,
      "size": 20
    }
    """;

  // 2. 주문번호(outboundId) 단건 검색
  final String SUCCESS2_DEFAULT = """
    {
      "outboundId": "1",
      "orderStart": "",
      "orderEnd": "",
      "customerName": "",
      "status": "",
      "page": 1,
      "size": 20
    }
    """;

  // 3. 날짜 범위 검색
  final String SUCCESS3_DEFAULT = """
    {
      "outboundId": "",
      "orderStart": "2026-05-01",
      "orderEnd": "2026-06-30",
      "customerName": "",
      "status": "",
      "page": 1,
      "size": 20
    }
    """;

  // 4. 날짜 + 고객사명 복합 검색
  final String SUCCESS4_DEFAULT = """
    {
      "outboundId": "",
      "orderStart": "2026-05-01",
      "orderEnd": "2026-06-30",
      "customerName": "한성",
      "status": "",
      "page": 1,
      "size": 20
    }
    """;

  // 5. 진행상태 검색
  final String SUCCESS5_DEFAULT = """
    {
      "outboundId": "",
      "orderStart": "",
      "orderEnd": "",
      "customerName": "",
      "status": "신규",
      "page": 1,
      "size": 20
    }
    """;

}