# k8s_istio_microservice
cloud native discovery

## roadmap
1. springboot
2. 高可用mysql集群
3. redis cluster
4. oauth2认证
5. 统一网关
6. k8s原生服务调用
7. apollo统一配置中心
8. fabric8 k8s统一部署

## 使用教程
1. 安装k8s
2. 安装helm
3. 安装mysql集群[presslabs/mysql-operator](https://github.com/presslabs/mysql-operator)
```bash
helm repo add presslabs https://presslabs.github.io/charts
helm install presslabs/mysql-operator --name mysql-operator

#部署mysql集群
kubectl apply -f script/k8s/mysql/*.yaml
```
4. 安装redis cluster
```bash
kubectl apply -f script/k8s/redis/redis.yaml
```

5. 部署[apollo](https://github.com/ctripcorp/apollo/tree/master/scripts/apollo-on-kubernetes)

6. 启动
```bash
 # 进入相关模块运行
 mvn clean package fabric8:deploy -Pkubernetes
 # 卸载
 mvn  fabric8:undeploy
 
```
7. 查看pod
```$xslt
NAME                                                  READY   STATUS    RESTARTS   AGE
deployment-apollo-admin-server-dev-766df67bdb-vz8ll   1/1     Running   0          30h
deployment-apollo-portal-server-64b69c58bb-6jrkw      1/1     Running   0          30h
kim-auth-service-6df4655d7-hzqtw                      1/1     Running   0          16m
kim-gateway-service-549b9ccbfc-98zd7                  1/1     Running   0          15m
kim-uc-service-7d9b495f67-j5bqh                       1/1     Running   0          96m
my-cluster-mysql-0                                    4/4     Running   0          37h
mysql-operator-0                                      2/2     Running   26         3d5h
redis-0                                               2/2     Running   0          35h
redis-1                                               2/2     Running   0          35h
redis-2                                               2/2     Running   0          35h
redis-3                                               2/2     Running   0          35h
redis-4                                               2/2     Running   0          35h
redis-5                                               2/2     Running   0          35h
redis-6                                               2/2     Running   0          35h
statefulset-apollo-config-server-dev-0                1/1     Running   0          30h
statefulset-apollo-config-server-dev-1                1/1     Running   0          23h
statefulset-apollo-config-server-dev-2                1/1     Running   0          23h
```
