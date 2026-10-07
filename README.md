# Izteleuev Daniel Ruslanovich — SauceDemo UI-автотесты (Selenium + TestNG + Allure)

## Сценарии

- [X] Авторизация: позитивный сценарий (`standard_user`)
- [X] Авторизация: заблокированный пользователь (`locked_out_user`)
- [X] Авторизация: неверный пароль (негативный)
- [X] Корзина: добавление товара
- [X] Корзина: удаление товара
- [X] Сортировка: по цене (Low -> High)
- [X] Сортировка: по названию (Z -> A)
- [X] Оформление заказа: полный цикл (Finish)
- [X] Оформление заказа: валидация обязательных полей (негативный)
- [X] Оформление заказа: отмена (Cancel)

## Скриншоты работы

### 1. Кроссбраузерный прогон Dashboard отчёта Allure

![Dashboard Allure](docs/screenshots/01-overview-dashboard.png)

### 2. Кроссбраузерность: Chrome / Firefox / Edge в Suites

![Suites Chrome Firefox Edge](docs/screenshots/02-suites-cross-browser.png)

### 3. Карточка теста: шаги, параметры, метки, вложения

![Карточка теста со шагами](docs/screenshots/03-test-card-steps.png)

### 4. Карточка упавшего теста со скриншотом (вложение при падении)

![Карточка упавшего теста](docs/screenshots/04-failed-card-with-screenshot.png)

### 5. Категории дефектов (`categories.json`)

![Категории дефектов](docs/screenshots/05-categories.png)

### 6. Environment и статистика упавшего прогона

![Environment](docs/screenshots/06-dashboard-failed-demo.png)

### 7. Скриншот, который автотест прикладывает при падении

![Скриншот падения](docs/screenshots/07-failure-screenshot-attachment.png)
