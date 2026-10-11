package com.payroll.service;

import com.payroll.model.Payroll;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class PayslipPrinter {

    public static void printPayslip(Payroll p) {
        String line = "=".repeat(50);
        System.out.println("\n" + line);
        System.out.println("                 PAYSLIP");
        System.out.println(line);
        System.out.printf("Employee   : %s (ID %d)%n", p.empName, p.empId);
        System.out.printf("Department : %s%n", p.department);
        System.out.printf("Period     : %s %d%n", monthName(p.month), p.year);
        System.out.printf("Days       : %d working, %d absent, %.1f OT hrs%n", p.workingDays, p.daysAbsent, p.overtimeHours);
        System.out.println("-".repeat(50));
        System.out.println("EARNINGS");
        row("Basic", p.basic);
        row("HRA (20%)", p.hra);
        row("DA (10%)", p.da);
        row("TA (5%)", p.ta);
        row("Overtime", p.overtimePay);
        row("GROSS SALARY", p.gross);
        System.out.println("-".repeat(50));
        System.out.println("DEDUCTIONS");
        row("Provident Fund (12%)", p.pf);
        row("Professional Tax", p.professionalTax);
        row("Income Tax (TDS)", p.incomeTax);
        row("Loss of Pay", p.absentDeduction);
        row("TOTAL DEDUCTIONS", p.totalDeductions);
        System.out.println(line);
        row("NET SALARY", p.net);
        System.out.println(line);
    }

    public static void printReport(List<Payroll> list, int month, int year) {
        System.out.printf("%n--- Payroll Report: %s %d ---%n", monthName(month), year);
        System.out.printf("%-5s %-20s %-12s %12s %12s %12s%n", "ID", "Name", "Dept", "Gross", "Deductions", "Net");
        double g = 0, d = 0, n = 0;
        for (Payroll p : list) {
            System.out.printf("%-5d %-20s %-12s %12.2f %12.2f %12.2f%n",
                    p.empId, trim(p.empName, 20), trim(p.department, 12), p.gross, p.totalDeductions, p.net);
            g += p.gross; d += p.totalDeductions; n += p.net;
        }
        System.out.println("-".repeat(78));
        System.out.printf("%-39s %12.2f %12.2f %12.2f%n", "TOTAL (" + list.size() + " employees)", g, d, n);
    }

    public static void printHistory(List<Payroll> list) {
        System.out.printf("%n%-12s %12s %12s %12s%n", "Period", "Gross", "Deductions", "Net");
        for (Payroll p : list) {
            System.out.printf("%-12s %12.2f %12.2f %12.2f%n",
                    monthName(p.month).substring(0, 3) + " " + p.year, p.gross, p.totalDeductions, p.net);
        }
    }

    private static void row(String label, double amt) {
        System.out.printf("  %-30s %15.2f%n", label, amt);
    }

    private static String monthName(int m) {
        return Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    private static String trim(String s, int n) {
        return s.length() <= n ? s : s.substring(0, n - 1) + ".";
    }
}
