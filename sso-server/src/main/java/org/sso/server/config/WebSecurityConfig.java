package org.sso.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Order(1)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http
	        // 必须配置，不然OAuth2的http配置不生效----不明觉厉
	        .requestMatchers()
	        .antMatchers("/zidingyi", "/login", "/oauth/authorize")
	        .and()
	        .authorizeRequests()
	        // 自定义页面或处理url是，如果不配置全局允许，浏览器会提示服务器将页面转发多次
	        .antMatchers("/zidingyi", "/login").permitAll()
	        .anyRequest()
	        .authenticated();

        http.formLogin()
        		.loginPage("/zidingyi")
        		.loginProcessingUrl("/login") // 处理表单登录 URL
        		.and().csrf().disable();
    }
	
}
