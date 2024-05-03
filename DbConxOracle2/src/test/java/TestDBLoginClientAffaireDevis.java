import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(Testlistnerdb.class)
public class TestDBLoginClientAffaireDevis {

    // Déclaration du WebDriver en tant que variable de classe
    private WebDriver driver;

    static String RequiredString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder s = new StringBuilder(n);
        int y;
        for (y = 0; y < n; y++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            s.append(AlphaNumericString.charAt(index));
        }
        return s.toString();
    }

    int upper = 1000;
    int lower = 100;
    int randomNumber = (int) (Math.random() * (upper - lower)) + lower;

    LocalDateTime myDateObj = LocalDateTime.now();
    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM HH:mm:ss");
    String formattedDate = myDateObj.format(myFormatObj);

    String randomVerification = formattedDate + " " + randomNumber + " " + RequiredString(6);

    @BeforeTest
    public void Configure() {
        System.out.println("Début Test  ");
        System.out.println("random string is : " + randomVerification);

        // Initialisation du WebDriver une seule fois avant tous les tests
        driver = initializeDriver();
    }

    @Test(priority = 1)
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

            try {
                testConnexion(url, login, motDePasse);
            } finally {
                // Fermer le navigateur après le test de connexion
               // driver.quit();
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
        System.setProperty("webdriver.chrome.driver",
                "C:/Users/DEV01/eclipse-workspace/TpCloudSysClientAffaire/src/test/java/DemoCreationClientaffaire/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        return driver;
    }

    private void testConnexion(String url, String login, String motDePasse) {
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
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

          

        } else {
            System.out.println("Certaines informations de connexion sont null. Impossible de charger la page.");
        }
    }

    @Test(priority = 2)
    public void TestNavigationMenu() {
        System.out.println("Ouvrir le menu de navigation");

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement secondToggleElement = driver.findElement(By.xpath("(//span[@class='a-TreeView-toggle'])[3]"));
          secondToggleElement.click();

       
        WebElement mesClientsLink = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Mes clients")));

        mesClientsLink.click();

 
       // driver.quit();
    }
    @Test(priority = 3)
    public void TestCreateClient() {
        System.out.println("Création client");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement creerButton = driver.findElement(By.id("B17851436701978027"));
        creerButton.click();

       
        Select selectList = new Select(driver.findElement(By.id("P37_TITRE_CLIENT")));
        selectList.selectByVisibleText("Madame");

       
        driver.findElement(By.id("P37_NOM_CLIENT")).sendKeys("MURIS" + randomVerification);
        driver.findElement(By.id("P37_MATRICULE_FISCALE")).sendKeys("03214");
        driver.findElement(By.id("P37_ADRESSE_1")).sendKeys("TUNIS");
        driver.findElement(By.id("P37_ADRESSE_2")).sendKeys("Résidence zohra,cité khadhra");
        driver.findElement(By.id("P37_RIB_BANCAIRE")).sendKeys("123456789789");
        driver.findElement(By.id("P37_MOBILE1")).sendKeys("56305396");
        driver.findElement(By.id("P37_TELEPHONE")).sendKeys("70123456");
        driver.findElement(By.id("P37_BANQUE")).sendKeys("BIAT");

       
        WebElement creerButton1 = driver.findElement(By.id("B17844237429978001"));
        creerButton1.click();
    }
    @Test(priority = 4)
    public void TestRechercheclient() {
        System.out.println("Recherche client  ");       
        driver.findElement(By.id("R17850337899978024_search_field")).sendKeys(randomVerification);
        driver.findElement(By.id("R17850337899978024_search_button")).click(); 
      
        try {
            Thread.sleep(3000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
    
    @Test(priority = 5)
    public void TestCommercial() {
        System.out.println("Executer test commercial");
        WebElement gestionDesTiersToggle = driver.findElement(By.xpath("(//span[@class='a-TreeView-toggle'])[1]"));
        gestionDesTiersToggle.click();
        driver.findElement(By.partialLinkText("Mes affaires")).click();
        WebElement creerButton = driver.findElement(By.id("B11386816663854057"));
        creerButton.click();

        WebDriverWait waitElementInPopup = new WebDriverWait(driver, 15);
        WebElement element = waitElementInPopup.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@class, 'a-Button--popupLOV')]")));
        element.click();

        Set<String> windowHandles = driver.getWindowHandles();
        Iterator<String> iterator = windowHandles.iterator();
        while (iterator.hasNext()) {
            String childWindow = iterator.next();
            driver.switchTo().window(childWindow);
            System.out.println(driver.getTitle() + " opened");
            if (driver.getTitle().equals("Search Dialog")) {
            	
            	
               
                driver.findElement(By.id("SEARCH")).sendKeys(randomVerification);

                WebElement searchButton = driver.findElement(By.xpath("//input[@type='button' and @value='Search']"));
                searchButton.click();
               
                WebElement linkElement = driver.findElement(By.xpath("//a[contains(text(), '" + randomVerification + "')]"));
                linkElement.click();
               
                break;
            }
        }
        Set<String> windowHandles2 = driver.getWindowHandles();
        for (String windowHandle : windowHandles2) {
            if (driver.switchTo().window(windowHandle).getTitle().equals("Mise à jour information affaires")) {
                break;
            }
        }
      
        driver.findElement(By.id("P117_DESCRIPTION_BESOIN")).sendKeys("AFF-clt : " + randomVerification);

       
        driver.findElement(By.id("B151988927049331746")).click();
       
    }
    @Test(priority = 6)
    public void TestDevis() {
        
        System.out.println(" Création un Devis");
        Select Famille = new Select(driver.findElement(By.id("P128_FAMILLE")));
        Famille.selectByVisibleText("Divers");
        
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement sousFamilleDropdown = driver.findElement(By.id("P128_SOUS_FAMILLE"));
        sousFamilleDropdown.click();
        
        

        
        WebElement financierOption = driver.findElement(By.xpath("//select[@id='P128_SOUS_FAMILLE']/option[contains(text(),'Financier')]"));
        financierOption.click();
     
    
       
        WebDriverWait wait2 = new WebDriverWait(driver, 10);
        WebElement iconElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='report_R153519597728533814']/div/div[1]/table/tbody/tr[4]/td[4]")));
        iconElement.click();
        
        WebElement iconElement2 = driver.findElement(By.xpath("//*[@id=\"report_R153519597728533814\"]/div/div[1]/table/tbody/tr[5]/td[4]"));
        
        iconElement2.click(); 
        
        WebElement iconElement3 = driver.findElement(By.xpath("//*[@id=\"report_R153519597728533814\"]/div/div[1]/table/tbody/tr[6]/td[4]"));
        
        iconElement3.click();  
        
        WebElement element = driver.findElement(By.id("B153511151758533811"));
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", element);
        

    
        System.out.println(" Devis créé ! ");
        
        try {
            Thread.sleep(4000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
  
        System.out.println(" Recherche affaire ");
       
        driver.findElement(By.id("B151989358334331746")).click();
        driver.findElement(By.id("R11368507481813501_search_field")).sendKeys(randomVerification);
        driver.findElement(By.id("R11368507481813501_search_button")).click(); 
        int maxRetries = 3;
        int retryCount = 0;
        boolean isElementDisplayed = false;

        while (retryCount < maxRetries && !isElementDisplayed) {
            try {
                WebDriverWait wait1 = new WebDriverWait(driver, 10);
                WebElement tdElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='11368620578813503']//td[contains(text(), '" + randomVerification + "')]")));

                if (tdElement != null) {
                    isElementDisplayed = true;
                    System.out.println("Affaire créé.");
                } else {
                    System.out.println("Aucun résultat trouvé");
                    retryCount++;
                }
            } catch (NoSuchElementException e) {
                System.out.println("L'élément n'est pas encore disponible. Réessayez...");
                retryCount++;
            }
        }

        Assert.assertTrue(isElementDisplayed, "Le nom d'affaire recherché n'est pas présent dans le tableau.");
        
      
      


    }

    
    
    @AfterTest
    public void tearDown() {
        System.out.println("Test final !");
        // driver.quit();
    }
}
