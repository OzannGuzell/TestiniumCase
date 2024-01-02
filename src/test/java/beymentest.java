import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Random;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class beymentest {
    static WebDriver driver;
    final static Logger logger = Logger.getLogger(beymentest.class);
    private String productPrices;


    @BeforeClass
    public static void initDriver() {
        System.setProperty("webdriver.chrome.driver", "C:\\bin\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.beymen.com/");
        if (driver.getCurrentUrl().contains("https://www.beymen.com/")) {
            logger.info("Beymen sayfasına gidildi");
        } else {
            logger.error("İlgili sayfaya acilamadi");
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement cookies = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("onetrust-accept-btn-handler")));
        cookies.click();
        WebElement selectMan = driver.findElement(By.id("genderManButton"));
        selectMan.click();
    }

    @Test
    @Order(1)
    public void test1searchProduct() throws Exception {
        WebElement searchBox = driver.findElement(By.xpath("(//input[contains(@placeholder,'Ürün, Marka Arayın')])[1]"));
        File f = new File("C:\\Users\\DTOGUZEL\\Desktop\\TestiniumCase\\src\\main\\resources\\Beymen.xlsx");
        FileInputStream fis = new FileInputStream(f);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);
        String shorts = sheet.getRow(0).getCell(0).getStringCellValue();
        String shirt = sheet.getRow(0).getCell(1).getStringCellValue();
        wb.close();
        fis.close();
        searchBox.sendKeys(shorts);
        WebElement searchBoxClear = driver.findElement(By.xpath("//button[@class='o-header__search--close -hasButton']"));
        searchBoxClear.click();
        WebElement searchCancel = driver.findElement(By.className("o-header__form--close"));
        searchCancel.click();
        searchBox.sendKeys(shirt);
        searchBox.sendKeys(Keys.ENTER);
    }

    @Test
    @Order(2)
    public void test2selectProduct() {
        List<WebElement> selectProduct = driver.findElements(By.xpath("//div[@class='o-productCard']//div[@class='o-productCard__figure']"));
        if (!selectProduct.isEmpty()) {
            WebElement randomProduct = selectProduct.get(new Random().nextInt(selectProduct.size()));
            randomProduct.click();
        } else {
            logger.error("Sayfada ürün bulunamadığından tıklanamadı!");
        }
    }

    @Test
    @Order(3)
    public void test3verifyNameAndPrice() throws IOException {
        WebElement productDetailName = driver.findElement(By.className("o-productDetail__description"));
        WebElement productPrice = driver.findElement(By.id("priceNew"));
        String productDetail = productDetailName.getText();
        productPrices = productPrice.getText();
        FileWriter file = new FileWriter("C:\\Users\\DTOGUZEL\\Desktop\\TestiniumCase\\src\\main\\resources\\product_detail_and_price.txt");
        file.write(productDetail);
        file.write("\t");
        file.write(productPrices);
        file.close();
    }

    @Test
    @Order(4)
    public void test4addBasket() {
        WebElement selectSize = driver.findElement(By.xpath("(//span[@class='m-variation__item'])[1]"));
        selectSize.click();
        WebElement addbasket = driver.findElement(By.id("addBasket"));
        addbasket.click();
        WebElement gotoBasket = driver.findElement(By.cssSelector(".o-header__userInfo--item.bwi-cart-o.-cart"));
        gotoBasket.click();
    }

    @Test
    @Order(5)
    public void test5verifyBasket(){
        WebElement basketProductPrice = driver.findElement(By.cssSelector(".m-productPrice__salePrice"));
        String basketProductPrices = basketProductPrice.getText();
        if (productPrices.equals(basketProductPrices)) {
            logger.info("Ürün fiyatları eşittir.");
        } else {
            logger.error("Ürün fiyatları eşit değil!!");
        }
    }

    @Test
    @Order(6)
    public void test6deleteProduct() {
        WebElement deleteProduct = driver.findElement(By.id("removeCartItemBtn0-key-0"));
        deleteProduct.click();
        WebElement message = driver.findElement(By.xpath("//strong[@class='m-empty__messageTitle']"));
        String actualText = message.getText();
        String expectedText = "SEPETINIZDE ÜRÜN BULUNMAMAKTADIR";
        if (actualText.equals(expectedText)) {
            logger.info("Metin doğrulama başarılı!");
        } else {
            logger.error("Metin doğrulama başarısız. Beklenen metin: " + expectedText + ", Gerçekleşen metin: " + actualText);
        }

    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }

}