package comatching.comatching3.chat.config;

import jakarta.websocket.server.ServerContainer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WebSocketTimeoutConfig {
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webSocketIdleTimeoutCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            context.addApplicationListener("org.apache.tomcat.websocket.server.WsContextListener");
            context.addServletContainerInitializer((c, ctx) -> {
                ServerContainer serverContainer = (ServerContainer) ctx.getAttribute("javax.websocket.server.ServerContainer");
                if (serverContainer != null) {
                    try {
                        // WebSocket 세션 idle timeout을 5분(300000ms)으로 설정
                        serverContainer.setDefaultMaxSessionIdleTimeout(5 * 60 * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        });
    }
}
