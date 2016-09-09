package com.lg.hibernate.userguide.basictype;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import java.util.BitSet;

/**
 * Created by liuguo on 2016/9/9.
 */
public class BitSetType extends AbstractSingleColumnStandardBasicType<BitSet> implements DiscriminatorType<BitSet>{

    public static final BitSetType INSTANCE = new BitSetType();



    public BitSetType() {
        super(VarcharTypeDescriptor.INSTANCE, BitSetTypeDescriptor.INSTANCE);
    }


    @Override
    public BitSet stringToObject(String xml) throws Exception {
       return fromString(xml);
    }

    @Override
    public String objectToSQLString(BitSet value, Dialect dialect) throws Exception {
       return toString( value );
    }

    @Override
    public String getName() {
        return "bitset";
    }
}
