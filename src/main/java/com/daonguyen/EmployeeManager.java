package com.daonguyen;

import com.daonguyen.entity.Employee;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EmployeeManager {

    private static SessionFactory factory;

    public static void main(String[] args) {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            System.err.println("Failed to create sessionFactory object." + e);
            throw new ExceptionInInitializerError(e);
        }

        EmployeeManager employeeManager = new EmployeeManager();

        // Add few employees into database
        Integer empID1 = employeeManager.addEmployee("Ty", "Le", 1000);
        Integer empID2 = employeeManager.addEmployee("Teo", "Tran", 3000);
        Integer empID3 = employeeManager.addEmployee("Tin", "Nguyen", 2000);
        Integer empID4 = employeeManager.addEmployee("To", "Dinh", 4000);

        // List of all employees
        employeeManager.listEmployeesScalar();

        // Print total employee's count
        employeeManager.listEmployeesEntity();
    }

    public Integer addEmployee(String fname, String lname, int salary) {
        Session session = factory.openSession();
        Transaction trans = null;
        Integer empID = null;

        try {
            trans = session.beginTransaction();
            Employee employee = new Employee(fname, lname, salary);
            empID = (Integer) session.save(employee);
            trans.commit();
        } catch (HibernateException e) {
            if (trans != null)
                trans.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return empID;
    }

    public void listEmployeesScalar() {
        Session session = factory.openSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            String sql = "SELECT first_name, salary FROM employee";
            SQLQuery query = session.createSQLQuery(sql);
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            List data = query.list();

            for (Object object : data) {
                Map row = (Map) object;
                System.out.print("First Name: " + row.get("first_name"));
                System.out.println("| Salary: " + row.get("salary"));
            }

            trans.commit();
        } catch (HibernateException e) {
            if (trans != null)
                trans.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void listEmployeesEntity() {
        Session session = factory.openSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            String sql = "SELECT * FROM employee";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(Employee.class);
            List employees = query.list();

            for (Iterator iterator = employees.iterator(); iterator.hasNext();) {
                Employee employee = (Employee) iterator.next();
                System.out.print("First Name: " + employee.getFirstName());
                System.out.print("  Last Name: " + employee.getLastName());
                System.out.println("  Salary: " + employee.getSalary());
            }

            trans.commit();
        } catch (HibernateException e) {
            if (trans != null)
                trans.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
