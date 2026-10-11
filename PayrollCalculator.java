package com.payroll.service;

import com.payroll.model.Employee;
import com.payroll.model.Payroll;

/**
 * Salary rules (edit the constants to match your organisation):
 *  Earnings   : Basic + HRA (20%) + DA (10%) + TA (5%) + Overtime (2x hourly rate)
 *  Deductions : PF (12% of basic) + Professional Tax (Rs 200 if gross >= 15000)
 *               + Income Tax/TDS (new-regime style slabs) + Loss of Pay for absent days
 */
public class PayrollCalculator {
    static final double HRA_RATE = 0.20, DA_RATE = 0.10, TA_RATE = 0.05;
    static final double PF_RATE = 0.12;
    static final double OT_MULTIPLIER = 2.0, HOURS_PER_DAY = 8;
    static final double PROF_TAX = 200, PROF_TAX_LIMIT = 15000;
    static final double STANDARD_DEDUCTION = 75000, REBATE_LIMIT = 1200000, CESS = 0.04;

    public static Payroll calculate(Employee e, int month, int year, int workingDays,
                                    int daysAbsent, double overtimeHours) {
        Payroll p = new Payroll();
        p.empId = e.getId();
        p.empName = e.getName();
        p.department = e.getDepartment();
        p.month = month;
        p.year = year;
        p.workingDays = workingDays;
        p.daysAbsent = daysAbsent;
        p.overtimeHours = overtimeHours;

        p.basic = round(e.getBasicSalary());
        p.hra = round(p.basic * HRA_RATE);
        p.da = round(p.basic * DA_RATE);
        p.ta = round(p.basic * TA_RATE);
        double hourlyRate = p.basic / (workingDays * HOURS_PER_DAY);
        p.overtimePay = round(overtimeHours * hourlyRate * OT_MULTIPLIER);
        p.gross = round(p.basic + p.hra + p.da + p.ta + p.overtimePay);

        p.pf = round(p.basic * PF_RATE);
        p.professionalTax = p.gross >= PROF_TAX_LIMIT ? PROF_TAX : 0;
        p.absentDeduction = round(p.basic / workingDays * daysAbsent);
        p.incomeTax = monthlyIncomeTax(p.gross);

        p.totalDeductions = round(p.pf + p.professionalTax + p.incomeTax + p.absentDeduction);
        p.net = round(p.gross - p.totalDeductions);
        return p;
    }

    /** Annualises gross, applies standard deduction, slabs, 87A rebate and 4% cess; returns monthly TDS. */
    static double monthlyIncomeTax(double monthlyGross) {
        double taxable = Math.max(0, monthlyGross * 12 - STANDARD_DEDUCTION);
        if (taxable <= REBATE_LIMIT) return 0;
        double[] limits = {400000, 800000, 1200000, 1600000, 2000000, 2400000};
        double[] rates  = {0.00, 0.05, 0.10, 0.15, 0.20, 0.25, 0.30};
        double tax = 0, prev = 0;
        for (int i = 0; i < limits.length; i++) {
            if (taxable > limits[i]) {
                tax += (limits[i] - prev) * rates[i];
                prev = limits[i];
            } else {
                tax += (taxable - prev) * rates[i];
                prev = taxable;
                break;
            }
        }
        if (taxable > limits[limits.length - 1]) tax += (taxable - prev) * rates[rates.length - 1];
        tax *= (1 + CESS);
        return round(tax / 12);
    }

    static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
