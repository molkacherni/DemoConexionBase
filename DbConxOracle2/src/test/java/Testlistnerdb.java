



import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class Testlistnerdb  extends TestListenerAdapter {

    @Override
    public void onTestSuccess(ITestResult tr) {
        printTestResult(tr.getMethod().getMethodName(), "PASS", 1, 0,0, "\u001B[28m"); // Green color
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        printTestResult(tr.getMethod().getMethodName(), "FAIL", 0, 1,0, "\u001B[29m"); // Red color
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        printTestResult(tr.getMethod().getMethodName(), "SKIP", 0, 0,1, "\u001B[27m"); // Yellow color (skipped)
    }

    private void printTestResult(String testName, String status, int passes, int failures, int skips, String color) {
        System.out.println("\n");
        System.out.print(color);
        System.out.println("===============================================");
        System.out.println(testName + " - " + status);
        System.out.println("Total tests run: 1, Passes: " + passes + ", Failures: " + failures  + ", Skips: " + skips );
        System.out.println("===============================================");
        System.out.print("\u001B[0m");
        System.out.println("\n");
    }
 }
 