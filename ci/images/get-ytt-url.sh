#!/bin/bash
set -e

VERSION=0.41.1
case $ARCH in
    aarch64)   FILE="ytt-linux-arm64" ;;
    *)       FILE="ytt-linux-amd64" ;;
esac
echo https://github.com/vmware-tanzu/carvel-ytt/releases/download/v$VERSION/$FILE