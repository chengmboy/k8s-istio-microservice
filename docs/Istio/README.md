# kim Istio discovery
## 流量管控
###1. 请求路由
1. 创建两个版本的payment
```bash
kim-payment-service-v2-b44dd6876-pz9nw                   1/1     Running   0          46s
kim-payment-service-v1-5ffbb6f7f5-d4l47               1/1     Running   0          61s
```
2. 为payment注入Sidecar
```$xslt
kubectl get deployments kim-payment-service -o yaml| istioctl kube-inject -f - | kubectl apply -f -
kubectl get deployments kim-payment-service-v1 -o yaml| istioctl kube-inject -f - | kubectl apply -f -
# 查看pod
kim-payment-service-b44dd6876-pz9nw                   2/2     Running   0          46s
kim-payment-service-v1-5ffbb6f7f5-d4l47               2/2     Running   0          61s
```
3. 创建DestinationRule

```$xslt
kubectl apply -f script/Istio/kim-destination-rule-all.yaml
```
4.创建VirtualService
```$xslt
kubectl apply -f script/Istio/virtual-service-all-v1.yaml
```

### 2. 根据用户身份进行路由

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
{"requestId":"53d5d5ae728175da","code":200,"message":null,"data":"Hello, cheng!, id: 1 email: cheng@163.com"}
# 其他用户访问
curl --request GET 'http://localhost/payment/balance/query' --header 'end-user: jim' --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBraW0iLCJ1c2VyX25hbWUiOiJjaGVuZyIsInNjb3BlIjpbInNlcnZlciJdLCJleHAiOjE1ODUxNDk3OTcsInVzZXJJZCI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiQURNSU4iXSwianRpIjoiNThhMzdkYjktMmMwMi00M2YxLWJlNmItZGY5NTQwZDJhNjM3IiwiY2xpZW50X2lkIjoiY2xvdWRycyJ9.D2E7T0J95_iYsU_aYOlRCSwQNx0XxubGsxYWa4d7kFA'
{"requestId":"9081c2f4694169cb","code":200,"message":null,"data":"Hello, cheng!, id: 1"}
```

## 故障注入

