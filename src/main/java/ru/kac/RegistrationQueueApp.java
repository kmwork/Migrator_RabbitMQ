package ru.kac;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistrationQueueApp {

    public static void main(String[] argv) throws Exception {
        RabbitMqConfig mq = RabbitMqConfig.getInstance();

        try (Connection connection = mq.createMqConnection(); Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(mq.getOutExchange(), BuiltinExchangeType.FANOUT, mq.getOutExchangeDurable());
            String queueName = mq.getQueue();
            channel.queueBind(queueName, mq.getOutExchange(), "info");

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                log.info(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        }
    }
}

