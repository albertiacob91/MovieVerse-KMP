FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . /app
RUN chmod +x ./gradlew
RUN ./gradlew :server:installDist --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/server/build/install/server /app

EXPOSE 8081

CMD ["./bin/server"]