from pprint import pprint
from airflow.providers.mysql.hooks.mysql import MySqlHook
from airflow.models import Variable

def step03(**context):
    ti = context['ti']
    json_data = ti.xcom_pull(task_ids='OLLAMA', key='step02')
    dt = Variable.get("TARGET_DAY", default_var='2026-04-02')

    params = (dt, json_data['summary'], json_data['reasoning'], json_data['recommendation'])

    print(params)

    sql = """
        INSERT INTO `AIRFLOW_LOG` 
            (`target_day`, `summary`, `reasoning`, `recommendation`)
        VALUES
            (%s, %s, %s, %s)
    """

    mysql_hook = MySqlHook(mysql_conn_id='MariaDB')
    conn = mysql_hook.get_conn()
    cursor = conn.cursor()

    try:
        cursor.execute(sql, params)
        conn.commit()
    except Exception as e:
        conn.rollback()
        pprint(f"❌ 데이터베이스 작업 중 에러 발생: {e}")
        raise e
    finally:
        cursor.close()
        conn.close()

