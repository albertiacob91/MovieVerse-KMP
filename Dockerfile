FROM gradle:8.10-jdk17 AS build
WORKDIR /app
COPY . /app
RUN gradle :server:installDist --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/server/build/install/server /app

EXPOSE 8081

CMD ["./bin/server"]