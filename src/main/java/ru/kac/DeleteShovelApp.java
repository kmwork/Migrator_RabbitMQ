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
        Client c = RestAccess.mqRestClient();
        List<ShovelStatus> status = c.getShovelsStatus(mq.getMqVirtualHost());
        log.info("[DeleteShovelApp] status of shovel = " + status);
        if (status != null && status.size() > 0) {
            c.deleteShovel(mq.getMqVirtualHost(), mq.getShovelQueueName());
            c.deleteShovel(mq.getMqVirtualHost(), mq.getShovelExchangeName());
        }
        log.info("[DeleteShovelApp] **************** end ****************");
    }
}
