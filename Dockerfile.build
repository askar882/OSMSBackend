FROM maven:3-jdk-8-slim
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

RUN mkdir "/app"
WORKDIR /app
COPY ./ .
RUN mvn clean install
EXPOSE 8000
CMD [ "mvn", "spring-boot:run" ]
