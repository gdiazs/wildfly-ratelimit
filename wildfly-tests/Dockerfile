FROM jboss/wildfly:23.0.0.Final
COPY target/wildfly-tests.war /opt/jboss/wildfly/standalone/deployments
ENV BOTTLENECK_BASE_URL http://bottleneck-instance:9080/api