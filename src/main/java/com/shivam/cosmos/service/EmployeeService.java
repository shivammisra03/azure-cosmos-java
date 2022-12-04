package com.shivam.cosmos.service;

import com.shivam.cosmos.model.Employee;

import java.util.List;

public interface EmployeeService {

    Employee getEmployeeById(String id);

    Employee createEmployee(Employee employee);

    int bulkUpsert(List<Employee> employeeList);
}
