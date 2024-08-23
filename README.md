## Веб-приложение для размещения объявлений о продаже товаров

### Используемые паттерны
✔️ Microservices<br>
✔️ HighLoad<br>
✔️ Event-Driven<br>
✔️ Choreographed Saga<br>

### Используемые технологии
✔️ Spring Boot (MVC, AOP, JPA, Security, Cache, Kafka)<br>
✔️ PostgreSQL, Redis<br>
✔️ Kafka, Kafka Connect<br>
✔️ Docker Compose, Kubernetes, Helm<br>
✔️ Loki, Jaeger, Prometheus, Grafana<br>

### Функционал
✔️ Регистрация пользователя (userservice)<br>
✔️ Регистрация товаров для продажи (productservice)<br>
✔️ Учет запаса товаров (stockservice)<br>
✔️ Создание заказа (orderservice)<br>
✔️ Снятие/передача/пополнение денег (balanceservice)<br>
✔️ Учет и обновление статуса доставки товара (deliveryservice)<br>
✔️ Отправка уведомлений пользователям (notificationservice)<br>

### Основная Saga
1) Пользователь создаёт заказ (orderservice)
2) Товар резервируется (stockservice)
3) Ожидается пополнение счета заказчика (balanceservice)
4) После пополнения деньги резервируются (balanceservice)
5) Ожидается отправка товара продавцом (deliveryservice)
6) Ожидается получение товара покупателем (deliveryservice)
7) Деньги переводятся продавцу, уменьшение запаса товара фиксируется

### Способы запуска
* docker/compose.yaml
* kubernetes/install.sh

### Схема
#### Сервисы
![chart](docs/chart_services.png)
#### Упрощённая саги
![chart](docs/chart_simple.png)
#### Полная саги
![chart](docs/chart_saga.png)