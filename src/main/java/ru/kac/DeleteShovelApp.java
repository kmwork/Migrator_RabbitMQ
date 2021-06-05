package ru.kac;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.ShovelStatus;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Удаление shovel
 */
@Slf4j
public class DeleteShovelApp {
    public static void main(String[] args) {
        log.info("[DeleteShovelApp] **************** start ****************");
        RabbitMqConfig mq = RabbitMqConfig.getInstance();
        Client mqClient = RestAccess.mqRestClient();
        delBridge(mqClient, mq.getMqVirtualHost(), mq.getShovelForQueueName(), mq.getShovelForExchangeName());
        log.info("[DeleteShovelApp] **************** end ****************");
    }

    private static void delBridge(Client c, String vhost, String shovelOnQueue, String shovelOnExchange) {
        List<ShovelStatus> status = c.getShovelsStatus(vhost);
        log.info("[DeleteShovelApp] status of shovel = " + status);
        if (status != null && !status.isEmpty()) {
            c.deleteShovel(vhost, shovelOnQueue);
            c.deleteShovel(vhost, shovelOnExchange);
        }
    }
}
