package com.lg.hibernate.guide.test;

import org.hibernate.annotations.Type;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.BitSet;

import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;
import static org.junit.Assert.assertEquals;

/**
 * Created by liuguo on 2016/9/18.
 */
public class BitSetUserTypeTest extends BaseCoreFunctionalTestCase{

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{Product2.class};
    }

    @Override
    protected Configuration constructAndConfigureConfiguration() {
        Configuration configuration =  super.constructAndConfigureConfiguration();

        configuration.registerTypeContributor((typeContributions, serviceRegistry) -> {
            typeContributions.contributeType( BitSetUserType.INSTANCE,"bitset");
        });

        return configuration;
    }

    @Test
    public void test() throws Exception {
        BitSet bitSet = BitSet.valueOf( new long[] {1, 2, 3} );

        doInHibernate(this::sessionFactory,session -> {
            Product2 product = new Product2( );
            product.setId( 1 );
            product.setBitSet( bitSet );
            session.persist( product );
        });

        doInHibernate(this::sessionFactory,session -> {
            Product2 product = session.get(Product2.class,1);
            assertEquals(bitSet,product.getBitSet());
        });

    }

    //tag::basic-custom-type-BitSetUserType-mapping-example[]
    @Entity(name = "Product2")
    public static class Product2 {

        @Id
        private Integer id;

        @Type( type = "bitset" )
        private BitSet bitSet;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public BitSet getBitSet() {
            return bitSet;
        }

        public void setBitSet(BitSet bitSet) {
            this.bitSet = bitSet;
        }
    }
}
