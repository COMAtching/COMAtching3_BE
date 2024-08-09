package comatching.comatching3.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableRabbit
public class RabbitMQListenerConfig implements RabbitListenerConfigurer {
	@Autowired
	private LocalValidatorFactoryBean validator;

	/**
	 * configure rabbitMQ validation(for listener)
	 * @param registrar the registrar to be configured
	 */
	@Override
	public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
		registrar.setValidator(this.validator);
	}
}
