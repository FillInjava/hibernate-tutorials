package com.lg.hibernate.guide.test.cache;

import com.lg.hibernate.guide.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.Session;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.cache.ehcache.EhCacheRegionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.jboss.logging.Logger;
import org.junit.Test;

import javax.persistence.*;
import java.util.Map;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertEquals;

/**
 * Created by liuguo on 2016/9/23.
 */
public class SecondLevelCacheTest extends BaseEntityManagerFunctionalTestCase {

    private static final Logger log = Logger.getLogger( SecondLevelCacheTest.class );

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{
                Person.class
        };
    }

    @Override
    protected void addConfigOptions(Map options) {
        options.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, Boolean.TRUE.toString());
        options.put(AvailableSettings.CACHE_REGION_FACTORY, EhCacheRegionFactory.class.getName());
        options.put(AvailableSettings.USE_QUERY_CACHE, Boolean.TRUE.toString());
        options.put(AvailableSettings.GENERATE_STATISTICS, Boolean.TRUE.toString());
        options.put(AvailableSettings.CACHE_REGION_PREFIX, "");
    }

    @Test
    public void testCache() throws Exception {
        doInJPA(this::entityManagerFactory, entityManager -> {
            entityManager.persist(new Person());
            entityManager.persist(new Person());
            Person p1 = new Person();
            p1.setName("John Doe");
            p1.setCode("unique-code");

            entityManager.persist(p1);

            return p1;
        });

        doInJPA(this::entityManagerFactory,entityManager -> {
            log.info( "Jpa load by id" );
            Person p =  entityManager.find(Person.class,1L);
            log.info("name="+p.getName());
        });

        doInJPA(this::entityManagerFactory,entityManager -> {
            log.info( "Session load by id" );
            Session session = entityManager.unwrap(Session.class);
            Person p =  session.get(Person.class,3L);
            log.info("name="+p.getName());
        });

        doInJPA(this::entityManagerFactory,entityManager -> {
            log.info( "Native load by natural-id" );
            Session session = entityManager.unwrap(Session.class);
            Person p =  session
                    .byNaturalId(Person.class)
                    .using("code","unique-code")
                    .load();

            assertEquals("John Doe",p.getName());
        });


    }

    @Entity(name = "Person")
    @Cacheable
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class Person {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private String name;
        @NaturalId
        @Column(name = "code", unique = true)
        private String code;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

    }
}
