package cloud.weareithero.api.carbonEmission;

public interface CarbonEmissionResponseExamples {
    
    // 1. 연도별 조회 (전체)
    final String SUCCESS_YEAR = """
      {
        "selectedYear": "2026",
        "selectedQuarter": "",
        "selectedMonth": ""
      }
      """;
      
    // 2. 분기별 조회 (특정 연도, 특정 분기)
    final String SUCCESS_QUARTER = """
      {
        "selectedYear": "2026",
        "selectedQuarter": "2",
        "selectedMonth": ""
      }
      """;
      
    // 3. 월별 조회 (특정 연도, 특정 월)
    final String SUCCESS_MONTH = """
      {
        "selectedYear": "2026",
        "selectedQuarter": "",
        "selectedMonth": "6"
      }
      """;
}