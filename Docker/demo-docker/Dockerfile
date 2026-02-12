FROM openjdk:17
COPY target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]



# 考虑到线上的bug/调优等，建议使用jdk
#FROM openjdk:8-jre-alpine
#LABEL maintainer="lpruoyu@gmail.com"
##COPY之前自己手动编译出jar包
#COPY target/*.jar /app.jar
##touch /app.jar ，在linux中随便touch个文件会发现，touch之后，该文件的时间变了，内容不会变；
##所以touch *.jar的好处是：记录一下最后该jar保存进来的时间，方便以后追溯
#RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone && touch /app.jar
#ENV JAVA_OPTS=""
#ENV PARAMS=""
##每一个启动的Java容器都是一个小的jvm，而且都是进程隔离的，
##在这儿加这俩ENV的作用就是，方便以后修改jvm运行参数，方便调优
#ENTRYPOINT [ "sh","-c","java -Djava.security.egd=file:/dev/./urandom$JAVA_OPTS -jar /app.jar $PARAMS" ]
