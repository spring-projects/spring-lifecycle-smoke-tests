FROM ubuntu:focal-20220531

ADD setup.sh /setup.sh
ADD get-crac-jdk-url.sh /get-crac-jdk-url.sh
ADD get-docker-url.sh /get-docker-url.sh
RUN ./setup.sh

ENV JAVA_HOME /opt/crac-jdk
ENV PATH $JAVA_HOME/bin:/opt/ytt/bin:/opt/docker-compose/bin:$PATH
ENV GRADLE_OPTS -Dorg.gradle.project.buildDir=/tmp/gradle-build
RUN git config --global --add safe.directory /workspace
ADD docker-lib.sh /docker-lib.sh

ENTRYPOINT [ "switch", "shell=/bin/bash", "--", "codep", "/bin/docker daemon" ]
