package com.javainuse.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javainuse.model.Employer;
import com.javainuse.model.User;
import com.javainuse.service.EmployeeService;
import com.javainuse.model.Employee;
import com.javainuse.service.EmployeeServiceImpl;
import com.javainuse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.javainuse.controller.ControllerConstants.*;


/**
 * Created by Ivan Minchev on 10/18/2017.
 */

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeData;

    @Autowired
    private UserService userData;


    @RequestMapping(value = LIST_EMPLOYEES_URL, method = RequestMethod.GET)
    public List<Employee> listEmployees(Pageable pageable) {
        Page<Employee> page = this.employeeData.listEmployees(pageable);
        return page.getContent();
    }


    @RequestMapping(value = EDIT_EMPLOYEE_URL, method = RequestMethod.PUT)
    public String updateEmployee(@PathVariable("id") Long id, @RequestBody Employee payload) {
        Employee employeeForEdit = this.employeeData.findEmployeeById(id);

        if (employeeForEdit == null) {
            throw new UsernameNotFoundException("No employee found with id: " + id);
        }

        employeeForEdit.setFirstName(payload.getFirstName());
        employeeForEdit.setMiddleInitial(payload.getMiddleInitial());
        employeeForEdit.setLastName(payload.getLastName());
        employeeForEdit.setEmployeeNumber(payload.getEmployeeNumber());
        employeeForEdit.setDepartmentID(payload.getDepartmentID());
        employeeForEdit.setPhoneNumber(payload.getPhoneNumber());
        employeeForEdit.setDateOfHire(payload.getDateOfHire());
        this.employeeData.updateEmployee(employeeForEdit, employeeForEdit.getUsername());

        return String.format("Employee with username '%s' was updated successfully!", employeeForEdit.getUsername());
    }


    @RequestMapping(value = DELETE_EMPLOYEE_URL, method = RequestMethod.DELETE)
    public String deleteEmployee(@PathVariable("id") Long id) {
        Employee employeeToBeDeleted = this.employeeData.findEmployeeById(id);
        Employer userEmployer = employeeToBeDeleted.getEmployer();

        if (userEmployer != null) {
        }

        if (employeeToBeDeleted == null) {
            throw new UsernameNotFoundException("There is no employee with id: " + id);
        }

        Long userIdToBeDeleted = this.userData.findByUsername(employeeToBeDeleted.getUsername()).getUid();

        this.employeeData.deleteEmployee(employeeToBeDeleted.getId());
        this.userData.deleteUser(userIdToBeDeleted);

        return String.format("Employee and User with username '%s' was deleted successfully!", employeeToBeDeleted.getUsername());
    }


    /**
     * Both methods below are invoked with reflection from UserController.
     */
    public void addNewEmployee(User user) {
        Employee employee = new Employee();
        employee.setUsername(user.getUsername());
        this.employeeData.addEmployee(employee);
    }

    private void setEmployeeData (EmployeeServiceImpl employeeData) {
        this.employeeData = employeeData;
    }
}
