# kac-rabbitmq-installer-lm-gm

# Мигратор для RabbitMQ (для ITX2)

## локальный запуск
``
    с  опцией "-Dapp.profile=kostya" (кавычки упустить) 
    (файл настроек тут - src/main/resources/kostya-profile/kostya-rabbitmq.properties)
    тут нужно создать свои сертификаты и положить в папку src/main/resources/rabbitmq_keys/
    описание как делать ключи тут https://github.com/michaelklishin/tls-gen
``
### для демо:
``
    с  опцией "-Dapp.profile=demo" (кавычки упустить) -- нужно достароить под нужный сервер 
    (файл настроек тут - demo-profile/demo-rabbitmq.properties)
``
# запуск
``
    класс ru.kac.RegistrationQueueApp читатает из Exchange (и регистрирует его если обменик не создан) 
    класс ru.kac.MqPublisherApp пишет в Exchange (и регистрирует его если обменик не создан)
    класс ru.kac.DropExchangeApp удаляет Exchange (если есть)
``
#shovel 
## todo https://sleeplessbeastie.eu/2020/04/15/how-to-move-messages-to-different-rabbitmq-node/ 
