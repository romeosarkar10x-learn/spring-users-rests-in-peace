package io.romeosarkar10x.learn.springUsersRestsInPeace.Config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages="io.romeosarkar10x.learn.springUsersRestsInPeace")
@Import(CassandraConfig.class)
public class AppConfig {

}
