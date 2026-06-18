from pprint import pprint
from airflow.providers.mysql.hooks.mysql import MySqlHook
from airflow.models import Variable

def step03(**context):
    ti = context['ti']
    jobs = ti.xcom_pull(task_ids='INIT', key='step00')
    list = ti.xcom_pull(task_ids='OLLAMA', key='step02')
    # dt = Variable.get("TARGET_DAY", default_var='2026-04-02')

    mysql_hook = MySqlHook(mysql_conn_id='MariaDB')
    conn = mysql_hook.get_conn()
    cursor = conn.cursor()

    sql1 = """
        UPDATE `AIRFLOW_JOB` SET `delete_yn` = 1, `airflow_log_id` = %s WHERE `id` = %s
    """

    sql2 = """
        INSERT INTO `AIRFLOW_LOG` 
            (`target_day`, `summary`, `reasoning`, `recommendation`)
        VALUES
            (%s, %s, %s, %s)
    """
    try:
        job_log_mapping = {}
        for data in list:
            jobId = data["jobId"]
            targetDay = data["targetDay"]
            parsedDict = data["parsedDict"]
            params = (targetDay, parsedDict['summary'], parsedDict['reasoning'], parsedDict['recommendation'])

            cursor.execute(sql2, params)
            airflowLogId = cursor.lastrowid
            job_log_mapping[jobId] = airflowLogId

        for data in jobs:
            jobId = data["jobId"]
            airflowLogId = job_log_mapping.get(jobId, 0)

            params = (airflowLogId, jobId)
            cursor.execute(sql1, params)
            
        conn.commit()
    except Exception as e:
        conn.rollback()
        pprint(f"❌ 데이터베이스 작업 중 에러 발생: {e}")
        raise e
    finally:
        cursor.close()
        conn.close()
