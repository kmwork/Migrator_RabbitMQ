package ru.kac;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BooleanSupplier;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Отправка тестового json в очередь
 */
@Slf4j
public class MqMessageSendTester {

    /**
     * Отправить сообщения в очерень
     *
     * @param messageCount количество сообщениц
     * @param fileJsonName файл json из ресурсов
     */
    @SneakyThrows
    public static void publishMessagesInBatch(int messageCount, String fileJsonName) {
        RabbitMqConfig mq = RabbitMqConfig.getInstance();
        try (Connection connection = mq.createMqConnection(); Channel ch = connection.createChannel()) {

            ch.exchangeDeclare(mq.getOutExchange(), BuiltinExchangeType.FANOUT, mq.getOutExchangeDurable());
            String queueName = mq.getQueue();
            Map<String, Object> arguments = mq.getMqArguments();
            ch.queueDeclare(queueName, mq.getOutExchangeDurable(), false, false, arguments);

            ch.confirmSelect();

            ConcurrentNavigableMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

            ConfirmCallback cleanOutstandingConfirms = (sequenceNumber, multiple) -> {
                if (multiple) {
                    ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(
                            sequenceNumber, true
                    );
                    confirmed.clear();
                } else {
                    outstandingConfirms.remove(sequenceNumber);
                }
            };

            ch.addConfirmListener(cleanOutstandingConfirms, (sequenceNumber, multiple) -> {
                String body = outstandingConfirms.get(sequenceNumber);
                String msg = String.format(
                        "Message with body %s has been nack-ed. Sequence number: %d, multiple: %b%n",
                        body, sequenceNumber, multiple
                );
                log.error(msg);
                cleanOutstandingConfirms.handle(sequenceNumber, multiple);
            });


            String strJson = AppUtils.readJson(fileJsonName);
            long start = System.nanoTime();
            for (int i = 0; i < messageCount; i++) {
                outstandingConfirms.put(ch.getNextPublishSeqNo(), strJson);
                ch.basicPublish("", queueName, null, strJson.getBytes(StandardCharsets.UTF_8));
            }


            if (!waitUntil(Duration.ofSeconds(60), () -> outstandingConfirms.isEmpty())) {
                throw new IllegalStateException("All messages could not be confirmed in 60 seconds");
            }

            long end = System.nanoTime();
            String msq = String.format("Published %,d messages in batch in %,d ms%n", messageCount, Duration.ofNanos(end - start).toMillis());
            log.info(msq);
        }
    }


    static boolean waitUntil(Duration timeout, BooleanSupplier condition) throws InterruptedException {
        int waited = 0;
        while (!condition.getAsBoolean() && waited < timeout.toMillis()) {
            Thread.sleep(100L);
            waited = +100;
        }
        return condition.getAsBoolean();
    }


}
