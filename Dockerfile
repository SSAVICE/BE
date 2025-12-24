# 1. 자바 21 - 실행 전용 파일 JRE
FROM eclipse-temurin:21-jre-jammy

# 2. JAR 파일 위치 변수 설정
WORKDIR /app

# 3. 타임존 설정
ENV TZ=Asia/Seoul

# 4. 빌드 결과물(JAR) 복사
# build.gradle 설정에 따라 plain jar가 생성될 수 있으므로, 실행 가능한 jar만 명시적으로 가져오는 것이 좋습니다.
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 5. 컨테이너 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]