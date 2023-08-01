#!/bin/bash
set -ex

export ARCH=$(uname -m)

###########################################################
# UTILS
###########################################################

export DEBIAN_FRONTEND=noninteractive
apt-get update
apt-get install --no-install-recommends -y tzdata ca-certificates net-tools libxml2-utils git curl libudev1 libxml2-utils iptables iproute2 jq unzip build-essential libz-dev libfreetype-dev nano
ln -fs /usr/share/zoneinfo/UTC /etc/localtime
dpkg-reconfigure --frontend noninteractive tzdata
rm -rf /var/lib/apt/lists/*

curl https://raw.githubusercontent.com/spring-io/concourse-java-scripts/v0.0.4/concourse-java.sh > /opt/concourse-java.sh

mkdir -p /opt/ytt/bin
YTT_URL=$( ./get-ytt-url.sh )
curl --location $YTT_URL > /opt/ytt/bin/ytt
chmod +x /opt/ytt/bin/ytt

###########################################################
# CRaC JDK
###########################################################
CRAC_JDK_URL=$( ./get-crac-jdk-url.sh )

mkdir -p /opt/crac-jdk
cd /opt/crac-jdk
curl -L ${CRAC_JDK_URL} | tar zx --strip-components=1
test -f /opt/crac-jdk/bin/java
test -f /opt/crac-jdk/bin/javac
echo 'ulimit -n 1024' >> /root/.bashrc

###########################################################
# DOCKER
###########################################################
cd /
DOCKER_URL=$( ./get-docker-url.sh )
curl -L ${DOCKER_URL} | tar zx
mv /docker/* /bin/
chmod +x /bin/docker*

###########################################################
# DOCKER COMPOSE
###########################################################
mkdir -p /opt/docker-compose/bin
DOCKER_COMPOSE_URL=$( ./get-docker-compose-url.sh )
curl --location $DOCKER_COMPOSE_URL > /opt/docker-compose/bin/docker-compose
chmod +x /opt/docker-compose/bin/docker-compose

###########################################################
# GRADLE ENTERPRISE
###########################################################
mkdir ~/.gradle
echo 'systemProp.user.name=concourse' > ~/.gradle/gradle.properties