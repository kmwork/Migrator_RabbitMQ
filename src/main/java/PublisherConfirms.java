package main.java;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class PublisherConfirms {

    static final int MESSAGE_COUNT = 7;

    static Connection createConnection() throws Exception {
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost("localhost");
        cf.setVirtualHost("vhost_ch");
        cf.setUsername("for_ch_root");
        cf.setPassword("1");
        return cf.newConnection();
    }

    public static void main(String[] args) throws Exception {
        publishMessagesInBatch();
    }

    static void publishMessagesInBatch() throws Exception {
        try (Connection connection = createConnection()) {
            Channel ch = connection.createChannel();

            //String queue = "exchange1";
            String queue = "itx_ch_gm_queue";
            Map<String, Object> arguments = new HashMap<>();
            //arguments.put("x-max-length", 209709);
            arguments.put("x-max-length", 1048545);
            arguments.put("reject-publish", "longstr");
            arguments.put("x-overflow", "reject-publish");
            ch.queueDeclare(queue, true, false, false, arguments);

            ch.confirmSelect();

            int batchSize = 100;
            int outstandingMessageCount = 0;

            long start = System.nanoTime();
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                //String body = String.valueOf(i);
                long value = System.currentTimeMillis();


                String body = String.format("{\n" +
                        "\"key\": %d,\n" +
                        "\"value\": %d \n" +
                        "}", value, value);
                ch.basicPublish("", queue, null, body.getBytes());
                outstandingMessageCount++;

                if (outstandingMessageCount == batchSize) {
                    ch.waitForConfirmsOrDie(5);
                    outstandingMessageCount = 0;
                }
            }

            if (outstandingMessageCount > 0) {
                ch.waitForConfirmsOrDie(5);
            }
            long end = System.nanoTime();
            System.out.format("Published %,d messages in batch in %,d ms%n", MESSAGE_COUNT, Duration.ofNanos(end - start).toMillis());
        }
    }

}
