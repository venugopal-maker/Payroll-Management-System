package com.payroll.dao;

import com.payroll.model.Employee;
import com.payroll.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public int add(Employee e) throws SQLException {
        String sql = "INSERT INTO employees (name,email,phone,department,designation,join_date,basic_salary,status) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getDepartment());
            ps.setString(5, e.getDesignation());
            ps.setDate(6, Date.valueOf(e.getJoinDate()));
            ps.setDouble(7, e.getBasicSalary());
            ps.setString(8, e.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public boolean update(Employee e) throws SQLException {
        String sql = "UPDATE employees SET name=?,email=?,phone=?,department=?,designation=?,basic_salary=?,status=? WHERE emp_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getDepartment());
            ps.setString(5, e.getDesignation());
            ps.setDouble(6, e.getBasicSalary());
            ps.setString(7, e.getStatus());
            ps.setInt(8, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM employees WHERE emp_id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Employee findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM employees WHERE emp_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Employee> findAll() throws SQLException {
        return query("SELECT * FROM employees ORDER BY emp_id", null);
    }

    public List<Employee> findActive() throws SQLException {
        return query("SELECT * FROM employees WHERE status='ACTIVE' ORDER BY emp_id", null);
    }

    public List<Employee> searchByName(String name) throws SQLException {
        return query("SELECT * FROM employees WHERE name LIKE ? ORDER BY name", "%" + name + "%");
    }

    private List<Employee> query(String sql, String param) throws SQLException {
        List<Employee> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Employee map(ResultSet rs) throws SQLException {
        return new Employee(rs.getInt("emp_id"), rs.getString("name"), rs.getString("email"),
                rs.getString("phone"), rs.getString("department"), rs.getString("designation"),
                rs.getDate("join_date").toLocalDate(), rs.getDouble("basic_salary"), rs.getString("status"));
    }
}
