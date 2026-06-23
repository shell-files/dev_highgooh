import json
from pprint import pprint
from websocket import create_connection
from airflow.sdk import Variable

def send_websocket_alarm(message):
    """
    지정된 웹소켓 서버 주소로 JSON 데이터를 전송하는 공통 함수
    """
    ws_url = Variable.get("WS_HOST", default_var="ws://localhost/ws/hg-websocket?client=airflow")

    try:
        print(ws_url)
        # 웹소켓 연결 오픈
        ws = create_connection(ws_url)
        connect_frame = "CONNECT\naccept-version:1.1,1.2\nheart-beat:0,0\n\n\x00"
        ws.send(connect_frame)
        # 딕셔너리 데이터를 JSON 문자열로 변환하여 전송
        # json_payload = json.dumps(message_dict, ensure_ascii=False)
        stomp_send_frame = (
            "SEND\n"
            "destination:/app/chat.send\n"
            "content-type:text/plain\n"
            "\n"
            f"{message}"
            "\x00"
        )
        ws.send(stomp_send_frame)
        print(f" WebSockets 알람 전송 성공: {message}")
        
        # 연결 닫기
        disconnect_frame = "DISCONNECT\n\n\x00"
        ws.send(disconnect_frame)
        ws.close()
    except Exception as e:
        pprint(f"❌ WebSockets 알람 전송 실패: {e}")

def step04(**context):
    ti = context['ti']
    jobs = ti.xcom_pull(task_ids='INIT', key='step00')
    payload = f"이상치탐지 분석 : {[data["targetDay"] for data in jobs]}"
    send_websocket_alarm(payload)
