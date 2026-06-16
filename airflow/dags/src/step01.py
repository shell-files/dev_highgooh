from airflow.providers.mysql.hooks.mysql import MySqlHook
from airflow.models import Variable

def step01(**context):
    mysql_hook = MySqlHook(mysql_conn_id='MariaDB')
    #dt = '2026-04-02'
    dt = Variable.get("TARGET_DAY", default_var='2026-04-02')
    sql = """
        SELECT 정산_기준_시간, 원자재_이름, 합금_종류, 공정_과정, 공정_순서, 총_투입량_kg, 
               실제_가동_시간_hour, 실제_직접_배출량_CO2_T, 실제_전력_사용량_kWh, 실제_간접_배출량_CO2_T, 실제_총_내재배출량_SEE_alu,
               기준_직접_배출량_CO2_T, 기준_전력_사용량_kWh, 기준_간접_배출량_CO2_T, 총_배출_초과량, 기준_대비_초과율_percent
        FROM v_llm
        WHERE 정산_기준_시간 >= %s AND  정산_기준_시간 < ADDDATE(%s, INTERVAL 1 DAY)
        GROUP BY 정산_기준_시간, 공정_순서
        ORDER BY 정산_기준_시간 desc
    """

    records = mysql_hook.get_records(sql, parameters=(dt, dt))

    json_data = [
        {
            "정산_기준_시간": row[0],
            "원자재_이름": row[1],
            "합금_종류": row[2],
            "공정_과정": row[3],
            "공정_순서": row[4],
        }
        for row in records
    ]

    ti = context['ti']
    ti.xcom_push(key='step01', value=json_data)
