helm repo add grafana https://grafana.github.io/helm-charts
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add jaegertracing https://jaegertracing.github.io/helm-charts
helm repo add lsst-sqre https://lsst-sqre.github.io/charts/
helm repo add licenseware https://licenseware.github.io/charts/
helm repo update

helm install redis bitnami/redis
helm install postgres bitnami/postgresql --set global.postgresql.auth.postgresPassword="postgres",global.postgresql.auth.username="postgres",postgresql.auth.password="postgres"
helm install kafdrop lsst-sqre/kafdrop --set kafka.brokerConnect="kafka-controller-headless:9092"
helm install kafka bitnami/kafka --set listeners.client.protocol="plaintext",listeners.controller.protocol="plaintext",listeners.interbroker.protocol="plaintext",listeners.external.protocol="plaintext"
helm install kafka-connect licenseware/kafka-connect --set kafka.create="false",configMapPairs.CONNECT_BOOTSTRAP_SERVERS="kafka-controller-headless:9092",schema-registry.create="false"

