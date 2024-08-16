helm repo add grafana https://grafana.github.io/helm-charts
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add jaegertracing https://jaegertracing.github.io/helm-charts
helm repo add lsst-sqre https://lsst-sqre.github.io/charts/
helm repo update

kubectl create -f https://strimzi.io/install/latest?namespace=default
helm install redis bitnami/redis --set auth.password="redis",replica.replicaCount="1"
helm install postgres bitnami/postgresql -f postgres.yaml
helm install kafdrop lsst-sqre/kafdrop --set kafka.brokerConnect="kafka-kafka-bootstrap:9092"

minikube addons enable ingress
helm install micromarket .