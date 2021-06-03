#!/bin/sh
LOCAL_VHOST=vhost_ch
LOCAL_USER=for_ch_root
LOCAL_PASSORD=1
LOCAL_MQ_HOST=localhost
LOCAL_MQ_PORT=5672
LOCAL_MQ_QUEUE=itx_ch_gm_queue_shovel

EXTERNAL_VHOST=vhost_shovel
EXTERNAL_USER=shovel_user
EXTERNAL_PASSORD=1
EXTERNAL_MQ_HOST=192.168.1.45
EXTERNAL_MQ_PORT=5672
EXTERNAL_MQ_QUEUE=itx_ch_gm_queue_shovel


JSON_VAR='{
  "src-uri": "amqp://'$LOCAL_USER':'$LOCAL_PASSORD'@'$LOCAL_MQ_HOST':'$LOCAL_MQ_PORT'/'$LOCAL_VHOST'",
  "src-queue": "'"$LOCAL_MQ_QUEUE"'",
  "dest-uri": "amqp://'$EXTERNAL_USER':'$EXTERNAL_PASSORD'@'$EXTERNAL_MQ_HOST':'$EXTERNAL_MQ_PORT'/'$EXTERNAL_VHOST'",
  "dest-queue": "'"$EXTERNAL_MQ_QUEUE"'"
}'
echo "============================================"
echo "$JSON_VAR"
echo "============================================"
sudo rabbitmqctl -p vhost_ch set_parameter shovel proba-kostya-migration-not-ssl "$JSON_VAR"



## amqps://server-name?cacertfile=/path/to/cacert.pem&certfile=/path/to/cert.pem&keyfile=/path/to/key.pem&verify=verify_peer&fail_if_no_peer_cert=true&auth_mechanism=external
## connect to server-name, with SSL and EXTERNAL authentication
