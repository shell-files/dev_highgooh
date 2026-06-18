from airflow import DAG
from datetime import datetime, timedelta
from airflow.providers.standard.operators.empty import EmptyOperator
from airflow.providers.standard.operators.python import PythonOperator
from src.step00 import step00
from src.step01 import step01
from src.step02 import step02
from src.step03 import step03

with DAG(
    dag_id='app01_dag',
    start_date=datetime(2026, 1, 1),
    schedule=None,
    catchup=False
) as app01_dag:
    
    start = EmptyOperator(task_id='start')
    end = EmptyOperator(task_id='end')

    step00 = PythonOperator(
        task_id='INIT',
        python_callable=step00
    )

    step01 = PythonOperator(
        task_id='SELECT',
        python_callable=step01
    )

    step02 = PythonOperator(
        task_id='OLLAMA',
        python_callable=step02
    )

    step03 = PythonOperator(
        task_id='INSERT',
        python_callable=step03
    )

    (
       start
       >> step00
       >> step01
       >> step02
       >> step03
       >> end 
    )
