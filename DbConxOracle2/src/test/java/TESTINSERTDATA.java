import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Listeners(Testlistnerdb.class)
public class TESTINSERTDATA {

    @Test
    public void testDatabaseConnectionAndLogin() throws SQLException {
        System.out.println("Tentative de connexion à la base de données Oracle...");

        // Établir la connexion à la base de données Oracle et récupérer les informations de connexion
        String[] dbInfo = getDatabaseConnectionInfo();
        if (dbInfo != null && dbInfo.length == 3) {
            String url = dbInfo[0];
            String login = dbInfo[1];
            String motDePasse = dbInfo[2];

            // Après avoir récupéré les informations de connexion depuis la base de données
            System.out.println("Informations de connexion récupérées avec succès :");

            WebDriver driver = null;
            try {
                System.out.println("Initialisation du WebDriver...");
                driver = initializeDriver();
                System.out.println("WebDriver initialisé avec succès.");

                testConnexion(driver, url, login, motDePasse);
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        } else {
            System.out.println("Failed to retrieve necessary values from the database.");
        }
    }

    private String[] getDatabaseConnectionInfo() {
        String DB_URL = "jdbc:oracle:thin:@92.205.22.177:1521:XE";
        String USER = "ERP";
        String PASS = "ERP";
        String[] dbInfo = new String[3];
        String error = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Requête SQL pour récupérer l'URL
            String sqlUrl = "SELECT ATR1 FROM ERP_REFERENTIEL WHERE CODE_INTERNE = 'Par_117_DEV_URL'";
            System.out.println("Exécution de la requête SQL pour récupérer l'URL : " + sqlUrl);
            try (PreparedStatement statement = conn.prepareStatement(sqlUrl);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    dbInfo[0] = resultSet.getString("ATR1");
                    System.out.println("URL récupérée depuis la base de données : " + dbInfo[0]);
                } else {
                    error += "Aucun résultat trouvé pour l'URL dans la base de données.\n";
                }
            }

            // Requête SQL pour récupérer le login
            String sqlLogin = "SELECT ATR1 FROM ERP_REFERENTIEL WHERE CODE_INTERNE = 'Par_118_DEV_LOGIN'";
            System.out.println("Exécution de la requête SQL pour récupérer le login : " + sqlLogin);
            try (PreparedStatement statement = conn.prepareStatement(sqlLogin);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    dbInfo[1] = resultSet.getString("ATR1");
                    System.out.println("Login récupéré depuis la base de données : " + dbInfo[1]);
                } else {
                    error += "Aucun résultat trouvé pour le login dans la base de données.\n";
                }
            }

            // Requête SQL pour récupérer le mot de passe
            String sqlPassword = "SELECT ATR1 FROM ERP_REFERENTIEL WHERE CODE_INTERNE = 'Par_119_DEV_Mot de passe'";
            System.out.println("Exécution de la requête SQL pour récupérer le mot de passe : " + sqlPassword);
            try (PreparedStatement statement = conn.prepareStatement(sqlPassword);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    dbInfo[2] = resultSet.getString("ATR1");
                    System.out.println("Mot de passe récupéré depuis la base de données : " + dbInfo[2]);
                } else {
                    error += "Aucun résultat trouvé pour le mot de passe dans la base de données.\n";
                }
            }
        } catch (SQLException e) {
            error += "Erreur SQL : " + e.getMessage() + "\n";
        }

        if (!error.isEmpty()) {
            System.out.println("Erreurs lors de la récupération des informations depuis la base de données :");
            System.out.println(error);
        }

        return dbInfo;
    }

    private WebDriver initializeDriver() {
        System.setProperty("webdriver.chrome.driver", "C:/Users/DEV01/eclipse-workspace/TpCloudSysClientAffaire/src/test/java/DemoCreationClientaffaire/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        return driver;
    }

    private void testConnexion(WebDriver driver, String url, String login, String motDePasse) {
        if (url != null && login != null && motDePasse != null) {
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("P10001_USERNAME")));

            WebElement usernameField = driver.findElement(By.id("P10001_USERNAME"));
            usernameField.sendKeys(login);

            WebElement passwordField = driver.findElement(By.id("P10001_PASSWORD"));
            passwordField.sendKeys(motDePasse);

            driver.findElement(By.id("P101_LOGIN")).click();

            System.out.println("Identifiants de connexion saisis avec succès.");
            try {
                // Mettez en pause pendant 10 secondes (10000 millisecondes)
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Fermer le WebDriver après la pause
            //driver.quit(); 
            } else {
            System.out.println("Certaines informations de connexion sont null. Impossible de charger la page.");
        }
    }
}
