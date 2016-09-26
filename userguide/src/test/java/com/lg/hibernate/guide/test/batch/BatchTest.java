package com.lg.hibernate.guide.test.batch;

import com.lg.hibernate.guide.test.BaseEntityManagerFunctionalTestCase;

import com.lg.hibernate.guide.test.BaseEntityManagerFunctionalTestCase;
import com.lg.hibernate.userguide.model.Call;
import com.lg.hibernate.userguide.model.Partner;
import com.lg.hibernate.userguide.model.Person;
import com.lg.hibernate.userguide.model.Phone;
import org.hibernate.*;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.jboss.logging.Logger;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertEquals;

/**
 * Created by liuguo on 2016/9/20.
 */
public class BatchTest extends BaseEntityManagerFunctionalTestCase {

    private final static Logger log = Logger.getLogger(BatchTest.class);

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{
                Person.class,
                Phone.class,
                Call.class,
                Partner.class
        };
    }

    @Test
    public void testScroll() {
        withScroll();
    }

    @Test
    public void testStatelessSession() {
        withStatelessSession();
    }

    @Test
    public void testBulk(){
        doInJPA(this::entityManagerFactory,entityManager -> {
            entityManager.persist(new Person("Vlad"));
            entityManager.persist(new Person("Mihalcea"));
        });

        doInJPA(this::entityManagerFactory,entityManager -> {
           entityManager.unwrap(Session.class).setJdbcBatchSize(10);
        });

        doInJPA(this::entityManagerFactory,entityManager -> {
            String oldName = "Vlad";
            String newName = "Alexandru";

            int entities = entityManager
                    .createQuery("update Person p set p.name=:newName where p.name=:oldName")
                    .setParameter("newName",newName)
                    .setParameter("oldName",oldName)
                    .executeUpdate();

            assertEquals(1,entities);
        });

        doInJPA(this::entityManagerFactory,entityManager -> {
            String oldName = "Alexandru";
            String newName = "Vlad";

            int updatedEntities = entityManager
                    .unwrap(Session.class)
                    .createQuery("update Person p set p.name=:newName where p.name=:oldName")
                    .setParameter("newName",newName)
                    .setParameter("oldName",oldName)
                    .executeUpdate();

            assertEquals(1, updatedEntities);
        });

        doInJPA( this::entityManagerFactory, entityManager -> {
            String oldName = "Vlad";
            String newName = "Alexandru";

            Session session = entityManager.unwrap( Session.class );
            //tag::batch-bulk-hql-update-version-example[]
            int updatedEntities = session.createQuery(
                    "update versioned Person " +
                            "set name = :newName " +
                            "where name = :oldName" )
                    .setParameter( "oldName", oldName )
                    .setParameter( "newName", newName )
                    .executeUpdate();
            //end::batch-bulk-hql-update-version-example[]
            assertEquals(1, updatedEntities);
        } );

        doInJPA(this::entityManagerFactory,entityManager -> {
            String name = "Alexandru";

            int deletedEntities = entityManager
                    .createQuery("delete Person p where p.name = :name")
                    .setParameter("name",name)
                    .executeUpdate();

            assertEquals(1,deletedEntities);
        });


    }

    private void withoutBatch(){
        EntityManager entityManager = null;
        EntityTransaction txn = null;

        try {
            entityManager = this.entityManagerFactory().createEntityManager();
            txn = entityManager.getTransaction();

            txn.begin();

            for (int i = 0; i < 100_000; i++) {
                Person p = new Person(String.format("Person %d", i));
                entityManager.persist(p);
            }

            txn.commit();

        } catch (Exception e) {
           if(txn != null && txn.isActive()){
               txn.rollback();
           }
           throw e;
        } finally {

            if(entityManager != null){
                entityManager.close();
            }
        }
    }

    private void withBatch(){
        int entityCount = 100;
        EntityManager entityManager = null;
        EntityTransaction txn = null;

        try {
            entityManager = this.entityManagerFactory().createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();

            int batchSize = 20;

            for (int i = 0; i < entityCount; i++) {
                Person Person = new Person( String.format( "Person %d", i ) );
                entityManager.persist( Person );

                if(i % 20 == 0){
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            txn.commit();

        } catch (Exception e) {
            if(txn != null && txn.isActive()){
                txn.rollback();
            }
        } finally {
            if(entityManager!=null){
                entityManager.close();
            }
        }

    }

    private void withScroll(){
        withBatch();

        EntityManager entityManager = null;
        EntityTransaction txn = null;
        ScrollableResults scrollableResults = null;

        try {
            entityManager = this.entityManagerFactory().createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();

            int batchSize = 25;

            Session session = entityManager.unwrap(Session.class);

            scrollableResults =  session.createQuery("select p from Person p")
                    .setCacheMode(CacheMode.IGNORE)
                    .scroll(ScrollMode.FORWARD_ONLY);

            int count = 0;
            while (scrollableResults.next()){
                Person p = (Person) scrollableResults.get(0);
                processPerson(p);

                if(++count % batchSize == 0){
                    entityManager.flush();
                    entityManager.clear();
                }
            }

            txn.commit();


        } catch (Exception e) {

            if(txn != null && txn.isActive()){
                txn.rollback();
            }
            throw e;
        } finally {
            if(scrollableResults != null){
                scrollableResults.close();
            }
            if(entityManager != null){
                entityManager.close();
            }
        }
    }

    private void withStatelessSession(){
        withBatch();

        SessionFactory sessionFactory = null;
        StatelessSession statelessSession = null;
        ScrollableResults results = null;

        Transaction txn = null;
        try {
            sessionFactory = this.entityManagerFactory().unwrap(SessionFactory.class);
            statelessSession = sessionFactory.openStatelessSession();

            txn = statelessSession.getTransaction();

            txn.begin();

            results = statelessSession
                    .createQuery("select p from Person p")
                    .scroll(ScrollMode.FORWARD_ONLY);

            while (results.next()){
                Person p = (Person) results.get(0);
                processPerson(p);
                statelessSession.update(p);
            }

            txn.commit();
        } catch (Exception e) {
            if(txn != null && txn.getStatus() == TransactionStatus.ACTIVE) txn.rollback();
            throw e;
        } finally {
            if(results != null){
                results.close();
            }
            if(statelessSession != null){
                statelessSession.close();
            }
        }
    }
    private void processPerson(Person Person) {
        if ( Person.getId() % 1000 == 0 ) {
            log.infof( "Processing [%s]", Person.getName());
        }
    }


}
