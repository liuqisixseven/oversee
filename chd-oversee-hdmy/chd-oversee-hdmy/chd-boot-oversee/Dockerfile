FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER jeecgos@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /jeecg-cloud-demo

WORKDIR /jeecg-boot-module-oversee

EXPOSE 7003

ADD ./target/jeecg-boot-module-oversee-3.3.0.jar ./

CMD sleep 10;java -Dfile.encoding=utf-8 -Djava.security.egd=file:/dev/./urandom -jar jeecg-boot-module-oversee-3.3.0.jar
