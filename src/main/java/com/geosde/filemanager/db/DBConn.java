// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2008-3-5 11:34:59
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DBConn.java
package com.geosde.filemanager.db;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBConn {

    public DBConn() {
        strDBDriver = "";
        strDBUrl = "";
        strDBUser = "";
        strDBPassword = "";
        strDBIsPool = "";
        strDBPoolName = "";
        mycontext = null;
        mydatasource = null;
    }

    public void init(String strDBName) throws Exception {
        ConfigInfo configInfo = new ConfigInfo();
        if (strDBName != null && strDBName.length() > 0) {
            strDBDriver = configInfo.getPropertiesValue("parameter", strDBName
                    + "Driver");
            strDBUrl = configInfo.getPropertiesValue("parameter", strDBName
                    + "Url");
            strDBUser = configInfo.getPropertiesValue("parameter", strDBName
                    + "User");
            strDBPassword = configInfo.getPropertiesValue("parameter",
                    strDBName + "Password");
            strDBIsPool = configInfo.getPropertiesValue("parameter", strDBName
                    + "IsPool");
            strDBPoolName = configInfo.getPropertiesValue("parameter",
                    strDBName + "PoolName");
        } else {
            String strDefaultDBName = configInfo.getPropertiesValue(
                    "parameter", "db");
            strDBDriver = configInfo.getPropertiesValue("parameter",
                    strDefaultDBName + "Driver");
            strDBUrl = configInfo.getPropertiesValue("parameter",
                    strDefaultDBName + "Url");
            strDBUser = configInfo.getPropertiesValue("parameter",
                    strDefaultDBName + "User");
            strDBPassword = configInfo.getPropertiesValue("parameter",
                    strDefaultDBName + "Password");
            strDBIsPool = configInfo.getPropertiesValue("parameter",
                    strDefaultDBName + "IsPool");
            strDBPoolName = configInfo.getPropertiesValue("parameter",
                    strDefaultDBName + "PoolName");
        }
        if (strDBIsPool.equalsIgnoreCase("true")) {
            try {
                mycontext = new InitialContext();
                mydatasource = (DataSource) mycontext.lookup(strDBPoolName);
            } catch (Exception e) {
                System.out.println(e);
                throw e;
            }
        } else {
            try {

                Class.forName(strDBDriver);
            } catch (Exception e) {
                System.out.println(e);
                throw e;
            }
        }
    }

    public Connection getConnection() throws Exception {
        Connection conn = null;

        if (iValidate == 1) {
            if (strDBIsPool.equalsIgnoreCase("true")) {
                try {
                    conn = mydatasource.getConnection();
                } catch (Exception e) {
                    System.out.println(e);
                    throw e;
                }
            } else {
                try {
                    conn = DriverManager.getConnection(strDBUrl, strDBUser,
                            strDBPassword);
                } catch (Exception e) {
                    System.out.println(e);
                    throw e;
                }
            }
        }
        return conn;
    }

    public void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String strDBDriver;
    public String strDBUrl;
    public String strDBUser;
    public String strDBPassword;
    public String strDBIsPool;
    public String strDBPoolName;
    public Context mycontext;
    public DataSource mydatasource;
    public static int iValidate = 1;

}
