package ru.kac;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import lombok.SneakyThrows;

public class ShovelUtils {

    @SneakyThrows
    public static Client shovelClient() {

        RabbitMqConfig mq = RabbitMqConfig.getInstance();

        Client c = new Client(
                new ClientParameters()
                        .url(mq.getMqManagementUrl())
                        .username(mq.getMqUserName())
                        .password(mq.getMqPassword())
        );
        return c;
    }
}


