package ru.kmwork.rabbitmq;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RestAccess {

    @SneakyThrows
    public static Client mqRestClient() {

        RabbitMqConfig mq = RabbitMqConfig.getInstance();

        return new Client(
                new ClientParameters()
                        .url(mq.getMqManagementUrl())
                        .username(mq.getMqUserName())
                        .password(mq.getMqPassword())
        );
    }

    @SneakyThrows
    public static Client shovelRestClient() {

        RabbitMqConfig mq = RabbitMqConfig.getInstance();

        return new Client(
                new ClientParameters()
                        .url(mq.getShovelManagementUrl())
                        .username(mq.getShovelUserName())
                        .password(mq.getShovelPassword())
        );
    }
}


