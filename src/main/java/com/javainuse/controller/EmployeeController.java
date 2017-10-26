package com.javainuse.controller;

import com.javainuse.model.User;
import com.javainuse.service.EmployeeServiceImpl;
import com.javainuse.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by Ivan Minchev on 10/18/2017.
 */

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeData;


    @RequestMapping(value = "/listEmployees", method = RequestMethod.GET)
    public List<Employee> listEmployees() {
        return this.employeeData.getAllEmployees();
    }


    public void addNewEmployee(User user) {
        Employee employee = new Employee();
        employee.setUsername(user.getUsername());
        this.employeeData.addEmployee(employee);
    }

    @RequestMapping(method = RequestMethod.PUT, value="/editEmployee/{username}")
    public void updateEmployee(@PathVariable String username, @RequestBody Employee payload) {
        Employee employeeForEdit = this.employeeData.findEmployeeByUsername(username);

        if (employeeForEdit != null) {
            employeeForEdit.setFirstName(payload.getFirstName());
            employeeForEdit.setMiddleInitial(payload.getMiddleInitial());
            employeeForEdit.setLastName(payload.getLastName());
            employeeForEdit.setEmployeeNumber(payload.getEmployeeNumber());
            employeeForEdit.setDepartmentID(payload.getDepartmentID());
            employeeForEdit.setPhoneNumber(payload.getPhoneNumber());
            employeeForEdit.setDateOfHire(payload.getDateOfHire());
            this.employeeData.updateEmployee(employeeForEdit, username);
        } else {
            throw new IllegalArgumentException("No such employee in database!");
        }
    }

//    @RequestMapping(method = RequestMethod.PUT, value="/editEmployee/{id}")
//    public void updateEmployee(@PathVariable long id, @RequestBody Employee employee) {
//        this.employeeData.updateEmployee(id, employee);
//    }

    @RequestMapping(method = RequestMethod.DELETE, value="/deleteEmployee/{id}")
    public void deleteEmployee(@PathVariable long id) {
        this.employeeData.deleteEmployee(id);
    }

    private void setEmployeeData (EmployeeServiceImpl employeeData) {
        this.employeeData = employeeData;
    }
}




//    @RequestMapping(method = RequestMethod.POST, value = "/addNewEmployee")
//    public void addNewEmployee(@RequestBody Employee employee) {
//        employeeData.addEmployee(employee);
//    }


//@Controller
//public class EmployeeController {
//
//    private String errorMessage;
//
//    @Autowired
//    private EmployeeRepository employeeData;
//
//    @RequestMapping(value = "/addNewEmployee.html", method = RequestMethod.GET)
//    public ModelAndView getNewEmployeeView() {
//        Employee emp = new Employee();
//        return new ModelAndView("newEmployeeView", "form", emp);
//
//    }
//
//    @RequestMapping(value = "/addNewEmployee.html", method = RequestMethod.POST)
//    public String addNewEmployee(Employee employee) {
//        try {
//            employeeData.save(employee);
//            return ("redirect:/listEmployees.html");
//        } catch (Exception ex) {
//            System.out.println(ex.getClass().getSimpleName());
//            this.errorMessage = ex.getMessage();
//            return ("redirect:/showError.html");
//        }
//    }
//
//
//    @RequestMapping(value = "/showError.html", method = RequestMethod.GET)
//    public ModelAndView showErrorMessage() {
//        Map<String, String> map = new HashMap<>();
//        ModelAndView model = new ModelAndView("errorMessageView");
//        map.put("error", errorMessage);
//        model.addAllObjects(map);
//        return model;
//    }
//
//    @RequestMapping(value = "/listEmployees.html", method = RequestMethod.GET)
//    public ModelAndView employees() {
//        List<Employee> allEmployees = employeeData.findAll();
//        return new ModelAndView("allEmployees", "employees", allEmployees);
//    }
//
//
//
//    @RequestMapping(value="/editDelete/{operation}/{id}", method = RequestMethod.GET)
//    public String deleteEmployee(@PathVariable("operation") String operation, @PathVariable("id") long id) {
//        switch (operation) {
//            case "delete":
//                employeeData.delete(id);
//                break;
//            case "edit":
//                return ("redirect:/editCurrentEmployee/" + id);
//        }
//        return ("redirect:/listEmployees.html");
//    }
//
//    @RequestMapping(value = "/editCurrentEmployee/{id}", method = RequestMethod.GET)
//    public ModelAndView getEditEmployeeForm(@PathVariable("id") long id, Model model) {
//        Employee employee = employeeData.findOne(id);
//        return new ModelAndView("editEmployeeView", "employeeForEdit", employee);
//    }
//
//    @RequestMapping(value = "/editCurrentEmployee/{id}", method = RequestMethod.POST)
//    public String editEmployee(Employee employee) {
//        try {
//            employeeData.save(employee);
//            return ("redirect:/listEmployees.html");
//        } catch (Exception ex) {
//            System.out.println(ex.getClass().getSimpleName());
//            this.errorMessage = ex.getMessage();
//            return ("redirect:/showError.html");
//        }
//    }
//
//
//    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
//    public String greeting(
//            @RequestParam(value = "name", defaultValue = "unknown")
//            String personName,
//            Model model) {
//            model.addAttribute("personForGreeting", personName);
//        return "greeting";
//    }
//
//}
