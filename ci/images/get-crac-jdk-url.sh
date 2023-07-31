#!/bin/bash
set -e

case $ARCH in
    aarch64)   echo "https://cdn.azul.com/zulu/bin/zulu17.44.17-ca-crac-jdk17.0.8-linux_aarch64.tar.gz" ;;
    *)       echo "https://cdn.azul.com/zulu/bin/zulu17.44.17-ca-crac-jdk17.0.8-linux_x64.tar.gz" ;;
esac

