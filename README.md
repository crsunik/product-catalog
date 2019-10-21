Technical requirements
=
For this application to run, instance of running PostgresSQL 9.5 database is required. 
Database url has to be passed as environment variable, alongside with username and password.

``` properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Other than that, Java 11 and relatively new gradle is required.

Application can be started using `gradle bootRun` command or by running class `RocheApplication` in Intellij Idea.

Development
=
For development purpose `docker-compose.yml` and `application-local.properties` are provided. 
Running the command `docker-compose up -d` in the project app PostgresSQL will be available at `port 54320`.
To run the app docker machine ip should be set up in the `application-local.properties`. 
After whole set up is done the only thing to do is to launch the app with `local` spring profile.

Additionally after launching the app with `local` profile swagger ui will be available at `http://localhost:8080/roche/swagger-ui.html#`

Usage
=
To use this program:

Send POST request with Content-Type: application/json header to `http://localhost:8080/roche/api/v1/orders/roche/api/v1/products` to create a product:

Request:
```json
{
  "name": "product_name",
  "price": 20
}
```
Response:
```json
{
  "sku": "25341375",
  "name": "product_name",
  "price": 20,
  "createdDate": "21-10-2019"
}
```


Send GET request to `http://localhost:8080/roche/api/v1/products` to list all the products available

Request:
```json
[
  {
    "sku": "82912721",
    "name": "product_name_1",
    "price": 20,
    "createdDate": "21-10-2019"
  },
  {
    "sku": "99110213",
    "name": "product_name_2",
    "price": 30,
    "createdDate": "21-10-2019"
  }
]
```
Send GET request `http://localhost:8080/roche/api/v1/products/{sku}"` to retrieve details of the product with given sku

Response:
```json
{
  "sku": "99110213",
  "name": "product_name",
  "price": 30,
  "createdDate": "21-10-2019"
}
``` 

Send PUT request with Content-Type: application/json header `http://localhost:8080/roche/api/v1/products/{sku}"` to update given product

Request:
```json
{
  "sku": "99110213",
  "name": "product_name",
  "price": 30,
  "createdDate": "21-10-2019"
}
``` 

Send DELETE request `http://localhost:8080/roche/api/v1/products/{sku}"` to delete given product


Send POST request with Content-Type: application/json header to `http://localhost:8080/roche/api/v1/orders` to place an order:

Request:
```json
{
  "buyerEmail": "test@test",
  "orderLines": [
    {
      "productSku": "99110213",
      "quantity": 5
    },
    {
      "productSku": "82912721",
      "quantity": 7
    }
  ]
}

```

Response:
```json
{
  "id": 7,
  "orderLines": [
    {
      "product": {
        "sku": "99110213",
        "name": "product_name_1",
        "price": 30,
        "createdDate": "21-10-2019"
      },
      "quantity": 5,
      "amount": 150
    },
    {
      "product": {
        "sku": "82912721",
        "name": "product_name_2",
        "price": 21,
        "createdDate": "21-10-2019"
      },
      "quantity": 7,
      "amount": 147
    }
  ],
  "totalAmount": 297,
  "createdDate": "21-10-2019",
  "buyerEmail": "test@test"
}
```

Send GET request `http://localhost:8080/roche/api/v1/orders/{id}"` to retrieve details of the order with given id

Response:
```json
{
  "buyerEmail": "string",
  "createdDate": "string",
  "id": 0,
  "orderLines": [
    {
      "amount": 0,
      "product": {
        "createdDate": "string",
        "name": "string",
        "price": 0,
        "sku": "string"
      },
      "quantity": 0
    }
  ],
  "totalAmount": 0
}
``` 

Send GET request to `http://localhost:8080/roche/api/v1/orders?from={from}&to={to}` to retrieve all orders from given time period. 
Dates should be in format `dd-MM-yyyy` e.g. `01-10-2019`

```json
[
  {
    "id": 2,
    "orderLines": [
      {
        "product": {
          "sku": "82912721",
          "name": "product_name_1",
          "price": 21,
          "createdDate": "21-10-2019"
        },
        "quantity": 5,
        "amount": 105
      },
      {
        "product": {
          "sku": "99110213",
          "name": "product_name_2",
          "price": 30,
          "createdDate": "21-10-2019"
        },
        "quantity": 10,
        "amount": 300
      }
    ],
    "totalAmount": 405,
    "createdDate": "21-10-2019",
    "buyerEmail": "test@test"
  },
  {
    "id": 3,
    "orderLines": [
      {
        "product": {
          "sku": "82912721",
          "name": "product_name_1",
          "price": 21,
          "createdDate": "21-10-2019"
        },
        "quantity": 5,
        "amount": 105
      },
      {
        "product": {
          "sku": "99110213",
          "name": "product_name_2",
          "price": 30,
          "createdDate": "21-10-2019"
        },
        "quantity": 11,
        "amount": 330
      }
    ],
    "totalAmount": 435,
    "createdDate": "21-10-2019",
    "buyerEmail": "test@test"
  }
]
```

Technical stack
=
My decision was to go with the stack I'm most comfortable with: Spring Boot 2, Gradle, Spock, PostgreSQL, and Liquibase,
among other technologies.

Spring Boot because it's really easy to start developing fast with it and was part of the task.

Gradle because it's now mature, well-documented, has a vast ecosystem of stable plugin and integrations.

Spock because I find it to be very productive and easy to read thanks to the syntax sugar both Groovy and Spock offers.

PostgreSQL because it one of the best of the open source SQL databases, and it is evolving really fast.

Liquibase because it's elegant and portable between different database engines.

What I didn't focus on and can be improved
=
1. Currency was not included in the price calculations. In my opinion it would add unnecessary complexity to the task and since it wasn't mentioned in the task, I have decided to cut it out.  
2. SKU generator is simple random string generator. In the real application it should be a more sophisticated service that would ensure uniqueness of the generated number. 
As it is, it is good enough and database constraint guarantees that here will be no duplicates in the DB.
3. Security. Right now the project and endpoints are not secured at all.
4. At the moment I am writing this there is no integration test - simply, because I've run out of time. I will add it as soon as possible
5. Spring Cloud Contract could be introduced to ensure compatibility with service client

What I did focus on
=
1. Simple, yet elegant code decomposition. I've tried to keep every class small, easy to read and with clear purpose. 
2. All meaningful parts have unit tests. Rest Controllers were tested with Spring MockMVC to ensure that communication works without any surprises.
3. Main purpose of this application is to provide REST service meeting the requirements.
