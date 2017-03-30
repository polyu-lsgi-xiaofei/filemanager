package com.geosde.filemanager.db;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Session;

public class CassandraConnector {


    private static Cluster cluster = null;
    private static Session session = null;

    public static Session connect() {
        if (session != null) {
            return session;
        } else {
            ConfigInfo configInfo = new ConfigInfo();
            String hosts = configInfo.getPropertiesValue("cassandra", "hosts");
            String user = configInfo.getPropertiesValue("cassandra", "user");
            String pwd = configInfo.getPropertiesValue("cassandra", "password");
            String[] host_array = hosts.split(",");
            Builder builder = Cluster.builder();
            for (String host : host_array) {
                builder = builder.addContactPoint(host);
            }
            //cluster = Cluster.builder().addContactPoint("192.168.210.240").build();
            cluster = builder.withCredentials(user, pwd).build();
            session = cluster.connect();
            return session;
        }
    }

    public static void main(String[] args) {
        connect();
    }
    
    public static void close(){
    	session.close();
    	cluster.close();
    }
}
