package com.lg.hibernate.guide.test.flush;

import com.lg.hibernate.guide.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.jboss.logging.Logger;
import org.junit.Test;

import javax.persistence.*;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertTrue;

/**
 * Created by liuguo on 2016/9/18.
 */
public class AutoFlush extends BaseEntityManagerFunctionalTestCase{

    private final static Logger log = Logger.getLogger(AutoFlush.class);

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{
            Person.class, Advertisement.class
        };
    }

    @Test
    public void testFlushAutoCommit(){

        //Session session = null;
        EntityTransaction txn = null;
        EntityManager entityManager = null;
        try {
            //session = this.sessionFactory().openSession();
            entityManager = this.entityManagerFactory().createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();

            Person person = new Person( "John Doe" );

            entityManager.persist(person);
            log.info( "Entity is in persisted state" );

            txn.commit();
        } catch (HibernateException e) {
            if ( txn != null && txn.isActive()) txn.rollback();
            throw e;
        } finally {
            if(entityManager != null){
                entityManager.close();
            }
        }


    }

    @Test
    public void testFlushAutoJPQL() throws Exception {
        doInJPA(this::entityManagerFactory,entityManager -> {
            Person person = new Person( "John Doe" );
            entityManager.persist(person);

            entityManager.createQuery("select p from Advertisement p").getResultList();
            entityManager.createQuery("select p from Person p").getResultList();
        });

    }

    @Test
    public void testFlushAutoJPQLOverlap() throws Exception {
        doInJPA(this::entityManagerFactory,entityManager -> {
            Person person = new Person( "John Doe" );
            entityManager.persist(person);

            entityManager.createQuery("select p from Person p").getResultList();
        });

    }

    /**
     * When executing a native SQL query, a flush is always triggered when using the EntityManager API
     */
    @Test
    public void testFlushAutoSQL() {
        doInJPA(this::entityManagerFactory,entityManager -> {
            entityManager.createNativeQuery("delete from Person").executeUpdate();
        });

        doInJPA(this::entityManagerFactory,entityManager -> {
            log.info( "testFlushAutoSQL" );

            assertTrue(((Number)entityManager.createNativeQuery("select count(*) from Person").getSingleResult()).intValue() == 0);

            Person person = new Person( "John Doe" );
            entityManager.persist( person );

            Query<Number> query = (Query<Number>) entityManager.createNativeQuery("select count(*) from Person");

            assertTrue(query.getSingleResult().intValue() == 1);
        });
    }

    @Test
    public void testFlushAutoSQLNativeSession() throws Exception {
        doInJPA(this::entityManagerFactory, entityManager -> {
            entityManager.createNativeQuery("DELETE from Person").executeUpdate();
        });

        doInJPA(this::entityManagerFactory, entityManager -> {
            log.info( "testFlushAutoSQLNativeSession" );
            assertTrue(((Number) entityManager.createNativeQuery("select count(*) from Person").getSingleResult()).intValue() == 0);

            Person person = new Person("John Doe");
            entityManager.persist( person );

            Session session = entityManager.unwrap(Session.class);
            session.setFlushMode(FlushModeType.COMMIT);

            assertTrue(((Number) session
                    .createNativeQuery( "select count(*) from Person")
                    .uniqueResult()).intValue() == 0 );
        });


    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;
        private String name;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

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
    }

    @Entity(name = "Advertisement")
    public static class Advertisement{
        @Id
        @GeneratedValue
        private Long id;

        private String title;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
