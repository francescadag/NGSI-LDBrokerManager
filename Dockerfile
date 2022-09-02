FROM openjdk:16-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
RUN chmod +x mvnw
RUN chmod 777 mvnw
COPY .mvn .mvn
COPY pom.xml .

COPY src src

RUN ./mvnw -f /workspace/app/pom.xml install -P prod
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:16-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","it.eng.ngsild.broker.manager.BrokerManagerApp"]
