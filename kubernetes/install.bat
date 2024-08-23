helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add jaegertracing https://jaegertracing.github.io/helm-charts
helm repo update

kubectl create -f https://strimzi.io/install/latest?namespace=default

helm install redis bitnami/redis -f redis.yaml
helm install postgres bitnami/postgresql -f postgres.yaml
helm install loki grafana/loki-stack -f loki.yaml
helm install jaeger jaegertracing/jaeger -f jaeger.yaml

helm install micromarket .

minikube addons enable ingress
minikube tunnel