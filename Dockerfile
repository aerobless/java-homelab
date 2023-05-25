FROM openjdk:20 as builder

WORKDIR /app

COPY application/target/java-homelab*.jar ./java-homelab.jar

RUN java -Djarmode=layertools -jar ./java-homelab.jar extract

FROM openjdk:20

# COPY docker-configurations /docker-configurations

WORKDIR /app

COPY --from=builder app/dependencies ./
COPY --from=builder app/spring-boot-loader ./
COPY --from=builder app/snapshot-dependencies ./
COPY --from=builder app/application ./

# COPY java-homelab/frontend/dist/java-homelab /frontend

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.JarLauncher"]