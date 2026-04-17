# --- Stage 1: The Builder ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Resolve dependencies first (Cached unless pom.xml changes)
COPY pom.xml .
COPY src/main/webapp/WEB-INF/lib ./src/main/webapp/WEB-INF/lib
RUN mvn dependency:resolve -B

# 2. Copy source and build (This is the only part that will re-run)
COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk17

ENV DEBIAN_FRONTEND=noninteractive

RUN echo "Acquire::http::Pipeline-Depth 0; \n\
Acquire::http::No-Cache true; \n\
Acquire::BrokenProxy true;" > /etc/apt/apt.conf.d/99fixbadproxy

# 2. Install dependencies and Chrome 
# (Tomcat 10.1 is Debian-based, so we use standard debian commands)
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    gnupg \
    ca-certificates && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /etc/apt/keyrings/google-chrome.gpg && \
    echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable --no-install-recommends && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 4. DATA & DEPLOY
RUN mkdir -p /usr/local/tomcat/swiseph_data
COPY swiseph_data/*.se1 /usr/local/tomcat/swiseph_data/
RUN chmod -R 755 /usr/local/tomcat/swiseph_data
# ENV SE_PATH=/usr/local/tomcat/swiseph_data



RUN rm -rf /usr/local/tomcat/webapps/ROOT/
COPY --from=build /app/target/avatar.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]