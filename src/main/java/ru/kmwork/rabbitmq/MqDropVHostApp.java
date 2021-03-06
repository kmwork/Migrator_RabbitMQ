package ru.kmwork.rabbitmq;

import com.rabbitmq.http.client.Client;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Удаление vhost
 */
@Slf4j
public class MqDropVHostApp {

    @SneakyThrows
    public static void main(String[] args) {
        log.info("[MqDropVHostApp] start");
        run();
        log.info("[MqDropVHostApp] finish");
    }

    @SneakyThrows
    public static void run() {
        RabbitMqConfig mq = RabbitMqConfig.getInstance();
        Client c = RestAccess.mqRestClient();
        c.deleteVhost(mq.getMqVirtualHost());
    }
}

