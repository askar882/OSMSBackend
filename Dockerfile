FROM maven:3-jdk-8-slim AS builder

LABEL author=askar882

# 配置镜像
RUN mkdir ~/.m2
RUN printf '\
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n\
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">\n\
  <mirrors>\n\
    <mirror>\n\
      <id>aliyunmaven</id>\n\
      <mirrorOf>*</mirrorOf>\n\
      <name>阿里云公共仓库</name>\n\
      <url>https://maven.aliyun.com/repository/public</url>\n\
    </mirror>\n\
  </mirrors>\n\
  <profiles>\n\
    <profile>\n\
      <id>jdk-8</id>\n\
      <activation>\n\
        <jdk>1.8</jdk>\n\
      </activation>\n\
      <repositories>\n\
        <repository>\n\
          <id>spring</id>\n\
          <url>https://maven.aliyun.com/repository/spring</url>\n\
          <releases>\n\
            <enabled>true</enabled>\n\
          </releases>\n\
          <snapshots>\n\
            <enabled>true</enabled>\n\
          </snapshots>\n\
        </repository>\n\
      </repositories>\n\
    </profile>\n\
  </profiles>\n\
</settings>\n\
' > ~/.m2/settings.xml

# 构建
RUN mkdir "/app"
WORKDIR /app
COPY ./ .

RUN mvn package -Dmaven.test.skip


# 部署
FROM openjdk:8-jre-slim AS production

LABEL author=askar882

ARG VERSION
ENV VERSION=${VERSION:-1.0.0-SNAPSHOT}

RUN mkdir /app
WORKDIR /app
COPY --from=builder /app/target/OSMSBackend-${VERSION}.jar ./

CMD java -jar OSMSBackend-${VERSION}.jar
