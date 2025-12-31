FROM eclipse-temurin:24-jdk AS builder
WORKDIR /workspace/app

# Copy Gradle wrapper and build files
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradlew ./

# Copy frontend files
COPY package.json package-lock.json ./
COPY tsconfig.json tsconfig.app.json tsconfig.node.json tsconfig.vitest.json ./
COPY vite.config.ts env.d.ts ./

# Copy source code
COPY src src

RUN ./gradlew build -x test
RUN mkdir -p build/libs/dependency && (cd build/libs/dependency; jar -xf ../opportune-*.jar)


FROM eclipse-temurin:24-jre-alpine AS runner
VOLUME /tmp

RUN addgroup -S app && adduser -S spring-app -G app
USER spring-app

ARG DEPENDENCY=/workspace/app/build/libs/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","com.glinboy.opportune.OpportuneApplication"]
