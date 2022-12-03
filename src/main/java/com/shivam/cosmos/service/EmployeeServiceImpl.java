package com.shivam.cosmos.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.cosmos.model.Employee;
import com.shivam.cosmos.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Employee getEmployeeById(String id) {
        return employeeRepository.getEmployeeById(id);

    }

    @Override
    public boolean createEmployee(Employee employee) {

        employeeRepository.createEmployee(employee);
        return true;
    }

    @Override
    public int bulkCreate(List<Employee> employeeList) {
        List<JsonNode> itemsToBeInserted = new LinkedList<>();
        for(Employee e : employeeList){
            itemsToBeInserted.add(objectMapper.convertValue(e, JsonNode.class));
        }
        return employeeRepository.bulk(itemsToBeInserted);
    }
}
