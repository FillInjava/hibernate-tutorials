package com.lg.hibernate.guide.test;

import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

/**
 * Created by liuguo on 2016/9/18.
 */
public class BlobTest extends BaseCoreFunctionalTestCase {

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{
            Product.class
        };
    }

    @Test
    public void test() throws Exception {
        doInHibernate(this::sessionFactory,session -> {
            byte[] image = new byte[]{1,2,3};

            final Product product = new Product();
            product.setName("Mobile phone");
            product.setId(1);
            session.doWork(connection -> {
                product.setImage(BlobProxy.generateProxy(image));
            });

            session.save(product);

        });


        doInHibernate(this::sessionFactory,session -> {
            Product product = session.get(Product.class,1);
            try {
                try(InputStream inputstream = product.getImage().getBinaryStream()){
                    assertArrayEquals(new byte[]{1,2,3}, toBytes(inputstream));
                }
            }catch (Exception e) {
                fail(e.getMessage());
            }

        });

    }

    private byte[] toBytes(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int result = bufferedInputStream.read();
        while (result != -1){
            byteArrayOutputStream.write((byte)result);
            result = bufferedInputStream.read();
        }

        return byteArrayOutputStream.toByteArray();
    }

    //tag::basic-blob-example[]
    @Entity(name = "Product")
    public static class Product {

        @Id
        private Integer id;

        private String name;

        @Lob
        private Blob image;

        //Getters and setters are omitted for brevity

        //end::basic-blob-example[]
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Blob getImage() {
            return image;
        }

        public void setImage(Blob image) {
            this.image = image;
        }

        //tag::basic-blob-example[]
    }
}
