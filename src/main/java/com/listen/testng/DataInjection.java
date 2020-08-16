package com.listen.testng;

/**
 * @author Manjunath-PC
 * @created 16/08/2020
 * @project listen-testng-and-report
 */
public class DataInjection {
    static String userName = System.getProperty("user.name");
    static String osName = System.getProperty("os.name");
    static String osArch = System.getProperty("os.arch");
    static String osVersion = System.getProperty("os.version");
}
