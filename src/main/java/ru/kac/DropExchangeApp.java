package ru.kac;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Удаление exchange + queue
 */
@Slf4j
public class DropExchangeApp {

    @SneakyThrows
    public static void main(String[] argv) {
        log.info("[DropExchangeApp] start");
        RabbitMqConfig mq = RabbitMqConfig.getInstance();
        try (Connection connection = mq.createMqConnection(); Channel channel = connection.createChannel()) {
            channel.queueUnbind(mq.getQueue(), mq.getOutExchange(), "");
            channel.exchangeDeleteNoWait(mq.getOutExchange(), false);
            channel.queueDeleteNoWait(mq.getQueue(), false, false);
        }
        log.info("[DropExchangeApp] finish");
    }
}

