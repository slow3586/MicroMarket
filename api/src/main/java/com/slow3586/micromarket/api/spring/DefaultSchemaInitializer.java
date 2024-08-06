package com.slow3586.micromarket.api.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DefaultSchemaInitializer implements BeanPostProcessor {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource dataSource) {
            try {
                Connection conn = dataSource.getConnection();
                Statement statement = conn.createStatement();
                statement.execute(String.format("CREATE SCHEMA IF NOT EXISTS %s", applicationName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return bean;
    }
}
