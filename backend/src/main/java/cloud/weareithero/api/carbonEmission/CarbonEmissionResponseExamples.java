package cloud.weareithero.api.carbonEmission;

public interface CarbonEmissionResponseExamples {
    
    // 1. 연도별 조회 (전체)
    final String SUCCESS_YEAR = """
      {
        "year": "2026",
        "quarter": "none",
        "month": ""
      }
      """;
      
    // 2. 분기별 조회 (특정 연도, 특정 분기)
    final String SUCCESS_QUARTER = """
      {
        "year": "2026",
        "quarter": "2",
        "month": "none"
      }
      """;
      
    // 3. 월별 조회 (특정 연도, 특정 월)
    final String SUCCESS_MONTH = """
      {
        "year": "2026",
        "quarter": "none",
        "month": "6"
      }
      """;
}