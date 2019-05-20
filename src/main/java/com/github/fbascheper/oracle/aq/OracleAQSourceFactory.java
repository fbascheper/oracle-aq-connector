package com.github.fbascheper.oracle.aq;

import oracle.jms.AQjmsFactory;

import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import javax.jms.XAQueueConnectionFactory;
import javax.jms.XATopicConnectionFactory;
import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Objects;

public class OracleAQSourceFactory implements ObjectFactory {

    public static final String REF_DATA_SOURCE_NAME = "refDataSourceName";
    public static final String REF_DATA_SOURCE_FACTORY = "refDataSourceFactory";
    public static final String REF_DATA_SOURCE_TYPE = "refDataSourceType";

    public OracleAQSourceFactory() {
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        Object result;

        Reference reference = (Reference) obj;
        String referenceClassName = reference.getClassName();

        if (QueueConnectionFactory.class.getName().equals(referenceClassName)) {
            DataSource dataSource = (DataSource) getDataSource(reference, name, nameCtx, environment);
            result = AQjmsFactory.getQueueConnectionFactory(dataSource);

        } else if (XAQueueConnectionFactory.class.getName().equals(referenceClassName)) {
            XADataSource xaDataSource = (XADataSource) getDataSource(reference, name, nameCtx, environment);
            result = AQjmsFactory.getXAQueueConnectionFactory(xaDataSource);

        } else if (TopicConnectionFactory.class.getName().equals(referenceClassName)) {
            DataSource dataSource = (DataSource) getDataSource(reference, name, nameCtx, environment);
            result = AQjmsFactory.getTopicConnectionFactory(dataSource);

        } else if (XATopicConnectionFactory.class.getName().equals(referenceClassName)) {
            XADataSource xaDataSource = (XADataSource) getDataSource(reference, name, nameCtx, environment);
            result = AQjmsFactory.getXATopicConnectionFactory(xaDataSource);

        } else {
            throw new NamingException("Invalid reference class name '" + referenceClassName + "' provided");
        }

        return result;
    }

    private CommonDataSource getDataSource(Reference reference, Name name, Context nameCtx, Hashtable<?, ?> environment) throws NamingException {
        CommonDataSource result;

        StringRefAddr refAddrDataSourceName = (StringRefAddr) reference.get(REF_DATA_SOURCE_NAME);
        StringRefAddr refAddrDataSourceFactory = (StringRefAddr) reference.get(REF_DATA_SOURCE_FACTORY);

        if (refAddrDataSourceName != null) {
            String refDataSourceName = (String) refAddrDataSourceName.getContent();
            result = (CommonDataSource) nameCtx.lookup(refDataSourceName);

        } else if (refAddrDataSourceFactory != null) {
            String dsFactoryClassName = (String) refAddrDataSourceFactory.getContent();
            result = buildDataSource(dsFactoryClassName, reference, name, nameCtx, environment);


        } else {
            throw new NamingException("Either a '" + REF_DATA_SOURCE_NAME + " or a '" + REF_DATA_SOURCE_FACTORY + "' should have been provided");
        }

        Objects.requireNonNull(result, "DataSource should not have a null value");
        return result;
    }

    private CommonDataSource buildDataSource(String dsFactoryClassName, Reference reference, Name name, Context nameCtx, Hashtable<?, ?> environment) {
        try {
            ObjectFactory dataSourceObjectFactory = (ObjectFactory) Class.forName(dsFactoryClassName).newInstance();
            StringRefAddr refAddrDataSourceType = (StringRefAddr) reference.get(REF_DATA_SOURCE_TYPE);

            String dataSourceClassName = (String) refAddrDataSourceType.getContent();

            Reference reference1 = new Reference(dataSourceClassName, null, null);
            Enumeration<RefAddr> a = reference.getAll();
            while (a.hasMoreElements()) {
                reference1.add(a.nextElement());
            }

            Object result = dataSourceObjectFactory.getObjectInstance(reference1, name, nameCtx, environment);

            return (CommonDataSource) result;

        } catch (Exception ex) {
            throw new IllegalArgumentException("Could not build DataSource from factory " + dsFactoryClassName, ex);
        }

    }

}
