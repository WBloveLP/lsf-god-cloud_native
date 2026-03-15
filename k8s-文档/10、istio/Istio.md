
<h1>入门：https://istio.io/latest/zh/docs/setup/getting-started/</h1>



# 架构



Istio 服务网格从逻辑上分为数据平面和控制平面 。

- 数据平面 由一组被部署为 Sidecar 的智能代理（Envoy） 组成。这些代理负责协调和控制微服务之间的所有网络通信。 它们还收集和报告所有网格流量的遥测数据。

- 控制平面 管理并配置代理来进行流量路由。

![istio](assets/20260315_133846.png)

一个复杂的网络：

<img src="assets/54a4a9387c633c2895f00053fb70bddd.jpeg" alt="istio" style="display: block; margin: 0 auto;">

简化版：

![istio2](assets/o414wxy5me.jpeg)




# 安装

```sh
# https://istio.io/latest/zh/docs/setup/additional-setup/download-istio-release/
curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.10.0 TARGET_ARCH=x86_64 sh 

cd istio-1.10.0/

export PATH=$PWD/bin:$PATH
```


给命名空间添加标签，指示 Istio 在部署应用的时候，自动注入 Envoy Sidecar 代理：


```sh
kubectl label namespace default istio-injection=enabled
```



# Bookinfo示例应用


### 介绍


>https://istio.io/latest/zh/docs/examples/bookinfo/


未使用 Istio 的 Bookinfo 应用：

![noistio.svg](assets/noistio.svg)


要在 Istio 中运行这一样例应用，无需对应用本身做出任何改变。 您只要简单地在启用 Istio 的环境中对这些服务进行配置并运行这些服务， 具体一点说就是把 Envoy Sidecar 注入到每个服务之中。最终的部署结果将如下图所示：


![withistio.svg](assets/withistio.svg)

所有的微服务都和 Envoy Sidecar 集成在一起，被集成服务所有的出入流量都被 Sidecar 所劫持， 这样就为外部控制准备了所需的 Hook（钩子），然后就可以利用 Istio 控制平面为整个应用提供服务路由、遥测数据收集以及策略实施等功能。


### 安装Demo

按需安装：https://istio.io/latest/zh/docs/setup/additional-setup/config-profiles/


```sh
# https://istio.io/latest/zh/docs/setup/install/istioctl/
istioctl install --set profile=demo -y

watch kubectl get pod -n istio-system
```



### 运行Bookinfo


部署 Bookinfo 示例应用：

```sh
kubectl apply -f samples/bookinfo/platform/kube/bookinfo.yaml
```


### 把应用关联到Istio网关


每个 Gateway 由类型为 LoadBalancer 的 Service 支撑，该 Service 的外部负载均衡器 IP 和端口用于访问 Gateway。 大多数云平台上运行的集群默认支持类型为 LoadBalancer 的 Kubernetes Service

```sh
kubectl apply -f samples/bookinfo/networking/bookinfo-gateway.yaml
```

```sh
kubectl get svc istio-ingressgateway -n istio-system

NAME                   TYPE           CLUSTER-IP      EXTERNAL-IP   PORT  
istio-ingressgateway   LoadBalancer   10.96.203.157   <pending>     15021:30463/TCP,80:32420/TCP,443:31631/TCP,31400:31902/TCP,15443:30871/TCP
```

在本地内网： http://10.96.203.157/productpage 访问


### 使用 Ingress Gateway 服务的 Node Port访问


https://istio.io/latest/zh/docs/tasks/traffic-management/ingress/ingress-control/#using-node-ports-of-the-ingress-gateway-service


```sh
kubectl get svc -n istio-system
```


```sh
export INGRESS_NAME=istio-ingressgateway

export INGRESS_NS=istio-system
```


```sh
export INGRESS_PORT=$(kubectl -n "${INGRESS_NS}" get service "${INGRESS_NAME}" -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')


export SECURE_INGRESS_PORT=$(kubectl -n "${INGRESS_NS}" get service "${INGRESS_NAME}" -o jsonpath='{.spec.ports[?(@.name=="https")].nodePort}')


export TCP_INGRESS_PORT=$(kubectl -n "${INGRESS_NS}" get service "${INGRESS_NAME}" -o jsonpath='{.spec.ports[?(@.name=="tcp")].nodePort}')
```

```sh
# export INGRESS_HOST=worker-node-address

export INGRESS_HOST=$(kubectl get po -l istio=ingressgateway -n "${INGRESS_NS}" -o jsonpath='{.items[0].status.hostIP}')
```


```sh
[root@k8s-master istio-1.10.0]# env | grep INGRESS_
INGRESS_NAME=istio-ingressgateway
INGRESS_PORT=32420
SECURE_INGRESS_PORT=31631
TCP_INGRESS_PORT=31902
INGRESS_NS=istio-system
INGRESS_HOST=192.168.10.139
```

使用 http://192.168.10.139:32420/productpage 来访问


# 仪表盘【可视化】

```sh
#出错了就再运行一遍
kubectl apply -f samples/addons
```

他会部署这些：grafana   prometheus   jaeger【链路追踪】  kiali【流控】

```sh
kubectl get pod -n istio-system

istio-system           grafana-56d978ff77-6nt8b                     1/1     Running   0          4m26s
istio-system           istio-egressgateway-55d4df6c6b-pp7jf         1/1     Running   0          156m
istio-system           istio-ingressgateway-69dc4765b4-94hbf        1/1     Running   0          156m
istio-system           istiod-798c47d594-tqwvq                      1/1     Running   0          156m
istio-system           jaeger-5c7c5c8d87-7vct5                      1/1     Running   0          4m26s
istio-system           kiali-5bb9c9cf49-w8c76                       1/1     Running   0          4m25s
istio-system           prometheus-8958b965-tn9g4                    2/2     Running   0          4m25s
```

```sh
kubectl get svc -n istio-system 
#可以把kiali和tracing做成NodePort暴露出去查看一下【或者使用ingress暴露出去】
kubectl edit svc -n istio-system kiali
kubectl edit svc -n istio-system tracing

[root@k8s-master kube-prometheus-stack]# kubectl get svc -n istio-system 
NAME                   TYPE           CLUSTER-IP      EXTERNAL-IP   PORT
kiali                  NodePort       10.96.110.116   <none>        20001:31689/TCP,9090:32763/TCP 
tracing                NodePort       10.96.24.228    <none>        80:30001/
```

访问： 
192.168.10.137:31689 
192.168.10.137:30001 



# Istio——概念

https://istio.io/latest/zh/docs/concepts/traffic-management/


图示：（流量进来在gateway给每个请求加的唯一id，就可以追踪了）

![istio原理](./istio原理.jpg)



# 更多参考官方文档，写的是真细致