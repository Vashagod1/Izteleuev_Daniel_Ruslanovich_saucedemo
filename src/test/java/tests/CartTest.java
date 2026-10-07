package tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

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
import pages.InventoryPage;
import utils.AllureAttachments;

@Epic("SauceDemo UI")
@Feature("Корзина")
@Owner("Izteleuev Daniel")
@Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
public class CartTest extends BaseTest {

    private static final String BACKPACK = "Sauce Labs Backpack";

    @Test(description = "Позитивный сценарий: добавление товара в корзину")
    @Story("Добавление товара в корзину")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("SD-CART-1")
    @Description("После добавления Sauce Labs Backpack значок корзины показывает 1, "
            + "а на странице корзины товар присутствует с ценой $29.99.")
    public void userCanAddBackpackToCart() {

        InventoryPage inventory = loginAsStandardUser();
        inventory.addProduct(BACKPACK);

        assertEquals(
                inventory.getCartBadgeText(),
                "1",
                "Ожидался счётчик корзины 1 после добавления одного товара"
        );

        AllureAttachments.screenshot(driver, "Каталог после добавления товара в корзину");

        CartPage cart = inventory.openCart();

        List<String> itemNames = cart.getItemNames();
        assertTrue(
                itemNames.contains(BACKPACK),
                "Корзина должна содержать товар " + BACKPACK + ", фактически: " + itemNames
        );

        assertEquals(
                cart.getItemPrice(BACKPACK),
                "$29.99",
                "Ожидалась цена товара " + BACKPACK + " $29.99"
        );
    }

    @Test(description = "Позитивный сценарий: удаление товара из корзины")
    @Story("Удаление товара из корзины")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("SD-CART-2")
    @Description("После удаления единственного товара список корзины пуст, а значок с количеством исчезает.")
    public void userCanRemoveBackpackFromCart() {

        CartPage cart = addToCartAndOpenCart(BACKPACK);

        List<String> itemNamesBeforeRemove = cart.getItemNames();
        assertTrue(
                itemNamesBeforeRemove.contains(BACKPACK),
                "Перед удалением корзина должна содержать " + BACKPACK
        );

        cart.removeProduct(BACKPACK);

        List<String> itemNamesAfterRemove = cart.getItemNames();
        assertTrue(
                itemNamesAfterRemove.isEmpty(),
                "После удаления корзина должна быть пуста, фактически: " + itemNamesAfterRemove
        );

        assertFalse(
                cart.isCartBadgeDisplayed(),
                "После удаления последнего товара значок корзины не должен отображаться"
        );
    }
}
