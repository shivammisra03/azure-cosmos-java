package com.shivam.cosmos.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employee {

    private String id;
    private String name;
    private String org;
    private String city;
    private String errorMessage;

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", org='" + org + '\'' +
                ", city='" + city + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
