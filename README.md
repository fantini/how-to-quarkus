# how-to-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Running services in dev

### S3 Services

```shell script
docker run -it --publish 4566:4566 -e SERVICES=s3 -e START_WEB=0 localstack/localstack:1.4.0
aws configure --profile localstack
 AWS Access Key ID [None]: test-key
 AWS Secret Access Key [None]: test-secret
 Default region name [None]: us-east-1
 Default output format [None]:
aws s3 mb s3://quarkus.s3.quickstart --profile localstack --endpoint-url=http://localhost:4566
```

### AMQP 

#### AMQP Docker-Compose

```
version: '2'
services:
  artemis:
    image: webcenter/activemq
    ports:
      - "8161:8161"
      - "61616:61616"
      - "5672:5672"
    environment:
      ACTIVEMQ_ADMIN_LOGIN: quarkus
      ACTIVEMQ_ADMIN_PASSWORD: quarkus
    networks:
      - mq-network
networks:
  mq-network:
    driver: bridge
volumes:
  activemq:
```
#### AMQP Services

```shell script
docker-compose -f compose.yaml up -d
```

## Related Guides

- QUARKUS AMAZON SERVICES ([guide](https://quarkiverse.github.io/quarkiverse-docs/quarkus-amazon-services/dev/amazon-s3.html))
- S3 CELEPAR ([guide](https://celepar.wikis.pr.gov.br/plataformadesenvolvimento/wiki/S3_Defini%C3%A7%C3%B5es))
- QUARKUS AMQP ([guide](https://quarkus.io/guides/amqp))

