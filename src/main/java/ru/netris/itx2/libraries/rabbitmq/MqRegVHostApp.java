package ru.netris.itx2.libraries.rabbitmq;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.UserPermissions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Регистрация vhost +exchange + queue
 */
@Slf4j
public class MqRegVHostApp {

    @SneakyThrows
    public static void main(String[] args) {
        log.info("[MqRegVHostApp] start");
        run();
        log.info("[MqRegVHostApp] finish");
    }

    @SneakyThrows
    public static void run() {
        RabbitMqConfig mq = RabbitMqConfig.getInstance();
        Client c = RestAccess.mqRestClient();
        c.createVhost(mq.getMqVirtualHost());
        UserPermissions p = c.getPermissions(mq.getMqVirtualHost(), mq.getMqUserName());
        log.debug("UserPermissions = " + p);

        Client shovel = RestAccess.shovelRestClient();
        shovel.createVhost(mq.getShovelVirtualHost());
        UserPermissions shovelPermission = c.getPermissions(mq.getShovelVirtualHost(), mq.getShovelUserName());
        log.debug("shovelPermission = " + shovelPermission);


    }
}

