from airflow.providers.mysql.hooks.mysql import MySqlHook

def step00(**context):
    mysql_hook = MySqlHook(mysql_conn_id='MariaDB')

    sql = """
      SELECT 
             `id`, 
             DATE_FORMAT(`target_day`, '%Y-%m-%d') AS target_day
        FROM `AIRFLOW_JOB` 
       WHERE `delete_yn` = 0
    """

    records = mysql_hook.get_records(sql)

    json_data = [
        {
            "jobId": row[0],
            "targetDay": row[1]
        }
        for row in records
    ]

    ti = context['ti']
    ti.xcom_push(key='step00', value=json_data)
