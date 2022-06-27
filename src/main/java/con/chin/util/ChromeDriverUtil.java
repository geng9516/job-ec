package con.chin.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChromeDriverUtil {

    @Value("CHROMESESSIONDATA")
    private static String chromeSessionData;

    public static ChromeDriver getChromeDriver() {
        System.getProperties().setProperty("webdriver.chrome.driver", "/Users/geng9516/Documents/EC関連/99_クローラー/開発ツール/chromedriver");
//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--user-data-dir=/Users/geng9516/Documents/EC関連/99_クローラー/data");
//        chromeOptions.addArguments("--profile-directory=Profile 6");
//        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
        ChromeDriver chromeDriver = new ChromeDriver();
        return chromeDriver;
    }

    public static WebDriver getFirefoxDriver() {
        System.getProperties().setProperty("webdriver.gecko.driver", "/Users/geng9516/Documents/EC関連/99_クローラー/開発ツール/geckodriver");
        WebDriver driver = new FirefoxDriver();
        return driver;
    }
}
