# Java 21 환경 제공
FROM amazoncorretto:21

# 빌드 과정에서 사용할 JAR 파일 경로를 ARG로 설정 (기본값: build/libs/*.jar)
ARG JAR_FILE=build/libs/*.jar

# 빌드된 JAR 파일을 컨테이너 내부로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너가 실행될 때 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app.jar"]

# 컨테이너의 기본 시간대를 'Asia/Seoul'로 설정
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
