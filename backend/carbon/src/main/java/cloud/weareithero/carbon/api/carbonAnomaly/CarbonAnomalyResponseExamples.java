package cloud.weareithero.carbon.api.carbonAnomaly;

public interface CarbonAnomalyResponseExamples {
    
    // 1. 연도별 조회 성공 예시 (2025년 전체 데이터, 2페이지 요청 시)
    String SUCCESS_YEAR = """
      {
        "status": true,
        "message": "데이터 조회가 완료되었습니다.",
        "data": [
          {
            "id": 125,
            "process": "아노다이징",
            "anomalyScore": 85.45,
            "state": "조치대기",
            "createAt": "2025-06-12T14:30:00",
            "directEmission": 120.50,
            "properDirectEmission": 100.00,
            "electricityUsed": 450.20,
            "properElectricityUsed": 400.00,
            "indirectEmission": 85.10,
            "properIndirectEmission": 80.00
          }
        ]
      }
      """;
      
    // 2. 분기별 조회 성공 예시 (2026년 2분기 데이터)
    String SUCCESS_QUARTER = """
      {
        "status": true,
        "message": "데이터 조회가 완료되었습니다.",
        "data": [
          {
            "id": 241,
            "process": "아노다이징",
            "anomalyScore": 92.10,
            "state": "조치완료",
            "createAt": "2026-05-20T11:15:22",
            "directEmission": 210.00,
            "properDirectEmission": 150.00,
            "electricityUsed": 620.00,
            "properElectricityUsed": 500.00,
            "indirectEmission": 140.50,
            "properIndirectEmission": 110.00
          }
        ]
      }
      """;
      
    // 3. 월별 조회 성공 예시 (2026년 6월 데이터)
    String SUCCESS_MONTH = """
      {
        "status": true,
        "message": "데이터 조회가 완료되었습니다.",
        "data": [
          {
            "id": 302,
            "process": "4축 CNC",
            "anomalyScore": 45.22,
            "state": "조치대기",
            "createAt": "2026-06-02T09:05:10",
            "directEmission": 55.30,
            "properDirectEmission": 60.00,
            "electricityUsed": 210.15,
            "properElectricityUsed": 230.00,
            "indirectEmission": 35.40,
            "properIndirectEmission": 40.00
          }
        ]
      }
      """;

    // 4. 데이터 조회 실패 또는 에러 발생 시 예시
    String ERROR_RESPONSE = """
      {
        "status": false,
        "message": "데이터 조회 중 오류가 발생했습니다.",
        "data": null
      }
      """;
}