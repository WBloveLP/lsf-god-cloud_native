FROM tomcat:7.0-jre8

# 创建密钥目录
RUN mkdir -p /usr/local/tomcat/keys

# 复制配置文件
COPY server.xml /usr/local/tomcat/conf/

# 生成 keystore
RUN keytool -genkey -noprompt \
    -alias tomcat \
    -keyalg RSA \
    -keysize 2048 \
    -keystore /usr/local/tomcat/keys/tomcat.keystore \
    -storepass liupeng \
    -keypass liupeng \
    -validity 3650 \
    -dname "CN=localhost, OU=Test, O=Test, L=Test, ST=Test, C=CN"

# 设置权限
RUN chmod 755 /usr/local/tomcat/keys && \
    chmod 644 /usr/local/tomcat/keys/tomcat.keystore

EXPOSE 8080 8443

CMD ["catalina.sh", "run"]