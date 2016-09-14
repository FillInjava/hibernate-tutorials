package com.lg.hibernate.guide.test;

import com.lg.hibernate.userguide.basictype.BitProduct;
import com.lg.hibernate.userguide.basictype.BitSetType;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import java.util.BitSet;

import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;
import static org.junit.Assert.assertEquals;

/**
 * Created by liuguo on 2016/9/9.
 */
public class BitSetTypeTest extends BaseCoreFunctionalTestCase{


    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{
                BitProduct.class
        };
    }

    @Override
    protected Configuration constructAndConfigureConfiguration() {
        Configuration configuration = super.constructAndConfigureConfiguration();

        configuration.registerTypeContributor(((typeContributions, serviceRegistry) -> {
            typeContributions.contributeType(BitSetType.INSTANCE);
        }));

        return configuration;
    }

    @Test
    public void test() throws Exception {
        //tag::basic-custom-type-BitSetType-persistence-example[]
        BitSet bitSet = BitSet.valueOf( new long[] {1, 2, 3} );

        doInHibernate(this::sessionFactory, session->{
            BitProduct product = new BitProduct();
            product.setId(1);
            product.setBitSet(bitSet);

            session.save(product);
        } );

        doInHibernate(this::sessionFactory,session -> {
            BitProduct product = session.get(BitProduct.class,1);
            assertEquals(bitSet,product.getBitSet());
        });
    }
}
