package ru.kmwork.rabbitmq;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.ShovelDetails;
import com.rabbitmq.http.client.domain.ShovelInfo;
import com.rabbitmq.http.client.domain.ShovelStatus;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Создание shovel
 */
@Slf4j
public class MqRegShovelBridgeApp {
    public static void main(String[] args) {
        log.info("[MqRegShovelApp] **************** start ****************");
        RabbitMqConfig mq = RabbitMqConfig.getInstance();
        Client c = RestAccess.mqRestClient();
        List<ShovelStatus> status = c.getShovelsStatus(mq.getMqVirtualHost());
        log.info("[MqRegShovelApp] status of shovel = " + status);
        ShovelInfo queueInfo = new ShovelInfo();
        ShovelInfo exchangeInfo = new ShovelInfo();
        exchangeInfo.setName(mq.getShovelForExchangeName());
        queueInfo.setName(mq.getShovelForQueueName());

        exchangeInfo.setVirtualHost(mq.getMqVirtualHost());
        queueInfo.setVirtualHost(mq.getMqVirtualHost());

        ShovelDetails exchangeDetails = new ShovelDetails();
        ShovelDetails queueDetails = new ShovelDetails();

        exchangeDetails.setAddForwardHeaders(mq.getShovelAddForwardHeaders());

        exchangeDetails.setSourceURIs(Collections.singletonList(mq.getShovelSrcUri()));
        queueDetails.setSourceURIs(Collections.singletonList(mq.getShovelSrcUri()));

        exchangeDetails.setDestinationURIs(Collections.singletonList(mq.getShovelDestUri()));
        queueDetails.setDestinationURIs(Collections.singletonList(mq.getShovelDestUri()));

        exchangeDetails.setSourceExchange(mq.getOutExchange());
        exchangeDetails.setDestinationExchange(mq.getShovelDestExchange());

        queueDetails.setSourceQueue(mq.getQueue());
        queueDetails.setDestinationQueue(mq.getShovelDestQueue());

        exchangeInfo.setDetails(exchangeDetails);
        queueInfo.setDetails(queueDetails);
        c.declareShovel(mq.getMqVirtualHost(), queueInfo);
        c.declareShovel(mq.getMqVirtualHost(), exchangeInfo);

        log.info("[MqRegShovelApp] **************** end ****************");
    }
}
