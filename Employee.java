package com.payroll.model;

import java.time.LocalDate;

public class Employee {
    private int id;
    private String name, email, phone, department, designation, status;
    private LocalDate joinDate;
    private double basicSalary;

    public Employee(int id, String name, String email, String phone, String department,
                    String designation, LocalDate joinDate, double basicSalary, String status) {
        this.id = id; this.name = name; this.email = email; this.phone = phone;
        this.department = department; this.designation = designation;
        this.joinDate = joinDate; this.basicSalary = basicSalary; this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }
    public LocalDate getJoinDate() { return joinDate; }
    public double getBasicSalary() { return basicSalary; }
    public String getStatus() { return status; }

    public void setName(String v) { name = v; }
    public void setEmail(String v) { email = v; }
    public void setPhone(String v) { phone = v; }
    public void setDepartment(String v) { department = v; }
    public void setDesignation(String v) { designation = v; }
    public void setBasicSalary(double v) { basicSalary = v; }
    public void setStatus(String v) { status = v; }
}
