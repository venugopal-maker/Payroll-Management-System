package com.payroll.dao;

import com.payroll.model.Payroll;
import com.payroll.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    /** Inserts a payroll record, or overwrites it if one already exists for that employee/month/year. */
    public void save(Payroll p) throws SQLException {
        String sql = "INSERT INTO payroll (emp_id,pay_month,pay_year,working_days,days_absent,overtime_hours,basic,hra,da,ta,"
                + "overtime_pay,gross_salary,pf,professional_tax,income_tax,absent_deduction,total_deductions,net_salary) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE working_days=VALUES(working_days),days_absent=VALUES(days_absent),"
                + "overtime_hours=VALUES(overtime_hours),basic=VALUES(basic),hra=VALUES(hra),da=VALUES(da),ta=VALUES(ta),"
                + "overtime_pay=VALUES(overtime_pay),gross_salary=VALUES(gross_salary),pf=VALUES(pf),"
                + "professional_tax=VALUES(professional_tax),income_tax=VALUES(income_tax),"
                + "absent_deduction=VALUES(absent_deduction),total_deductions=VALUES(total_deductions),"
                + "net_salary=VALUES(net_salary),generated_on=CURRENT_TIMESTAMP";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setInt(i++, p.empId);
            ps.setInt(i++, p.month);
            ps.setInt(i++, p.year);
            ps.setInt(i++, p.workingDays);
            ps.setInt(i++, p.daysAbsent);
            ps.setDouble(i++, p.overtimeHours);
            ps.setDouble(i++, p.basic);
            ps.setDouble(i++, p.hra);
            ps.setDouble(i++, p.da);
            ps.setDouble(i++, p.ta);
            ps.setDouble(i++, p.overtimePay);
            ps.setDouble(i++, p.gross);
            ps.setDouble(i++, p.pf);
            ps.setDouble(i++, p.professionalTax);
            ps.setDouble(i++, p.incomeTax);
            ps.setDouble(i++, p.absentDeduction);
            ps.setDouble(i++, p.totalDeductions);
            ps.setDouble(i, p.net);
            ps.executeUpdate();
        }
    }

    public Payroll find(int empId, int month, int year) throws SQLException {
        List<Payroll> l = query(BASE + "WHERE p.emp_id=? AND p.pay_month=? AND p.pay_year=?", empId, month, year);
        return l.isEmpty() ? null : l.get(0);
    }

    public List<Payroll> historyOf(int empId) throws SQLException {
        return query(BASE + "WHERE p.emp_id=? ORDER BY p.pay_year DESC, p.pay_month DESC", empId);
    }

    public List<Payroll> byMonth(int month, int year) throws SQLException {
        return query(BASE + "WHERE p.pay_month=? AND p.pay_year=? ORDER BY e.emp_id", month, year);
    }

    private static final String BASE =
            "SELECT p.*, e.name, e.department FROM payroll p JOIN employees e ON e.emp_id=p.emp_id ";

    private List<Payroll> query(String sql, int... params) throws SQLException {
        List<Payroll> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setInt(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payroll p = new Payroll();
                    p.payrollId = rs.getInt("payroll_id");
                    p.empId = rs.getInt("emp_id");
                    p.month = rs.getInt("pay_month");
                    p.year = rs.getInt("pay_year");
                    p.workingDays = rs.getInt("working_days");
                    p.daysAbsent = rs.getInt("days_absent");
                    p.overtimeHours = rs.getDouble("overtime_hours");
                    p.basic = rs.getDouble("basic");
                    p.hra = rs.getDouble("hra");
                    p.da = rs.getDouble("da");
                    p.ta = rs.getDouble("ta");
                    p.overtimePay = rs.getDouble("overtime_pay");
                    p.gross = rs.getDouble("gross_salary");
                    p.pf = rs.getDouble("pf");
                    p.professionalTax = rs.getDouble("professional_tax");
                    p.incomeTax = rs.getDouble("income_tax");
                    p.absentDeduction = rs.getDouble("absent_deduction");
                    p.totalDeductions = rs.getDouble("total_deductions");
                    p.net = rs.getDouble("net_salary");
                    p.empName = rs.getString("name");
                    p.department = rs.getString("department");
                    list.add(p);
                }
            }
        }
        return list;
    }
}
