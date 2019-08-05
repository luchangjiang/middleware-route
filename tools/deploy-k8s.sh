#!/bin/bash  

# Before run this script, you need to login your docker and kubectl with correct access right

IMAGE_REPO=registry.cn-shenzhen.aliyuncs.com/newtype-dev/eddid-route-test
IMAGE_TAG=0.0.11-SNAPSHOT
HELM_CHART_NAME=eddid-route-test
mvn deploy -Ddocker.image.repo=$IMAGE_REPO -Ddocker.image.tag=$IMAGE_TAG
helm upgrade $HELM_CHART_NAME ./helm/eddid-route/ -i -f ./helm/eddid-route/values.test.yaml --debug --reuse-values --namespace test --set image.repository=$IMAGE_REPO --set image.tag=$IMAGE_TAG