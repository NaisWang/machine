package com.example.server.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @author : whz
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class TransactionConfiguration {
	@Bean
	@Qualifier("transactionManager")
	public PlatformTransactionManager txManager(@Qualifier("dataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
