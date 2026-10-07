package base;

import java.time.Duration;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import pages.CartPage;
import pages.InventoryPage;
import pages.LoginPage;
import utils.AllureAttachments;
import utils.AllureEnvironment;
import utils.TestConfig;

/**
 * Базовый класс тестов.
 * <p>
 * Отвечает за: кроссбраузерный запуск (Chrome / Firefox / Edge, headless-режим),
 * тайм-ауты, общие шаги сценариев, параметры Allure и вложения при падении теста.
 */
public abstract class BaseTest {

    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(60);
    private static final Duration SCRIPT_TIMEOUT = Duration.ofSeconds(10);
    private static final Dimension WINDOW_SIZE = new Dimension(1920, 1080);

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeSuite(alwaysRun = true)
    public void writeAllureEnvironment() {
        AllureEnvironment.write();
    }

    @Parameters({"browser", "headless"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browserParameter,
                      @Optional("false") String headlessParameter) {

        String browser = System.getProperty("browser", browserParameter).trim().toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", headlessParameter));

        driver = createDriver(browser, headless);
        prepareWindow(headless);

        driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT);
        driver.manage().timeouts().scriptTimeout(SCRIPT_TIMEOUT);

        wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT_SECONDS));

        Allure.parameter("Browser", browser);
        Allure.parameter("Headless", headless);
        Allure.parameter("Browser version", browserVersion());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        if (driver == null) {
            return;
        }

        try {
            if (!result.isSuccess()) {
                AllureAttachments.screenshot(driver, "Скриншот при падении: " + result.getName());
                AllureAttachments.pageSource(driver, "HTML страницы при падении: " + result.getName());
            }
        } finally {
            try {
                driver.quit();
            } catch (Exception ignored) {
                // браузер мог быть уже закрыт (например, при падении драйвера)
            }
            driver = null;
        }
    }

    // ------------------------------------------------------------------ общие шаги сценариев

    @Step("Открыть SauceDemo и авторизоваться как standard_user")
    protected InventoryPage loginAsStandardUser() {
        return new LoginPage(driver, wait)
                .open()
                .loginAs(TestConfig.STANDARD_USER, TestConfig.PASSWORD);
    }

    @Step("Добавить товар {productName} в корзину и открыть корзину")
    protected CartPage addToCartAndOpenCart(String productName) {
        return loginAsStandardUser()
                .addProduct(productName)
                .openCart();
    }

    // ------------------------------------------------------------------ драйвер

    private WebDriver createDriver(String browser, boolean headless) {

        return switch (browser) {

            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1920,1080");
                yield new ChromeDriver(options);
            }

            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    options.addArguments("-headless");
                }
                options.addArguments("--width=1920", "--height=1080");
                yield new FirefoxDriver(options);
            }

            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1920,1080");
                yield new EdgeDriver(options);
            }

            default -> throw new IllegalArgumentException(
                    "Неподдерживаемый браузер: '" + browser
                            + "'. Доступные значения: chrome, firefox, edge."
            );
        };
    }

    private void prepareWindow(boolean headless) {
        if (headless) {
            driver.manage().window().setSize(WINDOW_SIZE);
        } else {
            driver.manage().window().maximize();
        }
    }

    private String browserVersion() {
        if (driver instanceof HasCapabilities capabilities) {
            return capabilities.getCapabilities().getBrowserVersion();
        }
        return "unknown";
    }
}
