package ru.kmwork.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Регистрация vhost +exchange + queue
 */
@Slf4j
public class MqRegExchangeApp {

    @SneakyThrows
    public static void main(String[] args) {
        log.info("[MqRegExchangeApp] start");
        run();
        log.info("[MqRegExchangeApp] finish");
    }

    @SneakyThrows
    public static void run() {
        RabbitMqConfig mq = RabbitMqConfig.getInstance();
        try (Connection connection = mq.createMqConnection(); Channel ch = connection.createChannel()) {
            ch.exchangeDeclare(mq.getOutExchange(), BuiltinExchangeType.FANOUT, mq.getOutExchangeDurable());
            String queueName = mq.getQueue();
            Map<String, Object> arguments = mq.getMqArguments();
            ch.queueDeclare(queueName, mq.getOutExchangeDurable(), false, false, arguments);
            ch.queueBind(queueName, mq.getOutExchange(), "");
        }
    }


}
