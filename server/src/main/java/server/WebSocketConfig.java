package server;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class  WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures message broker.
     *
     * @param config The message broker registry.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/api/websocket/v1/channel");
        config.setApplicationDestinationPrefixes("/api/websocket");
    }

    /**
     * Registers Stomp endpoints.
     *
     * @param registry The Stomp endpoint registry.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connect");
    }

}

