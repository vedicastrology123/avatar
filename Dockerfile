# --- Stage 1: Build Java (Unchanged) ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src/main/webapp/WEB-INF/lib ./src/main/webapp/WEB-INF/lib
RUN mvn dependency:resolve -B
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Final Image (Tomcat + Node + Satori) ---
FROM tomcat:10.1-jdk17

# 1. Install Node.js
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_22.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

# 2. Setup Node App (The Renderer)
WORKDIR /renderer

# Copy package files first to leverage Docker cache
COPY renderer/package*.json ./

# This will now install satori-html, resvg, etc. automatically
RUN npm install --omit=dev

# Copy the rest of the renderer code (index.js, fonts, etc.)
COPY renderer/ .

# 3. Setup Tomcat & Astrology Data
WORKDIR /usr/local/tomcat
RUN mkdir -p /usr/local/tomcat/swiseph_data
COPY swiseph_data/*.se1 /usr/local/tomcat/swiseph_data/
RUN rm -rf webapps/ROOT/
COPY --from=build /app/target/avatar.war webapps/ROOT.war

# 4. Final Execution
EXPOSE 8080 3000

# Using absolute paths for the CMD to avoid directory confusion
# CMD ["sh", "-c", "node /renderer/index.js & catalina.sh run"]

# Change the last line to this:
CMD ["sh", "-c", "cd /renderer && node index.js & catalina.sh run"]