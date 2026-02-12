#这个也得有
FROM openjdk:8u102-jre
#复制打好的jar包
COPY target/*.jar /app.jar

RUN  apk add -U tzdata; \
ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime; \
echo 'Asia/Shanghai' >/etc/timezone; \
touch /app.jar;

ENV JAVA_OPTS=""
ENV PARAMS=""

EXPOSE 8081

ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar /app.jar $PARAMS" ]