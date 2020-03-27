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
9. Istio

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
8. 验证
```$xslt
# 获取token
curl 'http://localhost:${kim-gateway-service-port}/auth/oauth/token?grant_type=password&username=cheng&password=123456&client_id=kim&client_secret=123456'
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODQ5Nzg1OTUsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiMTU1NTAwYzEtMWQ4OS00NzlkLTgwMGUtZmJjYmI5NGIzNjZkIiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.tOFasO-O4e6VeIGVqo2Ss6nUiuFnQsEVkafoWUnm3sI",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJhdGkiOiIxNTU1MDBjMS0xZDg5LTQ3OWQtODAwZS1mYmNiYjk0YjM2NmQiLCJleHAiOjE1ODc1MjczOTUsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiNmIxYjUwOTgtZjk5Ni00NDgyLWJjOTItYzFhZDBhNmRhMTJiIiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.Y7wuZDMSGAVE56M4FwJxCx-0UE-rgz8rk1cllkEh_2E",
    "expires_in": 42997,
    "scope": "server",
    "license": "made by kim",
    "userId": 1,
    "jti": "155500c1-1d89-479d-800e-fbcbb94b366d"
}

# 通过token访问接口
curl --location --request GET 'http://localhost:31412/payment/balance/query' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODQ5Nzg1OTUsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiMTU1NTAwYzEtMWQ4OS00NzlkLTgwMGUtZmJjYmI5NGIzNjZkIiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.tOFasO-O4e6VeIGVqo2Ss6nUiuFnQsEVkafoWUnm3sI'
{
    "requestId": "1f60794b14f381cd",
    "code": 200,
    "message": null,
    "data": "Hello, cheng!, id: 1"
}
```
9. 安装[Istio](https://istio.io/docs/setup/getting-started/)
```$xslt
# 安装demo Istio
istioctl manifest apply --set profile=demo
- Applying manifest for component Base...
✔ Finished applying manifest for component Base.
- Applying manifest for component Citadel...
- Applying manifest for component Kiali...
- Applying manifest for component IngressGateway...
- Applying manifest for component Prometheus...
- Applying manifest for component EgressGateway...
- Applying manifest for component Galley...
- Applying manifest for component Pilot...
- Applying manifest for component Tracing...
- Applying manifest for component Injector...
- Applying manifest for component Policy...
- Applying manifest for component Telemetry...
- Applying manifest for component Grafana...
✔ Finished applying manifest for component Citadel.
✔ Finished applying manifest for component Prometheus.
✔ Finished applying manifest for component Galley.
✔ Finished applying manifest for component Kiali.
✔ Finished applying manifest for component Tracing.
✔ Finished applying manifest for component Injector.
✔ Finished applying manifest for component Policy.
✔ Finished applying manifest for component Pilot.
✔ Finished applying manifest for component IngressGateway.
✔ Finished applying manifest for component EgressGateway.
✔ Finished applying manifest for component Grafana.
✔ Finished applying manifest for component Telemetry.


✔ Installation complete

# 查看pod，全部在running状态
 kubectl get pod -n istio-system 
NAME                                      READY   STATUS    RESTARTS   AGE
grafana-5f798469fd-q8dxt                  1/1     Running   0          81s
istio-citadel-6688f56667-xtmwh            1/1     Running   0          83s
istio-egressgateway-76b766f94-jlgv4       1/1     Running   0          83s
istio-galley-7c9468666f-gnt87             1/1     Running   0          82s
istio-ingressgateway-7f4cd554fd-2g78f     1/1     Running   0          83s
istio-pilot-85558ffbf6-njmlv              1/1     Running   0          82s
istio-policy-66777cbd-5jptr               1/1     Running   1          83s
istio-sidecar-injector-77d8c95c5c-qsvdp   1/1     Running   0          82s
istio-telemetry-fbbc9f778-chhqs           1/1     Running   1          82s
istio-tracing-cd67ddf8-75jv9              1/1     Running   0          83s
kiali-7964898d8c-jrh62                    1/1     Running   0          82s
prometheus-586d4445c7-8sm4n               1/1     Running   0          82s

```
10. 使用Istio管控当前kim微服务集群[更多Istio样例](./docs/Istio/README.md)
```bash
# 给gateway注入Sidecar。
kubectl get deployments kim-gateway-service -o yaml| istioctl kube-inject -f - | kubectl apply -f -
# 注入后发现还可以访问的通，是为之前gateway存在service
kubectl get svc kim-gateway-service
NAME                  TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
kim-gateway-service   NodePort   10.99.151.228   <none>        8080:31412/TCP   40h
# 创建Istio gateway
kubectl apply -f script/Istio/kim-gateway.yaml
gateway.networking.istio.io/kim-gateway created
virtualservice.networking.istio.io/kim created
# 验证kim可被外网访问
curl http://localhost/health
{"status":"UP"}

```

## 公众号

更多微服务相关信息，请关注公众号，不定时分享干货

![二维码](docs/images/二维码.png)

