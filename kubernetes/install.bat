helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

kubectl create -f https://strimzi.io/install/latest?namespace=default
helm install redis bitnami/redis -f redis.yaml
helm install postgres bitnami/postgresql -f postgres.yaml

helm install micromarket .
minikube addons enable ingress
minikube addons enable metrics-server