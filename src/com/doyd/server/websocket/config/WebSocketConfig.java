package com.doyd.server.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.doyd.module.pc.interceptor.LoginHandshakeInterceptor;
import com.doyd.module.pc.socket.LoginSocketHandler;

@Configuration  
@EnableWebMvc  
@EnableWebSocket 
public class WebSocketConfig extends WebMvcConfigurerAdapter implements
		WebSocketConfigurer {

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		//允许连接的域,只能以http或https开头
        String[] allowsOrigins = {"*"};
        //WebIM WebSocket通道
        //registry.addHandler(appFindCompanySocketHandler(), "/aide/ws/company/list/sockjs").setAllowedOrigins(allowsOrigins).addInterceptors(new FindCompanyHandshakeInterceptor()).withSockJS();
        registry.addHandler(loginSocketHandler(),"/ws/pc/login").setAllowedOrigins(allowsOrigins).addInterceptors(new LoginHandshakeInterceptor());
        registry.addHandler(loginSocketHandler(), "/ws/sockjs/pc/login").setAllowedOrigins(allowsOrigins).addInterceptors(new LoginHandshakeInterceptor()).withSockJS();
	}
	
	@Bean
	public LoginSocketHandler loginSocketHandler(){
		return new LoginSocketHandler();
	}

}
