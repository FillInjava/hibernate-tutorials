package com.lg.hibernate.guide.test.hql;

import com.lg.hibernate.guide.test.BaseEntityManagerFunctionalTestCase;
import com.lg.hibernate.userguide.model.*;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertEquals;
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
                Call.class,
                CreditCardPayment.class,
                WireTransferPayment.class
        };
    }

    @Before
    public void init(){
        doInJPA(this::entityManagerFactory,entityManager -> {

            Person p1 = new Person("liuguo");
            p1.setNickName("lg");
            p1.setAddress("Earth");
            //转成亚洲的时间
            p1.setCreatedOn(Timestamp.from(LocalDateTime.of(2016,1,1,0,0,0).toInstant(ZoneOffset.of("+8"))));
            p1.getAddresses().put(AddressType.HOME,"Home Address");
            p1.getAddresses().put(AddressType.OFFICE,"Offoce Address");
            entityManager.persist(p1);

            Person p2 = new Person("Mr.Jack");
            p2.setAddress("Earth");
            p2.setCreatedOn(Timestamp.from(LocalDateTime.of(2016,2,1,22,58,10).toInstant(ZoneOffset.of("+8"))));
            entityManager.persist(p2);

            Person person3 = new Person("Dr_ John Doe" );
            entityManager.persist(person3);

            Phone phone1 = new Phone( "123-456-7890" );
            phone1.setId( 1L );
            phone1.setType( PhoneType.MOBILE );
            phone1.getRepairTimestamps().add( Timestamp.from( LocalDateTime.of( 2016, 1, 1, 12, 0, 0 ).toInstant(ZoneOffset.of("+8")) ) );
            phone1.getRepairTimestamps().add( Timestamp.from( LocalDateTime.of( 2016, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.of("+8") ) ) );
            p1.addPhone(phone1);

            Call call11 = new Call();
            call11.setDuration( 12 );
            call11.setTimestamp( Timestamp.from( LocalDateTime.of( 2016, 1, 1, 0, 0, 0 ).toInstant( ZoneOffset.UTC ) ) );

            Call call12 = new Call();
            call12.setDuration( 33 );
            call12.setTimestamp( Timestamp.from( LocalDateTime.of( 2016, 1, 1, 1, 0, 0 ).toInstant( ZoneOffset.UTC ) ) );

            phone1.addCall(call11);
            phone1.addCall(call12);

            Phone phone2 = new Phone( "098-765-4321" );
            phone2.setId( 2L );
            phone2.setType( PhoneType.LAND_LINE );

            Phone phone3 = new Phone( "098-765-4320" );
            phone3.setId( 3L );
            phone3.setType( PhoneType.LAND_LINE );

            p2.addPhone( phone2 );
            p2.addPhone( phone3 );

            CreditCardPayment creditCardPayment = new CreditCardPayment();
            creditCardPayment.setCompleted( true );
            creditCardPayment.setAmount( BigDecimal.ZERO );
            creditCardPayment.setPerson( p1 );

            WireTransferPayment wireTransferPayment = new WireTransferPayment();
            wireTransferPayment.setCompleted( true );
            wireTransferPayment.setAmount( BigDecimal.valueOf( 100 ) );
            wireTransferPayment.setPerson( p2 );

            entityManager.persist( creditCardPayment );
            entityManager.persist( wireTransferPayment );

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

    @Test
    public void testInnerJOIN() throws Exception {
        doInJPA(this::entityManagerFactory,entityManager -> {
            List<Person> persons = entityManager
                    .createQuery(
                            "select distinct pr " +
                            "from Person pr " +
                            "join pr.phones ph " +
                            "where ph.type=:phoneType",Person.class)
                    .setParameter("phoneType", PhoneType.MOBILE)
                    .getResultList();
        });

    }

    @Test
    public void test_hql_collection_qualification_associations_1() {

        doInJPA( this::entityManagerFactory, entityManager -> {
            Long id = 1L;
            //tag::hql-collection-qualification-example[]

            // select all the calls (the map value) for a given Phone
            List<Call> calls = entityManager.createQuery(
                    "select ch " +
                            "from Phone ph " +
                            "join ph.callHistory ch " +
                            "where ph.id = :id ", Call.class )
                    .setParameter( "id", id )
                    .getResultList();
            //end::hql-collection-qualification-example[]
            assertEquals(2, calls.size());
        });

    }
}
