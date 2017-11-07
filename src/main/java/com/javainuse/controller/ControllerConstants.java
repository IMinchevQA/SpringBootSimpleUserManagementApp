package com.javainuse.controller;

/**
 * Created by Ivan Minchev on 11/2/2017.
 */
public class ControllerConstants {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_EMPLOYER = "ROLE_EMPLOYER";
    public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    public static final String CHANGE_USER_STATUS_URL = "/users/change_status/{id}";
    public static final String LIST_ALL_USERS_URL = "/users/list_all_users";
    public static final String LIST_ALL_EMPLOYEES_URL = "/employees/list_all_employees";
    public static final String UPDATE_EMPLOYEE_URL = "/employees/update_employee/{id}";
    public static final String DELETE_EMPLOYEE_URL = "/employees/delete_employee/{id}";
    public static final String LIST_ALL_EMPLOYERS_URL = "/employers/list_employers";
    public static final String UPDATE_EMPLOYER_URL = "/employers/update_employer/{id}";
    public static final String DELETE_EMPLOYER_URL = "/employers/delete_employer/{id}";
    public static final String SUBSCRIBE_EMPLOYEE = "/employers/{employer_id}/subscribe_employee/{employee_id}";
}
