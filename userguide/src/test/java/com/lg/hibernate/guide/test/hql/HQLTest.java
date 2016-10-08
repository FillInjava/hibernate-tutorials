package com.lg.hibernate.guide.test.hql;

import com.lg.hibernate.guide.test.BaseEntityManagerFunctionalTestCase;
import com.lg.hibernate.userguide.model.Call;
import com.lg.hibernate.userguide.model.Person;
import com.lg.hibernate.userguide.model.Phone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by liuguo on 2016/9/26.
 */
public class HQLTest extends BaseEntityManagerFunctionalTestCase{
    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{
                Person.class,
                Phone.class,
                Call.class
        };
    }

    @Before
    public void init(){
        doInJPA(this::entityManagerFactory,entityManager -> {
            entityManager.persist(new Person("lg"));

            Person p1 = new Person();
            Person p2 = new Person();

            entityManager.persist(p1);
            entityManager.persist(p2);
        });
    }

    @Test
    public void testQueryAndTypedQuery() throws Exception {
        doInJPA(this::entityManagerFactory,entityManager -> {
            //tag::jpql-query-test
            Query query = entityManager
                    .createQuery("select p from Person p where p.name = :name")
                    .setParameter("name","lg");

            Person p = (Person) query.getSingleResult();

            assertNotNull(p);
            //end::jpql-query-test

            //tag:: jpql-typedQuery-test
            TypedQuery<Person> typedQuery = entityManager
                    .createQuery("select p from Person p where p.name = :name",Person.class)
                    .setParameter("name","lg");

            Person p2 = typedQuery.getSingleResult();

            assertNotNull(p2);

        });

    }

    @Test
    public void testNamedNativeQuery() throws Exception {
        doInJPA(this::entityManagerFactory,entityManager -> {
           Query query = entityManager
                   .createNamedQuery("find_person_by_name")
                   .setParameter("name","lg");

            Person p = (Person) query.getSingleResult();
            assertNotNull(p);

            TypedQuery<Person> typedQuery = entityManager
                    .createNamedQuery("find_person_by_name",Person.class)
                    .setParameter("name","lg");

            Person p2 = typedQuery.getSingleResult();

            assertNotNull(p2);

        });

    }

    @Test
    public void testJPAParameterBind() throws Exception {
        doInJPA(this::entityManagerFactory,entityManager -> {
           Query query = entityManager
                   .createQuery("select p from Person p where p.name like :name")
                   .setParameter("name","l%");

            List list = query.getResultList();

            assertTrue(list.size() >0 );
        });

    }
}
