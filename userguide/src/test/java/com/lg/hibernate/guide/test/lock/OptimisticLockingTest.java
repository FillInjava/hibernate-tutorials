package com.lg.hibernate.guide.test.lock;

import com.lg.hibernate.guide.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

/**
 * Created by liuguo on 2016/9/20.
 */
public class OptimisticLockingTest extends BaseEntityManagerFunctionalTestCase{

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{
            Person.class,
                Phone.class
        };
    }

    @Override
    protected Map getConfig() {
        return super.getConfig();
    }

    @Test
    public void test() throws Exception {
        Phone _phone = doInJPA(this::entityManagerFactory,entityManager -> {
            Person person = new Person();
            person.setName( "John Doe" );
            entityManager.persist( person );

            Phone phone = new Phone();
            phone.setNumber( "123-456-7890" );
            phone.setPerson( person );
            entityManager.persist( phone );

            return phone;
        });

        doInJPA(this::entityManagerFactory,entityManager -> {
            Person person = entityManager.find( Person.class, _phone.getPerson().getId() );
            person.setName( person.getName().toUpperCase() );

            Phone phone = entityManager.find( Phone.class, _phone.getId() );
            phone.setNumber( phone.getNumber().replace( "-", " ") );
        });

    }

    @Entity(name = "Person")
    public static class Person{

        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "`name`")
        private String name;

        //tag::locking-optimistic-version-number-example[]
        @Version
        private long version;
        //end::locking-optimistic-version-number-example[]

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getVersion() {
            return version;
        }

        public void setVersion(long version) {
            this.version = version;
        }

    }
    @Entity(name = "Phone")
    public static class Phone{
        @Id
        @GeneratedValue
        private Long id;

        private String number;

        @ManyToOne
        private Person person;

        //tag::locking-optimistic-version-timestamp-example[]
        @Version
        private Date version;
        //end::locking-optimistic-version-timestamp-example[]


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public Date getVersion() {
            return version;
        }

        public void setVersion(Date version) {
            this.version = version;
        }
    }
}

