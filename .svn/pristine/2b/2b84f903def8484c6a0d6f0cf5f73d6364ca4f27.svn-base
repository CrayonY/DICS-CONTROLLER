#!/usr/bin/env bash

mvn clean package -Dmaven.test.skip=true -U

docker build -t hub.c.163.com/gongweimin/springcloud/ucd/perception/software .

docker push hub.c.163.com/gongweimin/springcloud/ucd/perception/software

