package cloud.weareithero.api.packing;

public interface PackingResponseExamples {

    final String SUCCESS1_DEFAULT = """
      {
        "orderId": 0,
        "orderStart": "",
        "orderEnd": "",
        "partnerName": "",
        "stepCode": 0,
        "page": 1,
        "size": 20
      }
      """;
  final String SUCCESS2_DEFAULT = """
      {
        "asnId": 1,
        "orderStart": "",
        "orderEnd": "",
        "partnerName": "",
        "stepCode": 0,
        "page": 1,
        "size": 20
      }
      """;
  final String SUCCESS3_DEFAULT = """
      {
        "asnId": 0,
        "orderStart": "2026-05-20",
        "orderEnd": "2026-06-25",
        "partnerName": "",
        "stepCode": 0,
        "page": 1,
        "size": 20
      }
      """;
  final String SUCCESS4_DEFAULT = """
      {
        "asnId": 1,
        "orderStart": "2026-05-20",
        "orderEnd": "2026-06-25",
        "partnerName": "",
        "stepCode": 0,
        "page": 1,
        "size": 20
      }
      """;
  final String ADD_PACKING_DEFAULT = """
      {
        "orderId": 1,
        "packingInvoice" : [
        {"orderId": 1,
        "packingInvoiceNumber": "IVC-1-1",
        "productId": 1,
        "carrierId": 7,           
        "stepCode": 19}
        ]
      }
      """;

      
    
}
