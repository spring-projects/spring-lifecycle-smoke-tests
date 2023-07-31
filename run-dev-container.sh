#!/usr/bin/env bash

REBUILD=false
HOST_WORK_DIR="$( pwd )"
CONTAINER_WORK_DIR=/workspace

while test $# -gt 0; do
  case "$1" in
    -h|--help)
      echo "run-dev-container.sh - run Spring Checkpoint Restore smoke tests dev container"
      echo " "
      echo "run-dev-container.sh [options]"
      echo " "
      echo "options:"
      echo "-h, --help                show brief help"
      echo "-r, --rebuild             force container image rebuild"
      exit 0
      ;;
    -r)
      export REBUILD=true
      shift
      ;;
    --rebuild)
      export REBUILD=true
      shift
      ;;
    *)
      break
      ;;
  esac
done


docker image ls | grep spring-checkpoint-restore-smoke-tests-dev >/dev/null 2>&1 || export REBUILD=true

test "$REBUILD" = false || docker build \
  -t spring-checkpoint-restore-smoke-tests-dev -f $HOST_WORK_DIR/ci/images/ci-image/Dockerfile $HOST_WORK_DIR/ci/images

docker run -it --entrypoint /bin/bash --privileged -v $HOST_WORK_DIR:$CONTAINER_WORK_DIR:delegated -w $CONTAINER_WORK_DIR spring-checkpoint-restore-smoke-tests-dev