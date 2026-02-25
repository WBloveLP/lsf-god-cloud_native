FROM openjdk:17
COPY ./target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]

# docker login --username=nick1928495244 crpi-co509r5gdyecg5t2.cn-hangzhou.personal.cr.aliyuncs.com

# docker tag [ImageId] crpi-co509r5gdyecg5t2.cn-hangzhou.personal.cr.aliyuncs.com/lpruoyu/printreqpath:[镜像版本号]
# docker build -t crpi-co509r5gdyecg5t2.cn-hangzhou.personal.cr.aliyuncs.com/lpruoyu/cookieanduri:latest -f Dockerfile .

# docker push crpi-co509r5gdyecg5t2.cn-hangzhou.personal.cr.aliyuncs.com/lpruoyu/printreqpath:[镜像版本号]
# docker push crpi-co509r5gdyecg5t2.cn-hangzhou.personal.cr.aliyuncs.com/lpruoyu/cookieanduri:latest