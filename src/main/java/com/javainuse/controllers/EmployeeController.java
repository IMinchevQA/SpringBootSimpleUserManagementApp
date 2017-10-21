package com.javainuse.controllers;

import com.javainuse.data.EmployeeRepository;
import com.javainuse.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Ivan Minchev on 10/18/2017.
 */
@Controller
public class EmployeeController {

    private String errorMessage;

    @Autowired
    private EmployeeRepository employeeData;

    @RequestMapping(value = "/addNewEmployee.html", method = RequestMethod.GET)
    public ModelAndView getNewEmployeeView() {
        Employee emp = new Employee();
        return new ModelAndView("newEmployeeView", "form", emp);

    }

    @RequestMapping(value = "/addNewEmployee.html", method = RequestMethod.POST)
    public String addNewEmployee(Employee employee) {
        try {
            employeeData.save(employee);
            return ("redirect:/listEmployees.html");
        } catch (Exception ex) {
            System.out.println(ex.getClass().getSimpleName());
            this.errorMessage = ex.getMessage();
            return ("redirect:/showError.html");
        }
    }


    @RequestMapping(value = "/showError.html", method = RequestMethod.GET)
    public ModelAndView showErrorMessage() {
        Map<String, String> map = new HashMap<>();
        ModelAndView model = new ModelAndView("errorMessageView");
        map.put("error", errorMessage);
        model.addAllObjects(map);
        return model;
    }

    @RequestMapping(value = "/listEmployees.html", method = RequestMethod.GET)
    public ModelAndView employees() {
        List<Employee> allEmployees = employeeData.findAll();
        return new ModelAndView("allEmployees", "employees", allEmployees);
    }



    @RequestMapping(value="/editDelete/{operation}/{id}", method = RequestMethod.GET)
    public String deleteEmployee(@PathVariable("operation") String operation, @PathVariable("id") long id) {
        switch (operation) {
            case "delete":
                employeeData.delete(id);
                break;
            case "edit":
                return ("redirect:/editCurrentEmployee/" + id);
        }
        return ("redirect:/listEmployees.html");
    }

    @RequestMapping(value = "/editCurrentEmployee/{id}", method = RequestMethod.GET)
    public ModelAndView getEditEmployeeForm(@PathVariable("id") long id, Model model) {
        Employee employee = employeeData.findOne(id);
        return new ModelAndView("editEmployeeView", "employeeForEdit", employee);
    }

    @RequestMapping(value = "/editCurrentEmployee/{id}", method = RequestMethod.POST)
    public String editEmployee(Employee employee) {
        try {
            employeeData.save(employee);
            return ("redirect:/listEmployees.html");
        } catch (Exception ex) {
            System.out.println(ex.getClass().getSimpleName());
            this.errorMessage = ex.getMessage();
            return ("redirect:/showError.html");
        }
    }


    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public String greeting(
            @RequestParam(value = "name", defaultValue = "unknown")
            String personName,
            Model model) {
            model.addAttribute("personForGreeting", personName);
        return "greeting";
    }

}
