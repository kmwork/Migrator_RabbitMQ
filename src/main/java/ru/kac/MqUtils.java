package ru.kac;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.SslContextFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqUtils {

    private static final Properties prop = AppUtils.loadProperties("rabbitmq.properties");

    private MqUtils() {
    }

    @SneakyThrows
    public static Connection createMqConnection() {
        String mqHost = prop.getProperty("mq.host");
        int mqPort = Integer.valueOf(prop.getProperty("mq.port"));
        boolean isSSL = Boolean.valueOf(prop.getProperty("mq.isSSL"));
        String mqVirtualHost = prop.getProperty("mq.virtualHost");
        String mqUserName = prop.getProperty("mq.username");
        String mqPassword = prop.getProperty("mq.password");

        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost(mqHost);
        cf.setPort(mqPort);
        if (isSSL) {
            SslContextFactory sslContextFactory = SslUtils.createSslFactory();
            cf.setSslContextFactory(sslContextFactory);
            cf.useSslProtocol();
        }
        cf.setVirtualHost(mqVirtualHost);
        cf.setUsername(mqUserName);
        cf.setPassword(mqPassword);

        return cf.newConnection();
    }

    public static String getQueue() {
        return prop.getProperty("mq.queue");
    }

    public static Map<String, Object> getMqArguments() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-max-length", Integer.valueOf(prop.getProperty("mq.xMaxLength")));
        arguments.put("reject-publish", prop.getProperty("mq.rejectPublish"));
        arguments.put("x-overflow", prop.getProperty("mq.xOverflow"));

        return arguments;
    }

    public static String getOutExchange() {
        return prop.getProperty("mq.out_exchange");
    }
}
