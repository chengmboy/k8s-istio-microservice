# kim Istio discovery
## 流量管控
1. 请求路由
      1. 创建两个版本的payment
      
      ```
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

