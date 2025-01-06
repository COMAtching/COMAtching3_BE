package comatching.comatching3.charge.websocket;

/*@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/wss")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
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
                    if (authToken != null && authToken.startsWith("Bearer ")) {
                        String jwt = authToken.substring(7);
                        // 여기에서 JWT 토큰을 검증하고 사용자 정보를 설정
                        String uuid = jwtUtil.getUUID(jwt);
                        accessor.setUser(new UsernamePasswordAuthenticationToken(uuid, null, new ArrayList<>()));
                    } else {
                        throw new BusinessException(ResponseCode.TOKEN_EXPIRED);
                    }
                }
                return message;
            }
        });
    }
}*/
