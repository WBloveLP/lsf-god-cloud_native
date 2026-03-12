<h1>
    <center>DevOps实战</center>
</h1>



# 一、DevOps概念

## 1、DevOps是什么

 Development和Operations的组合词；

DevOps: Development 和 Operations 的组合 

- DevOps 看作开发（软件工程）、技术运营和质量保障（QA）三者的交集。 
- 突出重视软件开发人员和运维人员的沟通合作，通过自动化流程来使得软件构建、测试、 发布更加快捷、频繁和可靠。 
- DevOps 希望做到的是软件产品交付过程中 **IT 工具链的打通**，使得各个团队减少时间损 耗，更加高效地协同工作。专家们总结出了下面这个 DevOps 能力图，良好的闭环可以大大 增加整体的产出。



## 2、CICD是什么

**持续**集成    **持续**部署

### 1、基本理念 



**1、持续集成（Continuous Integration）** 

持续集成是指软件个人研发的部分向软件整体部分交付，频繁进行集成以便更快地发现 其中的错误。“持续集成”源自于极限编程（XP），是 XP 最初的 12 种实践之一。 

**CI 需要具备这些：** 

- **全面的自动化测试。**这是实践持续集成&持续部署的基础，同时，选择合适的 自动化测试工具也极其重要； 
- **灵活的基础设施。容器，虚拟机**的存在让开发人员和 QA 人员不必再大费周 折； 
- **版本控制工具。**如 **Git**，CVS，SVN 等； 
- **自动化的构建和软件发布流程的工具**，如 **Jenkins**，flow.ci； 
- **反馈机制。**如构建/测试的失败，可以快速地反馈到相关负责人，以尽快解决达到一个更稳定的版本。 

**2、持续交付（Continuous Delivery）** 

持续交付在持续集成的基础上，将集成后的代码部署到**更贴近真实运行环境的**「类生产环境」（production-like environments）中。持续交付优先于整个产品生命周期的软件部署，建立 在高水平自动化持续集成之上。 

灰度发布。 

持续交付和持续集成的优点非常相似： 

- **快速发布。**能够应对业务需求，并更快地实现软件价值。 
- **编码->测试->上线->交付**的频繁迭代周期缩短，同时获得迅速反馈；
- **高质量的软件发布标准**。整个交付过程标准化、可重复、可靠， 
- **整个交付过程进度可视化，**方便团队人员了解项目成熟度； 
- **更先进的团队协作方式。**从需求分析、产品的用户体验到交互 设计、开发、测试、运维等角色密切协作，相比于传统的瀑布式软件团队，更少浪费。 

**3、持续部署（Continuous Deployment）** 

**持续部署**是指当交付的代码通过评审之后，**自动部署到生产环境中**。持续部署是持续交付的最高阶段。这意味着，所有通过了一系列的自动化测试的改动都将自动部署到生产环境。它也可以被称为“Continuous Release”。 

> “开发人员**提交代码**，持续集成服务器获取代码，执行单元测试，根据测试结果决定是否部署到预演环境，如果成功部署到预演环境，进行整体*验收测试，如果测试通过，自动部署到产品环境，***全程自动化高效运转。**

持续部署主要好处是，可以相对独立地部署新的功能，并能快速地收集真实用户的反馈。 

> “You build it, you run it”，这是 **Amazon** *一年可以完成 5000 万次部署，* 平均每个工程师每天部署超过 50 次的核心秘籍。
>
> 5000/365 = 15 万次 

开发人员代码敲完。可以release的时候，提交代码，   剩下的全部一站式自动搞定

### 2、最佳实践

#### 1、内循环与外循环

![CI/CD inner and outer loop](assets/inner-outer-loop.png)

- 内循环（开发要做的事情）：
  - 编码、测试、运行、debug、提交
- 代码推送到代码仓库（svn，git）【代码回滚】
- 进行CI过程（持续集成），万物皆可容器化。打包成一个Docker镜像
- 镜像推送到镜像仓库
- 测试
- 持续部署流程（CD），拿到之前的镜像，进行CD。怎么放到各种环境。uat、test、prod
- 外循环（）
  - 运行时监控
  - 生产环境的管理
  - 监控
  - 线上反馈到开发
- 来到内循环



MVC：Model（bean,entity,to,po......） View(thymeleaf、前后分离....)  Controller（xxxxx）









#### 2、实践流程


新功能，bug修复。

- ==创建分支来做这个事情（开发功能）==
- ==提交分支的代码改变==
- **进入持续集成流程**
  - **当前分支代码功能性自动化构建和测试**
  - **自动工具推送这次提交**
  - **自动化集成测试**
  - **可以看到效果**
  - **人工确认此次功能是否发布到生产环境**
- **代码合并。**
- **进入持续部署流程**
  - 构建、测试、发布......





#### 3、CICD LandSpace

禅道


# 二、Jenkins 



```
/var/jenkins_home   jenkins的家目录
包含了jenkins的所有配置。


以后要注意备份  /var/jenkins_home （以文件的方式固化的）
```

Java

Jenkins镜像用  https://hub.docker.com/r/jenkinsci/jenkins/

驱动我们整个CICD过程的很多工具

![1618201317036](assets/1618201317036.png)

## 1、Jenkins安装

https://www.jenkins.io/zh/doc/book/installing/

```sh
docker run \
  -u root \
  -d \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins-data:/var/jenkins_home \
  -v /etc/localtime:/etc/localtime:ro \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --restart=always \
  jenkinsci/blueocean
  
  
  #自己构建镜像 RUN的时候就把时区设置好
  #如果是别人的镜像，docker hub，UTC； 容器运行时 ， -v /etc/localtime:/etc/localtime:ro
  
  
  
  jenkinsci/jenkins 是没有 blueocean插件的，得自己装 
  jenkinsci/blueocean：带了的
  
  #/var/run/docker.sock 表示Docker守护程序通过其监听的基于Unix的套接字。 该映射允许 jenkinsci/blueocean 容器与Docker守护进程通信， 如果 jenkinsci/blueocean 容器需要实例化其他Docker容器，则该守护进程是必需的。 如果运行声明式管道，其语法包含agent部分用 docker；例如， agent { docker { ... } } 此选项是必需的。 
  
  #如果你的jenkins 安装插件装不上。使用这个镜像【 registry.cn-qingdao.aliyuncs.com/lfy/jenkins:plugins-blueocean 】默认访问账号/密码是【admin/admin】
```

安装插件，并配置用户

![1618202173600](assets/1618202173600.png)





```sh
#备份jenkins 
tar -cvf jenkins_data.tar /var/lib/docker/volumes/jenkins-data/_data/

#恢复jenkins
tar -xvf jenkins_data.tar /var/lib/docker/volumes/jenkins-data/_data/
```



## 2、Jenkins实战



代码在本地修改----提交到远程gitee----触发jenkins整个自动化构建流程（打包，测试，发布，部署）









### 1、准备一个git项目进行测试

我们以gitee为例，github可能太慢了。需要idea安装gitee插件。或者自己熟悉手动命令也行。

步骤：

- 1、idea创建Spring Boot项目

- 2、VCS - 创建git 仓库

- 3、gitee创建一个空仓库，示例为public

- 4、idea提交内容到gitee

- 5、开发项目基本功能，并在项目中创建一个Jenkinsfile文件

- 6、创建一个名为 devops-java-demo的流水线项目，使用项目自己的流水线

  



Jenkins的工作流程

1、先定义一个流水线项目，指定项目的git位置

- 流水线启动
  - 1、先去git位置自动拉取代码
  - 2、解析拉取代码里面的Jenkinsfile文件
  - 3、按照Jenkinsfile指定的流水线开始加工项目
    - 





> Jenkins重要的点
>
> 0、jenkins的家目录 /var/jenkins_home 已经被我们docker外部挂载了  ；
>
> ​	/var/lib/docker/volumes/jenkins-data/_data
>
> 1、WORKSPACE（工作空间）=/var/jenkins_home/workspace/java-devops-demo
>
>   - 每一个流水线项目，占用一个文件夹位置
>
>   - BUILD_NUMBER=5；当前第几次构建
>
>   - WORKSPACE_TMP（临时目录）=/var/jenkins_home/workspace/java-devops-demo@tmp
>
>       - ```
>       JOB_URL=http://139.198.9.163:8080/job/java-devops-demo/
>       ```
>    ```
> 
>    ```
>
> ```
> 
> ```
>
> ```
> 
> ```
>
> ```
> 
> ```
>
> 2、常用的环境如果没有，jenkins配置环境一大堆操作
>
> 3、jenkins_url ：  http://139.198.27.103:8080/
>
> ```
> 
> ```
>
> ```
> 
> ```

### 2、远程构建触发

期望效果：`远程的github代码提交了，jenkins流水线自动触发构建。`

实现流程：

1、保证jenkins所在主机能被远程访问

2、jenkins中远程触发需要权限，我们应该使用用户进行授权

3、配置gitee/github，webhook进行触发

```shell
#远程构建即使配置了github 的webhook，默认会403.我们应该使用用户进行授权
1、创建一个用户
2、一定随便登陆激活一次
3、生成一个apitoken 
http://leifengyang:113620edce6200b9c78ecadb26e9cf122e@139.198.186.134:8080/job/devops-java-demo/build?token=leifengyang

```

远程触发： `JENKINS_URL`/job/simple-java-maven-app/build?token=`TOKEN_NAME`  请求即可



### 6、流水线语法

#### 1、基础格式

```groovy
pipeline {
    agent any
    environment {
        CC = 'clang'
    }
    stages {
        stage('Example') {
            steps {
                sh 'printenv'
                sh 'echo $CC'
            }
        }
    }
}
```



#### 2、环境变量





#### 3、密钥管理

// environment   aa = credxxx("xxxxx")







#### 4、自定义agent

![1618670600107](assets/1618670600107.png)

```groovy
//需要安装docker、docker pipeline插件

pipeline {
    agent none 
    stages {
        stage('Example Build') {
            agent { 
                docker 'maven:3-alpine' 
                //args 是指定 docker run 的所有指令
                args '-v /var/jenkins_home/maven/.m2:/root/.m2'
            } 
            steps {
                echo 'Hello, Maven'
                sh 'mvn --version'
            }
        }
        stage('Example Test') {
            agent { docker 'openjdk:8-jre' } 
            steps {
                echo 'Hello, JDK'
                sh 'java -version'
            }
        }
    }
}
```



- 配置maven加速
  - 把Maven的配置文件放在jenkins-data里面的某个位置

```xml
<?xml version="1.0" encoding="UTF-8"?>

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <!-- localRepository
   | The path to the local repository maven will use to store artifacts.
   |
   | Default: ${user.home}/.m2/repository
  <localRepository>H:\Devsoft\apache-maven-3.6.1\repository</localRepository>
用户目录下的.m2是所有jar包的地方; maven容器内jar包的位置
  -->
  <localRepository>/root/.m2</localRepository>



  <pluginGroups>

  </pluginGroups>

  <proxies>

  </proxies>

  <servers>

  </servers>


  <mirrors>
	 <mirror>
        <id>nexus-aliyun</id>
        <mirrorOf>central</mirrorOf>
        <name>Nexus aliyun</name>
        <url>http://maven.aliyun.com/nexus/content/groups/public</url>
	 </mirror>
  </mirrors>

  <profiles>
		<profile>
			 <id>jdk-1.8</id>
			 <activation>
			   <activeByDefault>true</activeByDefault>
			   <jdk>1.8</jdk>
			 </activation>
			 <properties>
			   <maven.compiler.source>1.8</maven.compiler.source>
			   <maven.compiler.target>1.8</maven.compiler.target>
			   <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
			 </properties>
		</profile>
  </profiles>
</settings>

```

默认所有的可变配置项都推荐放在jenkins-home的位置，增强移植性



如何自定义使用maven的配置文件

```groovy
//流水线这样
 stage('编译'){
            agent {
                docker {
                    image 'maven:3-alpine'  //用完就会杀掉
                    args '-v maven-repo:/root/.m2'
                    //docker run -v repo:/root/.m2 这个容器是直接在linux上的,docker volume也很灵活
                 }
            }
            steps {
               sh 'pwd && ls -alh'
               sh 'mvn -v'
               sh 'mvn clean package -s "/var/jenkins_home/appconfig/maven/settings.xml"  -Dmaven.test.skip=true '
                //残留的问题，下一次运行这个命令，已经之前下载的jar还会下载。

            }
        }


//再给 linux主机挂载的jenkins_home位置自定义一个 settings.xml 即可

//原理： jenkins在解析流水线期间，可以任意访问jenkins家目录的位置和相关环境信息

//一个配置文件+docker maven agent = maven加速

```



- 临时容器导致的问题
  - 1、第一次检出代码，默认在 /var/jenkins_home/workspace/【java-devops-demo】
  - 2、使用docker临时agent的时候，每一个临时容器运行又分配临时目录 /var/jenkins_home/workspace/java-devops-demo@2；默认就是workspace/java-devops-demo的内容
  - 3、在临时容器里面 运行的mvn package命令，会在 /var/jenkins_home/workspace/java-devops-demo@2 进行工作
  - 4、package到了 /var/jenkins_home/workspace/java-devops-demo@2 位置
  - 5、进入下一步进行打包镜像，又会回到  /var/jenkins_home/workspace/【java-devops-demo】这个默认位置
  - 6、这个位置没有运行过 mvn clean package ，没有target。 默认的 工作目录 没有 target
  - 







#### 5、参数化





#### 6、条件表达式

```sh
##默认的when

##还可以写脚本哦。。。

```





### 7、修改jenkins插件源

推荐白天装

```
http://mirror.xmission.com/jenkins/updates/update-center.json

```



jenkins插件中心的插件；



![1618796240524](assets/1618796240524.png)





![1618796224692](assets/1618796224692.png)





### 8、推荐安装的插件

- Docker Pipeline && Docker   
  - 安装Docker Pipeline会自动安装docker相关的
  - 这个允许我们自定义agent使用docker环境
- Git Parameter
  - 解析git参数，允许我们选择分支进行构建
- Active Choices 
  - 可以做到参数的级联选择
- [Generic Webhook Trigger](https://plugins.jenkins.io/generic-webhook-trigger)
  - 通用的webhook触发器，构建更强大的webhook功能
- Role-based Authorization Strategy
  - RBAC权限指定，给一个用户精确指定权限
- List Git Branches Parameter
  - 列出分支参数
- [Build With Parameters](https://plugins.jenkins.io/build-with-parameters)
  - 基于自定义参数构建

我的插件列表

![1618810359055](assets/1618810359055.png)





### 9、邮件发送

- 系统配置 =-==  配置管理员邮箱
  - ![1618925343196](assets/1618925343196.png)
- 配置邮件发送的认证权限信息
  - 登录自己邮箱，开启POP3/SMTP邮件服务
  - 获取到自己的授权码 rkffwdqdyprccbcf
  - 配置并测试好邮件发送即可
- 邮件模板内容

```html
emailext body: '''<!DOCTYPE html>    
<html>    
<head>    
<meta charset="UTF-8">    
<title>${ENV, var="JOB_NAME"}-第${BUILD_NUMBER}次构建日志</title>    
</head>    
    
<body leftmargin="8" marginwidth="0" topmargin="8" marginheight="4"    
    offset="0">    
    <table width="95%" cellpadding="0" cellspacing="0"  style="font-size: 11pt; font-family: Tahoma, Arial, Helvetica, sans-serif">
<h3>本邮件由系统自动发出，请勿回复！</h3>    
        <tr>    
           <br/>            
            各位同事，大家好，以下为${PROJECT_NAME }项目构建信息</br> 
            <td><font color="#CC0000">构建结果 - ${BUILD_STATUS}</font></td>   
        </tr>    
        <tr>    
            <td><br />    
            <b><font color="#0B610B">构建信息</font></b>    
            <hr size="2" width="100%" align="center" /></td>    
        </tr>    
        <tr>    
            <td>    
                <ul>    
                    <li>项目名称 ： ${PROJECT_NAME}</li>    
                    <li>构建编号 ： 第${BUILD_NUMBER}次构建</li>    
                    <li>触发原因： ${CAUSE}</li>    
                    <li>构建状态： ${BUILD_STATUS}</li>    
                    <li>构建日志： <a href="${BUILD_URL}console">${BUILD_URL}console</a></li>    
                    <li>构建  Url ： <a href="${BUILD_URL}">${BUILD_URL}</a></li>    
                    <li>工作目录 ： <a href="${PROJECT_URL}ws">${PROJECT_URL}ws</a></li>    
                    <li>项目  Url ： <a href="${PROJECT_URL}">${PROJECT_URL}</a></li>    
                </ul>    


<h4><font color="#0B610B">最近提交</font></h4>
<ul>
<hr size="2" width="100%" />
${CHANGES_SINCE_LAST_SUCCESS, reverse=true, format="%c", changesFormat="<li>%d [%a] %m</li>"}
</ul>
详细提交: <a href="${PROJECT_URL}changes">${PROJECT_URL}changes</a><br/>

            </td>    
        </tr>    
    </table>    
</body>    
</html>''', subject: '${ENV, var="JOB_NAME"}-第${BUILD_NUMBER}次构建日志', to: '17512080612@163.com'


大家自己修改  to 的位置
```





实现 短信通知，钉钉通知，微信通知，xxxxx







## 3、高级使用

### 1、级联变量

>  参照插件介绍即可

#### 1、基本选择![1618811989683](assets/1618811989683.png)

```groovy
// groovy脚本
return ["hello","world","abc:selected"]


//fallback 定义错误返回
return "error"

```





#### 2、引用级联![1618812004163](assets/1618812004163.png)

要指定之前参考参数

![1618812063784](assets/1618812063784.png)



```groovy
//如下
if ( selected_branch.equals("hello") ) {
 return ["1","2"]
}
if( selected_branch.equals("world") ){
 return ["11","22"]
}else{
 return ["111","222"]
}

//也可以执行shell

```

### 2、分支选择





### 3、自定义远程触发

Generic Webhook Trigger：参照官网进行使用

```sh

```







# 三、Jenkins On Docker

```groovy

```







# 四、Jenkins On Kubernetes

## 1、部署

参照  《生态链路整合》章节进行部署测试





## 2、实战

### 1、远程触发

#### 1、基础环境

- 1、安装Generic Webhook Trigger

- 2、随便创建一个流水线项目，只需要编写如下流水线

  - ```groovy
    pipeline {
      agent any
      triggers {
        GenericTrigger(
         genericVariables: [
          [key: 'ref', value: '$.ref']
         ],
         causeString: 'Triggered on $ref',
         token: 'fdsfdsfdsfgdhgfhgju6yy544edwqrfdsgfhrtw563yhgfd',
         tokenCredentialId: '',
         printContributedVariables: true,
         printPostContent: true,
         silentResponse: false
        //  regexpFilterText: '$ref',
        //  regexpFilterExpression: 'refs/heads/' + BRANCH_NAME
        )
      }
      stages {
        stage('Some step') {
          steps {
            sh "echo $ref"
          }
        }
      }
    }
    ```

- 3、给gitee添加webhook功能

  - ![1622813681713](assets/1622813681713.png)

- 4、将上面的流水线先运行一次，以后webhook就可以触发了





#### 2、优化1-分支代码提交感知

请求详情如下

```json
----------------Header----------------
Request URL: http://139.198.160.218:30912/generic-webhook-trigger/invoke?token=fdsfdsfdsfgdhgfhgju6yy544edwqrfdsgfhrtw563yhgfd
Request Method: POST
Content-Type: application/json
User-Agent: git-oschina-hook
X-Gitee-Token: 
X-Gitee-Timestamp: 1622813858195
X-Gitee-Ping: false
X-Gitee-Event: Push Hook
X-Git-Oschina-Event: Push Hook
---------------Payload----------------
{
  "ref": "refs/heads/release", //当前分支
  "before": "9cb8f11b27b0efaa986938b597c54988d6272887",
  "after": "996d315d680aebb586ec81e0c0725f79d8d002e3",
  "total_commits_count": 1,
  "commits_more_than_ten": false,
  "created": false,
  "deleted": false,
  "compare": "https://gitee.com/leifengyang/mall-2/compare/9cb8f11b27b0efaa986938b597c54988d6272887...996d315d680aebb586ec81e0c0725f79d8d002e3",
  "commits": [
    {
      "id": "996d315d680aebb586ec81e0c0725f79d8d002e3",
      "tree_id": "c05bc2cae9f63793c6a596314b62c6da5ac00d83",
      "parent_ids": [
        "9cb8f11b27b0efaa986938b597c54988d6272887"
      ],
      "message": "release-1\n",  //提交信息
      "timestamp": "2021-06-04T21:36:52+08:00",
      "url": "https://gitee.com/leifengyang/mall-2/commit/996d315d680aebb586ec81e0c0725f79d8d002e3",
      "author": {
        "time": "2021-06-04T21:36:52+08:00",
        "id": 1233660,
        "name": "leifengyang",   //作者信息
        "email": "534096094@qq.com",
        "username": "leifengyang",
        "user_name": "leifengyang",
        "url": "https://gitee.com/leifengyang"
      },
      "committer": {
        "id": 1233660,
        "name": "leifengyang",   //本次提交者信息
        "email": "534096094@qq.com",
        "username": "leifengyang",
        "user_name": "leifengyang",
        "url": "https://gitee.com/leifengyang"
      },
      "distinct": true,
      "added": [   //本次提交新增的文件

      ],
      "removed": [  //本次提交移除的文件

      ],
      "modified": [  //本次提交修改的文件
        "mall-admin/pom.xml"
      ]
    }
  ],
  "head_commit": {
    "id": "996d315d680aebb586ec81e0c0725f79d8d002e3",
    "tree_id": "c05bc2cae9f63793c6a596314b62c6da5ac00d83",
    "parent_ids": [
      "9cb8f11b27b0efaa986938b597c54988d6272887"
    ],
    "message": "release-1\n",
    "timestamp": "2021-06-04T21:36:52+08:00",
    "url": "https://gitee.com/leifengyang/mall-2/commit/996d315d680aebb586ec81e0c0725f79d8d002e3",
    "author": {
      "time": "2021-06-04T21:36:52+08:00",
      "id": 1233660,
      "name": "leifengyang",
      "email": "534096094@qq.com",
      "username": "leifengyang",
      "user_name": "leifengyang",
      "url": "https://gitee.com/leifengyang"
    },
    "committer": {
      "id": 1233660,
      "name": "leifengyang",
      "email": "534096094@qq.com",
      "username": "leifengyang",
      "user_name": "leifengyang",
      "url": "https://gitee.com/leifengyang"
    },
    "distinct": true,
    "added": [

    ],
    "removed": [

    ],
    "modified": [
      "mall-admin/pom.xml"
    ]
  },
  "repository": {
    "id": 16127155,
    "name": "mall-2",  //仓库名
    "path": "mall-2",
    "full_name": "leifengyang/mall-2",  //仓库完整名
    "owner": {
      "id": 1233660,
      "name": "leifengyang",
      "email": "534096094@qq.com",
      "username": "leifengyang",
      "user_name": "leifengyang",
      "url": "https://gitee.com/leifengyang",
      "login": "leifengyang",
      "avatar_url": "https://gitee.com/assets/no_portrait.png",
      "html_url": "https://gitee.com/leifengyang",  
      "type": "User",
      "site_admin": false
    },
    "private": false,
    "html_url": "https://gitee.com/leifengyang/mall-2", 
    "url": "https://gitee.com/leifengyang/mall-2",
    "description": "",
    "fork": false,
    "created_at": "2021-05-29T10:43:45+08:00",
    "updated_at": "2021-06-04T21:37:37+08:00",
    "pushed_at": "2021-06-04T21:37:37+08:00",
    "git_url": "git://gitee.com/leifengyang/mall-2.git",
    "ssh_url": "git@gitee.com:leifengyang/mall-2.git",
    "clone_url": "https://gitee.com/leifengyang/mall-2.git", //仓库Git地址
    "svn_url": "svn://gitee.com/leifengyang/mall-2",
    "git_http_url": "https://gitee.com/leifengyang/mall-2.git",
    "git_ssh_url": "git@gitee.com:leifengyang/mall-2.git",
    "git_svn_url": "svn://gitee.com/leifengyang/mall-2",
    "homepage": null,
    "stargazers_count": 2,
    "watchers_count": 2,
    "forks_count": 0,
    "language": null,
    "has_issues": true,
    "has_wiki": true,
    "has_pages": false,
    "license": "Apache-2.0",
    "open_issues_count": 0,
    "default_branch": "master",  //默认分支
    "namespace": "leifengyang",
    "name_with_namespace": "leifengyang/mall-2",
    "path_with_namespace": "leifengyang/mall-2"
  },
  "project": {
    "id": 16127155,
    "name": "mall-2",
    "path": "mall-2",
    "full_name": "leifengyang/mall-2",
    "owner": {
      "id": 1233660,
      "name": "leifengyang",
      "email": "534096094@qq.com",
      "username": "leifengyang",
      "user_name": "leifengyang",
      "url": "https://gitee.com/leifengyang",
      "login": "leifengyang",
      "avatar_url": "https://gitee.com/assets/no_portrait.png",
      "html_url": "https://gitee.com/leifengyang",
      "type": "User",
      "site_admin": false
    },
    "private": false,
    "html_url": "https://gitee.com/leifengyang/mall-2",
    "url": "https://gitee.com/leifengyang/mall-2",
    "description": "",
    "fork": false,
    "created_at": "2021-05-29T10:43:45+08:00",
    "updated_at": "2021-06-04T21:37:37+08:00",
    "pushed_at": "2021-06-04T21:37:37+08:00",
    "git_url": "git://gitee.com/leifengyang/mall-2.git",
    "ssh_url": "git@gitee.com:leifengyang/mall-2.git",
    "clone_url": "https://gitee.com/leifengyang/mall-2.git",
    "svn_url": "svn://gitee.com/leifengyang/mall-2",
    "git_http_url": "https://gitee.com/leifengyang/mall-2.git",
    "git_ssh_url": "git@gitee.com:leifengyang/mall-2.git",
    "git_svn_url": "svn://gitee.com/leifengyang/mall-2",
    "homepage": null,
    "stargazers_count": 2,
    "watchers_count": 2,
    "forks_count": 0,
    "language": null,
    "has_issues": true,
    "has_wiki": true,
    "has_pages": false,
    "license": "Apache-2.0",
    "open_issues_count": 0,
    "default_branch": "master",
    "namespace": "leifengyang",
    "name_with_namespace": "leifengyang/mall-2",
    "path_with_namespace": "leifengyang/mall-2"
  },
  "user_id": 1233660,
  "user_name": "leifengyang",
  "user": {
    "id": 1233660,
    "name": "leifengyang",
    "email": "534096094@qq.com",
    "username": "leifengyang",
    "user_name": "leifengyang",
    "url": "https://gitee.com/leifengyang"
  },
  "pusher": {
    "id": 1233660,
    "name": "leifengyang",  //推送者信息
    "email": "534096094@qq.com",
    "username": "leifengyang",
    "user_name": "leifengyang",
    "url": "https://gitee.com/leifengyang"
  },
  "sender": {
    "id": 1233660, 
    "name": "leifengyang",  
    "email": "534096094@qq.com",
    "username": "leifengyang",
    "user_name": "leifengyang",
    "url": "https://gitee.com/leifengyang",
    "login": "leifengyang",
    "avatar_url": "https://gitee.com/assets/no_portrait.png",
    "html_url": "https://gitee.com/leifengyang",
    "type": "User",
    "site_admin": false
  },
  "enterprise": null,
  "hook_name": "push_hooks",  //钩子名
  "hook_id": 642201,
  "hook_url": "https://gitee.com/leifengyang/mall-2/hooks/642201/edit",
  "password": "",
  "timestamp": "1622813858195",
  "sign": "",
  "token": "fdsfdsfdsfgdhgfhgju6yy544edwqrfdsgfhrtw563yhgfd"
}
```





- 提取以下有用信息
  - **本次触发分支名**
  - 本次触发类型（四种）
    - push_hooks： push触发
    - tag_push_hooks：tag + push触发
    - merge_request_hooks： PR触发
    - note_hooks： 评论触发
  - **本次提交者**
  - **仓库地址**
  - **增删改的文件记录**



```groovy
//变化后的流水线
pipeline {
  agent any
  triggers {
    GenericTrigger(
     genericVariables: [
      [key: 'ref', value: '$.ref'],
      [key: 'committer', value: '$.pusher.username'],
      [key: 'added', value: '$.commits[*].added'],
      [key: 'removed', value: '$.commits[*].removed'],
      [key: 'modified', value: '$.commits[*].modified'],
      [key: 'gitUrl', value: '$.repository.clone_url'],
      [key: 'hookName', value: '$.hook_name'],
      [key: 'respBody', value: '$.*']
     ],
     causeString: '$committer 触发了 $ref 分支改变',
     token: 'fdsfdsfdsfgdhgfhgju6yy544edwqrfdsgfhrtw563yhgfd',
     printContributedVariables: true,
     printPostContent: true,
     silentResponse: false
    )
  }
  stages {

    stage('参数获取') {
      agent {
        label 'maven'
      }
      steps {
        sh "echo 分支： $ref"
        sh "echo 提交者： $committer"
        sh "echo 添加： $added"
        sh "echo 移除： $removed"
        sh "echo 修改： $modified"
        sh "echo git地址： $gitUrl"
        sh "echo 钩子： $hookName"
      }
    }

    stage('拉取代码'){
        agent {
            label 'maven'
        }

        steps{
            checkout([$class: 'GitSCM', branches: [[name: '$ref']], extensions: [], userRemoteConfigs: [[url: '$gitUrl']]])
            sh 'pwd && ls -a'
            sh 'ls -a /'
            sh 'git branch'
        }
    }

    stage('其他地方'){
        agent {
            label 'maven'
        }
        steps{
            sh 'pwd && ls -a'
            sh 'ls -a /'
        }
    }
  }
}
```





#### 3、优化2-slave节点传递数据

多个node之间，jenkins使用 stash/unstash 传递数据。 原理就是打压缩包和解压缩包

```groovy
        steps{
            //xxxxxxx
            //保存文件
            stash includes: '**/**', name: 'gitfiles'
            
            
            //别的任意多个其他步骤中都可以使用以下取值
            unstash 'gitfiles'
        }
```



### 2、流水线基础优化

#### 1、优化3-提取有用信息

```groovy
pipeline {
  agent any


  environment { 
      CC = 'clang'
      //所有提交变化的项目
      CHANGED_PROJECTS = "";
  }

  triggers {
    GenericTrigger(
     genericVariables: [
      [key: 'ref', value: '$.ref'],
      [key: 'committer', value: '$.pusher.username'],
      [key: 'added', value: '$.commits[*].added'],
      [key: 'removed', value: '$.commits[*].removed'],
      [key: 'modified', value: '$.commits[*].modified'],
      [key: 'gitUrl', value: '$.repository.clone_url'],
      [key: 'hookName', value: '$.hook_name'],
      [key: 'respBody', value: '$.*']
     ],
     causeString: '$committer 触发了 $ref 分支改变',
     token: 'fdsfdsfdsfgdhgfhgju6yy544edwqrfdsgfhrtw563yhgfd',
     printContributedVariables: true,
     printPostContent: true,
     silentResponse: false
    )
  }
  stages {

    stage('参数获取') {
      agent {
        label 'maven'
      }
      steps {
        sh "echo 分支： $ref"
        sh "echo 提交者： $committer"
        sh "echo 添加： $added"
        sh "echo 移除： $removed"
        sh "echo 修改： $modified"
        sh "echo git地址： $gitUrl"
        sh "echo 钩子： $hookName"
      }
    }

    stage('拉取代码'){
        agent {
            label 'maven'
        }

        steps{
            checkout([$class: 'GitSCM', branches: [[name: '$ref']], extensions: [], userRemoteConfigs: [[url: '$gitUrl']]])
            sh 'pwd && ls -alh'

            sh 'ls -a /'
            sh 'git branch'
            //保存git文件
            stash includes: '**/**', name: 'gitfiles'

        }


    }

    stage('其他地方'){
        agent {
            label 'maven'
        }
        steps{
            unstash 'gitfiles'
            sh 'pwd && ls -alh'
            sh 'ls -a /'
        }
    }

    stage('提取有用信息'){
        agent {
            label 'maven'
        }
        steps{
          //可以再次获取信息
          
          unstash 'gitfiles'
          sh 'pwd && ls -alh'
          script {
              // some block added removed modified
              // 提取所有变化
              def matcher = "${modified}"+","+"${added}"+","+"${removed}" =~ /[\[\]]/
              // 分割为数组
              def changes = matcher.replaceAll('').split(",")
              // 数组进行去重
              def results = [];
              HashSet h  =   new  HashSet();
              for (change in changes) {
                int i = change.indexOf('/'); //找到/分割的项目名
                if(i > 0){ 
                  //截串获取项目名
                  change = change.substring(0, i)
                }
                //去掉多余引号
                h.add(change.replaceAll('"',''));
              }
              results.addAll(h)

              //给环境变量赋值
              CHANGED_PROJECTS = results.join(",");
              echo "${CHANGED_PROJECTS}"
          }

        }
    }

  }
}
```



#### 2、优化4-环境准备阶段合并

```groovy
pipeline {
  agent any


  environment { 
      //git检出的代码存放的文件夹
      GIT_DIR = 'mall'
      //所有提交变化的项目
      CHANGED_PROJECTS = "";
      BASE_PROJECTS= "mall-common,mall-security"
      IMAGE_PROJECTS = "mall-admin,mall-demo,mall-portal,mall-search"
      HARBOR_CREDENTIAL_ID = 'harbor-id'  
      REGISTRY = 'harbor.itdachang.com'
      HARBOR_NAMESPACE = 'mall'
      GITLAB_CREDENTIAL_ID = 'git-secret'
      //这个不用，因为打包机自带
      // KUBECONFIG = 'devcluster-kubeconfig'

  }

  triggers {
    GenericTrigger(
     genericVariables: [
      [key: 'ref', value: '$.ref'],
      [key: 'committer', value: '$.pusher.username'],
      [key: 'added', value: '$.commits[*].added'],
      [key: 'removed', value: '$.commits[*].removed'],
      [key: 'modified', value: '$.commits[*].modified'],
      [key: 'gitUrl', value: '$.repository.clone_url'],
      [key: 'hookName', value: '$.hook_name'],
      [key: 'respBody', value: '$.*']
     ],
     causeString: '$committer 触发了 $ref 分支改变',
     token: 'fdsfdsfdsfgdhgfhgju6yy544edwqrfdsgfhrtw563yhgfd',
     printContributedVariables: true,
     printPostContent: true,
     silentResponse: false
    )
  }
  stages {
    
    stage('环境信息提取') {
      agent {
        label 'maven'
      }
      steps {
        sh "echo ==============远程触发基本信息如下============="
        sh "echo 分支： $ref"
        sh "echo 提交者： $committer"
        sh "echo 添加： $added"
        sh "echo 移除： $removed"
        sh "echo 修改： $modified"
        sh "echo git地址： $gitUrl"
        sh "echo 钩子： $hookName"
        script {
              // 提取所有变化
              def matcher = "${modified}"+","+"${added}"+","+"${removed}" =~ /[\[\]]/
              // 分割为数组
              def changes = matcher.replaceAll('').split(",")
              // 数组进行去重
              def results = [];
              HashSet h  =   new  HashSet();
              for (change in changes) {
                int i = change.indexOf('/'); //找到/分割的项目名
                if(i > 0){ 
                  //截串获取项目名
                  change = change.substring(0, i)
                }
                //去掉多余引号
                h.add(change.replaceAll('"',''));
              }
              results.addAll(h)

              //给环境变量赋值
              CHANGED_PROJECTS = results.join(",");
          }
          echo "此次发生改变的项目：${CHANGED_PROJECTS}"
      }
    }

    stage('代码拉取与构建'){
        agent {
            label 'maven'
        }
        steps{
          dir("${GIT_DIR}") {
              // 项目检出到指定目录
              checkout([$class: 'GitSCM', 
                branches: [[name: '$ref']], 
                extensions: [], 
                userRemoteConfigs: [[url: '$gitUrl']]])
             
              //项目进行构建
              sh 'mvn clean package -s "/root/maven/settings.xml"  -Dmaven.test.skip=true'
          }
          //打包
          stash includes: "${GIT_DIR}/**", name: 'gitfiles'
          dir("${GIT_DIR}") {
            sh 'ls -alh'
          }
        }
    }
  }
}
```





### 3、流水线完善

#### 1、优化5-镜像打包





#### 2、优化6-发布代码





#### 3、优化7-邮件构建报告



