CREATE TABLE IF NOT EXISTS TASK (TASK_ID INTEGER IDENTITY PRIMARY KEY, DESCRIPTION VARCHAR(500), COST DECIMAL(10,2), TASK_STATUS VARCHAR(20), TASK_FINISHED VARCHAR(500)); CREATE TABLE IF NOT EXISTS TASK_OPERATION (OPERATION_ID INTEGER IDENTITY PRIMARY KEY, TASK_ID INTEGER, DESCRIPTION VARCHAR(500), PLANNED_QUANTITY INTEGER, ACTUAL_QUANTITY INTEGER, PRICE DECIMAL(10,2), COST DECIMAL(10,2), OPERATION_STATUS VARCHAR(20), FOREIGN KEY(TASK_ID) REFERENCES TASK(TASK_ID) ON DELETE CASCADE);