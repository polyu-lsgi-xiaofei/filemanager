// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2008-2-18 12:50:40
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ConfigInfo.java
package com.geosde.filemanager.db;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ConfigInfo
{

    public ConfigInfo()
    {
    }

    public String getPropertiesValue(String strPropertiesFile, String strItem)
    {
        String strItemValue = "";
        ResourceBundle resources1 = null;
        try
        {
            resources1 = ResourceBundle.getBundle(strPropertiesFile);
            strItemValue = resources1.getString(strItem);
        }
        catch(MissingResourceException e)
        {
            System.out.println("ConfigInfo.getPropertiesValue error:" + e.getMessage());
        }
        return strItemValue;
    }

    public static String getPropertiesValue(String strItem)
    {
        String strValue = "";
        try
        {
            if(resources == null)
                initResources();
            strValue = resources.getString(strItem);
        }
        catch(Exception e)
        {
            System.out.println("ConfigInfo.getPropertiesValue error:" + e.getMessage());
        }
        return strValue;
    }

    private static void initResources()
    {
        try
        {
           resources  = ResourceBundle.getBundle("parameter");
        }
        catch(MissingResourceException e)
        {
            throw new Error(e.getClassName());
        }
    }

    public static final String strDefaultFile = "parameter";
    private static ResourceBundle resources = null;

}