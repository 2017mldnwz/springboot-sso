package org.sso.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.sso.server.service.UserDetailService;

@Order(100)
@Configuration
@EnableAuthorizationServer
public class SsoAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailService userDetailService;
    
    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setSigningKey("test_key");
        return accessTokenConverter;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    	clients.inMemory()
		        .withClient("app-a")
		        .secret(passwordEncoder.encode("app-a-1234"))
		        .authorizedGrantTypes("refresh_token","authorization_code")
		        .accessTokenValiditySeconds(3600)
		        .scopes("all")
		        .autoApprove(true)
		        .redirectUris("http://127.0.0.1:9090/app1/login")
		    .and()
		        .withClient("app-b")
		        .secret(passwordEncoder.encode("app-b-1234"))
		        .authorizedGrantTypes("refresh_token","authorization_code")
		        .accessTokenValiditySeconds(7200)
		        .scopes("all")
		        .autoApprove(true)   //自动授权
		        .redirectUris("http://127.0.0.1:9091/app2/login");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(jwtTokenStore())
                .accessTokenConverter(jwtAccessTokenConverter())
                .userDetailsService(userDetailService);
                //.pathMapping("/oauth/confirm_access","/custom/confirm_access");  //自定义授权页面
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
    	//其他应用要访问认证服务器的tokenKey（test_key）的时候需要经过身份认证，获取到秘钥才能解析jwt
        security.tokenKeyAccess("isAuthenticated()"); 
    }
}
