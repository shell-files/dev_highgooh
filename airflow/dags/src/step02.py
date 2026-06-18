from pprint import pprint
from ollama import Client
from airflow.models import Variable
import json

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
        **LLM 프롬프트 예시:**
        "너는 제조 공정 탄소 배출 관리 전문가야. 아래 제공된 데이터는 기준 탄소 배출량을 20% 이상 초과한 **이상치(Outlier) 데이터** 리스트야.

        **[데이터]**
        {jsonData}

        **[요청 사항]**
        1. 어떤 공정과 어떤 원자재 조합에서 가장 심각한 탄소 배출 초과(이상치)가 발생했는지 요약해줘.
        2. 가동 시간 대비 전력 사용량이 비정상적으로 높은 공정을 찾아 원인을 추론해줘.
        3. 이 데이터를 바탕으로 공장 관리자에게 보고할 '탄소 배출 이상치 개선 권고서'를 격식 있는 톤으로 작성해줘."

        **[출력 형식]**
        반드시 아래와 같은 JSON 구조로만 답변하고, JSON 외의 다른 설명, 인사말, 백틱(```) 기호는 절대 포함하지 마.
        특히 'reasoning'과 'recommendation' 값은 배열이나 오브젝트를 쓰지 말고, 반드시 하나의 긴 '문자열(String)'로 줄바꿈 기호(\\n)를 사용해 작성해줘.

        각 항목은 아래 명시된 **[글자 수 제한(공백 포함)]**을 엄격히 준수해야 해.

        {{
            "summary": "가장 심각한 공정 및 원자재 조합 요약 내용 (단일 문자열, [글자 수 제한: 공백 포함 150자 이내로 핵심만 압축할 것])",
            "reasoning": "비정상 공정 명칭, 판단 시간대, 전력 사용량 수치 및 구체적인 발생 원인 추론 과정을 상세히 서술한 하나의 줄바꿈 텍스트 (단일 문자열, [글자 수 제한: 공백 포함 최소 100자 이상, 300자 이내로 상세히 기술할 것])",
            "recommendation": "탄소 배출 이상치 개선 권고서 전문 (단일 문자열, [글자 수 제한: 공백 포함 최소 300자 이상, 500자 이내로 격식 있게 작성할 것])"
        }}
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
                    {'role': 'user','content': message,}
                ],
                format='json',
                options={
                    'temperature': 0
                }
            )

            raw_content = response.message.content
            parsedDict = json.loads(raw_content)
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
