# Overview poc-jaeger

Simple poc using [Jaeger] and [Spring Boot]

# Requirements

1. Install [docker]
2. Install [docker-compose]

# Setup

1. Run the command `docker-compose build`
2. Run the command `docker-compose up`
    1. jaeger
    2. zookeeper
    3. kafka
    4. order-service
    5. payment-service

## [Jaeger]

Open the url `http://localhost:16686`

# Testing

1. Start `order-service`
2. Start `payment-service`
4. Execute curl sync
    ```
    curl --location --request POST 'localhost:8080/api-order/api/v1/orders' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "items": [
        {
            "productId": "6a82baf4-6bae-4f31-a375-26a3f82239a7",
            "quantity": 2,
            "price": 190.00
        }
        ]
    }'
    ```

5. Execute curl async
   ```
    curl --location --request POST 'localhost:8080/api-order/api/v1/orders/async' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "items": [
        {
            "productId": "6a82baf4-6bae-4f31-a375-26a3f82239a7",
            "quantity": 2,
            "price": 190.00
        }
        ]
    }'
    ```
   

[Jaeger]: https://www.jaegertracing.io
[Spring Boot]: https://spring.io
[Docker]: https://docs.docker.com/get-docker
[docker-compose]: https://docs.docker.com/compose/install/
