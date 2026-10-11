package com.payroll.app;

import com.payroll.dao.EmployeeDAO;
import com.payroll.dao.PayrollDAO;
import com.payroll.model.Employee;
import com.payroll.model.Payroll;
import com.payroll.service.PayrollCalculator;
import com.payroll.service.PayslipPrinter;
import com.payroll.util.InputHelper;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    private static final EmployeeDAO empDao = new EmployeeDAO();
    private static final PayrollDAO payDao = new PayrollDAO();

    public static void main(String[] args) {
        System.out.println("=== EMPLOYEE PAYROLL MANAGEMENT SYSTEM ===");
        while (true) {
            System.out.println("\n1. Add employee");
            System.out.println("2. View all employees");
            System.out.println("3. Search employee by name");
            System.out.println("4. Update employee");
            System.out.println("5. Delete employee");
            System.out.println("6. Generate payroll (one employee)");
            System.out.println("7. Generate payroll (all active employees)");
            System.out.println("8. View payslip");
            System.out.println("9. Employee payroll history");
            System.out.println("10. Monthly payroll report");
            System.out.println("0. Exit");
            int choice = InputHelper.integer("Choose: ", 0, 10);
            try {
                switch (choice) {
                    case 1: addEmployee(); break;
                    case 2: showEmployees(empDao.findAll()); break;
                    case 3: showEmployees(empDao.searchByName(InputHelper.str("Name contains: "))); break;
                    case 4: updateEmployee(); break;
                    case 5: deleteEmployee(); break;
                    case 6: generateOne(); break;
                    case 7: generateAll(); break;
                    case 8: viewPayslip(); break;
                    case 9: history(); break;
                    case 10: report(); break;
                    case 0: System.out.println("Goodbye!"); return;
                }
            } catch (SQLException ex) {
                System.out.println("Database error: " + friendly(ex));
            }
        }
    }

    private static String friendly(SQLException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) return "That email already exists.";
        return ex.getMessage();
    }

    private static void addEmployee() throws SQLException {
        String name = InputHelper.str("Name: ");
        String email = InputHelper.email("Email: ");
        String phone = InputHelper.phone("Phone (10 digits): ");
        String dept = InputHelper.str("Department: ");
        String desig = InputHelper.str("Designation: ");
        LocalDate join = InputHelper.date("Join date (YYYY-MM-DD): ");
        double basic = InputHelper.decimal("Basic salary (monthly): ", 1);
        int id = empDao.add(new Employee(0, name, email, phone, dept, desig, join, basic, "ACTIVE"));
        System.out.println("Employee added with ID " + id);
    }

    private static void showEmployees(List<Employee> list) {
        if (list.isEmpty()) { System.out.println("No employees found."); return; }
        System.out.printf("%n%-5s %-20s %-12s %-20s %-11s %12s %-8s%n",
                "ID", "Name", "Dept", "Designation", "Joined", "Basic", "Status");
        for (Employee e : list) {
            System.out.printf("%-5d %-20s %-12s %-20s %-11s %12.2f %-8s%n", e.getId(), cut(e.getName(), 20),
                    cut(e.getDepartment(), 12), cut(e.getDesignation(), 20), e.getJoinDate(), e.getBasicSalary(), e.getStatus());
        }
    }

    private static String cut(String s, int n) { return s.length() <= n ? s : s.substring(0, n - 1) + "."; }

    private static Employee pick(String prompt) throws SQLException {
        int id = InputHelper.integer(prompt, 1, Integer.MAX_VALUE);
        Employee e = empDao.findById(id);
        if (e == null) System.out.println("No employee with ID " + id);
        return e;
    }

    private static void updateEmployee() throws SQLException {
        Employee e = pick("Employee ID to update: ");
        if (e == null) return;
        System.out.println("Press Enter to keep the current value.");
        String v;
        if (!(v = InputHelper.optional("Name [" + e.getName() + "]: ")).isEmpty()) e.setName(v);
        if (!(v = InputHelper.optional("Email [" + e.getEmail() + "]: ")).isEmpty()) {
            if (v.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) e.setEmail(v); else System.out.println("  Invalid email, kept old.");
        }
        if (!(v = InputHelper.optional("Phone [" + e.getPhone() + "]: ")).isEmpty()) {
            if (v.matches("\\d{10}")) e.setPhone(v); else System.out.println("  Invalid phone, kept old.");
        }
        if (!(v = InputHelper.optional("Department [" + e.getDepartment() + "]: ")).isEmpty()) e.setDepartment(v);
        if (!(v = InputHelper.optional("Designation [" + e.getDesignation() + "]: ")).isEmpty()) e.setDesignation(v);
        if (!(v = InputHelper.optional("Basic salary [" + e.getBasicSalary() + "]: ")).isEmpty()) {
            try {
                double b = Double.parseDouble(v);
                if (b > 0) e.setBasicSalary(b); else System.out.println("  Must be positive, kept old.");
            } catch (NumberFormatException ex) { System.out.println("  Invalid number, kept old."); }
        }
        if (!(v = InputHelper.optional("Status ACTIVE/INACTIVE [" + e.getStatus() + "]: ")).isEmpty()) {
            if (v.equalsIgnoreCase("ACTIVE") || v.equalsIgnoreCase("INACTIVE")) e.setStatus(v.toUpperCase());
            else System.out.println("  Invalid status, kept old.");
        }
        System.out.println(empDao.update(e) ? "Employee updated." : "Update failed.");
    }

    private static void deleteEmployee() throws SQLException {
        Employee e = pick("Employee ID to delete: ");
        if (e == null) return;
        String ok = InputHelper.str("Delete " + e.getName() + " and all payroll records? (yes/no): ");
        if (ok.equalsIgnoreCase("yes")) System.out.println(empDao.delete(e.getId()) ? "Deleted." : "Delete failed.");
        else System.out.println("Cancelled.");
    }

    private static void generateOne() throws SQLException {
        Employee e = pick("Employee ID: ");
        if (e == null) return;
        if (!"ACTIVE".equals(e.getStatus())) { System.out.println("Employee is inactive."); return; }
        int month = InputHelper.integer("Month (1-12): ", 1, 12);
        int year = InputHelper.integer("Year: ", 2000, 2100);
        int wd = InputHelper.integer("Working days in month (1-31): ", 1, 31);
        int absent = InputHelper.integer("Days absent (0-" + wd + "): ", 0, wd);
        double ot = InputHelper.decimal("Overtime hours: ", 0);
        Payroll p = PayrollCalculator.calculate(e, month, year, wd, absent, ot);
        payDao.save(p);
        PayslipPrinter.printPayslip(p);
    }

    private static void generateAll() throws SQLException {
        int month = InputHelper.integer("Month (1-12): ", 1, 12);
        int year = InputHelper.integer("Year: ", 2000, 2100);
        int wd = InputHelper.integer("Working days in month (1-31): ", 1, 31);
        List<Employee> list = empDao.findActive();
        if (list.isEmpty()) { System.out.println("No active employees."); return; }
        for (Employee e : list) {
            int absent = InputHelper.integer(e.getName() + " - days absent (0-" + wd + "): ", 0, wd);
            double ot = InputHelper.decimal(e.getName() + " - overtime hours: ", 0);
            payDao.save(PayrollCalculator.calculate(e, month, year, wd, absent, ot));
        }
        System.out.println("Payroll generated for " + list.size() + " employees.");
        PayslipPrinter.printReport(payDao.byMonth(month, year), month, year);
    }

    private static void viewPayslip() throws SQLException {
        int id = InputHelper.integer("Employee ID: ", 1, Integer.MAX_VALUE);
        int month = InputHelper.integer("Month (1-12): ", 1, 12);
        int year = InputHelper.integer("Year: ", 2000, 2100);
        Payroll p = payDao.find(id, month, year);
        if (p == null) System.out.println("No payroll found for that employee/month. Generate it first.");
        else PayslipPrinter.printPayslip(p);
    }

    private static void history() throws SQLException {
        int id = InputHelper.integer("Employee ID: ", 1, Integer.MAX_VALUE);
        List<Payroll> list = payDao.historyOf(id);
        if (list.isEmpty()) System.out.println("No payroll history.");
        else PayslipPrinter.printHistory(list);
    }

    private static void report() throws SQLException {
        int month = InputHelper.integer("Month (1-12): ", 1, 12);
        int year = InputHelper.integer("Year: ", 2000, 2100);
        List<Payroll> list = payDao.byMonth(month, year);
        if (list.isEmpty()) System.out.println("No payroll generated for that month.");
        else PayslipPrinter.printReport(list, month, year);
    }
}
