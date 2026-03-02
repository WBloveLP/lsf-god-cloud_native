<center>
<h1>
    Kubernetes 配置与存储
    </h1>    
</center>

# 总览

```sh
kubectl explain pod.spec.volumes
```

![20260226_161251](assets/20260226_161251.png)


--------------------------

![1620633822448](assets/1620633822448.png)



 Kubernetes 目前支持多达 28 种数据卷类型（其中大部分特定于具体的云环境如 GCE/AWS/Azure 等），如需查阅所有的数据卷类型，请查阅 Kubernetes 官方文档 [Volumes](https://kubernetes.io/docs/concepts/storage/volumes/) 。如：

- 非持久性存储  
  - emptyDir
  - HostPath
- 网络连接性存储
  - SAN：iSCSI、ScaleIO Volumes、FC (Fibre Channel)
  - NFS：nfs，cfs
- 分布式存储
  - Glusterfs
  - RBD (Ceph Block Device)
  - CephFS
  - Portworx Volumes
  - Quobyte Volumes
- 云端存储
  - GCEPersistentDisk
  - AWSElasticBlockStore
  - AzureFile
  - AzureDisk
  - Cinder (OpenStack block storage)
  - VsphereVolume
  - StorageOS
- 自定义存储
  - FlexVolume



# 一、配置


>无论是secret还是ConfigMap，使用挂载的方式会热更新，但是subPath（子路径）挂载除外

配置最佳实战: 

- 云原生 应用12要素 中，提出了配置分离。https://12factor.net/zh_cn/config
- 在推送到集群之前，配置文件应存储在**版本控制**中。 这允许您在必要时快速回滚配置更改。 它还有助于集群重新创建和恢复。
- **使用 YAML 而不是 JSON 编写配置文件**。虽然这些格式几乎可以在所有场景中互换使用，但 YAML 往往更加用户友好。
- 建议相关对象分组到一个文件。比如 [guestbook-all-in-one.yaml](https://github.com/kubernetes/examples/blob/master/web/guestbook/all-in-one/guestbook-all-in-one.yaml) 
- 除非必要，否则不指定默认值：简单的最小配置会降低错误的可能性。
- 将对象描述放在注释中，以便更好地进行内省。



## 1、Secret

- `Secret` 对象类型用来**保存敏感信息**，例如密码、OAuth 令牌和 SSH 密钥。 将这些信息放在 `secret` 中比放在 [Pod](https://kubernetes.io/docs/concepts/workloads/pods/pod-overview/) 的定义或者 [容器镜像](https://kubernetes.io/zh/docs/reference/glossary/?all=true#term-image) 中来说更加安全和灵活。
- `Secret` 是一种包含少量敏感信息例如密码、令牌或密钥的对象。用户可以创建 Secret，同时系统也创建了一些 Secret。





### I、Secret种类

![1620443365394](assets/1620443365394.png)

- 细分类型

![1620444574342](assets/1620444574342.png)



### II、Pod如何引用

要使用 Secret，Pod 需要引用 Secret。 Pod 可以用三种方式之一来使用 Secret：

- 作为挂载到一个或多个容器上的 [卷](https://kubernetes.io/zh/docs/concepts/storage/volumes/) 中的[文件](https://kubernetes.io/zh/docs/concepts/configuration/secret/#using-secrets-as-files-from-a-pod)。（volume进行挂载）
- 作为[容器的环境变量](https://kubernetes.io/zh/docs/concepts/configuration/secret/#using-secrets-as-environment-variables)（envFrom字段引用）
- 由 [kubelet 在为 Pod 拉取镜像时使用](https://kubernetes.io/zh/docs/concepts/configuration/secret/#using-imagepullsecrets)（此时Secret是docker-registry类型的）

Secret 对象的名称必须是合法的 [DNS 子域名](https://kubernetes.io/zh/docs/concepts/overview/working-with-objects/names#dns-subdomain-names)。 在为创建 Secret 编写配置文件时，你可以设置 `data` 与/或 `stringData` 字段。 `data` 和 `stringData` 字段都是可选的。`data` 字段中所有键值都必须是 base64 编码的字符串。如果不希望执行这种 base64 字符串的转换操作，你可以选择设置 `stringData` 字段，其中可以使用任何字符串作为其取值。



### III、实验

#### 1)、创建Secret



```yaml
## 命令行
#### 1、使用基本字符串
kubectl create secret generic dev-db-secret \
  --from-literal=username=devuser \
  --from-literal=password='S!B\*d$zDsb='
  
#干跑一遍 
kubectl create secret generic dev-db-secret \
  --from-literal=username=devuser \
  --from-literal=password='S!B\*d$zDsb=' --dry-run=client -oyaml
#就会获得以下yaml【自己编写secret的yaml，也得base64编码后写】
apiVersion: v1
kind: Secret
metadata:
  name: dev-db-secret  
data:
  password: UyFCXCpkJHpEc2I9  ## 只是base64编码了一下（不会导致乱码），并没有加密
  username: ZGV2dXNlcg==


#### 2、使用文件内容
echo -n 'admin' > ./username.txt
echo -n '1f2d1e2e67df' > ./password.txt

kubectl create secret generic db-user-pass \
  --from-file=./username.txt \
  --from-file=./password.txt
# 默认密钥名称是文件名。 你可以选择使用 --from-file=[key=]source 来设置密钥名称。如下
kubectl create secret generic db-user-pass-02 \
  --from-file=un=./username.txt \
  --from-file=pd=./password.txt
```



```yaml
## 使用yaml
dev-db-secret yaml内容如下
```

![1620444050943](assets/1620444050943.png)



获取Secret内容

```sh
kubectl get secret dev-db-secret -oyaml
kubectl get secret lpwb-tls  -o yaml
```







#### 2)、使用Secret

##### a、环境变量引用

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: secret-env-pod
spec:
  containers:
  - name: mycontainer
    image: redis
    env:
      - name: SECRET_USERNAME
        valueFrom:
          secretKeyRef:
            name: mysecret
            key: username
      - name: SECRET_PASSWORD
        valueFrom:
          secretKeyRef:
            name: mysecret
            key: password
  restartPolicy: Never
```



环境变量引用的方式不会被自动更新



##### b、卷挂载

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: mypod
spec:
  containers:
  - name: mypod
    image: redis
    volumeMounts:
    - name: foo
      mountPath: "/etc/foo"
      readOnly: true
  volumes:
  - name: foo
    secret:
      secretName: mysecret
```

挂载方式的secret 在secret变化的时候会自动更新**（子路径除外）**





## 2、ConfigMap

- 跟secret用法差不多；不过ConfigMap 保存的是明文，不会用base64编码
- ConfigMap 来将你的配置数据和应用程序代码分开。
- ConfigMap 是一种 API 对象，用来将非机密性的数据保存到键值对中。使用时， [Pods](https://kubernetes.io/docs/concepts/workloads/pods/pod-overview/) 可以将其用作环境变量、命令行参数或者存储卷中的配置文件。

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: game-demo
data:
  # 类属性键；每一个键都映射到一个简单的值
  player_initial_lives: "3"
  ui_properties_file_name: "user-interface.properties"

  # 类文件键
  game.properties: |
    enemy.types=aliens,monsters
    player.maximum-lives=5    
  user-interface.properties: |
    color.good=purple
    color.bad=yellow
    allow.textmode=true
```



你可以使用四种方式来使用 ConfigMap 配置 Pod 中的容器：

1. 在容器命令和参数内
2. 容器的环境变量
3. 在只读卷里面添加一个文件，让应用来读取
4. 编写代码在 Pod 中运行，使用 Kubernetes API 来读取 ConfigMap



```yaml
apiVersion: v1
kind: Pod
metadata:
  name: configmap-demo-pod
spec:
  containers:
    - name: demo
      image: alpine
      command: ["sleep", "3600"]
      env:
        # 定义环境变量
        - name: PLAYER_INITIAL_LIVES # 请注意这里和 ConfigMap 中的键名是不一样的
          valueFrom:
            configMapKeyRef:
              name: game-demo           # 这个值来自 ConfigMap
              key: player_initial_lives # 需要取值的键
        - name: UI_PROPERTIES_FILE_NAME
          valueFrom:
            configMapKeyRef:
              name: game-demo
              key: ui_properties_file_name
      volumeMounts:
      - name: config
        mountPath: "/config"
        readOnly: true
  volumes:
    # 你可以在 Pod 级别设置卷，然后将其挂载到 Pod 内的容器中
    - name: config
      configMap:
        # 提供你想要挂载的 ConfigMap 的名字
        name: game-demo
        # 来自 ConfigMap 的一组键，将被创建为文件
        items:
        - key: "game.properties"
          path: "game.properties"
        - key: "user-interface.properties"
          path: "user-interface.properties"
```



**ConfigMap的修改，可以触发挂载文件的自动更新**





## 使用subPath：

有时，在单个 Pod 中共享卷以供多方使用是很有用的。 `volumeMounts.subPath` 属性可用于指定所引用的卷内的子路径，而不是其根路径。

无论是secret还是ConfigMap，使用挂载的方式会热更新，但是subPath（子路径）挂载除外

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: "pod-time"
  namespace: default
  labels:
    app: "pod-time"
spec:
  containers:
  - name: pod-time
    image: "busybox"
    command: ["sleep","60000"]
    volumeMounts: ## 描述容器想把自己的哪个路径进行挂载
    - name: localtime
      mountPath: /etc/localtime  ## 2 挂到容器的这个位置
      # mountPath: /etc
      # subPath: localtime 
  volumes:  ## 描述每个volumeMounts到底该怎么挂载，在哪里挂载
    - name: localtime
      hostPath:  ## 1 主机的这个文件  
        path: /usr/share/zoneinfo/Asia/Shanghai
        # type: Directory  ### 到底是什么。文件/文件夹 .....
```




# 二、临时存储

Kubernetes 为了不同的目的，支持几种不同类型的临时卷：

- [emptyDir](https://kubernetes.io/zh/docs/concepts/storage/volumes/#emptydir)： Pod 启动时为空，存储空间来自本地的 kubelet 根目录（通常是根磁盘）或内存
- [configMap](https://kubernetes.io/zh/docs/concepts/storage/volumes/#configmap)、 [downwardAPI](https://kubernetes.io/zh/docs/concepts/storage/volumes/#downwardapi)、 [secret](https://kubernetes.io/zh/docs/concepts/storage/volumes/#secret)： 将不同类型的 Kubernetes 数据注入到 Pod 中
- [CSI 临时卷](https://kubernetes.io/zh/docs/concepts/storage/volumes/#csi-ephemeral-volumes)： 类似于前面的卷类型，但由专门[支持此特性](https://kubernetes-csi.github.io/docs/drivers.html) 的指定 [CSI 驱动程序](https://github.com/container-storage-interface/spec/blob/master/spec.md)提供
- [通用临时卷](https://kubernetes.io/zh/docs/concepts/storage/ephemeral-volumes/#generic-ephemeral-volumes)： 它可以由所有支持持久卷的存储驱动程序提供





## 1、emptyDir

- 当 Pod 分派到某个 Node 上时，`emptyDir` 卷会被创建
- 在 Pod 在该节点上运行期间，卷一直存在。
- 卷最初是空的。 
- 尽管 Pod 中的容器挂载 `emptyDir` 卷的路径可能相同也可能不同，这些容器都可以读写 `emptyDir` 卷中相同的文件。 
- 容器崩溃并不会导致 Pod 被从节点上移除，因此容器崩溃期间 emptyDir 卷中的数据是安全的。
- 当 Pod 因为某些原因被从节点上删除时，`emptyDir` 卷中的数据也会被永久删除。【只要是新POD，数据就没了】
- 存储空间来自本地的 kubelet 根目录（通常是根磁盘）或内存

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: "multi-container-pod"
  namespace: default
  labels:
    app: "multi-container-pod"
spec:
  volumes:    ### 以后见到的所有名字 都应该是一个合法的域名方式
  - name: nginx-vol
    emptyDir: {}  ### docker匿名挂载，外部创建一个位置  /abc
  containers:  ## kubectl exec -it podName  -c nginx-container（容器名）-- /bin/sh
  - name: nginx-container
    image: "nginx"
    volumeMounts:  #声明卷挂载  -v
      - name: nginx-vol
        mountPath: /usr/share/nginx/html
  - name: content-container
    image: "alpine"
    command: ["/bin/sh","-c","while true;do sleep 1; date > /app/index.html;done;"]
    volumeMounts: 
      - name: nginx-vol
        mountPath: /app
```

emptyDir 内存配置示例：

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: test-pd
spec:
  containers:
  - image: registry.k8s.io/test-webserver
    name: test-container
    volumeMounts:
    - mountPath: /cache
      name: cache-volume
  volumes:
  - name: cache-volume
    emptyDir:
      sizeLimit: 500Mi
      medium: Memory
```


## 2、hostPath

hostPath：当前主机路径

场景：
  - jenkins要用到宿主机的docker，来运行宿主机上的Docker命令。
  - 挂载当前机器节点的时间

https://kubernetes.io/zh/docs/concepts/storage/volumes/#hostpath

![1620631873199](assets/1620631873199.png)



```yaml
apiVersion: v1
kind: Pod
metadata:
  name: test-pd
spec:
  containers:
  - image: k8s.gcr.io/test-webserver
    name: test-container
    volumeMounts:
    - mountPath: /test-pd
      name: test-volume
  volumes:
  - name: test-volume
    hostPath:
      # 宿主上目录位置
      path: /data
      # 此字段为可选
      type: Directory
```



```yaml
apiVersion: v1
kind: Pod
metadata:
  name: test-webserver
spec:
  containers:
  - name: test-webserver
    image: k8s.gcr.io/test-webserver:latest
    volumeMounts:
    - mountPath: /var/local/aaa
      name: mydir
    - mountPath: /var/local/aaa/1.txt
      name: myfile
  volumes:
  - name: mydir
    hostPath:
      # 确保文件所在目录成功创建。
      path: /var/local/aaa
      type: DirectoryOrCreate
  - name: myfile
    hostPath:
      path: /var/local/aaa/1.txt
      type: FileOrCreate
```



典型应用：解决容器时间问题

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: busy-box-test
  namespace: default
spec:
  restartPolicy: OnFailure
  containers:
  - name: busy-box-test
    image: busybox
    imagePullPolicy: IfNotPresent
    volumeMounts:
    - name: date-config
      mountPath: /etc/localtime
    command: ["sleep", "60000"]
  volumes:
  - name: date-config
    hostPath:
      path: /etc/localtime
```





#  三、持久化


![1620633782779](assets/1620633782779.png)

- Kubernetes 支持很多类型的卷。 [Pod](https://kubernetes.io/docs/concepts/workloads/pods/pod-overview/) 可以同时使用任意数目的卷类型
- 临时卷类型的生命周期与 Pod 相同，但持久卷可以比 Pod 的存活期长
- 当 Pod 不再存在时，Kubernetes 也会销毁临时卷；
- Kubernetes 不会销毁 持久卷。
- 对于给定 Pod 中**任何类型的卷**，在**容器**重启期间数据都不会丢失。
- 使用卷时, 在 `.spec.volumes` 字段中设置为 Pod 提供的卷，并在 `.spec.containers[*].volumeMounts` 字段中声明卷在容器中的挂载位置。



支持的卷类型   https://kubernetes.io/zh/docs/concepts/storage/volumes/#volume-types)


## 1、使用NFS

>NFS生产环境不太建议使用，可以去购买云服务器厂商的一些NFS升级改造版

#### 1、安装NFS

```sh
#在任意机器【充当nfs服务端】
#服务器端防火墙开放111、662、875、892、2049的 tcp / udp 允许，否则远端客户无法连接。
yum install -y nfs-utils
#执行命令 vi /etc/exports，创建 exports 文件，文件内容如下：
mkdir -p /nfs/data
echo "/nfs/data/ *(insecure,rw,sync,no_root_squash)" > /etc/exports
#/nfs/data  172.26.248.0/20(rw,no_root_squash)

# 执行以下命令，启动 nfs 服务;创建共享目录
systemctl enable rpcbind
systemctl enable nfs-server
systemctl start rpcbind
systemctl start nfs-server
exportfs -r
#检查配置是否生效
exportfs
# 输出结果如下所示
/nfs/data     	<world>
```


```sh
#在任意机器【远端（nfs客户端）同步】
#安装客户端工具
yum install -y nfs-utils

#执行以下命令检查 nfs 服务器端是否有设置共享目录
# showmount -e $(nfs服务器的IP)
showmount -e 192.168.10.137
# 输出结果如下所示
Export list for 192.168.10.137
/nfs/data *
```

以上命令已经可以使用nfs挂载了


```sh
#执行以下命令挂载 nfs 服务器上的共享目录到本机路径 /root/nfsmount
# mount -t nfs $(nfs服务器的IP):/root/nfs_root /root/nfsmount
mkdir -p /root/nfsmount
#运行这个命令就可以在本机（nfs客户端）修改文件了；也可以备份文件到本机了
mount -t nfs 192.168.10.137:/nfs/data /root/nfsmount 

#卸载文件夹
umount -l /root/nfsmount
df -h | grep nfsmount
```


```sh
# 写入一个测试文件【哪个机器都行】
echo "hello nfs server" > /root/nfsmount/test.txt
#在 nfs 服务器上执行以下命令，验证文件写入成功
cat /nfs/data/test.txt
cat /root/nfsmount/test.txt
```



#### 2、VOLUME进行挂载测试

```yaml
#测试Pod直接挂载NFS了
apiVersion: v1
kind: Pod
metadata:
  name: vol-nfs
  namespace: default
spec:
  containers:
  - name: myapp
    image: nginx
    volumeMounts:
    - name: html
      mountPath: /usr/share/nginx/html/
  volumes:
  - name: html
    nfs:
      path: /nfs/data   #1000G
      server: 自己的nfs服务器地址
```








## 2、PV&PVC&StorageClass



### 1、基础概念

- **存储的管理**是一个与**计算实例的管理**完全不同的问题。
- PersistentVolume 子系统为用户 和管理员提供了一组 API，将存储如何供应的细节从其如何被使用中抽象出来。 
- 为了实现这点，我们引入了两个新的 API 资源：PersistentVolume 和 PersistentVolumeClaim。



**持久卷（PersistentVolume ）：**

- 持久卷（PersistentVolume，PV）是集群中的一块存储，可以由管理员事先供应，或者 使用[存储类（Storage Class）](https://kubernetes.io/zh/docs/concepts/storage/storage-classes/)来动态供应。
- 持久卷是集群资源，就像节点也是集群资源一样。PV 持久卷和普通的 Volume 一样，也是使用 卷插件来实现的，只是它们拥有独立于使用他们的Pod的生命周期。
- 此 API 对象中记述了存储的实现细节，无论其背后是 NFS、iSCSI 还是特定于云平台的存储系统。



**持久卷申请（PersistentVolumeClaim，PVC）：**（申请书）

- 绑定了Pod的pvc是不能被删除的，Pod删了才可以删
- pvc可以提前创建并和pv绑定；以后Pod只需要关联pvc即可
- 表达的是用户对存储的请求
- 概念上与 Pod 类似。 Pod 会耗用节点资源，而 PVC 申领会耗用 PV 资源。
- Pod 可以请求特定数量的资源（CPU 和内存）；同样 PVC 申领也可以请求特定的大小和访问模式 （例如，可以要求 PV 卷能够以 ReadWriteOnce、ReadOnlyMany 或 ReadWriteMany 模式之一来挂载，参见[访问模式](https://kubernetes.io/zh/docs/concepts/storage/persistent-volumes/#access-modes)）。



**存储类（Storage Class）**:

- 尽管 PersistentVolumeClaim 允许用户消耗抽象的存储资源，常见的情况是针对不同的 问题用户需要的是具有不同属性（如，性能）的 PersistentVolume 卷。
- 集群管理员需要能够提供不同性质的 PersistentVolume，并且这些 PV 卷之间的差别不 仅限于卷大小和访问模式，同时又不能将卷是如何实现的这些细节暴露给用户。
-  为了满足这类需求，就有了 *存储类（StorageClass）* 资源。

![1620637231945](assets/1620637231945.png)

---------------

![1620637253227](assets/1620637253227.png)


-----------------


![1620637286643](assets/1620637286643.png)

### 2、实战

https://kubernetes.io/zh/docs/tasks/configure-pod-container/configure-persistent-volume-storage/


### 3、pv细节

#### 1、访问模式

https://kubernetes.io/zh/docs/concepts/storage/persistent-volumes/#access-modes



- ReadWriteOnce：卷可以被一个节点以读写方式挂载。 ReadWriteOnce 访问模式仍然可以在同一节点上运行的多个 Pod 访问（读取或写入）该卷。 对于单个 Pod 的访问，请参考 ReadWriteOncePod 访问模式。
- ReadOnlyMany：卷可以被多个节点以只读方式挂载。
- ReadWriteMany：卷可以被多个节点以读写方式挂载。


- ReadWriteOncePod：【新特性】卷可以被单个 Pod 以读写方式挂载。 如果你想确保整个集群中只有一个 Pod 可以读取或写入该 PVC， 请使用 ReadWriteOncePod 访问模式。【ReadWriteOncePod 访问模式仅适用于 CSI 卷和 Kubernetes v1.22+。】

在命令行接口（CLI）中，访问模式也使用以下缩写形式：

- RWO - ReadWriteOnce
- ROX - ReadOnlyMany
- RWX - ReadWriteMany
- RWOP - ReadWriteOncePod


| 卷插件 | ReadWriteOnce | ReadOnlyMany | ReadWriteMany | ReadWriteOncePod |
| --- | --- | --- | --- | --- |
| AzureFile | ✓ | ✓ | ✓ | \- |
| CephFS | ✓ | ✓ | ✓ | \- |
| CSI | 取决于驱动 | 取决于驱动 | 取决于驱动 | 取决于驱动 |
| FC | ✓ | ✓ | \- | \- |
| FlexVolume | ✓ | ✓ | 取决于驱动 | \- |
| GCEPersistentDisk | ✓ | ✓ | \- | \- |
| Glusterfs | ✓ | ✓ | ✓ | \- |
| HostPath | ✓ | \- | \- | \- |
| iSCSI | ✓ | ✓ | \- | \- |
| NFS | ✓ | ✓ | ✓ | \- |
| RBD | ✓ | ✓ | \- | \- |
| VsphereVolume | ✓ | \- | \-（Pod 运行于同一节点上时可行） | \- |
| PortworxVolume | ✓ | \- | ✓ | \- |




#### 2、回收策略

pvc绑定pv后,该pv默认就只能这个pvc用了，默认别人就都不能用了
- Released：pv释放。释放了和pvc的关联关系，绑定不存在。以后所有pvc都不能重新绑定上来
- Available：pv可用。可以和任意pvc进行绑定


除了再新建一个相同storageClassName的pv这种办法让新pvc用外，能不能让这个被绑定过的pv可复用呢？————用：persistentVolumeReclaimPolicy【回收策略】


persistentVolumeReclaimPolicy【回收策略】： 定义pvc释放pv时，pv会发生什么。


目前的回收策略有：https://kubernetes.io/zh/docs/concepts/storage/persistent-volumes/#reclaim-policy
- Retain -- 手动回收：自己手动管理，自己可删可不删
- Recycle -- 擦除（rm -rf pv的文件路径/*）：清除pv里面的内容，pv会变为Available，然后pv就可以被其他人用了【目前，只有 nfs 和 hostPath 卷类型支持回收（Recycle）。】
- Delete -- pv跟着pvc删除【当前k8s版本在默认情况下，没有删除插件，只有AWS EBS、GCE PD、Azure Disk、Cinder卷支持删除（Delete）】

- PS：使用动态供应做的NFS挂载的PV, NFS支持Delete回收策略了




#### 3、阶段

https://kubernetes.io/zh/docs/concepts/storage/persistent-volumes/#phase


每个持久卷会处于以下阶段（Phase）之一：

- Available:卷是一个空闲资源，尚未绑定到任何申领
- Bound:该卷已经绑定到某申领
- Released:所绑定的申领已被删除，但是关联存储资源尚未被集群回收
- Failed:卷的自动回收操作失败


## 3、动态供应pv

![img](assets/image.png)





静态供应：

- 集群管理员创建若干 PV 卷。这些卷对象带有真实存储的细节信息，并且对集群 用户可用（可见）。PV 卷对象存在于 Kubernetes API 中，可供用户消费（使用）



动态供应：

- 集群自动根据PVC创建出对应PV进行使用





### 1、设置nfs动态供应

https://github.com/kubernetes-retired/external-storage/tree/master/nfs-client（过时了，去这儿：https://github.com/kubernetes-sigs/nfs-subdir-external-provisioner）

按照文档 https://github.com/kubernetes-sigs/nfs-subdir-external-provisioner/tree/master/deploy 部署，并换成 registry.cn-hangzhou.aliyuncs.com/lfy_k8s_images/nfs-subdir-external-provisioner:v4.0.2 镜像即可


```yaml
## 创建了一个存储类
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: managed-nfs-storage
  annotations:
    storageclass.kubernetes.io/is-default-class: "true"
provisioner: k8s-sigs.io/nfs-subdir-external-provisioner 
#provisioner指定一个供应商的名字。  
#必须匹配 k8s的deployment 的 env 的 PROVISIONER_NAME的值
parameters:
  archiveOnDelete: "true"  ## 删除pv的时候，pv的内容是否要备份
  #### 这里可以调整供应商能力。

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nfs-client-provisioner
  labels:
    app: nfs-client-provisioner
  # replace with namespace where provisioner is deployed
  namespace: default
spec:
  replicas: 1
  strategy:
    type: Recreate
  selector:
    matchLabels:
      app: nfs-client-provisioner
  template:
    metadata:
      labels:
        app: nfs-client-provisioner
    spec:
      serviceAccountName: nfs-client-provisioner
      containers:
        - name: nfs-client-provisioner
          image: registry.cn-hangzhou.aliyuncs.com/lfy_k8s_images/nfs-subdir-external-provisioner:v4.0.2
          # resources:
          #    limits:
          #      cpu: 10m
          #    requests:
          #      cpu: 10m
          volumeMounts:
            - name: nfs-client-root
              mountPath: /persistentvolumes
          env:
            - name: PROVISIONER_NAME
              value: k8s-sigs.io/nfs-subdir-external-provisioner
            - name: NFS_SERVER
              value: 192.168.10.137 ## 指定自己nfs服务器地址
            - name: NFS_PATH  
              value: /nfs/data  ## nfs服务器共享的目录
      volumes:
        - name: nfs-client-root
          nfs:
            server: 192.168.10.137 
            path: /nfs/data
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: nfs-client-provisioner
  # replace with namespace where provisioner is deployed
  namespace: default
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: nfs-client-provisioner-runner
rules:
  - apiGroups: [""]
    resources: ["nodes"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["persistentvolumes"]
    verbs: ["get", "list", "watch", "create", "delete"]
  - apiGroups: [""]
    resources: ["persistentvolumeclaims"]
    verbs: ["get", "list", "watch", "update"]
  - apiGroups: ["storage.k8s.io"]
    resources: ["storageclasses"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["events"]
    verbs: ["create", "update", "patch"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: run-nfs-client-provisioner
subjects:
  - kind: ServiceAccount
    name: nfs-client-provisioner
    # replace with namespace where provisioner is deployed
    namespace: default
roleRef:
  kind: ClusterRole
  name: nfs-client-provisioner-runner
  apiGroup: rbac.authorization.k8s.io
---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
  # replace with namespace where provisioner is deployed
  namespace: default
rules:
  - apiGroups: [""]
    resources: ["endpoints"]
    verbs: ["get", "list", "watch", "create", "update", "patch"]
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
  # replace with namespace where provisioner is deployed
  namespace: default
subjects:
  - kind: ServiceAccount
    name: nfs-client-provisioner
    # replace with namespace where provisioner is deployed
    namespace: default
roleRef:
  kind: Role
  name: leader-locking-nfs-client-provisioner
  apiGroup: rbac.authorization.k8s.io
```


### 2、测试nfs动态供应




```yaml
apiVersion: v1
kind: Pod
metadata:
  name: "nginx-666-pvc-000"
  namespace: default
  labels:
    app: "nginx-666-pvc-000"
spec:
  containers:
  - name: nginx-666-pvc-000
    image: "nginx"
    ports:
    - containerPort:  80
      name:  http
    volumeMounts:
    - name: localtime
      mountPath: /etc/localtime
    - name: html
      mountPath: /usr/share/nginx/html
  volumes:
    - name: localtime
      hostPath:
        path: /usr/share/zoneinfo/Asia/Shanghai
    - name: html
      persistentVolumeClaim:
         claimName:  nginx-666-pvc  ### 你的申请书的名字
  restartPolicy: Always
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: nginx-666-pvc
  namespace: default
  labels:
    app: nginx-666-pvc
spec:
  storageClassName: managed-nfs-storage  ## 存储类StorageClass的名字
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 70m
# ---
# apiVersion: v1
# kind: Service
# metadata:
#   name: MYAPP
#   namespace: default
# spec:
#   selector:
#     app: MYAPP
#   type: ClusterIP
#   ports:
#   - name: MYAPP
#     port: 
#     targetPort: 
#     protocol: TCP
#     nodePort: 
```

### 3、修改一个StorageClass为默认驱动

>或者创建的时候就在注解里写清楚该StorageClass为默认的

https://kubernetes.io/zh-cn/docs/tasks/administer-cluster/change-default-storage-class/


```sh
kubectl patch storageclass <your-StorageClass-name> -p '{"metadata": {"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'
```



```sh
kubectl patch storageclass managed-nfs-storage -p '{"metadata": {"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'
```


可以将多个 StorageClass 标记为默认值。 如果存在多个被标记为默认的 StorageClass，对于未明确指定 storageClassName 的 PersistentVolumeClaim，将使用最近创建的默认 StorageClass 进行创建。


修改完毕后，kubectl get sc，就会发现该StorageClass为default的了，以后写pvc就可以不用写storageClassName了：
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: nginx-666-pvc-x
  namespace: default
  labels:
    app: nginx-666-pvc-x
spec:
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 70m
```



-----------

PS: 使用动态供应做的NFS挂载的PV, NFS支持Delete回收策略了





| Name | Description | Default |
| --- | --- | :-: |
| onDelete | If it exists and has a delete value, delete the directory, if it exists and has a retain value, save the directory. | will be archived with name on the share: `archived-<volume.Name>` |
| archiveOnDelete | If it exists and has a false value, delete the directory. if `onDelete` exists, `archiveOnDelete` will be ignored. | will be archived with name on the share: `archived-<volume.Name>` |
| pathPattern | Specifies a template for creating a directory path via PVC metadata's such as labels, annotations, name or namespace. To specify metadata use `${.PVC.<metadata>}`. Example: If folder should be named like `<pvc-namespace>-<pvc-name>`, use `${.PVC.namespace}-${.PVC.name}` as pathPattern. | n/a |




