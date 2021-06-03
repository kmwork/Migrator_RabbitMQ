#!/bin/sh
LOCAL_VHOST=vhost_ch
LOCAL_USER=for_ch_root
LOCAL_PASSORD=1
LOCAL_MQ_HOST=localhost
LOCAL_MQ_PORT=5671
LOCAL_MQ_QUEUE=itx_ch_gm_queue_shovel

local_ssl_dir="/home/papa/work_kac/kFly/src/main/resources/rabbitmq_keys"
local_cacertfile="$local_ssl_dir/ca_certificate.pem"
local_certfile="$local_ssl_dir/client_certificate.pem"
local_keyfile="$local_ssl_dir/client_key.pem"
local_options="verify=verify_peer&fail_if_no_peer_cert=true&server_name_indication=localhost&auth_mechanism=external"
local_password="bunnies"
local_keyfile_p12="$local_ssl_dir/client_key.p12"

EXTERNAL_VHOST=vhost_shovel
EXTERNAL_USER=shovel_user
EXTERNAL_PASSORD=1
EXTERNAL_MQ_HOST=192.168.1.45
EXTERNAL_MQ_PORT=5672
EXTERNAL_MQ_QUEUE=itx_ch_gm_queue_shovel

#jmsUrl="amqps://localhost:5671?transport.trustStoreLocation=$local_keyfile_p12&transport.trustStorePassword=$local_password&transport.verifyHost=false&jms.sendTimeout=5000"
#echo "jmsUrl=$jmsUrl"
#JSON_VAR='{
#  "src-uri": "'"$jmsUrl"'",
#  "src-queue": "'"$LOCAL_MQ_QUEUE"'",
#  "dest-uri": "amqp://'$EXTERNAL_USER':'$EXTERNAL_PASSORD'@'$EXTERNAL_MQ_HOST':'$EXTERNAL_MQ_PORT'/'$EXTERNAL_VHOST'",
#  "dest-queue": "'"$EXTERNAL_MQ_QUEUE"'"
#}'

JSON_VAR='{
  "src-uri": "amqps://'$LOCAL_MQ_HOST':'$LOCAL_MQ_PORT'/'$LOCAL_VHOST'?cacertfile='$local_cacertfile'&certfile='$local_certfile'&keyfile='$local_keyfile'&password='$local_password'&'$local_options'",
  "src-queue": "'"$LOCAL_MQ_QUEUE"'",
  "dest-uri": "amqp://'$EXTERNAL_USER':'$EXTERNAL_PASSORD'@'$EXTERNAL_MQ_HOST':'$EXTERNAL_MQ_PORT'/'$EXTERNAL_VHOST'",
  "dest-queue": "'"$EXTERNAL_MQ_QUEUE"'"
}'

echo "============================================"
echo "$JSON_VAR"
echo "============================================"
sudo rabbitmqctl -p vhost_ch set_parameter shovel proba-kostya-migration-with-ssl "$JSON_VAR"



## amqps://server-name?cacertfile=/path/to/cacert.pem&certfile=/path/to/cert.pem&keyfile=/path/to/key.pem&verify=verify_peer&fail_if_no_peer_cert=true&auth_mechanism=external
## connect to server-name, with SSL and EXTERNAL authentication
