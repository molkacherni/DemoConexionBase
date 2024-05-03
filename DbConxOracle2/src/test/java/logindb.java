import org.testng.annotations.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class logindb {
    @Test
    public void testDatabaseConnection() {
        Connection conn = null;
        
        try {
            
            Class.forName("oracle.jdbc.OracleDriver");
            
            String dbURL = "jdbc:oracle:thin:@92.205.22.177:1521:XE";
            String username = "ERP";
            String password = "ERP";
            conn = DriverManager.getConnection(dbURL, username, password);
            if (conn != null) {
                System.out.println("Connected to database");
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
           
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
              
            }
        }
    }
}
