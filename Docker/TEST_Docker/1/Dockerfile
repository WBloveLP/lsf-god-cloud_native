# 指定基础镜像
FROM openjdk:11


COPY claim-app-0.0.1-SNAPSHOT.jar /tmp/app.jar

# 暴露端口
EXPOSE 9527
# 入口，java项目的启动命令
ENTRYPOINT java -jar /tmp/app.jar