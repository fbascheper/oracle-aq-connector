# Oracle AQ connector 

> A simple connector to use ConnectionFactories for Oracle AQ in Tomcat

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.fbascheper/oracle-aq-connector/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.fbascheper/oracle-aq-connector)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/fbascheper/oracle-aq-connector/master/LICENSE.txt)

***

## Tomcat configuration

Update your Tomcat configuration files in order to use the following JNDI references:

* ``java:/comp/env/jdbc/myDS`` 
* ``java:/comp/env/jms/aqQueueConnectionFactory`` 
 

### Global resources in ``server.xml``

  <GlobalNamingResources>
    <Resource name="java:/comp/env/jdbc/myDS" auth="Container"
                  type="oracle.jdbc.xa.client.OracleXADataSource"
                  description="Oracle XA Datasource"
                  factory="oracle.jdbc.pool.OracleDataSourceFactory"
                  url="jdbc:oracle:thin:@dbhost:1521:XE"
                  userName="user"
                  password="passwd"
    />

    <Resource name="java:/comp/env/jms/aqQueueConnectionFactory" auth="Container"
                  type="javax.jms.XAQueueConnectionFactory"
                  description="Oracle AQ queue connection-factory"
                  factory="com.github.fbascheper.oracle.aq.OracleAQSourceFactory"
                  refDataSourceFactory="oracle.jdbc.pool.OracleDataSourceFactory"
                  refDataSourceType="oracle.jdbc.xa.client.OracleXADataSource"
                  url="jdbc:oracle:thin:@dbhost:1521:XE"
                  userName="aquser"
                  password="aqpasswd"
    />
  </GlobalNamingResources>


### Resource links in ``config.xml``

````
    <ResourceLink name="jdbc/myDS"
                  global="java:/comp/env/jdbc/myDS"
                  type="javax.sql.XADataSource" />

    <ResourceLink name="jms/aqQueueConnectionFactory"
	              global="java:/comp/env/jms/aqQueueConnectionFactory"
				  type="javax.jms.XAQueueConnectionFactory" />

````
