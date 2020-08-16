

## TESTNG LISTENER

*

> **This Repository is used to create a JSON report using TestNG Listeners**

*

Current TestNG Version 7.3.0
issues: need to use in vmarguments *-Dtestng.dtd.http=true*
If Version 7.1.0 used
issues : *IAnnotationTransformer*->*transform* method called multiple times

**USES SERVICE LOADER**

Folder Structure :

    src\main\java\com\listen\testng

TestNGJSONReportListener.java (content of my custom listener)

    src\main\resources\META-INF\services

org.testng.ITestNGListener (content package with class name like : *com.listen.testng.TestNGJSONReportListener* )


USAGE #

CLONE
MAKE CHANGES
CREATE A JAR