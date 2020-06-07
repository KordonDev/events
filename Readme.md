# Events

## Install Attendee
It is a spring boot application in kotlin with maven.

```
cd attendee
./mvnw install
./mvnw spring-boot:run
```

## Customation
* in the backend in `resource/static/kreiszeltlager-logo.jpg` paste the current logo for the header

## Run backend with docker
* `cd attendee`
* `./docker-compose/docker-registry.sh`
* `mvn compile jib:build`
* `cd docker-compose && docker-compose up && cd ..`
* `docker run -p8080:8080 localhost:5000/lager-melder:built-with-jib`

* `docker container stop registry`