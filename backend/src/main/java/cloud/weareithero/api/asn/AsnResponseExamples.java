package cloud.weareithero.api.asn;

public interface AsnResponseExamples {

  final String SUCCESS1_DEFAULT = """
      {
        "asnId": 0,
        "orderStart": "",
        "orderEnd": "",
        "page": 1,
        "size": 20
      }
      """;
  final String SUCCESS2_DEFAULT = """
      {
        "asnId": 1,
        "orderStart": "",
        "orderEnd": "",
        "page": 1,
        "size": 20
      }
      """;
  final String SUCCESS3_DEFAULT = """
      {
        "asnId": 0,
        "orderStart": "2026-05-20",
        "orderEnd": "2026-06-25",
        "page": 1,
        "size": 20
      }
      """;
  final String SUCCESS4_DEFAULT = """
      {
        "asnId": 1,
        "orderStart": "2026-05-20",
        "orderEnd": "2026-06-25",
        "page": 1,
        "size": 20
      }
      """;

}
