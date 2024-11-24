------------------------------------------------------------------------------------------------------------------------

------------1------------
CREATE OR REPLACE FUNCTION NEW_JOB(
    p_job_id VARCHAR,
    p_job_title VARCHAR,
    p_min_salary NUMERIC
)
    RETURNS VOID
    LANGUAGE plpgsql
AS $$
BEGIN
    IF p_job_id IS NULL OR LENGTH(p_job_id) = 0 THEN
        RAISE EXCEPTION 'Job ID cannot be null or empty';
    END IF;

    IF p_job_title IS NULL OR LENGTH(p_job_title) = 0 THEN
        RAISE EXCEPTION 'Job title cannot be null or empty';
    END IF;

    IF p_min_salary IS NULL OR p_min_salary <= 0 THEN
        RAISE EXCEPTION 'Minimum salary must be greater than zero';
    END IF;

    INSERT INTO jobs.jobs (job_id, job_title, min_salary, max_salary)
    VALUES (p_job_id, p_job_title, p_min_salary, p_min_salary * 2);

    RAISE NOTICE 'New job % added successfully', p_job_id;
END;
$$;

DO $$
    BEGIN
        PERFORM NEW_JOB('JV_DEV', 'Java Developer', 8000);
    END
$$; -- SY_ANAL already exists (was inserted in init.sql)


------------2------------


CREATE OR REPLACE FUNCTION ADD_JOB_HIST(
    p_emp_id INTEGER,
    p_new_job_id VARCHAR
)
    RETURNS VOID
    LANGUAGE plpgsql
AS $$
DECLARE
    v_min_salary NUMERIC;
    v_hire_date DATE;
BEGIN
    IF p_emp_id IS NULL THEN
        RAISE EXCEPTION 'Employee ID cannot be null';
    END IF;

    IF p_new_job_id IS NULL OR LENGTH(p_new_job_id) = 0 THEN
        RAISE EXCEPTION 'Job ID cannot be null or empty';
    END IF;

    SELECT hire_date INTO v_hire_date FROM jobs.employees WHERE employee_id = p_emp_id;

    INSERT INTO jobs.job_history (employee_id, start_date, end_date, job_id, department_id)
    VALUES (p_emp_id, v_hire_date, CURRENT_DATE, p_new_job_id,
            (SELECT department_id FROM jobs.employees WHERE employee_id = p_emp_id));

    UPDATE jobs.employees
    SET hire_date = CURRENT_DATE, job_id = p_new_job_id, salary = v_min_salary + 500
    WHERE employee_id = p_emp_id;

    RAISE NOTICE 'Job history updated for employee %', p_emp_id;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE NOTICE 'Employee or job not found';
END;
$$;

ALTER TABLE jobs.employees DISABLE TRIGGER ALL;
ALTER TABLE jobs.jobs DISABLE TRIGGER ALL;
ALTER TABLE jobs.job_history DISABLE TRIGGER ALL;

DO $$
    BEGIN
        PERFORM ADD_JOB_HIST(106, 'SY_ANAL');
    END
$$;


SELECT * FROM jobs.job_history WHERE employee_id = 106;
SELECT * FROM jobs.employees WHERE employee_id = 106;
COMMIT;

ALTER TABLE jobs.employees ENABLE TRIGGER ALL;
ALTER TABLE jobs.jobs ENABLE TRIGGER ALL;
ALTER TABLE jobs.job_history ENABLE TRIGGER ALL;


------------3------------


CREATE OR REPLACE FUNCTION UPD_JOBSAL(
    p_job_id VARCHAR,
    p_new_min_salary NUMERIC,
    p_new_max_salary NUMERIC
)
    RETURNS VOID
    LANGUAGE plpgsql
AS $$
BEGIN
    IF p_job_id IS NULL OR LENGTH(p_job_id) = 0 THEN
        RAISE EXCEPTION 'Job ID cannot be null or empty';
    END IF;

    IF p_new_min_salary IS NULL OR p_new_min_salary <= 0 THEN
        RAISE EXCEPTION 'Minimum salary must be greater than zero';
    END IF;

    IF p_new_max_salary IS NULL OR p_new_max_salary <= 0 THEN
        RAISE EXCEPTION 'Maximum salary must be greater than zero';
    END IF;

    IF p_new_max_salary < p_new_min_salary THEN
        RAISE EXCEPTION 'Maximum salary cannot be less than minimum salary';
    END IF;

    BEGIN
        UPDATE jobs.jobs
        SET min_salary = p_new_min_salary, max_salary = p_new_max_salary
        WHERE job_id = p_job_id;

        RAISE NOTICE 'Salaries updated for job %', p_job_id;

    EXCEPTION
        WHEN unique_violation THEN
            RAISE NOTICE 'Job ID % not found. No salaries updated.', p_job_id;
        WHEN OTHERS THEN
            RAISE NOTICE 'An unexpected error occurred. %', SQLERRM;
    END;
END;
$$;


DO $$
    BEGIN
        PERFORM UPD_JOBSAL('SY_ANAL', 7000, 140);
    END
$$;

ALTER TABLE jobs.employees DISABLE TRIGGER ALL;
ALTER TABLE jobs.jobs DISABLE TRIGGER ALL;

DO $$
    BEGIN
        PERFORM UPD_JOBSAL('SY_ANAL', 7000, 14000);
    END
$$;

SELECT * FROM jobs.jobs WHERE job_id = 'SY_ANAL';
COMMIT;

ALTER TABLE jobs.employees ENABLE TRIGGER ALL;
ALTER TABLE jobs.jobs ENABLE TRIGGER ALL;


------------4------------


CREATE OR REPLACE FUNCTION GET_YEARS_SERVICE(
    p_emp_id INTEGER
)
    RETURNS NUMERIC
    LANGUAGE plpgsql
AS $$
DECLARE
    v_years_service NUMERIC;
BEGIN
    IF p_emp_id IS NULL THEN
        RAISE EXCEPTION 'Employee ID cannot be null';
    END IF;

    SELECT EXTRACT(YEAR FROM AGE(CURRENT_DATE, hire_date)) INTO v_years_service
    FROM jobs.employees
    WHERE employee_id = p_emp_id;

    IF v_years_service IS NULL THEN
        RAISE EXCEPTION 'Employee ID % not found', p_emp_id;
    END IF;

    RETURN v_years_service;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE NOTICE 'Employee ID % not found', p_emp_id;
        RETURN NULL;
END;
$$;

DO $$
    BEGIN
        RAISE NOTICE 'Years of service for employee 999: %', GET_YEARS_SERVICE(999);
    END
$$;

DO $$
    BEGIN
        RAISE NOTICE 'Years of service for employee 106: %', GET_YEARS_SERVICE(106);
    END
$$;


------------5------------


CREATE OR REPLACE FUNCTION GET_JOB_COUNT(
    p_emp_id INTEGER
)
    RETURNS INTEGER
    LANGUAGE plpgsql
AS $$
DECLARE
    v_job_count INTEGER;
BEGIN
    IF p_emp_id IS NULL THEN
        RAISE EXCEPTION 'Employee ID cannot be null';
    END IF;

    SELECT COUNT(DISTINCT job_id) INTO v_job_count
    FROM (
             SELECT job_id FROM jobs.job_history WHERE employee_id = p_emp_id
             UNION
             SELECT job_id FROM jobs.employees WHERE employee_id = p_emp_id
         ) AS unique_jobs;

    IF v_job_count IS NULL THEN
        RAISE EXCEPTION 'Employee ID % not found', p_emp_id;
    END IF;

    RETURN v_job_count;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE NOTICE 'Employee ID % not found', p_emp_id;
        RETURN 0;
END;
$$;

DO $$
    BEGIN
        RAISE NOTICE 'Number of different jobs for employee 176: %', GET_JOB_COUNT(176);
    END
$$;


------------6------------


CREATE OR REPLACE FUNCTION CHECK_SAL_RANGE()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    v_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM jobs.employees
    WHERE job_id = OLD.job_id AND
        (salary < NEW.min_salary OR salary > NEW.max_salary);

    IF v_count > 0 THEN
        RAISE EXCEPTION 'Salary range violation for existing employees';
    END IF;

    RAISE NOTICE 'Salary range validated for job %', OLD.job_id;
    RETURN NEW;
END;
$$;

CREATE TRIGGER CHECK_SAL_RANGE
    BEFORE UPDATE OF min_salary, max_salary
    ON jobs.jobs
    FOR EACH ROW
EXECUTE FUNCTION CHECK_SAL_RANGE();

SELECT min_salary, max_salary FROM jobs.jobs WHERE job_id = 'SY_ANAL'; -- 7000,14000

SELECT employee_id, last_name, salary FROM jobs.employees WHERE job_id = 'SY_ANAL'; -- 8000.00

UPDATE jobs.jobs
SET min_salary = 5000, max_salary = 7000
WHERE job_id = 'SY_ANAL'; -- проверка обработки ошибки

SELECT min_salary, max_salary FROM jobs.jobs WHERE job_id = 'SY_ANAL'; -- 7000,14000

UPDATE jobs.jobs
SET min_salary = 7000, max_salary = 18000
WHERE job_id = 'SY_ANAL';

SELECT min_salary, max_salary FROM jobs.jobs WHERE job_id = 'SY_ANAL'; -- 7000,18000

