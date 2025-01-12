package comatching.comatching3.charge.websocket;

/*@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ChargeService chargeService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtUtil jwtUtil;

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        String destination = (String) event.getMessage().getHeaders().get("simpDestination");
        if ("/topic/chargeRequests".equals(destination)) {
            List<ChargePendingInfo> initialData = chargeService.getAllChargeRequests();
            simpMessagingTemplate.convertAndSend(destination, initialData);
        }
    }
}*/
