package com.oas.osmsbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 测试配置。
 *
 * @author askar882
 * @date 2022/05/15
 */
@Configuration
@EnableTransactionManagement
public class TestConfiguration {
    /**
     * 测试使用H2数据库，需要自定义{@link DataSource}。
     *
     * @return 自定义的 {@link DataSource}实例。
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:osms;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("osms");
        dataSource.setPassword("osms");
        return dataSource;
    }
}
