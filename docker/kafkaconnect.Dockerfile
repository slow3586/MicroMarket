FROM confluentinc/cp-kafka-connect:latest
RUN confluent-hub install --no-prompt debezium/debezium-connector-postgresql:2.5.4