package ru.kac;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class RabbitMqConfig {

    private final Properties prop = AppUtils.loadProperties("rabbitmq.properties");

    private final String mqHost = prop.getProperty("mq.host");
    private final int mqPort = Integer.valueOf(prop.getProperty("mq.port"));
    private final boolean isSSL = Boolean.valueOf(prop.getProperty("mq.isSSL"));
    private final String mqVirtualHost = prop.getProperty("mq.virtualHost");
    private final String mqUserName = prop.getProperty("mq.username");
    private final String mqPassword = prop.getProperty("mq.password");
    private final String mqManagementUrl = prop.getProperty("mq.management.url");

    private final String shovelSrcUri = prop.getProperty("shovel.src-uri");
    private final String shovelDestUri = prop.getProperty("shovel.dest-uri");


    private final Integer shovelReconnectDelay = Integer.valueOf(prop.getProperty("shovel.reconnect-delay"));
    private final Boolean shovelAddForwardHeaders = Boolean.valueOf(prop.getProperty("shovel.add-forward-headers"));

    private final String shovelDestQueue = prop.getProperty("shovel.dest-queue");
    private final String shovelDestExchange = prop.getProperty("shovel.dest-exchange");

    private final String shovelExchangeName = prop.getProperty("shovel.exchange.name");
    private final String shovelQueueName = prop.getProperty("shovel.queue.name");
    @Getter
    private final static RabbitMqConfig instance = new RabbitMqConfig();

    static {
        System.setProperty("javax.net.debug", "all");
    }

    private RabbitMqConfig() {
    }

    @SneakyThrows
    public Connection createMqConnection() {
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost(mqHost);
        cf.setPort(mqPort);
        if (isSSL) {
            cf.useSslProtocol(SslUtils.createSslContext());
        }
        cf.setVirtualHost(mqVirtualHost);
        cf.setUsername(mqUserName);
        cf.setPassword(mqPassword);

        return cf.newConnection();
    }

    public String getQueue() {
        return prop.getProperty("mq.queue");
    }

    public Map<String, Object> getMqArguments() {
        Map<String, Object> arguments = new HashMap<>();
        if (prop.getProperty("mq.xMaxLength") != null && !prop.getProperty("mq.xMaxLength").isBlank()) {
            arguments.put("x-max-length", Integer.valueOf(prop.getProperty("mq.xMaxLength")));
        }

        arguments.put("reject-publish", prop.getProperty("mq.rejectPublish"));

        String xOverflow = prop.getProperty("mq.xOverflow");
        if (xOverflow != null && !xOverflow.isBlank()) {
            arguments.put("x-overflow", xOverflow);
        }

        return arguments;
    }

    public String getOutExchange() {
        return prop.getProperty("mq.out_exchange");
    }

    public boolean getOutExchangeDurable() {
        return Boolean.valueOf(prop.getProperty("mq.out_exchange.durable"));
    }

}
