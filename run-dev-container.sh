#!/usr/bin/env bash

REBUILD=false
HOST_WORK_DIR="$( pwd )"
CONTAINER_WORK_DIR=/workspace

docker image ls | grep spring-checkpoint-restore-smoke-tests-dev | grep ${CONTAINER_TAG} >/dev/null 2>&1 || export REBUILD=true

test "$REBUILD" = false || docker build \
  -t spring-checkpoint-restore-smoke-tests-dev -f $HOST_WORK_DIR/ci/images/ci-image/Dockerfile $HOST_WORK_DIR/ci/images

docker run -it --entrypoint /bin/bash --privileged -v $HOST_WORK_DIR:$CONTAINER_WORK_DIR:delegated -w $CONTAINER_WORK_DIR spring-checkpoint-restore-smoke-tests-dev