package com.lg.hibernate.guide.test;

import com.lg.hibernate.userguide.entity.Product;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;

/**
 * Created by liuguo on 2016/9/12.
 */
public class UserGuideTest2 extends BaseCoreFunctionalTestCase{
    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{
            Product.class
        };
    }

    @Test
    public void test() throws Exception {

        doInHibernate(this::sessionFactory,session -> {
            session.persist(new Product("aa","falali","hello"));
            session.persist(new Product("bb","zzz","hello world"));

        });

        doInHibernate(this::sessionFactory,session->{
            List<Product> list =  session.createQuery("from Product").list();
            list.forEach(product -> {
                System.out.println(product.toString());
            });

        });
    }
}
