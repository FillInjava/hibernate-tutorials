package com.lg.hibernate.guide.test.Fetching;

import com.lg.hibernate.guide.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.annotations.NaturalId;
import org.jboss.logging.Logger;
import org.junit.Test;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by liuguo on 2016/9/20.
 */
public class FetchingTest extends BaseEntityManagerFunctionalTestCase{

    private static final Logger log = Logger.getLogger( FetchingTest.class );

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{
            Department.class,
                Employee.class,
                Project.class
        };
    }

    @Test
    public void test() throws Exception {
        doInJPA(this::entityManagerFactory,entityManager -> {
            Department department = new Department();
            department.id = 1L;
            entityManager.persist( department );

            Employee employee1 = new Employee();
            employee1.id = 1L;
            employee1.username = "user1";
            employee1.password = "3fabb4de8f1ee2e97d7793bab2db1116";
            employee1.accessLevel = 0;
            employee1.department = department;
            entityManager.persist( employee1 );

            Employee employee2 = new Employee();
            employee2.id = 2L;
            employee2.username = "user2";
            employee2.password = "3fabb4de8f1ee2e97d7793bab2db1116";
            employee2.accessLevel = 1;
            employee2.department = department;
            entityManager.persist( employee2 );
        });

        doInJPA(this::entityManagerFactory,entityManager -> {
            String username = "user1";
            String password = "3fabb4de8f1ee2e97d7793bab2db1116";

            Employee employee =  entityManager.createQuery("select e From Employee e where e.username=:username and e.password=:password",Employee.class)
                    .setParameter("username",username)
                    .setParameter("password",password)
                    .getSingleResult();

            assertNotNull(employee);

        });

        doInJPA(this::entityManagerFactory,entityManager -> {
            String username = "user1";
            String password = "3fabb4de8f1ee2e97d7793bab2db1116";

            Integer accessLevel = entityManager.createQuery("select e.accessLevel from Employee e " +
                    "where e.username=:username and e.password=:password",Integer.class)
                    .setParameter("username",username)
                    .setParameter("password",password)
                    .getSingleResult();

            assertEquals(Integer.valueOf(0),accessLevel);
        });

        doInJPA( this::entityManagerFactory, entityManager -> {
            String username = "user1";
            String password = "3fabb4de8f1ee2e97d7793bab2db1116";
            //tag::fetching-strategies-dynamic-fetching-jpql-example[]
            Employee employee = entityManager.createQuery(
                    "select e " +
                            "from Employee e " +
                            "left join fetch e.projects " +
                            "where " +
                            "	e.username = :username and " +
                            "	e.password = :password",
                    Employee.class)
                    .setParameter( "username", username)
                    .setParameter( "password", password)
                    .getSingleResult();
            //end::fetching-strategies-dynamic-fetching-jpql-example[]
            assertNotNull(employee);
        } );

        doInJPA(this::entityManagerFactory,entityManager -> {
            String username = "user1";
            String password = "3fabb4de8f1ee2e97d7793bab2db1116";
            //tag::fetching-strategies-dynamic-fetching-criteria-example[]

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);

            Root<Employee> root = criteriaQuery.from(Employee.class);

            root.fetch("projects", JoinType.LEFT);

            criteriaQuery.select(root).where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("username"),username),
                        criteriaBuilder.equal(root.get("password"),password)
                )
            );

            Employee employee = entityManager.createQuery(criteriaQuery).getSingleResult();

            assertNotNull(employee);

        });


    }

    @Entity(name = "Department")
    public static class Department{

        @Id
        private Long id;

        @OneToMany(mappedBy = "department")
        private List<Employee> employees = new ArrayList<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<Employee> getEmployees() {
            return employees;
        }

        public void setEmployees(List<Employee> employees) {
            this.employees = employees;
        }
    }

    @Entity(name = "Employee")
    public static class Employee{
        @Id
        private Long id;

        @NaturalId
        private String username;

        @Column(name = "pwsd")
        //@ColumnTransformer(
        //        read = "decrypt( 'AES', '00', pswd  )",
        //        write = "encrypt('AES', '00', ?)"
        //)
        private String password;

        private int accessLevel;

        @ManyToOne(fetch = FetchType.LAZY)
        private Department department;

        @ManyToMany(mappedBy = "employees")
        private List<Project> projects = new ArrayList<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getAccessLevel() {
            return accessLevel;
        }

        public void setAccessLevel(int accessLevel) {
            this.accessLevel = accessLevel;
        }

        public Department getDepartment() {
            return department;
        }

        public void setDepartment(Department department) {
            this.department = department;
        }

        public List<Project> getProjects() {
            return projects;
        }

        public void setProjects(List<Project> projects) {
            this.projects = projects;
        }
    }

    @Entity(name = "Project")
    public static class Project{
        @Id
        private Long id;

        @ManyToMany
        private List<Employee> employees = new ArrayList<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<Employee> getEmployees() {
            return employees;
        }

        public void setEmployees(List<Employee> employees) {
            this.employees = employees;
        }
    }
}
