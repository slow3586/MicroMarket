FROM quay.io/strimzi/kafka:0.42.0-kafka-3.7.1
USER root:root
COPY ./plugins/ /opt/kafka/plugins/
USER 1001