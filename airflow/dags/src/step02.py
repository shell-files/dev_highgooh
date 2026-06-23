from pprint import pprint
from ollama import Client
from airflow.sdk import Variable
import json

SYSTEM_PROMPT = (
    "당신은 한국어로만 응답하는 제조 공정 탄소 배출 관리 전문가입니다. "
    "어떠한 경우에도 영어를 사용하지 마십시오. "
    "반드시 지정된 JSON 스키마 구조로만 응답하고, 키 이름을 절대 변경하지 마십시오. "
    "모든 값은 중첩 오브젝트 없이 단일 문자열로만 작성하십시오."
)

JSON_SCHEMA = {
    "type": "object",
    "properties": {
        "summary": {
            "type": "string",
            "description": "가장 심각한 공정 및 원자재 조합 요약 (150자 이내 한국어 문자열)"
        },
        "reasoning": {
            "type": "string",
            "description": "비정상 공정명, 시간대, 전력 수치, 원인 추론을 포함한 서술 (100~300자 한국어 문자열)"
        },
        "recommendation": {
            "type": "string",
            "description": "탄소 배출 이상치 개선 권고서 전문 (300~500자 한국어 문자열)"
        }
    },
    "required": ["summary", "reasoning", "recommendation"],
    "additionalProperties": False
}

def step02(**context):
    ollama_host = Variable.get("OLLAMA_HOST", default_var="http://localhost:11434")
    ollama_model = Variable.get("OLLAMA_MODEL", default_var="llama3:latest")
    client = Client(host=ollama_host)

    ti = context['ti']
    list = ti.xcom_pull(task_ids='SELECT', key='step01')
    messages = []
    for data in list:
        jobId = data["jobId"]
        targetDay = data["targetDay"]
        jsonData = data["jsonData"]
        message = f"""
        당신은 제조 공정 탄소 배출 관리 전문가입니다. 아래 데이터는 기준 탄소 배출량을 20% 이상 초과한 이상치(Outlier) 데이터 리스트입니다.

        [데이터]
        {jsonData}

        [요청 사항]
        1. 어떤 공정과 원자재 조합에서 가장 심각한 탄소 배출 초과(이상치)가 발생했는지 요약하시오.
        2. 가동 시간 대비 전력 사용량이 비정상적으로 높은 공정을 찾아 원인을 추론하시오.
        3. 공장 관리자에게 보고할 탄소 배출 이상치 개선 권고서를 격식 있는 한국어로 작성하시오.

        [절대 규칙]
        - 응답 전체를 반드시 한국어(한글)로만 작성할 것. 영어 사용 금지.
        - 키 이름(summary, reasoning, recommendation)을 절대 변경하지 말 것.
        - 각 값은 반드시 단일 문자열(String)로만 작성하고, 중첩 오브젝트나 배열 사용 금지.
        - "reasoning"과 "recommendation" 값은 줄바꿈(\\n)을 활용한 단일 문자열로 작성할 것.

        [글자 수 제한 - 공백 포함, 반드시 준수]
        - summary: 150자 이내
        - reasoning: 100자 이상 300자 이내
        - recommendation: 300자 이상 500자 이내
        """
        data = {
            "jobId": jobId,
            "targetDay": targetDay,
            "message": message
        }
        messages.append(data)

    result = []
    for data in messages: 
        jobId = data["jobId"]
        targetDay = data["targetDay"]
        message = data["message"]
        try:
            response = client.chat(
                model=ollama_model, 
                messages=[
                    {'role': 'system', 'content': SYSTEM_PROMPT},
                    {'role': 'user','content': message,}
                ],
                format=JSON_SCHEMA,
                options={
                    'temperature': 0
                }
            )

            raw_content = response.message.content
            parsedDict = json.loads(raw_content)
            
            # 키 존재 여부 및 타입 검증
            for key in ["summary", "reasoning", "recommendation"]:
                if key not in parsedDict:
                    raise ValueError(f"필수 키 누락: {key}")
                if not isinstance(parsedDict[key], str):
                    raise ValueError(f"키 '{key}'의 값이 문자열이 아님: {type(parsedDict[key])}")
                
        except json.JSONDecodeError as e:
            print(f"JSON 파싱 실패: {e}")
            parsedDict = {
                "status": False,
                "summary": "파싱 실패로 인한 대체 텍스트",
                "reasoning": f"모델 응답 파싱 실패. 원본: {raw_content}",
                "recommendation": "개선 권고서를 파싱하지 못했습니다."
            }
        data = {
            "jobId": jobId,
            "targetDay": targetDay,
            "parsedDict": parsedDict
        }
        result.append(data)

    # 3. dict 구조인 상태 그대로 XCom에 저장
    ti.xcom_push(key='step02', value=result)
    print("성공적으로 dict 형식으로 변환하여 XCom에 저장했습니다.")
