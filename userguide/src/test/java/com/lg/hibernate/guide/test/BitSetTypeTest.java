package com.lg.hibernate.guide.test;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.BitSet;

/**
 * Created by liuguo on 2016/9/9.
 */
public class BitSetTypeTest extends BaseUnitTestCase{

    private static SessionFactory sessionFactory;

    /**
     * 初始化方法
     * 用户初始化SessionFactory
     * @throws Exception
     */
    @Before
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
    @After
    public void tearDown() throws Exception {
        if(sessionFactory!=null){
            sessionFactory.close();
        }
    }

    @Test
    public void testBitSetType() throws Exception {
        BitSet bitSet = BitSet.valueOf( new long[] {1, 2, 3} );


    }
}
