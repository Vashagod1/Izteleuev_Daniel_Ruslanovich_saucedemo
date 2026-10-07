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
import pages.CartPage;
import pages.CheckoutPage;
import utils.AllureAttachments;

@Epic("SauceDemo UI")
@Feature("Оформление заказа")
@Owner("Izteleuev Daniel")
@Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
public class CheckoutTest extends BaseTest {

    private static final String BACKPACK = "Sauce Labs Backpack";

    @Test(description = "Позитивный сценарий: полное оформление заказа")
    @Story("Полное оформление заказа")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("SD-CHECKOUT-1")
    @Description("Пользователь заполняет данные покупателя, видит итоговую сумму на шаге Overview "
            + "и подтверждает заказ (экран Thank you for your order!).")
    public void userCanCompleteCheckout() {

        CheckoutPage checkout = addToCartAndOpenCart(BACKPACK)
                .checkout()
                .fillCustomerInfo("Ivan", "Ivanov", "101000")
                .continueToOverview();

        String stepTitle = checkout.getTitleText();
        assertEquals(
                stepTitle,
                "Checkout: Overview",
                "Ожидался шаг оформления Checkout: Overview, фактически: " + stepTitle
        );

        String total = checkout.getTotalText();
        assertTrue(
                total.startsWith("Total:"),
                "На шаге Overview должна отображаться итоговая сумма, фактически: " + total
        );

        AllureAttachments.text("Итоговая сумма заказа", total);

        checkout.finish();

        assertEquals(
                checkout.getCompleteHeader(),
                "Thank you for your order!",
                "После Finish ожидался экран подтверждения заказа"
        );

        AllureAttachments.screenshot(driver, "Экран подтверждения заказа");
    }

    @Test(description = "Негативный сценарий: оформление без данных покупателя")
    @Story("Валидация обязательных полей")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("SD-CHECKOUT-2")
    @Description("При пустой форме оформления отображается ошибка валидации First Name is required, "
            + "переход на следующий шаг не выполняется.")
    public void checkoutRequiresCustomerData() {

        CheckoutPage checkout = addToCartAndOpenCart(BACKPACK).checkout();

        checkout.continueExpectingError();

        String errorText = checkout.getErrorText();
        assertTrue(
                errorText.contains("First Name is required"),
                "Ожидалась валидация обязательного поля First Name, фактически: " + errorText
        );
    }

    @Test(description = "Отмена оформления заказа возвращает пользователя в корзину")
    @Story("Отмена оформления заказа")
    @Severity(SeverityLevel.MINOR)
    @TmsLink("SD-CHECKOUT-3")
    @Description("Кнопка Cancel на первом шаге оформления возвращает на страницу корзины, "
            + "товар остаётся в корзине.")
    public void userCanCancelCheckout() {

        CartPage cart = addToCartAndOpenCart(BACKPACK);

        CartPage returnedToCart = cart.checkout().cancel();

        assertEquals(
                returnedToCart.getTitleText(),
                "Your Cart",
                "После отмены оформления ожидался возврат на страницу корзины"
        );

        assertTrue(
                returnedToCart.getItemNames().contains(BACKPACK),
                "После отмены оформления товар должен остаться в корзине"
        );
    }
}
