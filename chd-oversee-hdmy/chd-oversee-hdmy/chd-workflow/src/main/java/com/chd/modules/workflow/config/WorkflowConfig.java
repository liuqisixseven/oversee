package com.chd.modules.workflow.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowConfig  implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    private String fontName="Serif";
    private String xmlEncoding="UTF-8";
    @Override
    public void configure(SpringProcessEngineConfiguration configure) {
        //配置中文
        configure.setActivityFontName(fontName);
        configure.setLabelFontName(fontName);
        configure.setAnnotationFontName(fontName);
        //设置是否升级 false不升级  true升级
        configure.setDatabaseSchemaUpdate("false");
        //设置自定义的uuid生成策略
//        configure.setIdGenerator(uuidGenerator());
        configure.setXmlEncoding(xmlEncoding);
        //启用任务关系计数
        configure.setEnableTaskRelationshipCounts(true);
        //启动同步功能 一定要启动否则报错
//        configure.setAsyncExecutor(springAsyncExecutor());
        //自定义sql
//        Set<Class<?>> customMybatisMappers = new HashSet<>();
//        customMybatisMappers.add(CustomCommentMapper.class);
//        configure.setCustomMybatisMappers(customMybatisMappers);
        //集成DMN
//        List<EngineConfigurator> configurators = new ArrayList<>();
//        configurators.add(dmnEngineConfiguration);
//        configure.setConfigurators(configurators);
    }

}
