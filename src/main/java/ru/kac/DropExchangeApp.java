package ru.kac;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DropExchangeApp {

    public static void main(String[] argv) throws Exception {
        log.info("[DropExchangeApp] start");
        try (Connection connection = MqUtils.createMqConnection(); Channel channel = connection.createChannel()) {
            channel.queueUnbind(MqUtils.getQueue(), MqUtils.getOutExchange(), "");
            channel.exchangeDeleteNoWait(MqUtils.getOutExchange(), false);
            channel.queueDeleteNoWait(MqUtils.getQueue(), false, false);
        }
        log.info("[DropExchangeApp] finish");
    }
}

