# Dockerfile.single

FROM bellsoft/liberica-openjdk-debian:21.0.4-cds

# Gradle 설치 및 빌드 환경 포함
RUN apt-get update && apt-get install -y wget unzip curl && rm -rf /var/lib/apt/lists/*
RUN wget -q https://services.gradle.org/distributions/gradle-8.12-bin.zip -O gradle.zip && \
    unzip gradle.zip -d /opt/gradle && rm gradle.zip
ENV GRADLE_HOME=/opt/gradle/gradle-8.12
ENV PATH=$GRADLE_HOME/bin:$PATH

WORKDIR /app
COPY . .

RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

# 실행
EXPOSE 8080
CMD ["java", "-jar", "build/libs/app.jar"]
