FROM hub.c.163.com/library/java:8-alpine

MAINTAINER gongweimin 18310239629@163.com

ADD target/*.jar monitorSoftware.jar

EXPOSE 28080

ENTRYPOINT ["java","-jar","/monitorSoftware.jar"]