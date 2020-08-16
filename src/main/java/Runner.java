import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * @author Manjunath-PC
 * @created 16/08/2020
 * @project listen-testng-and-report
 */
//@Listeners({TestMama.class})
public class Runner {

    @BeforeSuite
    public void bf(){
        System.out.println("Runner.bf");
    }

    @Test(testName = "HELLO",alwaysRun = true,suiteName = "Test")
    public void tst(){
        System.out.println("Runner.tst");
    }


    @Test(testName = "HELLO1",alwaysRun = true)
    public void tst1(){
        System.out.println("Runner.tst1");
    }
}
