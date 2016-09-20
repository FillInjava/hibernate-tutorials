package com.lg.hibernate.guide.test.batch;

import com.lg.hibernate.guide.test.BaseEntityManagerFunctionalTestCase;
import com.lg.hibernate.userguide.model.Call;
import com.lg.hibernate.userguide.model.Partner;
import com.lg.hibernate.userguide.model.Person;
import com.lg.hibernate.userguide.model.Phone;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.jboss.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Created by liuguo on 2016/9/20.
 */
public class BatchTest extends BaseEntityManagerFunctionalTestCase{

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

            session.createQuery("select p from Person p")
                    .setCacheMode(CacheMode.IGNORE)
                    .scroll(ScrollMode.FORWARD_ONLY);


        } catch (Exception e) {

            e.printStackTrace();
        } finally {
        }
    }
}
