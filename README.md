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
    класс ru.kac.ComplexRegistrationShovelWithExchangeApp -- создает объект для тикетов
    https://qligent.atlassian.net/browse/ITX2-289
    и 
    https://qligent.atlassian.net/browse/ITX2-288
    
    класс ru.kac.MqPublisherApp пишет в Exchange (и регистрирует его если обменик не создан)
    класс ru.kac.DropExchangeApp удаляет Exchange (если есть)
``
#как настроить руками shovel тут написано 
## todo https://sleeplessbeastie.eu/2020/04/15/how-to-move-messages-to-different-rabbitmq-node/ 

## задействован код 2х API 
## для протокола AMQP API - https://github.com/rabbitmq/rabbitmq-java-client
## для протокола RESTfull API - https://github.com/rabbitmq/hop


## Как работает
1. соединяется по http с поиском доступов
2. создаёт vhost если нужно
3. чистить данные и объекты у mq.host 
4. у mq.host создаёт объект
5. создает SHOVEL (мост), перезапирая старый мост

## Чем пожертвовал
1. нет простоты настроек
2. работаете как LM в GM и обратно
3. содается не 2 очереди а одна, так как это прототим (на выходных может доделаю по тикетам)

# Где смотреть работу 
на серверах Rabbit LM и GM
## LM = 192.168.195.92 
## GM =  192.168.195.102
