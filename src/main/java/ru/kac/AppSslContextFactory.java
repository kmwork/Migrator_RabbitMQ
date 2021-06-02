package ru.kac;

import com.rabbitmq.client.SslContextFactory;
import javax.net.ssl.SSLContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AppSslContextFactory implements SslContextFactory {
    private SSLContext sslContext;

    @Override
    public SSLContext create(String name) {
        return sslContext;
    }
}
