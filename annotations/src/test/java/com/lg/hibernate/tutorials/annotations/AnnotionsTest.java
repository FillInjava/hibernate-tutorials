package com.lg.hibernate.tutorials.annotations;

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
public class AnnotionsTest extends TestCase{

    private static SessionFactory sessionFactory;
    @Override
    public void setUp() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
           StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Override
    public void tearDown() throws Exception {
        if(sessionFactory!=null){
            sessionFactory.close();
        }
    }

    public void testAdd(){
        //save
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(new Event("test1",new Date()));
        session.save(new Event("test2",new Date()));
        session.getTransaction().commit();
        session.close();
    }

    public void testQuery(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<Event> events =  session.createQuery("from Event").list();

        for (Event event : events){
            System.out.println("Event (" + event.getDate() + ") : " + event.getTitle() );
        }
        session.getTransaction().commit();
        session.close();
    }

}
