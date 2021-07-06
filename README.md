# rabbitmq-installer-lm-gm

# Мигратор для RabbitMQ (проба)

## локальный запуск
`
    с опцией "-Dapp.profile=kostya" (кавычки упустить) 
    (файл настроек тут - src/main/resources/kostya-profile/kostya-rabbitmq.properties)
    тут нужно создать свои сертификаты и положить в папку src/main/resources/rabbitmq_keys/
    описание как делать ключи тут https://github.com/michaelklishin/tls-gen

### для демо:
`
    с опцией "-Dapp.profile=demo" (кавычки упустить) -- нужно настроить под нужный сервер 
    (файл настроек тут - demo-profile/demo-rabbitmq.properties)

# запуск
`
    класс ComplexRegistrationShovelWithExchangeApp -- создаёт объект для связи RabbitMQ
    остальные классы для нужд разработки
    
    
# Как настроить руками shovel тут написано 
## https://sleeplessbeastie.eu/2020/04/15/how-to-move-messages-to-different-rabbitmq-node/ 

## задействован код 2х API 
### для протокола AMQP API - https://github.com/rabbitmq/rabbitmq-java-client
### для протокола RESTfull API - https://github.com/rabbitmq/hop


## Как работает
1. соединяется по http с поиском доступов
2. создаёт vhost если нужно
3. чистит данные и объекты у mq.host 
4. у mq.host создаёт объект
5. создаёт SHOVEL (мост), перетирая старый мост

## Чем пожертвовал
1. нет простоты настроек
2. ПО работает как LM в GM и обратно (для переиспользования кода)
3. создаётся не 2 очереди а одна, так как это прототип (на выходных может доделаю по тикетам)

# Где смотреть работу 
на серверах Rabbit LM и GM
## LM = 192.168.1.2  (нужно изменить в настройках)
## GM =  192.168.1.3 (тут тоже изменить в настройках)


## пароли и IPv4 и ключи заменены на не настоящие - для демо
