package com.controller;

/**
 * Created by Ivan Minchev on 11/2/2017.
 */
public class ControllerConstants {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_EMPLOYER = "ROLE_EMPLOYER";
    public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    public static final String ADMIN_STRING = "Administrator";
    public static final String EMPLOYER_STRING = "Employer";
    public static final String EMPLOYEE_STRING = "Employee";
    public static final String LIST_ALL_USERS_URL = "/users/list_all_users";
    public static final String LIST_ALL_EMPLOYERS_URL = "/employers/list_employers";
    public static final String LIST_ALL_EMPLOYEES_URL = "/employees/list_all_employees";
    public static final String LIST_EMPLOYEES_OF_CURRENT_EMPLOYER_URL = "/employers/{employer_id}/list_employees";
    public static final String COUNT_EMPLOYEES_URL = "/employers/{employer_id}/count_employees";
    public static final String UPDATE_EMPLOYER_URL = "/employers/update_employer/{employer_id}";
    public static final String UPDATE_EMPLOYEE_URL = "/employees/update_employee/{employee_id}";
    public static final String SUBSCRIBE_EMPLOYEE_URL = "/employers/{employer_id}/subscribe_employee/{employee_id}";
    public static final String RELEASE_EMPLOYEE_URL = "/employers/{employer_id}/release_employee/{employee_id}";
    public static final String CHANGE_EMPLOYER_STATUS_URL = "/employers/{employer_id}/change_status";
    public static final String CHANGE_EMPLOYEE_STATUS_URL = "/employees/{employee_id}/change_status";
    public static final String DELETE_EMPLOYER_URL = "/employers/delete_employer/{employer_id}";
    public static final String DELETE_EMPLOYEE_URL = "/employees/delete_employee/{employee_id}";
    public static final String CREATE_TASK_URL = "/tasks/employer/{employer_id}/create_task";
    public static final String UPDATE_TASK_URL = "/tasks/update_task/{task_id}";
    public static final String ASSIGN_EMPLOYEE_TO_A_TASK_URL = "/tasks/employer/{employer_id}/assign_employee/{employee_id}/to_task/{task_id}";
    public static final String RELEASE_EMPLOYEE_FROM_A_TASK_URL = "/tasks/employer/{employer_id}/release_employee/{employee_id}/from_task/{task_id}";
    public static final String ADD_TASK_UPDATE_BY_EMPLOYEE_URL = "/tasks/employee/{employee_id}/add_update/{task_id}";
}
