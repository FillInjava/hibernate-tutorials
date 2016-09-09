package com.lg.hibernate.tutorials;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Date;
import java.util.List;

/**
 * Created by liuguo on 2016/9/9.
 */
public class BaseicTest extends TestCase {
    // A SessionFactory is set up once for an application!
    private SessionFactory sessionFactory;

    @Override
    protected void setUp() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()// configures settings from hibernate.cfg.xml
                .build();

        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }

    }

    @Override
    public void tearDown() throws Exception {
       if(sessionFactory!=null){
           sessionFactory.close();
       }
    }

    public void testBasicAddUsage(){
        // create a couple of events...
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save( new Event( "Our very first event!", new Date() ) );
        session.save( new Event( "A follow up event", new Date() ) );
        session.getTransaction().commit();
        session.close();

        testBasicQueryUsage();

    }

    public void testBasicQueryUsage(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        String hql = "from Event";
        List<Event> events = session.createQuery(hql).list();
        events.forEach(event -> {
            System.out.println("Event (" + event.getDate() + ") : " +event.getTitle() );
        });

        session.getTransaction().commit();
        session.close();

    }

}
