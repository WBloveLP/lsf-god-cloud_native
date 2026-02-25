Java应用部署在Kubernetes集群中的Pod里，通过Ingress对外暴露服务。

Ingress会处理HTTPS，将加密的请求解密后转发给后端的Java应用（通常是HTTP）。

因此，SSL证书用在Ingress上，而不需要在Java应用本身配置。


生成证书：https://kubernetes.github.io/ingress-nginx/user-guide/tls/  （也可以去青云申请免费证书进行配置）


```sh
## 命令：
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout ${KEY_FILE:tls.key} -out ${CERT_FILE:tls.cert} -subj "/CN=${HOST:itdachang.com}/O=${HOST:itdachang.com}"

kubectl create secret tls ${CERT_NAME:itdachang-tls} --key ${KEY_FILE:tls.key} --cert ${CERT_FILE:tls.cert}


## 示例命令：
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout tls.key -out tls.cert -subj "/CN=lpwb.com/O=lpwb.com"

kubectl create secret tls lpwb-tls --key tls.key --cert tls.cert
```


配置域名使用证书；

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: itdachang-ingress
  namespace: default
spec:
  tls:
   - hosts:
     - lpwb.com
     secretName: lpwb-tls
  rules:
  - host: lpwb.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: cookieuri-svc
            port:
              number: 80
```

配置好证书，访问域名【hosts: 192.168.10.138 lpwb.com】，就会默认跳转到https；

