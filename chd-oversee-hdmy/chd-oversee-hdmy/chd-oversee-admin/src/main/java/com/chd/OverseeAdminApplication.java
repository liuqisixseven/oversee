package com.chd;

import lombok.extern.slf4j.Slf4j;
import com.chd.common.util.oConvertUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
* 单体启动类（采用此类启动为单体模式）
* 特别提醒:
* 1.需要集成mongodb请删除 exclude={MongoAutoConfiguration.class}
* 2.切换微服务 勾选profile的SpringCloud，这个类就无法启动，启动会报错
*/
@Slf4j
@SpringBootApplication
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableScheduling
public class OverseeAdminApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OverseeAdminApplication.class);
    }

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(OverseeAdminApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = oConvertUtils.getString(env.getProperty("server.servlet.context-path"));

        log.info("\n=======================OverseeAdmin=======================\n\t"+
                "                ┌─┐┬ ┬┌─┐┌─┐┌─┐┌─┐┌─┐\n\t"+
                "                └─┐│ ││  │  ├┤ └─┐└─┐\n\t"+
                "                └─┘└─┘└─┘└─┘└─┘└─┘└─┘\n\t"+
         "\n----------------------------------------------------------\n\t" +
            "Application Oversee Admin is running! Access URLs:\n\t" +
            "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
            "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
            "Swagger文档: \thttp://" + ip + ":" + port + path + "/doc.html\n" +
            "\n============================================================");


    }

}
