package com.javainuse.controller;

import com.javainuse.model.Employee;
import com.javainuse.model.Employer;
import com.javainuse.model.TaskEmployee;
import com.javainuse.model.UpdateTaskProgress;
import com.javainuse.service.EmployeeService;
import com.javainuse.service.EmployerService;
import com.javainuse.service.TaskService;
import com.javainuse.service.UpdateService;
import com.javainuse.validator.TaskValidator;
import com.javainuse.validator.UpdateValidator;
import com.javainuse.validator.UserValidator;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

import static com.javainuse.controller.ControllerConstants.*;
import static com.javainuse.security.SecurityConstants.SECRET;
import static com.javainuse.security.SecurityConstants.TOKEN_PREFIX;

/**
 * Created by Ivan Minchev on 11/9/2017.
 */
@RestController
public class TaskController {
    
    @Autowired
    private TaskService taskData;
    
    @Autowired
    private TaskValidator taskValidator;

    @Autowired
    private EmployerService employerData;

    @Autowired
    private EmployeeService employeeData;

    @Autowired
    private UpdateService updateData;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UpdateValidator updateValidator;


    @RequestMapping(value = CREATE_TASK_URL, method = RequestMethod.POST)
    public String createTask(@RequestHeader("Authorization") String authToken,
                             @PathVariable("employer_id") long id,
                             @RequestBody TaskEmployee payload,
                             BindingResult bindingResult) throws IllegalAccessException {

        Employer employer = this.employerData.findEmployerById(id);

        if (employer == null) {
            throw new UsernameNotFoundException("No employer found with Id: " + id);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException("Only Administrator or Employer can create Tasks!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            this.checkRequestUserIdAndEmployerId(requestUserId, id, "Employer cannot create Task with another Employer's id!");
        }

        TaskEmployee task = new TaskEmployee();
        task.setTitle(payload.getTitle());
        task.setEmployer(employer);

        this.taskValidator.validate(task, bindingResult);
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            return error != null ? error : "UpdateTaskProgress failed";
        }

        this.taskData.addTask(task);
        employer.getTaskEmployees().add(task);
        this.employerData.updateEmployer(employer, employer.getUsername());

        return String.format("Task with title %s was created by Employer with username %s successfully!",
                task.getTitle(), employer.getUsername());
    }

    @RequestMapping(value = UPDATE_TASK_URL, method = RequestMethod.POST)
    public String updateTask(@RequestHeader("Authorization") String authToken,
                             @PathVariable("task_id") long id,
                             @RequestBody TaskEmployee payload,
                             BindingResult bindingResult) throws IllegalAccessException {

        TaskEmployee task = this.taskData.findTaskById(id);

        if (task == null) {
            throw new NullPointerException(String.format("Task with id '%s' not found!", id));
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException("Only Administrator or Employer can update a Task!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            if (requestUserId != task.getEmployer().getId()) {
                throw new IllegalAccessException("Employer cannot modify Task created by another Employer!");
            }
        }

        task.setTitle(payload.getTitle());

        this.taskValidator.validate(task, bindingResult);
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            return error != null ? error : "UpdateTaskProgress failed";
        }

        this.taskData.updateTask(task, task.getTitle());

        return String.format("Task with id '%s' updated successfully!",
                task.getTid());
    }


    @RequestMapping(value = ASSIGN_EMPLOYEE_TO_A_TASK_URL, method = RequestMethod.POST)
    public String assignEmployeeToTask(@RequestHeader("Authorization") String authToken,
                                       @PathVariable("employer_id") long employerId,
                                       @PathVariable("employee_id") long employeeId,
                                       @PathVariable("task_id") long taskId) throws IllegalAccessException {

        Employer employer = this.employerData.findEmployerById(employerId);
        Employee employee = this.employeeData.findEmployeeById(employeeId);
        TaskEmployee task = this.taskData.findTaskById(taskId);

        if (employer == null) {
            throw new UsernameNotFoundException("No employer found with Id: " + employerId);
        }

        if (employee == null) {
            throw new UsernameNotFoundException("No employee found with Id: " + employeeId);
        }

        if (task == null) {
            throw new NullPointerException("No task found with Id: " + taskId);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException("Only Administrator or Employer can assign Employee to a Task!");
        }


        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            this.checkRequestUserIdAndEmployerId(requestUserId, employerId, "Employer cannot assign Employee to Task created by another Employer!");
        }

        if (employerId != employee.getEmployer().getId()) {
            throw new IllegalAccessException("Employee cannot be assigned to Task created by another Employer!");
        }

        if (employerId != task.getEmployer().getId()) {
            throw  new IllegalAccessException("Task cannot be modified when Employer is not task creator!");
        }

        if (employee.getEmployeeTasks().isEmpty()) {
            employee.setEmployeeTasks(new HashSet<>());
        }

        employee.getEmployeeTasks().add(task);
        this.employeeData.updateEmployee(employee, employee.getUsername());


        return String.format("Employee with username '%s' assigned to Task with title '%s' successfully!",
                employee.getUsername(), task.getTitle());
    }


    @RequestMapping(value = RELEASE_EMPLOYEE_FROM_A_TASK_URL, method = RequestMethod.POST)
    public String releaseEmployeeFromTask(@RequestHeader("Authorization") String authToken,
                                       @PathVariable("employer_id") long employerId,
                                       @PathVariable("employee_id") long employeeId,
                                       @PathVariable("task_id") long taskId) throws IllegalAccessException {

        Employer employer = this.employerData.findEmployerById(employerId);
        Employee employee = this.employeeData.findEmployeeById(employeeId);
        TaskEmployee task = this.taskData.findTaskById(taskId);

        if (employer == null) {
            throw new UsernameNotFoundException("No employer found with Id: " + employerId);
        }

        if (employee == null) {
            throw new UsernameNotFoundException("No employee found with Id: " + employeeId);
        }

        if (task == null) {
            throw new NullPointerException("No task found with Id: " + taskId);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            throw new IllegalAccessException("Only Administrator or Employer can release Employee from a Task!");
        }


        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employerData.findEmployerByUsername(requestUsername).getId();

            this.checkRequestUserIdAndEmployerId(requestUserId, employerId, "Employer cannot release from Task an Employee subscribed to another Employer!");
        }

        if (employerId != employee.getEmployer().getId()) {
            throw new IllegalAccessException("Employee subscribed to another Employer cannot be released from Task!");
        }

        if (employerId != task.getEmployer().getId()) {
            throw  new IllegalAccessException("Employer cannot modify task created by another Employer!");
        }

        if (employee.getEmployeeTasks().isEmpty()) {
            throw new NullPointerException("Employee task list is empty");
        }

        if (!employee.getEmployeeTasks().contains(task)) {
            throw new NullPointerException("Employee task list does not contain current Task!");
        }

        employee.getEmployeeTasks().remove(task);
        this.employeeData.updateEmployee(employee, employee.getUsername());


        return String.format("Employee with username '%s' released from Task with title '%s' successfully!",
                employee.getUsername(), task.getTitle());
    }

    @RequestMapping(value = ADD_TASK_UPDATE_BY_EMPLOYEE_URL, method = RequestMethod.POST)
    public String addTaskUpdateByEmployee(@RequestHeader("Authorization") String authToken,
                                          @PathVariable("employee_id") long employee_id,
                                          @PathVariable("task_id") long task_id,
                                          @RequestBody UpdateTaskProgress payload,
                                          BindingResult bindingResult) throws IllegalAccessException {

        Employee employee = this.employeeData.findEmployeeById(employee_id);
        TaskEmployee task = this.taskData.findTaskById(task_id);

        if (employee == null) {
            throw new UsernameNotFoundException("No employee found with Id: " + employee_id);
        }

        if (task == null) {
            throw new NullPointerException("No task found with Id: " + task_id);
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYER)) {
            throw new IllegalAccessException("Only Employee can add task progress Update!");
        }

        if (this.userValidator.isUserInRole(authToken, ROLE_EMPLOYEE)) {
            String requestUsername = this.getUsernameRequestUser(authToken);
            long requestUserId = this.employeeData.findEmployeeByUsername(requestUsername).getId();

            if (requestUserId != employee_id) {
                throw new IllegalAccessException("Employee cannot add Task progress update for another Employee's Id!");
            }
        }

        if (employee.getEmployeeTasks().isEmpty()) {
            throw new NullPointerException("Employee task list is empty");
        }

        if (!employee.getEmployeeTasks().contains(task)) {
            throw new NullPointerException("Employee task list does not contain current Task!");
        }

        if (task.getUpdates() == null) {
            task.setUpdates(new HashSet<>());
        }

        UpdateTaskProgress update = new UpdateTaskProgress();
        update.setMessage(payload.getMessage());
        update.setUpdateTime();

        this.updateValidator.validate(update, bindingResult);
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getCode();
            return error != null ? error : "UpdateTaskProgress failed";
        }

        update.setTask(task);
        this.updateData.saveUpdate(update);


        return (String.format("Employee with username '%s' updated his Task successfully!", employee.getUsername()));
    }



    private void checkRequestUserIdAndEmployerId(long requestUserId, long employerPassedByUrlId, String message) throws IllegalAccessException {
        if (requestUserId != employerPassedByUrlId) {
            throw new IllegalAccessException(message);
        }
    }


    private String getUsernameRequestUser(String authToken) {
        String usernameRequestUser = Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(authToken.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
        return usernameRequestUser;
    }
}
