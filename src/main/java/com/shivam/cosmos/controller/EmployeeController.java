package com.shivam.cosmos.controller;

import com.shivam.cosmos.model.BulkResponse;
import com.shivam.cosmos.model.Employee;
import com.shivam.cosmos.service.EmployeeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @PostMapping(path = "/create")
    boolean createEmployees(@RequestBody Employee employee) {
        log.info("Creating Employee with id : {}", employee.getId());
        return employeeService.createEmployee(employee);
    }

    @GetMapping(path = "/get/{id}")
    Employee getEmployeeById(@RequestParam String id) {
        log.info("Fetching Employee with id : {}", id);
        return employeeService.getEmployeeById(id);
    }

    @PostMapping(path = "/bulk/create")
    BulkResponse bulkCreate(@RequestParam List<Employee> employeeList) {
        int numberOfInputDocs = employeeList.size();
        int documentInserted = employeeService.bulkCreate(employeeList);
        return new BulkResponse(numberOfInputDocs, documentInserted);

    }
}
