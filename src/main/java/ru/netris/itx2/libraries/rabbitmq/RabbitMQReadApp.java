package ru.netris.itx2.libraries.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class RabbitMQReadApp {


    @SneakyThrows
    public void run() {
        RabbitMqConfig mq = RabbitMqConfig.getInstance();
        try (Connection connection = mq.createMqConnection(); Channel channel = connection.createChannel()) {
            String queue = mq.getQueue();
            Map<String, Object> arguments = mq.getMqArguments();
            channel.queueDeclare(queue, mq.getOutExchangeDurable(), false, false, arguments);


            log.info(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                if (log.isTraceEnabled()) {
                    log.trace(" [x] Received '" + message + "'");
                }
            };
            channel.basicConsume(queue, true, deliverCallback, consumerTag -> {
            });
        }
    }


    @SneakyThrows
    public static void main(String[] args) {
        log.info("[RabbitMQReadApp] start");
        RabbitMQReadApp demoApp = new RabbitMQReadApp();
        demoApp.run();
        log.info("[RabbitMQReadApp] finish");
    }

}
