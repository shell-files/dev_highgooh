package cloud.weareithero.docs;

public interface ResponseExamples {
  
  final String SUCCESS_DEFAULT = """
    {
      "status": true,
      "result": {},
      "message": null
    }
    """;

  final String UNAUTHORIZED = """
    {
      "status": false,
      "result": null,
      "message": "Unauthorized"
    }
    """;

  final String ACCESS_DENIED = """
    {
      "status": false,
      "result": null,
      "message": "Access Denied"
    }
    """;

  final String USER_NOT_FOUND = """
    {
      "status": false,
      "result": null,
      "message": "존재하지 않는 사용자 입니다."
    }
    """;

}
