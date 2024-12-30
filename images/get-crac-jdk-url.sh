#!/bin/bash
set -e

case $ARCH in
    aarch64)   echo "https://download.bell-sw.com/java/17.0.13+13/bellsoft-jdk17.0.13+13-linux-aarch64-crac.tar.gz" ;;
    *)       echo "https://download.bell-sw.com/java/17.0.13+13/bellsoft-jdk17.0.13+13-linux-amd64-crac.tar.gz" ;;
esac

