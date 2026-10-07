package tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import pages.InventoryPage;
import pages.LoginPage;
import utils.TestConfig;

@Epic("SauceDemo UI")
@Feature("Авторизация")
@Owner("Izteleuev Daniel")
@Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
public class LoginTest extends BaseTest {

    @Test(description = "Позитивный сценарий: standard_user успешно входит в систему")
    @Story("Успешный вход в систему")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("SD-AUTH-1")
    @Description("Проверяет, что standard_user с корректным паролем попадает в каталог товаров "
            + "(URL /inventory.html и заголовок Products).")
    public void standardUserCanLogin() {

        InventoryPage inventory = new LoginPage(driver, wait)
                .open()
                .loginAs(TestConfig.STANDARD_USER, TestConfig.PASSWORD);

        assertTrue(
                inventory.isOpen(),
                "Ожидался переход на страницу каталога /inventory.html после успешного входа"
        );

        assertEquals(
                inventory.getTitleText(),
                "Products",
                "Ожидался заголовок Products на странице каталога"
        );
    }

    @Test(description = "Негативный сценарий: заблокированный пользователь не может войти")
    @Story("Вход заблокированного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("SD-AUTH-2")
    @Description("locked_out_user получает сообщение о блокировке и остаётся на странице авторизации.")
    public void lockedUserCannotLogin() {

        LoginPage loginPage = new LoginPage(driver, wait)
                .open()
                .loginExpectingError(TestConfig.LOCKED_OUT_USER, TestConfig.PASSWORD);

        assertTrue(
                loginPage.isErrorDisplayed(),
                "Ожидалось сообщение об ошибке на странице авторизации"
        );

        String errorText = loginPage.getErrorText();
        assertTrue(
                errorText.contains("locked out"),
                "Ожидалось сообщение о блокировке пользователя, фактически: " + errorText
        );
    }

    @Test(description = "Негативный сценарий: неверный пароль не пускает в систему")
    @Story("Вход с неверным паролем")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("SD-AUTH-3")
    @Description("standard_user с неверным паролем получает сообщение о несовпадении логина и пароля.")
    public void userCannotLoginWithWrongPassword() {

        LoginPage loginPage = new LoginPage(driver, wait)
                .open()
                .loginExpectingError(TestConfig.STANDARD_USER, "wrong_password");

        String errorText = loginPage.getErrorText();
        assertTrue(
                errorText.contains("do not match"),
                "Ожидалось сообщение о несовпадении логина и пароля, фактически: " + errorText
        );
    }
}
