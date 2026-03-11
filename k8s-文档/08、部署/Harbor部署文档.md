以后`*.itdachang.com`这个通配符证书，各个名称空间都放一份

```sh
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout tls.key -out tls.crt -subj "/CN=*.itdachang.com/O=*.itdachang.com"

kubectl create secret tls itdachang.com --key tls.key --cert tls.crt
kubectl create secret tls harbor.itdachang.com --key tls.key --cert tls.crt -n devops
...
```



------------------------------------------------------------



私有仓库：可以保存charts、images、...

Harbor还可以充当docker hub的中转站，代理docker hub



## 0、核心组件

![img](assets/2104126-20201217173704535-710001277.png)

- Nginx(Proxy)：用于代理Harbor的registry,UI, token等服务
- db：负责储存用户权限、审计日志、Dockerimage分组信息等数据。
- UI：提供图形化界面，帮助用户管理registry上的镜像, 并对用户进行授权
- jobsevice：负责镜像复制工作的，他和registry通信，从一个registry pull镜像然后push到另一个registry，并记录job_log
- Adminserver：是系统的配置管理中心附带检查存储用量，ui和jobserver启动时候回需要加载adminserver的配置。
- Registry：原生的docker镜像仓库，负责存储镜像文件。
- Log：为了帮助监控Harbor运行，负责收集其他组件的log，记录到syslog中


## 1、创建基本信息

```sh
# kubectl create ns devops
kubectl create namespace devops
#tls.key、tls.crt用之前的ingress的总证书即可
kubectl create secret tls itdachang.com --cert=tls.crt   --key=tls.key  -n devops
```



## 2、部署harbor


```sh
# 下载
helm repo add harbor https://helm.goharbor.io
helm pull harbor/harbor


我要harbor-1.6.2.tgz

helm pull harbor/harbor --version 1.6.2

tar -xvf harbor-1.6.2.tgz

cd harbor && ls
```



harbor内部组件用harbor默认带的证书（在harbor/cert目录下）。ingress需要用自己证书

```yaml

# 修改配置  vi  override.yaml
expose:  #web浏览器访问用的证书
  type: ingress
  tls:
    certSource: "secret"
    secret:
      secretName: "itdachang.com"
      notarySecretName: "itdachang.com"
  ingress:
    hosts:
      core: harbor.itdachang.com
      notary: notary-harbor.itdachang.com
externalURL: https://harbor.itdachang.com
internalTLS:  #harbor内部组件用的证书
  enabled: true
  certSource: "auto"
persistence:
  enabled: true
  resourcePolicy: "keep"
  persistentVolumeClaim:
    registry:  # 存镜像的
      storageClass: "rook-ceph-block"
      accessMode: ReadWriteOnce
      size: 5Gi
    chartmuseum: #存helm的chart
      storageClass: "rook-ceph-block"
      accessMode: ReadWriteOnce
      size: 5Gi
    jobservice: #
      storageClass: "rook-ceph-block"
      accessMode: ReadWriteOnce
      size: 1Gi
    database: #数据库  pgsql
      storageClass: "rook-ceph-block"
      accessMode: ReadWriteOnce
      size: 1Gi
    redis: #
      storageClass: "rook-ceph-block"
      accessMode: ReadWriteOnce
      size: 1Gi
    trivy: # 漏洞扫描
      storageClass: "rook-ceph-block"
      accessMode: ReadWriteOnce
      size: 5Gi
metrics:
  enabled: true
```



```sh
# 部署
helm install -f values.yaml  -f override.yaml  harbor ./ -n devops
```


```sh
watch kubectl get pod -n devops
```

## 3、部署完成

>参照使用：https://goharbor.io/docs/2.2.0/working-with-projects/
>         https://goharbor.io/docs/2.2.0/working-with-projects/create-projects/


默认访问账号密码：admin   Harbor12345

随便添加，删除一个项目，就是部署好了







## 4、推送与下载镜像

```sh
docker login  aliyunxxxx
## 给所有docker机器 配置/etc/hosts       
10.120.102.31 harbor.itdachang.com
192.168.0.14 harbor.itdachang.com
## 给vpc网络配置443转到任意一个ingress机器的443
以后任意域名直接配置  
vpcip（不能访问443？？？） 域名地址  
192.168.0.14 harbor.itdachang.com


docker login harbor.itdachang.com
```

> 自定义域名（使用自签证书）docker不能信任证书

```sh
docker pull busybox
docker tag busybox harbor.itdachang.com/mall/busybox:v1.0
docker push  harbor.itdachang.com/mall/busybox:v1.0
```

> 各个docker节点都应该信任这个证书





## 5、使用私有仓库与镜像代理

> 机器人账号
>
> admin Harbor12345
>
> 机器人：
>
> 账号： robot$hello+hellopull
>
> 密码： foTlux0RTBGzPlvNaxmAkEj4E6quYb10
>
> 



```sh
docker tag busybox harbor.itdachang.com/hello/busybox:v1.0
```

代理仓库，代理中央仓库

```sh
#代理官方镜像
docker pull harbor.itdachang.com/hello/library/alpine
#代理第三方
docker pull harbor.itdachang.com/hello/nginx/nginx-ingress
```



> webhook：钩子
>
> 可以结合cicd。触发外界行为





## 6、docker使用



### 1、基本配置



#### 1、使用https方式访问

由于harbor使用的是https。需要docker信任这个https；

```sh
# 把xx.cert文件 复制到  /etc/docker/certs.d/harbor.itdachang.com/tls.crt
```

> 云上`自定义域名`如下操作：
>
> 1、配置每个主机的 /etc/hosts文件。可指定域名地址为 `公网ip`或者`ingress节点所在ip`
>
> 2、在 `/etc/docker/certs.d/` 下面准备域名文件夹（包含非默认的端口号），并把域名的 `cert/crt`文件复制进去。并且修改文件名叫  `xxx.crt`，不能是cert文件
>
> ![1622273679643](assets/1622273679643.png)
>
> 3、建议配置 ingress节点所在ip 。这样我们使用域名来到了ingress节点。ingress节点的nginx监听到了此域名，则转发给指定服务
>
> ![1622273855432](assets/1622273855432.png)





#### 2、不使用https访问

```sh
#修改docker配置文件
{"insecure-registries":["https://test.com","192.168.1.13","更多的...."]}
```

### 2、镜像代理

![1622776953811](assets/1622776953811.png)

```sh
# 拉取docker官方镜像。并缓存起来。harbor.itdachang.com/自己的仓库名/ + /library + /镜像名：版本
docker pull harbor.itdachang.com/harbor-hub/library/busybox:latest
# 第三方。用第三方全名 harbor.itdachang.com/objs + 第三方
docker pull harbor.itdachang.com/objs/redislabs/redis
```



> 自建域名系统
>
> 10.120.102.31  harbor.itdachang.com





















