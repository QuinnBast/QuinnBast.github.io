# Step 1: Build the frontend
# NOTE: I was unable to build the frontend in a container for some reason. the NPM build complains.
# Thus, before building a container, ensure the frontend is built locally.
# This Dockerfile just copies the build artifacts manually for now.

#FROM node:22-slim AS frontend-build
#WORKDIR /src/frontend
#COPY src/package.json ./
#RUN npm install
#COPY src/ ./
#RUN npm run build

# Step 2: Build the backend
FROM eclipse-temurin:21-jdk-alpine AS backend-build
WORKDIR /app
COPY server/ ./
RUN ./gradlew clean build

# Step 3: Create final production image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy backend build artifacts
COPY --from=backend-build /app/build/distributions/*.tar app.tar
RUN tar -xf app.tar
RUN mkdir -p /app/src/dist
# Copy frontend build artifacts
COPY src/dist/ /app/src/dist/

# Expose application port
EXPOSE 9000

# Command to run the application
CMD ["PersonalWebsite-1.0-SNAPSHOT/bin/PersonalWebsite"]