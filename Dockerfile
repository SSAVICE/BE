# 1. 자바 21
FROM eclipse-temurin:21-jdk-jammy

# 2. JAR 파일 위치 변수 설정
ARG JAR_FILE=build/libs/*.jar

# 3. jar 파일을 컨테이너 안으로 복사
COPY ${JAR_FILE} app.jar

# 4. 타임존 설정 (한국 시간)
ENV TZ=Asia/Seoul

# 5. 컨테이너 실행 시 자바 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
