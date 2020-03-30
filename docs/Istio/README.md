# kim Istio discovery
## 流量管控
### 请求路由

默认的请求路由是采用轮询，我们这里使用Istio实现根据版本进行路由

1. 首先创建两个版本的payment

创建v1

修改BalanceController的query方法

return Response.ok(String.format("Hello v1, %s!, id: %s " , user.getName(),user.getId()));
```bash
# 打包部署
mvn clean package fabric8:deploy -Pkubernetes

# 为了区分deployment,修改名字为kim-payment-service-v1
kubectl get deployments kim-payment-service -o yaml |sed 's/name: kim-payment-service/name: kim-payment-service-v1/g'| kubectl apply -f -
```

创建v2

修改BalanceController的query方法

return Response.ok(String.format("Hello v2, %s!, id: %s email: %s" , user.getName(),user.getId(),user.getEmail()));

```bash
# 打包部署
mvn clean package fabric8:deploy -Pkubernetes

# 为了区分deployment,修改名字为kim-payment-service-v2，同时需要修改version，方便我们根据版本路由
kubectl get deployments kim-payment-service -o yaml |sed 's/version: 1.0-SNAPSHOT/version: 2.0-SNAPSHOT/g' |sed 's/name: kim-payment-service/name: kim-payment-service-v2/g'| kubectl apply -f -

```

检查
```bash
# 检查deployment 
kubectl get deployments  |grep kim
kim-auth-service                     1/1     1            1           4d17h
kim-gateway-service                  1/1     1            1           91m
kim-payment-service-v1               1/1     1            1           86s
kim-payment-service-v2               1/1     1            1           16m
kim-uc-service                       1/1     1            1           4d18h

# 检查pod（注意gateway显示2/2,表示已经注入过Istio，这里gateway一定需要注入，否则无法使用Istio功能）
kubectl get pod |grep kim
kim-auth-service-6df4655d7-hzqtw                      1/1     Running   46         4d17h
kim-gateway-service-54c9f9676d-qwsxw                  2/2     Running   0          34m
kim-payment-service-v1-857474445b-drdqm               1/1     Running   0          35s
kim-payment-service-v2-7777d56bf6-dln9x               1/1     Running   0          15m
kim-uc-service-7d9b495f67-j5bqh                       1/1     Running   0          4d18h

```

2. 我们现在多访问几次，会发现一下v1，一下v2
```bash
curl --request GET 'http://localhost/payment/balance/query'  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODUxNDk3OTcsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiNThhMzdkYjktMmMwMi00M2YxLWJlNmItZGY5NTQwZDJhNjM3IiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.D2E7T0J95_iYsU_aYOlRCSwQNx0XxubGsxYWa4d7kFA'
{"requestId":"5c47fab6698e64d1","code":200,"message":null,"data":"Hello v1, cheng!, id: 1 "}
{"requestId":"858d42f4344c34cc","code":200,"message":null,"data":"Hello v2, cheng!, id: 1 email: cheng@163.com"}
```

3. 创建DestinationRule

DestinationRule这个是指定义路由规则，也就是定义subsets，在创建VirtualService的时候需要使用到subset

```bash
kubectl apply -f script/Istio/kim-destination-rule-all.yaml
```
4. 创建VirtualService

这里将会指定路由

```bash
kubectl apply -f script/Istio/virtual-service-all-v1.yaml
```

5. 验证。现在无论你访问多少次，都只会返回v1都内容
```bash
curl --request GET 'http://localhost/payment/balance/query' --header 'end-user: jim' --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODUxNDk3OTcsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiNThhMzdkYjktMmMwMi00M2YxLWJlNmItZGY5NTQwZDJhNjM3IiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.D2E7T0J95_iYsU_aYOlRCSwQNx0XxubGsxYWa4d7kFA'
{"requestId":"5c47fab6698e64d1","code":200,"message":null,"data":"Hello v1, cheng!, id: 1 "}
```


### 根据用户身份进行路由

```bash
# 项目设置header中的end-user为用户名，配置根据用户名来路由,v2版本会返回邮箱，v1没有返回邮箱
    - match:
        - headers:
            end-user:
              exact: cheng
      route:
        - destination:
            host: kim-payment-service
            subset: v2
kubectl apply -f script/Istio/vs-cheng-v2.yaml
# 查看结果
# cheng用户访问
curl --request GET 'http://localhost/payment/balance/query' \
> --header 'end-user: cheng' \
> --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODUxNDk3OTcsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiNThhMzdkYjktMmMwMi00M2YxLWJlNmItZGY5NTQwZDJhNjM3IiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.D2E7T0J95_iYsU_aYOlRCSwQNx0XxubGsxYWa4d7kFA'
{"requestId":"858d42f4344c34cc","code":200,"message":null,"data":"Hello v2, cheng!, id: 1 email: cheng@163.com"}
# 其他用户访问
curl --request GET 'http://localhost/payment/balance/query' --header 'end-user: jim' --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODUxNDk3OTcsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiNThhMzdkYjktMmMwMi00M2YxLWJlNmItZGY5NTQwZDJhNjM3IiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.D2E7T0J95_iYsU_aYOlRCSwQNx0XxubGsxYWa4d7kFA'
{"requestId":"5c47fab6698e64d1","code":200,"message":null,"data":"Hello v1, cheng!, id: 1 "}
```

### 故障注入

可以模拟故障，测试应用健壮性

一、延迟故障
1. 创建vs-cheng-delay.yaml
```bash
# 给cheng这个指定用户，payment服务延迟7s
  http:
    - fault:
        delay:
          fixedDelay: 7s
          percentage:
            value: 100
      match:
        - headers:
            end-user:
              exact: cheng
      route:
        - destination:
            host: kim-payment-service
            subset: v1

kubectl apply -f script/Istio/vs-cheng-delay.yaml

```
2. 可以使用postman访问，发现end-user是cheng的会延迟7秒，但仍能正常运行。其他用户则正常返回。

3. 修改延迟为10s，发现程序报错，这是因为网关有硬编码读超时。

二、中止故障
1. 创建vs-cheng-abort.yaml
```bash
# 给特定用户返回500
  http:
    - fault:
        abort:
          httpStatus: 500
          percentage:
            value: 100
      match:
        - headers:
            end-user:
              exact: cheng
kubectl apply -f script/Istio/vs-cheng-abort.yaml

```
2. 此时再访问，服务将立即返回500错误。

### 流量转移

根据权重进行流量转移

1. 创建vs-weight.yaml
```bash

  http:
    - route:
        - destination:
            host: kim-payment-service
            subset: v1
          weight: 50
        - destination:
            host: kim-payment-service
            subset: v2
          weight: 50
kubectl apply -f script/Istio/vs-weight.yaml

```
2. 验证，这个时候多访问几次会发现，流量会按照各50%的流向v1和v2，不一定是交替，但是总流量趋向50%

### 设置请求超时

这个功能一般用不上，而且也尽量不要用。因为关于超时后的处理交给调用房自己处理更好。

1. 先对uc服务故障注入，延迟3秒
```bash
kubectl apply -f script/Istio/vs-uc-delay.yaml

```
2. 这个时候访问payment服务，触发了payment的fallback,请求可以正常返回。

3.设置payment的超时0.5s

```bash

  http:
    - route:
        - destination:
            host: kim-payment-service
            subset: v2
      timeout: 0.5s

kubectl apply -f script/Istio/vs-timeout.yaml

```
4. 这个时候访问payment服务，则会报错upstream request timeout错误。

### 断路器

这个在微服务中是至关重要的，可以防止集群发生雪崩效应

1. 添加断路器配置
```bash
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: kim-payment-service-circuit-breaking
spec:
  host: kim-payment-service
  trafficPolicy:
    connectionPool:
      tcp:
        maxConnections: 1
      http:
        http1MaxPendingRequests: 1
        maxRequestsPerConnection: 1
    outlierDetection: #异常点检测
      consecutiveErrors: 1 #连续错误
      interval: 1s
      baseEjectionTime: 3m #逐出时间
      maxEjectionPercent: 100 #最大逐出百分比

kubectl apply -f script/Istio/circuit-breaking.yaml

```

给gateway的deployment增加标注，使可以看到Istio更详细的代理信息
```bash
# 这里需要注意是要加在template下的metadata下的annotations。我就因为加到了Deployment下
的metadata的annotations导致一直看不到kim-payment-service的代理信息。
spec:
  template:
    metadata:
      annotations:
        sidecar.istio.io/statsInclusionPrefixes: cluster.outbound,cluster_manager,listener_manager,http_mixer_filter,tcp_mixer_filter,server,cluster.xds-grpc
 
# 重新部署gateway
kubectl apply -f <(istioctl kube-inject -f target/classes/META-INF/fabric8/kubernetes/kim-gateway-service-deployment.yml)

```

使用fortio（golang编写负载均衡测试库）进行测试

```bash

# 并发量1，无等待，总量20
fortio load -c 1 -qps 0 -n 20 -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODUzMjExMTAsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiNzJjODc1YjctMDU4ZS00NGZkLThjZTAtZDAyNzA3ZmQ3ZTAzIiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.T7P35hZHnsg95hkPib9XPczDL9CnrDNSdjsZFRgkneg'  http://localhost/payment/balance/query
Fortio 1.3.1 running at 0 queries per second, 8->8 procs, for 100 calls: http://localhost/payment/balance/query
16:48:00 I httprunner.go:82> Starting http test for http://localhost/payment/balance/query with 1 threads at -1.0 qps
Starting at max qps with 1 thread(s) [gomax 8] for exactly 100 calls (100 per thread + 0)
16:48:06 I periodic.go:533> T000 ended after 5.636441529s : 100 calls. qps=17.741690299720307
Ended after 5.636535962s : 100 calls. qps=17.741
Aggregated Function Time : count 100 avg 0.056362442 +/- 0.02626 min 0.026800321 max 0.199076643 sum 5.63624424
# range, mid point, percentile, count
>= 0.0268003 <= 0.03 , 0.0284002 , 6.00, 6
> 0.03 <= 0.035 , 0.0325 , 15.00, 9
> 0.035 <= 0.04 , 0.0375 , 27.00, 12
> 0.04 <= 0.045 , 0.0425 , 42.00, 15
> 0.045 <= 0.05 , 0.0475 , 55.00, 13
> 0.05 <= 0.06 , 0.055 , 69.00, 14
> 0.06 <= 0.07 , 0.065 , 75.00, 6
> 0.07 <= 0.08 , 0.075 , 83.00, 8
> 0.08 <= 0.09 , 0.085 , 91.00, 8
> 0.09 <= 0.1 , 0.095 , 94.00, 3
> 0.1 <= 0.12 , 0.11 , 98.00, 4
> 0.12 <= 0.14 , 0.13 , 99.00, 1
> 0.18 <= 0.199077 , 0.189538 , 100.00, 1
# target 50% 0.0480769
# target 75% 0.07
# target 90% 0.08875
# target 99% 0.14
# target 99.9% 0.197169
Sockets used: 1 (for perfect keepalive, would be 1)
Code 200 : 20 (100.0 %)
Response Header Sizes : count 100 avg 345.05 +/- 0.2179 min 345 max 346 sum 34505
Response Body/Total Sizes : count 100 avg 447.97 +/- 0.3861 min 446 max 449 sum 44797
All done 100 calls (plus 0 warmup) 56.362 ms avg, 17.7 qps

# 现在我们调大并发量到3，则会看到30%的流量是失败的
fortio load -c 3 -qps 0 -n 30 -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODUzMjExMTAsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiNzJjODc1YjctMDU4ZS00NGZkLThjZTAtZDAyNzA3ZmQ3ZTAzIiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.T7P35hZHnsg95hkPib9XPczDL9CnrDNSdjsZFRgkneg'  http://localhost/payment/balance/query
Fortio 1.3.1 running at 0 queries per second, 8->8 procs, for 30 calls: http://localhost/payment/balance/query
17:18:15 I httprunner.go:82> Starting http test for http://localhost/payment/balance/query with 3 threads at -1.0 qps
Starting at max qps with 3 thread(s) [gomax 8] for exactly 30 calls (10 per thread + 0)
17:18:15 W http_client.go:679> Parsed non ok code 503 (HTTP/1.1 503)
17:18:15 W http_client.go:679> Parsed non ok code 503 (HTTP/1.1 503)
17:18:16 W http_client.go:679> Parsed non ok code 503 (HTTP/1.1 503)
17:18:16 W http_client.go:679> Parsed non ok code 503 (HTTP/1.1 503)
17:18:16 W http_client.go:679> Parsed non ok code 503 (HTTP/1.1 503)
17:18:16 W http_client.go:679> Parsed non ok code 503 (HTTP/1.1 503)
17:18:16 W http_client.go:679> Parsed non ok code 503 (HTTP/1.1 503)
17:18:16 I periodic.go:533> T000 ended after 1.001258553s : 10 calls. qps=9.987430289646674
17:18:16 W http_client.go:679> Parsed non ok code 503 (HTTP/1.1 503)
17:18:16 I periodic.go:533> T002 ended after 1.16432362s : 10 calls. qps=8.58867743316931
17:18:16 W http_client.go:679> Parsed non ok code 503 (HTTP/1.1 503)
17:18:16 I periodic.go:533> T001 ended after 1.18290278s : 10 calls. qps=8.453780115386998
Ended after 1.182937446s : 30 calls. qps=25.361
Aggregated Function Time : count 30 avg 0.11157716 +/- 0.05699 min 0.032849082 max 0.227197988 sum 3.34731476
# range, mid point, percentile, count
>= 0.0328491 <= 0.035 , 0.0339245 , 3.33, 1
> 0.035 <= 0.04 , 0.0375 , 6.67, 1
> 0.04 <= 0.045 , 0.0425 , 13.33, 2
> 0.045 <= 0.05 , 0.0475 , 23.33, 3
> 0.06 <= 0.07 , 0.065 , 30.00, 2
> 0.07 <= 0.08 , 0.075 , 33.33, 1
> 0.08 <= 0.09 , 0.085 , 43.33, 3
> 0.09 <= 0.1 , 0.095 , 46.67, 1
> 0.1 <= 0.12 , 0.11 , 60.00, 4
> 0.12 <= 0.14 , 0.13 , 66.67, 2
> 0.14 <= 0.16 , 0.15 , 83.33, 5
> 0.18 <= 0.2 , 0.19 , 90.00, 2
> 0.2 <= 0.227198 , 0.213599 , 100.00, 3
# target 50% 0.105
# target 75% 0.15
# target 90% 0.2
# target 99% 0.224478
# target 99.9% 0.226926
Sockets used: 9 (for perfect keepalive, would be 3)
Code 200 : 21 (70.0 %)
Code 503 : 9 (30.0 %)
Response Header Sizes : count 30 avg 241.83333 +/- 158.3 min 0 max 346 sum 7255
Response Body/Total Sizes : count 30 avg 436.2 +/- 30.28 min 367 max 460 sum 13086
All done 30 calls (plus 0 warmup) 111.577 ms avg, 25.4 qps

# 查看代理信息,可以看到upstream_rq_pending_overflow为9，表示溢出流量
kubectl exec kim-gateway-service-579c9b974b-h254j  -c istio-proxy -- pilot-agent request GET stats |grep kim-payment-service|grep pending |grep v1
cluster.outbound|8080|v1|kim-payment-service.default.svc.cluster.local.circuit_breakers.default.rq_pending_open: 0
cluster.outbound|8080|v1|kim-payment-service.default.svc.cluster.local.circuit_breakers.high.rq_pending_open: 0
cluster.outbound|8080|v1|kim-payment-service.default.svc.cluster.local.upstream_rq_pending_active: 0
cluster.outbound|8080|v1|kim-payment-service.default.svc.cluster.local.upstream_rq_pending_failure_eject: 0
cluster.outbound|8080|v1|kim-payment-service.default.svc.cluster.local.upstream_rq_pending_overflow: 9
cluster.outbound|8080|v1|kim-payment-service.default.svc.cluster.local.upstream_rq_pending_total: 41

# 查看异常点信息，ejections_active为1，证明断路器生效，已经将异常点逐出
kubectl exec kim-gateway-service-579c9b974b-h254j  -c istio-proxy -- pilot-agent request GET stats |grep kim-payment-service|grep "outlier_detection" |grep v1
cluster.outbound|8080|v1|kim-payment-service.default.svc.cluster.local.outlier_detection.ejections_active: 1

# 断路器开启后继续访问，发现会立即返回no healthy upstream
curl --request GET 'http://localhost/payment/balance/query' --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODUzMjExMTAsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiNzJjODc1YjctMDU4ZS00NGZkLThjZTAtZDAyNzA3ZmQ3ZTAzIiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.T7P35hZHnsg95hkPib9XPczDL9CnrDNSdjsZFRgkneg'
no healthy upstream
```

### 流量镜像

流量镜像（也称为阴影）是一个强大的概念，它使要素团队能够以最小的风险将更改引入生产。
镜像会将实时流量的副本发送到镜像服务。镜像流量发生在主要服务的关键请求路径的带外。

1. 我们先把流量全部指向v1
```bash
kubectl apply -f script/Istio/virtual-service-all-v1.yaml

```
2. 使用流量镜像，把v1的流量镜像到v2

```bash
kubectl apply -f script/Istio/mrroring.yaml

```

3. 访问payment
```bash
curl --request GET 'http://localhost/payment/balance/query' --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODUzMjExMTAsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiNzJjODc1YjctMDU4ZS00NGZkLThjZTAtZDAyNzA3ZmQ3ZTAzIiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.T7P35hZHnsg95hkPib9XPczDL9CnrDNSdjsZFRgkneg'

# 查看payment v1,v2日志
kubectl logs kim-payment-service-v1-5f5c8b4fc6-hnkkw -c istio-proxy  -f
[2020-03-27T10:43:25.971Z] "GET /user/login/cheng HTTP/1.1" 200 - "-" "-" 0 745 12 12 "-" "Java/1.8.0_151" "425ef933-da38-9725-965f-a44f1af76986" "kim-uc-service:8080" "10.1.2.82:8080" outbound|8080||kim-uc-service.default.svc.cluster.local - 10.98.51.223:8080 10.1.2.78:54250 - default
[2020-03-27T10:43:25.964Z] "GET /balance/query HTTP/1.1" 200 - "-" "-" 0 92 23 20 "192.168.65.3, 127.0.0.1" "PostmanRuntime/7.23.0" "9cb9ed34-9f57-9117-8d67-eeb2aa34ee39" "kim-payment-service:8080" "127.0.0.1:8080" inbound|8080|http|kim-payment-service.default.svc.cluster.local - 10.1.2.78:8080 127.0.0.1:0 - default

kubectl logs kim-payment-service-v2-5f5c8b4fc6-bb76x -c istio-proxy  -f

[2020-03-27T10:43:26.007Z] "GET /user/login/cheng HTTP/1.1" 200 - "-" "-" 0 745 10 8 "-" "Java/1.8.0_151" "c38b9ccf-f27f-91e5-b154-91cad01c4eb6" "kim-uc-service:8080" "10.1.2.82:8080" outbound|8080||kim-uc-service.default.svc.cluster.local - 10.98.51.223:8080 10.1.2.80:42404 - default
[2020-03-27T10:43:25.964Z] "GET /balance/query HTTP/1.1" 200 - "-" "-" 0 112 64 57 "192.168.65.3, 127.0.0.1,10.1.2.88" "PostmanRuntime/7.23.0" "9cb9ed34-9f57-9117-8d67-eeb2aa34ee39" "kim-payment-service-shadow:8080" "127.0.0.1:8080" inbound|8080|http|kim-payment-service.default.svc.cluster.local - 10.1.2.80:8080 10.1.2.88:0 - default


```

### 入口

#### 入口网关

#### 安全网关

##### 单向认证

准备证书

```bash
# 创建根证书
openssl req -x509 -sha256 -nodes -days 365 -newkey rsa:2048 -subj '/O=example Inc./CN=example.com' -keyout example.com.key -out example.com.crt

# 生成服务器证书请求
openssl req -out kim.example.com.csr -newkey rsa:2048 -nodes -keyout kim.example.com.key -subj "/CN=kim.example.com/O=kim organization"

# 使用根证书签发服务器证书
openssl x509 -req -days 365 -CA example.com.crt -CAkey example.com.key -set_serial 0 -in kim.example.com.csr -out kim.example.com.crt

# 查看目录
 ll
total 40
-rw-r--r--  1 chengmboy  staff  1046 Mar 29 10:44 example.com.crt # 根证书
-rw-r--r--  1 chengmboy  staff  1704 Mar 29 10:44 example.com.key # 根密钥
-rw-r--r--  1 chengmboy  staff  1046 Mar 29 10:46 kim.example.com.crt # 服务器证书
-rw-r--r--  1 chengmboy  staff   936 Mar 29 10:46 kim.example.com.csr # 服务器证书请求
-rw-r--r--  1 chengmboy  staff  1704 Mar 29 10:46 kim.example.com.key # 服务器证书密钥

# 创建k8s secret，注意secret的名字必须为istio-ingressgateway-certs，Istio的默认配置
kubectl create -n istio-system secret tls istio-ingressgateway-certs --key kim.example.com.key --cert kim.example.com.crt

# 查看Istio证书挂载情况
kubectl exec -it -n istio-system $(kubectl -n istio-system get pods -l istio=ingressgateway -o jsonpath='{.items[0].metadata.name}') -- ls -al /etc/istio/ingressgateway-certs
drwxrwxrwt 3 root root  120 Mar 29 02:52 .
drwxr-xr-x 1 root root 4096 Mar 23 07:22 ..
drwxr-xr-x 2 root root   80 Mar 29 02:52 ..2020_03_29_02_52_14.075686744
lrwxrwxrwx 1 root root   31 Mar 29 02:52 ..data -> ..2020_03_29_02_52_14.075686744
lrwxrwxrwx 1 root root   14 Mar 29 02:52 tls.crt -> ..data/tls.crt
lrwxrwxrwx 1 root root   14 Mar 29 02:52 tls.key -> ..data/tls.key

```

修改网关配置

```
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: kim-gateway
spec:
  selector:
    istio: ingressgateway # use istio default controller
  servers:
    - port:
        number: 443
        name: https
        protocol: HTTPS
      tls:
        mode: SIMPLE
        serverCertificate: /etc/istio/ingressgateway-certs/tls.crt
        privateKey: /etc/istio/ingressgateway-certs/tls.key
      hosts:
        - "kim.example.com"
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: kim-gateway-service
spec:
  hosts:
    - "kim.example.com"
  gateways:
    - kim-gateway
  http:
   - route:
      - destination:
          host: kim-gateway-service
          port:
            number: 8080
# 生效配置
kubectl apply -f ../gateway-tls.yaml

```

校验

```
curl -v -HHost:kim.example.com --resolve kim.example.com:443:127.0.0.1 --cacert example.com.crt https://kim.example.com:443/health
* Added kim.example.com:443:127.0.0.1 to DNS cache
* Hostname kim.example.com was found in DNS cache
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to kim.example.com (127.0.0.1) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* Cipher selection: ALL:!EXPORT:!EXPORT40:!EXPORT56:!aNULL:!LOW:!RC4:@STRENGTH
* successfully set certificate verify locations:
*   CAfile: example.com.crt
  CApath: none
* TLSv1.2 (OUT), TLS handshake, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (IN), TLS handshake, Server key exchange (12):
* TLSv1.2 (IN), TLS handshake, Server finished (14):
* TLSv1.2 (OUT), TLS handshake, Client key exchange (16):
* TLSv1.2 (OUT), TLS change cipher, Client hello (1):
* TLSv1.2 (OUT), TLS handshake, Finished (20):
* TLSv1.2 (IN), TLS change cipher, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Finished (20):
* SSL connection using TLSv1.2 / ECDHE-RSA-CHACHA20-POLY1305
* ALPN, server accepted to use h2
* Server certificate:
*  subject: CN=kim.example.com; O=kim organization
*  start date: Mar 29 02:46:21 2020 GMT
*  expire date: Mar 29 02:46:21 2021 GMT
*  common name: kim.example.com (matched)
*  issuer: O=example Inc.; CN=example.com
*  SSL certificate verify ok.
* Using HTTP2, server supports multi-use
* Connection state changed (HTTP/2 confirmed)
* Copying HTTP/2 data in stream buffer to connection buffer after upgrade: len=0
* Using Stream ID: 1 (easy handle 0x7fb5dd809c00)
> GET /health HTTP/2
> Host:kim.example.com
> User-Agent: curl/7.54.0
> Accept: */*
> 
* Connection state changed (MAX_CONCURRENT_STREAMS updated)!
< HTTP/2 200 
< x-content-type-options: nosniff
< x-xss-protection: 1; mode=block
< cache-control: no-cache, no-store, max-age=0, must-revalidate
< pragma: no-cache
< expires: 0
< content-type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
< date: Sun, 29 Mar 2020 03:35:07 GMT
< x-envoy-upstream-service-time: 47
< x-envoy-upstream-healthchecked-cluster: kim-gateway-service.default
< server: istio-envoy
< 
* Connection #0 to host kim.example.com left intact
{"status":"UP"}
```

##### 双向认证

k8s挂载CA证书

```
kubectl create -n istio-system secret generic istio-ingressgateway-ca-certs --from-file=example.com.crt

# 查看挂载情况
kubectl exec -it -n istio-system $(kubectl -n istio-system get pods -l istio=ingressgateway -o jsonpath='{.items[0].metadata.name}') -- ls -al /etc/istio/ingressgateway-ca-certs
total 4
drwxrwxrwt 3 root root  100 Mar 29 03:39 .
drwxr-xr-x 1 root root 4096 Mar 23 07:22 ..
drwxr-xr-x 2 root root   60 Mar 29 03:39 ..2020_03_29_03_39_32.424301783
lrwxrwxrwx 1 root root   31 Mar 29 03:39 ..data -> ..2020_03_29_03_39_32.424301783
lrwxrwxrwx 1 root root   22 Mar 29 03:39 example.com.crt -> ..data/example.com.crt

```

修改gateway配置

```
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: kim-gateway
spec:
  selector:
    istio: ingressgateway # use istio default controller
  servers:
    - port:
        number: 443
        name: https
        protocol: HTTPS
      tls:
        mode: MUTUAL
        serverCertificate: /etc/istio/ingressgateway-certs/tls.crt
        privateKey: /etc/istio/ingressgateway-certs/tls.key
        caCertificates: /etc/istio/ingressgateway-ca-certs/example.com.crt
      hosts:
        - "kim.example.com"

# 生效配置
kubectl apply -f ../gateway-mutual-tls.yaml

```

验证

```
# 不带客户端证书
curl -v -HHost:kim.example.com --resolve kim.example.com:443:127.0.0.1 --cacert example.com.crt https://kim.example.com:443/health
* Added kim.example.com:443:127.0.0.1 to DNS cache
* Hostname kim.example.com was found in DNS cache
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to kim.example.com (127.0.0.1) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* Cipher selection: ALL:!EXPORT:!EXPORT40:!EXPORT56:!aNULL:!LOW:!RC4:@STRENGTH
* successfully set certificate verify locations:
*   CAfile: example.com.crt
  CApath: none
* TLSv1.2 (OUT), TLS handshake, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (IN), TLS handshake, Server key exchange (12):
* TLSv1.2 (IN), TLS handshake, Request CERT (13):
* TLSv1.2 (IN), TLS handshake, Server finished (14):
* TLSv1.2 (OUT), TLS handshake, Certificate (11):
* TLSv1.2 (OUT), TLS handshake, Client key exchange (16):
* TLSv1.2 (OUT), TLS change cipher, Client hello (1):
* TLSv1.2 (OUT), TLS handshake, Finished (20):
* TLSv1.2 (IN), TLS alert, Server hello (2):
* error:1401E410:SSL routines:CONNECT_CR_FINISHED:sslv3 alert handshake failure
* stopped the pause stream!
* Closing connection 0
curl: (35) error:1401E410:SSL routines:CONNECT_CR_FINISHED:sslv3 alert handshake failure
# 带客户端证书
curl -v -HHost:kim.example.com --resolve kim.example.com:443:127.0.0.1 --cacert example.com.crt --cert kim-client.example.com.crt --key kim-client.example.com.key  https://kim.example.com:443/health
* Added kim.example.com:443:127.0.0.1 to DNS cache
* Hostname kim.example.com was found in DNS cache
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to kim.example.com (127.0.0.1) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* Cipher selection: ALL:!EXPORT:!EXPORT40:!EXPORT56:!aNULL:!LOW:!RC4:@STRENGTH
* successfully set certificate verify locations:
*   CAfile: example.com.crt
  CApath: none
* TLSv1.2 (OUT), TLS handshake, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (IN), TLS handshake, Server key exchange (12):
* TLSv1.2 (IN), TLS handshake, Request CERT (13):
* TLSv1.2 (IN), TLS handshake, Server finished (14):
* TLSv1.2 (OUT), TLS handshake, Certificate (11):
* TLSv1.2 (OUT), TLS handshake, Client key exchange (16):
* TLSv1.2 (OUT), TLS handshake, CERT verify (15):
* TLSv1.2 (OUT), TLS change cipher, Client hello (1):
* TLSv1.2 (OUT), TLS handshake, Finished (20):
* TLSv1.2 (IN), TLS change cipher, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Finished (20):
* SSL connection using TLSv1.2 / ECDHE-RSA-CHACHA20-POLY1305
* ALPN, server accepted to use h2
* Server certificate:
*  subject: CN=kim.example.com; O=kim organization
*  start date: Mar 29 02:46:21 2020 GMT
*  expire date: Mar 29 02:46:21 2021 GMT
*  common name: kim.example.com (matched)
*  issuer: O=example Inc.; CN=example.com
*  SSL certificate verify ok.
* Using HTTP2, server supports multi-use
* Connection state changed (HTTP/2 confirmed)
* Copying HTTP/2 data in stream buffer to connection buffer after upgrade: len=0
* Using Stream ID: 1 (easy handle 0x7fdfa5000400)
> GET /health HTTP/2
> Host:kim.example.com
> User-Agent: curl/7.54.0
> Accept: */*
> 
* Connection state changed (MAX_CONCURRENT_STREAMS updated)!
< HTTP/2 200 
< x-content-type-options: nosniff
< x-xss-protection: 1; mode=block
< cache-control: no-cache, no-store, max-age=0, must-revalidate
< pragma: no-cache
< expires: 0
< content-type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
< date: Sun, 29 Mar 2020 03:47:46 GMT
< x-envoy-upstream-service-time: 44
< x-envoy-upstream-healthchecked-cluster: kim-gateway-service.default
< server: istio-envoy
< 
* Connection #0 to host kim.example.com left intact
{"status":"UP"}
```

##### 为多个域名配置tls

创建和部署新证书

```
openssl req -out bookinfo.com.csr -newkey rsa:2048 -nodes -keyout bookinfo.com.key -subj "/CN=bookinfo.com/O=bookinfo organization"
openssl x509 -req -days 365 -CA example.com.crt -CAkey example.com.key -set_serial 0 -in bookinfo.com.csr -out bookinfo.com.crt

# 创建secret
kubectl create -n istio-system secret tls istio-ingressgateway-bookinfo-certs --key bookinfo.com.key --cert bookinfo.com.crt

# 让istio-ingressgateway挂载新证书
cat > gateway-patch.json <<EOF
[{
  "op": "add",
  "path": "/spec/template/spec/containers/0/volumeMounts/0",
  "value": {
    "mountPath": "/etc/istio/ingressgateway-bookinfo-certs",
    "name": "ingressgateway-bookinfo-certs",
    "readOnly": true
  }
},
{
  "op": "add",
  "path": "/spec/template/spec/volumes/0",
  "value": {
  "name": "ingressgateway-bookinfo-certs",
    "secret": {
      "secretName": "istio-ingressgateway-bookinfo-certs",
      "optional": true
    }
  }
}]
EOF

# 配置生效
kubectl -n istio-system patch --type=json deploy istio-ingressgateway -p "$(cat gateway-patch.json)"

# 查看
 kubectl exec -it -n istio-system $(kubectl -n istio-system get pods -l istio=ingressgateway -o jsonpath='{.items[0].metadata.name}') -- ls -al /etc/istio/ingressgateway-bookinfo-certs
total 4
drwxrwxrwt 3 root root  120 Mar 29 13:00 .
drwxr-xr-x 1 root root 4096 Mar 29 12:59 ..
drwxr-xr-x 2 root root   80 Mar 29 13:00 ..2020_03_29_13_00_29.548204895
lrwxrwxrwx 1 root root   31 Mar 29 13:00 ..data -> ..2020_03_29_13_00_29.548204895
lrwxrwxrwx 1 root root   14 Mar 29 13:00 tls.crt -> ..data/tls.crt
lrwxrwxrwx 1 root root   14 Mar 29 13:00 tls.key -> ..data/tls.key


```

创建新gateway文件

```
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: bookinfo-gateway
spec:
  selector:
    istio: ingressgateway # use istio default controller
  servers:
    - port:
        number: 443
        name: https
        protocol: HTTPS
      tls:
        mode: SIMPLE
        serverCertificate: /etc/istio/ingressgateway-bookinfo-certs/tls.crt
        privateKey: /etc/istio/ingressgateway-bookinfo-certs/tls.key
      hosts:
        - "bookinfo.com"
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: bookinfo-gateway-service
spec:
  hosts:
    - "bookinfo.com"
  gateways:
    - bookinfo-gateway
  http:
    - route:
        - destination:
            host: kim-gateway-service
            port:
              number: 8080
```

 验证

```
 -HHost:bookinfo.com --resolve bookinfo.com:443:127.0.0.1 --cacert example.com.crt https://bookinfo.com:443/health* Added bookinfo.com:443:127.0.0.1 to DNS cache
* Hostname bookinfo.com was found in DNS cache
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to bookinfo.com (127.0.0.1) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* Cipher selection: ALL:!EXPORT:!EXPORT40:!EXPORT56:!aNULL:!LOW:!RC4:@STRENGTH
* successfully set certificate verify locations:
*   CAfile: example.com.crt
  CApath: none
* TLSv1.2 (OUT), TLS handshake, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (IN), TLS handshake, Server key exchange (12):
* TLSv1.2 (IN), TLS handshake, Server finished (14):
* TLSv1.2 (OUT), TLS handshake, Client key exchange (16):
* TLSv1.2 (OUT), TLS change cipher, Client hello (1):
* TLSv1.2 (OUT), TLS handshake, Finished (20):
* TLSv1.2 (IN), TLS change cipher, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Finished (20):
* SSL connection using TLSv1.2 / ECDHE-RSA-CHACHA20-POLY1305
* ALPN, server accepted to use h2
* Server certificate:
*  subject: CN=bookinfo.com; O=bookinfo organization
*  start date: Mar 29 12:56:44 2020 GMT
*  expire date: Mar 29 12:56:44 2021 GMT
*  common name: bookinfo.com (matched)
*  issuer: O=example Inc.; CN=example.com
*  SSL certificate verify ok.
* Using HTTP2, server supports multi-use
* Connection state changed (HTTP/2 confirmed)
* Copying HTTP/2 data in stream buffer to connection buffer after upgrade: len=0
* Using Stream ID: 1 (easy handle 0x7fb11a005600)
> GET /health HTTP/2
> Host:bookinfo.com
> User-Agent: curl/7.54.0
> Accept: */*
> 
* Connection state changed (MAX_CONCURRENT_STREAMS updated)!
< HTTP/2 200 
< x-content-type-options: nosniff
< x-xss-protection: 1; mode=block
< cache-control: no-cache, no-store, max-age=0, must-revalidate
< pragma: no-cache
< expires: 0
< content-type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
< date: Sun, 29 Mar 2020 13:17:14 GMT
< x-envoy-upstream-service-time: 54
< x-envoy-upstream-healthchecked-cluster: kim-gateway-service.default
< server: istio-envoy
< 
* Connection #0 to host bookinfo.com left intact
{"status":"UP"}

# 验证之前的
curl -v -HHost:kim.example.com --resolve kim.example.com:443:127.0.0.1 --cacert example.com.crt --cert kim-client.example.com.crt --key kim-client.example.com.key  https://kim.example.com:443/health
···
{"status":"UP"}
```

####  密钥发现服务（SDS）

开启密钥发现服务

```
istioctl manifest generate --profile=demo \
--set values.gateways.istio-egressgateway.enabled=false \
--set values.gateways.istio-ingressgateway.sds.enabled=true > \
$HOME/istio-ingressgateway.yaml

kubectl apply -f $HOME/istio-ingressgateway.yaml
```

创建密钥

```
kubectl create -n istio-system secret generic kim-credential --from-file=key=kim.example.com.key \
--from-file=cert=kim.example.com.crt
```

创建网关

```
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: kim-gateway
spec:
  selector:
    istio: ingressgateway # use istio default controller
  servers:
    - port:
        number: 443
        name: https
        protocol: HTTPS
      tls:
        mode: SIMPLE
        credentialName: kim-credential # must be the same as secret
      hosts:
        - "kim.example.com"
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: kim-gateway-service
spec:
  hosts:
    - "kim.example.com"
  gateways:
    - kim-gateway
  http:
    - route:
        - destination:
            host: kim-gateway-service
            port:
              number: 8080
              
# 生效配置

kubectl apply -f ../gateway-sds.yaml

```

验证

```
 curl -v -HHost:kim.example.com --resolve kim.example.com:443:127.0.0.1 --cacert example.com.crt https://kim.example.com:443/health
* Added kim.example.com:443:127.0.0.1 to DNS cache
* Hostname kim.example.com was found in DNS cache
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to kim.example.com (127.0.0.1) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* Cipher selection: ALL:!EXPORT:!EXPORT40:!EXPORT56:!aNULL:!LOW:!RC4:@STRENGTH
* successfully set certificate verify locations:
*   CAfile: example.com.crt
  CApath: none
* TLSv1.2 (OUT), TLS handshake, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (IN), TLS handshake, Server key exchange (12):
* TLSv1.2 (IN), TLS handshake, Server finished (14):
* TLSv1.2 (OUT), TLS handshake, Client key exchange (16):
* TLSv1.2 (OUT), TLS change cipher, Client hello (1):
* TLSv1.2 (OUT), TLS handshake, Finished (20):
* TLSv1.2 (IN), TLS change cipher, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Finished (20):
* SSL connection using TLSv1.2 / ECDHE-RSA-CHACHA20-POLY1305
* ALPN, server accepted to use h2
* Server certificate:
*  subject: CN=kim.example.com; O=kim organization
*  start date: Mar 29 02:46:21 2020 GMT
*  expire date: Mar 29 02:46:21 2021 GMT
*  common name: kim.example.com (matched)
*  issuer: O=example Inc.; CN=example.com
*  SSL certificate verify ok.
* Using HTTP2, server supports multi-use
* Connection state changed (HTTP/2 confirmed)
* Copying HTTP/2 data in stream buffer to connection buffer after upgrade: len=0
* Using Stream ID: 1 (easy handle 0x7fcb1180e000)
> GET /health HTTP/2
> Host:kim.example.com
> User-Agent: curl/7.54.0
> Accept: */*
> 
* Connection state changed (MAX_CONCURRENT_STREAMS updated)!
< HTTP/2 200 
< x-content-type-options: nosniff
< x-xss-protection: 1; mode=block
< cache-control: no-cache, no-store, max-age=0, must-revalidate
< pragma: no-cache
< expires: 0
< content-type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
< date: Sun, 29 Mar 2020 13:52:55 GMT
< x-envoy-upstream-service-time: 69
< x-envoy-upstream-healthchecked-cluster: kim-gateway-service.default
< server: istio-envoy
< 
* Connection #0 to host kim.example.com left intact
{"status":"UP"}
```

##### 更换证书

删除原来的密钥

```
kubectl delete secrets -n istio-system kim-credential
```

创建新证书

```
mkdir new_certificates
openssl req -x509 -sha256 -nodes -days 365 -newkey rsa:2048 -subj '/O=example Inc./CN=example.com' -keyout new_certificates/example.com.key -out new_certificates/example.com.crt
openssl req -out new_certificates/kim.example.com.csr -newkey rsa:2048 -nodes -keyout new_certificates/kim.example.com.key -subj "/CN=kim.example.com/O=kim organization"
openssl x509 -req -days 365 -CA new_certificates/example.com.crt -CAkey new_certificates/example.com.key -set_serial 0 -in new_certificates/kim.example.com.csr -out new_certificates/kim.example.com.crt

```

部署新证书

```
kubectl create -n istio-system secret generic kim-credential \
--from-file=key=new_certificates/kim.example.com.key \
--from-file=cert=new_certificates/kim.example.com.crt
```

验证

```
 curl -v -HHost:kim.example.com --resolve kim.example.com:443:127.0.0.1 --cacert new_certificates/example.com.crt https://kim.example.com:443/health
* Added kim.example.com:443:127.0.0.1 to DNS cache
* Hostname kim.example.com was found in DNS cache
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to kim.example.com (127.0.0.1) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* Cipher selection: ALL:!EXPORT:!EXPORT40:!EXPORT56:!aNULL:!LOW:!RC4:@STRENGTH
* successfully set certificate verify locations:
*   CAfile: new_certificates/example.com.crt
  CApath: none
* TLSv1.2 (OUT), TLS handshake, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (IN), TLS handshake, Server key exchange (12):
* TLSv1.2 (IN), TLS handshake, Server finished (14):
* TLSv1.2 (OUT), TLS handshake, Client key exchange (16):
* TLSv1.2 (OUT), TLS change cipher, Client hello (1):
* TLSv1.2 (OUT), TLS handshake, Finished (20):
* TLSv1.2 (IN), TLS change cipher, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Finished (20):
* SSL connection using TLSv1.2 / ECDHE-RSA-CHACHA20-POLY1305
* ALPN, server accepted to use h2
* Server certificate:
*  subject: CN=kim.example.com; O=kim organization
*  start date: Mar 29 13:51:59 2020 GMT
*  expire date: Mar 29 13:51:59 2021 GMT
*  common name: kim.example.com (matched)
*  issuer: O=example Inc.; CN=example.com
*  SSL certificate verify ok.
* Using HTTP2, server supports multi-use
* Connection state changed (HTTP/2 confirmed)
* Copying HTTP/2 data in stream buffer to connection buffer after upgrade: len=0
* Using Stream ID: 1 (easy handle 0x7ffde480d800)
> GET /health HTTP/2
> Host:kim.example.com
> User-Agent: curl/7.54.0
> Accept: */*
> 
* Connection state changed (MAX_CONCURRENT_STREAMS updated)!
< HTTP/2 200 
< x-content-type-options: nosniff
< x-xss-protection: 1; mode=block
< cache-control: no-cache, no-store, max-age=0, must-revalidate
< pragma: no-cache
< expires: 0
< content-type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
< date: Sun, 29 Mar 2020 13:53:50 GMT
< x-envoy-upstream-service-time: 17
< x-envoy-upstream-healthchecked-cluster: kim-gateway-service.default
< server: istio-envoy
< 
* Connection #0 to host kim.example.com left intact
{"status":"UP"}
# 使用原有的证书则会报错
 curl -v -HHost:kim.example.com --resolve kim.example.com:443:127.0.0.1 --cacert example.com.crt https://kim.example.com:443/health
* Added kim.example.com:443:127.0.0.1 to DNS cache
* Hostname kim.example.com was found in DNS cache
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to kim.example.com (127.0.0.1) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* Cipher selection: ALL:!EXPORT:!EXPORT40:!EXPORT56:!aNULL:!LOW:!RC4:@STRENGTH
* successfully set certificate verify locations:
*   CAfile: example.com.crt
  CApath: none
* TLSv1.2 (OUT), TLS handshake, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (OUT), TLS alert, Server hello (2):
* error:04FFF06A:rsa routines:CRYPTO_internal:block type is not 01
* stopped the pause stream!
* Closing connection 0
curl: (35) error:04FFF06A:rsa routines:CRYPTO_internal:block type is not 01

```

##### 多域名配置证书

创建新证书

```
openssl req -out kim-new.example.com.csr -newkey rsa:2048 -nodes -keyout kim-new.example.com.key -subj "/CN=kim-new.example.com/O=helloworld organization"
openssl x509 -req -days 365 -CA example.com.crt -CAkey example.com.key -set_serial 1 -in kim-new.example.com.csr -out kim-new.example.com.crt

kubectl create -n istio-system secret generic kim-new-credential --from-file=key=kim-new.example.com.key \
--from-file=cert=kim-new.example.com.crt
```

修改原有网关配置

```
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: kim-gateway
spec:
  selector:
    istio: ingressgateway # use istio default controller
  servers:
    - port:
        number: 443
        name: https-kim
        protocol: HTTPS
      tls:
        mode: SIMPLE
        credentialName: kim-credential # must be the same as secret
      hosts:
        - "kim.example.com"
    - port:
        number: 443
        name: https-kim-new
        protocol: HTTPS
      tls:
        mode: SIMPLE
        credentialName: kim-new-credential # must be the same as secret
      hosts:
        - "kim-new.example.com"
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: kim-gateway-service
spec:
  hosts:
    - "kim.example.com"
    - "kim-new.example.com"
  gateways:
    - kim-gateway
  http:
    - route:
        - destination:
            host: kim-gateway-service
            port:
              number: 8080
```

验证

```
curl -v -HHost:kim-new.example.com --resolve kim-new.example.com:443:127.0.0.1 --cacert example.com.crt https://kimm:443/health.com
{"status":"UP"}
curl -v -HHost:kim.example.com --resolve kim.example.com:443:127.0.0.1 --cacert example.com.crt https://kim.example.com:443/health
{"status":"UP"}
```

##### 双向认证

删除原来的密钥

```
kubectl -n istio-system delete secret kim-new-credential

```

创建带有CA的密钥

```
kubectl create -n istio-system secret generic kim-new-credential --from-file=key=kim-new.example.com.key --from-file=cert=kim-new.example.com.crt --from-file=cacert=example.com.crt

```

修改gateway配置

```
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: kim-gateway
spec:
  selector:
    istio: ingressgateway # use istio default controller
  servers:
    - port:
        number: 443
        name: https-kim
        protocol: HTTPS
      tls:
        mode: SIMPLE
        credentialName: kim-credential # must be the same as secret
      hosts:
        - "kim.example.com"
    - port:
        number: 443
        name: https-kim-new
        protocol: HTTPS
      tls:
        mode: MUTUAL
        credentialName: kim-new-credential # must be the same as secret
      hosts:
        - "kim-new.example.com"
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: kim-gateway-service
spec:
  hosts:
    - "kim.example.com"
    - "kim-new.example.com"
  gateways:
    - kim-gateway
  http:
    - route:
        - destination:
            host: kim-gateway-service
            port:
              number: 8080

kubectl apply -f ../gateway-sds-mutual.yaml
```

验证

```
# 不带客户端证书
curl -v -HHost:kim-new.example.com --resolve kim-new.example.com:443:127.0.0.1 --cacert example.com.crt https://kim-new.example.com:443/health
* Added kim-new.example.com:443:127.0.0.1 to DNS cache
* Hostname kim-new.example.com was found in DNS cache
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to kim-new.example.com (127.0.0.1) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* Cipher selection: ALL:!EXPORT:!EXPORT40:!EXPORT56:!aNULL:!LOW:!RC4:@STRENGTH
* successfully set certificate verify locations:
*   CAfile: example.com.crt
  CApath: none
* TLSv1.2 (OUT), TLS handshake, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (IN), TLS handshake, Server key exchange (12):
* TLSv1.2 (IN), TLS handshake, Request CERT (13):
* TLSv1.2 (IN), TLS handshake, Server finished (14):
* TLSv1.2 (OUT), TLS handshake, Certificate (11):
* TLSv1.2 (OUT), TLS handshake, Client key exchange (16):
* TLSv1.2 (OUT), TLS change cipher, Client hello (1):
* TLSv1.2 (OUT), TLS handshake, Finished (20):
* TLSv1.2 (IN), TLS alert, Server hello (2):
* error:1401E410:SSL routines:CONNECT_CR_FINISHED:sslv3 alert handshake failure
* stopped the pause stream!
* Closing connection 0
curl: (35) error:1401E410:SSL routines:CONNECT_CR_FINISHED:sslv3 alert handshake failure

# 带客户端证书
curl -v -HHost:kim-new.example.com --resolve kim-new.example.com:443:127.0.0.1 --cacert example.com.crt --cert kim-client.example.com.crt --key kim-client.example.com.key  https://kim-new.example.com:443/health
* Added kim-new.example.com:443:127.0.0.1 to DNS cache
* Hostname kim-new.example.com was found in DNS cache
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to kim-new.example.com (127.0.0.1) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* Cipher selection: ALL:!EXPORT:!EXPORT40:!EXPORT56:!aNULL:!LOW:!RC4:@STRENGTH
* successfully set certificate verify locations:
*   CAfile: example.com.crt
  CApath: none
* TLSv1.2 (OUT), TLS handshake, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (IN), TLS handshake, Server key exchange (12):
* TLSv1.2 (IN), TLS handshake, Request CERT (13):
* TLSv1.2 (IN), TLS handshake, Server finished (14):
* TLSv1.2 (OUT), TLS handshake, Certificate (11):
* TLSv1.2 (OUT), TLS handshake, Client key exchange (16):
* TLSv1.2 (OUT), TLS handshake, CERT verify (15):
* TLSv1.2 (OUT), TLS change cipher, Client hello (1):
* TLSv1.2 (OUT), TLS handshake, Finished (20):
* TLSv1.2 (IN), TLS change cipher, Client hello (1):
* TLSv1.2 (IN), TLS handshake, Finished (20):
* SSL connection using TLSv1.2 / ECDHE-RSA-CHACHA20-POLY1305
* ALPN, server accepted to use h2
* Server certificate:
*  subject: CN=kim-new.example.com; O=helloworld organization
*  start date: Mar 29 14:05:06 2020 GMT
*  expire date: Mar 29 14:05:06 2021 GMT
*  common name: kim-new.example.com (matched)
*  issuer: O=example Inc.; CN=example.com
*  SSL certificate verify ok.
* Using HTTP2, server supports multi-use
* Connection state changed (HTTP/2 confirmed)
* Copying HTTP/2 data in stream buffer to connection buffer after upgrade: len=0
* Using Stream ID: 1 (easy handle 0x7ff472806c00)
> GET /health HTTP/2
> Host:kim-new.example.com
> User-Agent: curl/7.54.0
> Accept: */*
> 
* Connection state changed (MAX_CONCURRENT_STREAMS updated)!
< HTTP/2 200 
< x-content-type-options: nosniff
< x-xss-protection: 1; mode=block
< cache-control: no-cache, no-store, max-age=0, must-revalidate
< pragma: no-cache
< expires: 0
< content-type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
< date: Sun, 29 Mar 2020 14:20:10 GMT
< x-envoy-upstream-service-time: 23
< x-envoy-upstream-healthchecked-cluster: kim-gateway-service.default
< server: istio-envoy
< 
* Connection #0 to host kim-new.example.com left intact
{"status":"UP"}
```

