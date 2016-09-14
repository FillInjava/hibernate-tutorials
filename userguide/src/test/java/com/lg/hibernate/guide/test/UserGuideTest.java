package com.lg.hibernate.guide.test;

import com.lg.hibernate.userguide.entity.Contact;
import com.lg.hibernate.userguide.entity.Name;
import com.lg.hibernate.userguide.entity.Product;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

/**
 * Created by liuguo on 2016/9/9.
 */
public class UserGuideTest extends TestCase{

    private static SessionFactory sessionFactory;

    /**
     * 初始化方法
     * 用户初始化SessionFactory
     * @throws Exception
     */
    @Override
    public void setUp() throws Exception {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
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

    /**
     * 应用关闭时关闭连接
     * @throws Exception
     */
    @Override
    public void tearDown() throws Exception {
        if(sessionFactory!=null){
            sessionFactory.close();
        }
    }

    /**
     * 测试添加Concat
     * @throws Exception
     */
    public void testAddConcat() throws Exception{
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Name name = new Name("smith","bloosg","li");

        session.save(new Contact(name,"note1","http://localhost/tpp",false));
        session.save(new Contact(name,"note2","http://localhost/tpp2",true));

        session.getTransaction().commit();
        session.close();
    }

    /**
     * 测试查询Concat
     * @throws Exception
     */
    public void testQueryConcat() throws Exception{
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Contact> contacts = session.createQuery("from Contact").list();

        contacts.forEach(contact -> System.out.println(contact.toString()));

        session.getTransaction().commit();
        session.close();
    }

    /**
     * 测试添加product
     * @throws Exception
     */
    public void testAddProduct() throws Exception{
        Session session = sessionFactory.openSession();
        session.beginTransaction();


        session.save(new Product("aa","falali","hello"));
        session.save(new Product("bb","zzz","hello world"));

        session.getTransaction().commit();
        session.close();
    }

    /**
     * 测试擦和讯Product
     * @throws Exception
     */
    public void testQueryProduct() throws Exception{

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Product> products = session.createQuery("from Product").list();

        products.forEach(product -> System.out.println(product.toString()));

        session.getTransaction().commit();
        session.close();
    }



}
