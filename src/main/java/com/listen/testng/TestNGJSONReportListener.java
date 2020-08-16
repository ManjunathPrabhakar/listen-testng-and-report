package com.listen.testng;

import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Manjunath-PC
 * @created 16/08/2020
 * @project testng-listeners-workshop
 */
public class TestNGJSONReportListener implements ITestListener, IReporter, ISuiteListener {

    String pathToSaveJsonReport = "testNG-reports\\";

    StringBuilder stringBuilder = new StringBuilder();
    boolean first = true;
    long startmills = 0;
    String suiteName = null;
    Date TESTSTARTDATE;
    Date TESTENDDATE;

    /**
     * This method is invoked before the SuiteRunner starts.
     *
     * @param suite
     */
    @Override
    public void onStart(ISuite suite) {
        suiteName = suite.getName();
        String val = "{" +
                "\"suiteName\":\"" + suite.getName() + "\"," +
                "\"suiteDetails\":[";

        stringBuilder.append(val);
    }

    /**
     * Invoked before running all the test methods belonging to the classes inside the &lt;test&gt; tag
     * and calling all their Configuration methods.
     *
     * @param context
     */
    @Override
    public void onStart(ITestContext context) {
        //System.out.println("Parameters for " + context.getName() +"->"+context.getCurrentXmlTest().getAllParameters());
        TESTSTARTDATE = context.getStartDate();
        String val = "{" +
                "\"testName\":\"" + context.getName() + "\"," +
                "\"testDetails\":[";
        stringBuilder.append(val);
    }

    /**
     * Invoked each time before a test will be invoked. The <code>ITestResult</code> is only partially
     * filled with the references to class, method, start millis and status.
     *
     * @param result the partially filled <code>ITestResult</code>
     * @see ITestResult#STARTED
     */
    @Override
    public void onTestStart(ITestResult result) {
        startmills = 0;
        startmills = result.getStartMillis();
        Date d = new Date(startmills);
        System.out.println(d);


        String val;
        if (first) {
            val = "{" +
                    "\"testName\":\"" + result.getName() + "\"," +
                    "\"testStart\":\"" + d + "\",";
            first = false;
        } else {
            val = ",{" +
                    "\"testName\":\"" + result.getName() + "\"," +
                    "\"testStart\":\"" + d + "\",";
        }

        stringBuilder.append(val);
    }

    /**
     * Invoked each time a test succeeds.
     *
     * @param result <code>ITestResult</code> containing information about the run test
     * @see ITestResult#SUCCESS
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        long endMills = result.getEndMillis();
        Date endTime = new Date(endMills);

        String val = "\"testResult\":\"" + "PASS" + "\"," +
                "\"testEnd\":\"" + endTime + "\"," +
                "\"totalTime\":\"" + getTimeDiff(endMills, startmills) + "\"" +
                "}";
        stringBuilder.append(val);
    }

    /**
     * Invoked each time a test fails.
     *
     * @param result <code>ITestResult</code> containing information about the run test
     * @see ITestResult#FAILURE
     */
    @Override
    public void onTestFailure(ITestResult result) {
        long endMills = result.getEndMillis();
        Date endTime = new Date(endMills);


        String val = "\"testResult\":\"" + "FAIL" + "\"," +
                "\"testEnd\":\"" + endTime + "\"," +
                "\"totalTime\":\"" + getTimeDiff(endMills, startmills) + "\"," +
                "\"failReason\":\"" + result.getThrowable() + "\"}";
        stringBuilder.append(val);
    }

    /**
     * Invoked each time a test is skipped.
     *
     * @param result <code>ITestResult</code> containing information about the run test
     * @see ITestResult#SKIP
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        long endMills = result.getEndMillis();
        Date endTime = new Date(endMills);
        Date diffTime = new Date(endMills - startmills);


        String val = "\"testResult\":\"" + "SKIP" + "\"," +
                "\"testEnd\":\"" + endTime + "\"," +
                "\"totalTime\":\"" + getTimeDiff(endMills, startmills) + "\"," +
                "\"skipReason\":\"" + result.getThrowable() + "\"}";
        stringBuilder.append(val);
    }


    /**
     * Invoked after all the test methods belonging to the classes inside the &lt;test&gt; tag have run
     * and all their Configuration methods have been called.
     *
     * @param context
     */
    @Override
    public void onFinish(ITestContext context) {
        int pass = 0, fail = 0, skip = 0;
        TESTENDDATE = context.getEndDate();
        first = true;

        IResultMap passedTests = context.getPassedTests();
        pass = passedTests.getAllResults().size();
        IResultMap failedTests = context.getFailedTests();
        fail = failedTests.getAllResults().size();
        IResultMap skippedTests = context.getSkippedTests();
        skip = skippedTests.getAllResults().size();

        String result = "PASS";
        if (fail > 0) {
            result = "FAIL";
        } else if (skip > 0 && fail == 0) {
            result = "SKIP";
        }


        int total = pass + fail + skip;
        String val = "]," +
                "\"testStartDate\":\"" + TESTSTARTDATE.toString() + "\"," +
                "\"testEndDate\":\"" + TESTENDDATE.toString() + "\"," +
                "\"totalTestTime\":\"" + getTimeDiff(TESTENDDATE, TESTSTARTDATE) + "\"," +
                "\"totalTestCases\":\"" + total + "\"," +
                "\"totalPass\":\"" + pass + "\"," +
                "\"totalFail\":\"" + fail + "\"," +
                "\"totalSkip\":\"" + skip + "\"," +
                "\"result\":\"" + result + "\"" +
                "},";
        stringBuilder.append(val);
    }

    /**
     * This method is invoked after the SuiteRunner has run all the tests in the suite.
     *
     * @param suite
     */
    @Override
    public void onFinish(ISuite suite) {
        String res = stringBuilder.toString();
        int v = stringBuilder.lastIndexOf(",");
        String val = "],";
        stringBuilder = new StringBuilder(res.substring(0, v));
        stringBuilder.append(val);
    }

    /**
     * Generate a report for the given suites into the specified output directory.
     *
     * @param xmlSuites
     * @param suites
     * @param outputDirectory
     */
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        int pass = 0, fail = 0, skip = 0;
        for (Object isuite : suites) {
            Map<String, ISuiteResult> suiteResults = ((ISuite) isuite).getResults();
            String sn = ((ISuite) isuite).getName();

            for (ISuiteResult obj : suiteResults.values()) {
                ITestContext tc = obj.getTestContext();

                pass = pass + tc.getPassedTests().getAllResults().size();
                //System.out.println("Passed Tests of" + sn + "=" + tc.getPassedTests().getAllResults().size());

                fail = fail + tc.getFailedTests().getAllResults().size();
                //System.out.println("Failed Tests of" + sn + "=" + tc.getFailedTests().getAllResults().size());

                skip = skip + tc.getSkippedTests().getAllResults().size();
                // System.out.println("Skipped Tests of" + sn + "=" + tc.getSkippedTests().getAllResults().size());
            }

        }

        String result = "PASS";
        if (fail > 0) {
            result = "FAIL";
        } else if (skip > 0 && fail == 0) {
            result = "SKIP";
        }


        int total = pass + fail + skip;
        String val = "\"totalTestCases\":\"" + total + "\"," +
                "\"totalPass\":\"" + pass + "\"," +
                "\"totalFail\":\"" + fail + "\"," +
                "\"totalSkip\":\"" + skip + "\"," +
                "\"result\":\"" + result + "\"" +
                "}";

        stringBuilder.append(val);

        //System.out.println(String.valueOf(stringBuilder));

        //JSONObject obj = new JSONObject();
        //obj.put("testSuite",stringBuilder.toString());

        try {
            Files.write(Paths.get(pathToSaveJsonReport + suiteName.replaceAll(" ", "") + "_" + startmills + ".json"), stringBuilder.toString().getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    private String getTimeDiff(long endTime, long startTime) {
        long durationInSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);

        long SECONDS_IN_A_MINUTE = 60;
        long MINUTES_IN_AN_HOUR = 60;
        long HOURS_IN_A_DAY = 24;
        long DAYS_IN_A_MONTH = 30;
        long MONTHS_IN_A_YEAR = 12;

        long sec = (durationInSeconds >= SECONDS_IN_A_MINUTE) ? durationInSeconds % SECONDS_IN_A_MINUTE : durationInSeconds;
        long min = (durationInSeconds /= SECONDS_IN_A_MINUTE) >= MINUTES_IN_AN_HOUR ? durationInSeconds % MINUTES_IN_AN_HOUR : durationInSeconds;
        long hrs = (durationInSeconds /= MINUTES_IN_AN_HOUR) >= HOURS_IN_A_DAY ? durationInSeconds % HOURS_IN_A_DAY : durationInSeconds;
        long days = (durationInSeconds /= HOURS_IN_A_DAY) >= DAYS_IN_A_MONTH ? durationInSeconds % DAYS_IN_A_MONTH : durationInSeconds;
        long months = (durationInSeconds /= DAYS_IN_A_MONTH) >= MONTHS_IN_A_YEAR ? durationInSeconds % MONTHS_IN_A_YEAR : durationInSeconds;
        long years = (durationInSeconds /= MONTHS_IN_A_YEAR);

        return (hrs + ":" + min + ":" + sec);
    }

    private String getTimeDiff(Date endTime, Date startTime) {
        long durationInSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime.getTime() - startTime.getTime());

        long SECONDS_IN_A_MINUTE = 60;
        long MINUTES_IN_AN_HOUR = 60;
        long HOURS_IN_A_DAY = 24;
        long DAYS_IN_A_MONTH = 30;
        long MONTHS_IN_A_YEAR = 12;

        long sec = (durationInSeconds >= SECONDS_IN_A_MINUTE) ? durationInSeconds % SECONDS_IN_A_MINUTE : durationInSeconds;
        long min = (durationInSeconds /= SECONDS_IN_A_MINUTE) >= MINUTES_IN_AN_HOUR ? durationInSeconds % MINUTES_IN_AN_HOUR : durationInSeconds;
        long hrs = (durationInSeconds /= MINUTES_IN_AN_HOUR) >= HOURS_IN_A_DAY ? durationInSeconds % HOURS_IN_A_DAY : durationInSeconds;
        long days = (durationInSeconds /= HOURS_IN_A_DAY) >= DAYS_IN_A_MONTH ? durationInSeconds % DAYS_IN_A_MONTH : durationInSeconds;
        long months = (durationInSeconds /= DAYS_IN_A_MONTH) >= MONTHS_IN_A_YEAR ? durationInSeconds % MONTHS_IN_A_YEAR : durationInSeconds;
        long years = (durationInSeconds /= MONTHS_IN_A_YEAR);

        return (hrs + ":" + min + ":" + sec);
    }


}
