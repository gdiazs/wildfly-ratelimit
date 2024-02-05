FROM bitnami/wildfly:26.1.3
COPY target/wildfly-tests.war /opt/bitnami/wildfly/standalone/deployments
ENV BOTTLENECK_BASE_URL http://bottleneck-instance:9080/api