package com.payroll.model;

public class Payroll {
    public int payrollId, empId, month, year, workingDays, daysAbsent;
    public double overtimeHours, basic, hra, da, ta, overtimePay, gross;
    public double pf, professionalTax, incomeTax, absentDeduction, totalDeductions, net;
    public String empName, department; // filled when reading for reports/payslips
}
