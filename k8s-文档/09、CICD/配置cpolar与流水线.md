# cpolar配置（公网映射本地）


`本地地址`填https://192.168.10.138:443


![alt text](assets/局部截取_20260317_174018.png)


![alt text](assets/局部截取_20260317_174153.png)

```sh
#配置证书

openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout tls.key -out tls.crt -subj "/CN=*.cpolar.top/O=*.cpolar.top"

kubectl create secret tls cpolar --key tls.key --cert tls.crt -n devops
```

```yaml
#编写ingress规则
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: jenkins-cpolar
  namespace: devops
spec:
  tls:
  - hosts:
      - 373ee3da.r38.cpolar.top
    secretName: cpolar
  rules:
  - host: 373ee3da.r38.cpolar.top
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: jenkins
            port:
              number: 8080
```

现在，访问https://373ee3da.r38.cpolar.top/就可以访问jenkins了


# Jenkins的Triggers


以前的方式：
新建流水线==>Triggers==>【选择】触发远程构建: 
  - gitee的webhook：http://lpruoyu:117d1deb9a53620ab6a7bf42e188d2bb11@373ee3da.r38.cpolar.top/job/end/build?token=lpruoyu



Generic Webhook Trigger方式：【新建流水线=>填git仓库地址，其他都不用搞，运行一下就自动填好了】
  - 参考：https://plugins.jenkins.io/generic-webhook-trigger/
  - gitee的webhook： http://373ee3da.r38.cpolar.top/generic-webhook-trigger/invoke?token=lpruoyu-token 【一个流水线一个token，自己在pipeline中改】


# pipeline测试：不配Pod Template


```
pipeline {
  agent {
    kubernetes {
      yaml """
apiVersion: v1
kind: Pod
metadata:
  namespace: devops
spec:
  serviceAccountName: jenkins
  volumes:
  - name: maven-repo
    persistentVolumeClaim: 
        claimName: maven-jar-pvc
  - name: maven-conf
    configMap:
      name: maven-conf
  - name: docker-socket
    hostPath:
      path: /var/run/docker.sock
      type: Socket
  containers:
  - name: jnlp
    image: jenkins/inbound-agent:latest-jdk21
    args: ["\$(JENKINS_SECRET)", "\$(JENKINS_NAME)"]
  - name: maven
    image: maven:3.9.9-eclipse-temurin-17
    command: ['cat']
    tty: true
    volumeMounts:
    - name: maven-conf
      mountPath: /root/maven/  
    - name: maven-repo
      mountPath: /root/maven/.m2/
    securityContext:
      runAsUser: 0
      runAsGroup: 0
  - name: docker
    image: docker:stable
    command: ['sleep', 'infinity']
    volumeMounts:
    - name: docker-socket
      mountPath: /var/run/docker.sock
    securityContext:
      runAsUser: 0
      runAsGroup: 0
"""
    }
  }
  stages {
    stage('Test') {
      steps {
        container('maven') {    
                echo "maven版本："
                sh 'mvn -v'
                echo "maven配置文件"
                sh 'cat /root/maven/settings.xml'
                echo "maven目录位置信息"
                sh 'ls /root/maven/ -al'
        }
        container('docker') {
          echo '开始执行 docker 命令'
          sh 'docker version'
          sh 'docker ps'
          echo 'docker 命令执行完毕'
        }
      }
    }
  }
}
```

# pipeline测试：配Pod Template


制作拥有jnlp+docker+maven的镜像：【crpi-co509r5gdyecg5t2.cn-hangzhou.personal.cr.aliyuncs.com/lpruoyu/with-docker-maven-jnlp-jdk21:latest】


```
# 基于 Jenkins 官方 inbound-agent (JDK 21)
FROM jenkins/inbound-agent:latest-jdk21

# 切换到 root 用户进行安装
USER root

# 安装必要工具和依赖
RUN apt-get update && apt-get install -y \
    curl \
    libseccomp2 \
    && rm -rf /var/lib/apt/lists/*

# 安装 Maven 3.9.9（设置 jenkins 用户权限）
ARG MAVEN_VERSION=3.9.9
ARG MAVEN_HOME=/opt/maven
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar xz -C /opt \
    && mv /opt/apache-maven-${MAVEN_VERSION} ${MAVEN_HOME} \
    && ln -s ${MAVEN_HOME}/bin/mvn /usr/local/bin/mvn \
    && chown -R jenkins:jenkins ${MAVEN_HOME}

# 安装 Docker CLI（静态二进制，与宿主机通信）
RUN curl -fsSL https://download.docker.com/linux/static/stable/x86_64/docker-24.0.7.tgz | tar xz -C /tmp \
    && mv /tmp/docker/docker /usr/local/bin/docker \
    && chmod +x /usr/local/bin/docker \
    && rm -rf /tmp/docker \
    && chown jenkins:jenkins /usr/local/bin/docker

# 创建 Maven 配置目录（jenkins 用户可读写）
RUN mkdir -p /home/jenkins/.m2 \
    && chown -R jenkins:jenkins /home/jenkins/.m2

# 设置环境变量（适配 jenkins 用户）
ENV MAVEN_HOME=${MAVEN_HOME} \
    M2_HOME=${MAVEN_HOME} \
    M2=/opt/maven/bin \
    PATH=${MAVEN_HOME}/bin:${PATH} \
    DOCKER_HOST=unix:///var/run/docker.sock

WORKDIR /home/jenkins/agent
```


pod template配置：

>前提：ConfigMap：maven-conf、PVC：maven-jar-pvc、K8s的打了标签jnlp-node-true的机器得装docker（废话）：/var/run/docker.sock，这些都得有

![alt text](assets/局部截取_20260317_170523.png)


![alt text](assets/局部截取_20260317_173440.png)


![alt text](assets/局部截取_20260317_170617.png)


![alt text](assets/局部截取_20260317_170630.png)


![alt text](assets/局部截取_20260317_170640.png)


![alt text](assets/局部截取_20260317_170653.png)



测试：

```
pipeline {
    agent none
    stages {
        stage('检查打包机') {
            agent {
                label 'mjnlpdockermaven'
            }
            steps {
                sh 'printenv'
                echo "lplplplp"
                echo "docker版本："
                sh ' docker version'
                sh ' docker ps'
                echo "maven版本："
                sh 'mvn -v'
                echo "maven配置文件"
                sh 'cat /root/maven/settings.xml'
                echo "maven目录位置信息"
                sh 'ls /root/maven/ -al'
            }
        }
    }
}

```