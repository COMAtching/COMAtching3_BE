package comatching.comatching3.config;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
//    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/wss")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    // todo : 리프레시 토큰 사용하게 만들어야함
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authToken = accessor.getFirstNativeHeader("Authorization");
                    if (authToken != null && !jwtUtil.isExpired(authToken) && authToken.startsWith("Bearer ")) {
                        String jwt = authToken.substring(7);
                        String uuid = jwtUtil.getUUID(jwt);
                        accessor.setUser(new UsernamePasswordAuthenticationToken(uuid, null, new ArrayList<>()));
                    } else {
                        handleRefreshToken(accessor);
                    }
                }
                return message;
            }

            private void handleRefreshToken(StompHeaderAccessor accessor) {
//                String refreshToken = accessor.getFirstNativeHeader("Refresh-Token");
//                if (refreshToken != null && !jwtUtil.isExpired(refreshToken)) {
//                    String uuid = jwtUtil.getUUID(refreshToken);
//                    String role = jwtUtil.getRole(refreshToken);
//
//                    String newAccessToken = jwtUtil.generateAccessToken(uuid, role);
//                    String newRefreshToken = jwtUtil.generateRefreshToken(uuid, role);
//                    accessor.setUser(new UsernamePasswordAuthenticationToken(uuid, null, new ArrayList<>()));
//
//                    Map<String, String> tokens = new HashMap<>();
//                    tokens.put("accessToken", newAccessToken);
//                    tokens.put("refreshToken", newRefreshToken);
//                    simpMessagingTemplate.convertAndSendToUser(accessor.getSessionId(), "/queue/tokens", tokens);
//                } else {
//                    throw new BusinessException(ResponseCode.TOKEN_EXPIRED);
//                }
                throw new BusinessException(ResponseCode.TOKEN_EXPIRED);
            }
        });
    }
}
