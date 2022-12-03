package com.shivam.cosmos.service;

import com.shivam.cosmos.model.Employee;

import java.util.List;

public interface EmployeeService {

    Employee getEmployeeById(String id);

    boolean createEmployee(Employee employee);

    int bulkCreate(List<Employee> employeeList);
}
