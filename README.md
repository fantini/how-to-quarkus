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

## Related Guides

- QUARKUS AMAZON SERVICES ([guide](https://quarkiverse.github.io/quarkiverse-docs/quarkus-amazon-services/dev/amazon-s3.html)): S3
- S3 CELEPAR ([guide](https://celepar.wikis.pr.gov.br/plataformadesenvolvimento/wiki/S3_Defini%C3%A7%C3%B5es)): S3 Definições

