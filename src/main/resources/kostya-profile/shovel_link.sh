#!/bin/sh
LOCAL_VHOST="vhost_ch"
LOCAL_USER="for_ch_root"
LOCAL_PASSORD="1"
LOCAL_MQ_HOST="localhost"
#LOCAL_MQ_PORT_WITH_SSL="5671"
LOCAL_MQ_PORT="5672"
LOCAL_MQ_QUEUE="kmwork_ch_gm_queue_shovel"
LOCAL_MQ_EXCHANE="kmwork_ch_gm_exchange_shovel"

local_ssl_dir="/home/kmwork/work_kac/kFly/src/main/resources/rabbitmq_keys"
local_cacertfile="$local_ssl_dir/ca_certificate.pem"
local_certfile="$local_ssl_dir/client_certificate.pem"
local_keyfile="$local_ssl_dir/client_key.pem"
###########local_options1="verify=verify_peer"
local_options1="verify=verify_none"
#####local_options2="fail_if_no_peer_cert=true"
local_options2="fail_if_no_peer_cert=false"
local_options3="server_name_indication=disable"
local_options4="auth_mechanism=EXTERNAL"
local_options5="depth=5"
local_options="$local_options1&$local_options2&$local_options3&$local_options4&$local_options5"
local_password="12345678"
local_keyfile_p12="$local_ssl_dir/client_key.p12"

EXTERNAL_VHOST="vhost_shovel"
EXTERNAL_USER="shovel_user"
EXTERNAL_PASSORD="1"
EXTERNAL_MQ_HOST="192.168.1.45"
EXTERNAL_MQ_PORT="5672"
EXTERNAL_MQ_QUEUE="kmwork_ch_gm_queue_shovel"
EXTERNAL_MQ_EXCHANE="kmwork_ch_gm_exchange_shovel"


#LOCAL_AMQP_URI_WITH_SSL="amqps://$LOCAL_MQ_HOST:$LOCAL_MQ_PORT_WITH_SSL/$LOCAL_VHOST?cacertfile=$local_cacertfile&certfile=$local_certfile&keyfile=$local_keyfile&password=$local_password&$local_options"
LOCAL_AMQP_URI="amqp://$LOCAL_USER:$LOCAL_PASSORD@$LOCAL_MQ_HOST:$LOCAL_MQ_PORT/$LOCAL_VHOST"
EXTERNAL_AMQP_URI="amqp://$EXTERNAL_USER:$EXTERNAL_PASSORD@$EXTERNAL_MQ_HOST:$EXTERNAL_MQ_PORT/$EXTERNAL_VHOST"

JSON_VAR_QUEUE='
  {
    "src-protocol": "amqp091",
    "dest-protocol": "amqp091",
    "ack-mode": "on-confirm",
    "src-delete-after": "never",
    "src-uri": "'$LOCAL_AMQP_URI'",
    "src-queue": "'$LOCAL_MQ_QUEUE'",
    "dest-uri": "'$EXTERNAL_AMQP_URI'",
    "dest-queue": "'$EXTERNAL_MQ_QUEUE'"
  }'
JSON_VAR_EXCANGE='{
    "src-protocol": "amqp091",
    "dest-protocol": "amqp091",
    "src-uri": "'$LOCAL_AMQP_URI'",
    "src-exchange": "'$LOCAL_MQ_EXCHANE'",
    "src-exchange-key": "",
    "dest-uri": "'$EXTERNAL_AMQP_URI'",
    "dest-exchange": "'$EXTERNAL_MQ_EXCHANE'"
  }'

echo "============================================ JSON_VAR_QUEUE ============================================"
echo "$JSON_VAR_QUEUE"
echo "========================================================================================================"
echo "________________________________________ JSON_VAR_EXCANGE ________________________________________"
echo "$JSON_VAR_EXCANGE"
echo "___________________________________________________________________________________________________"
sudo rabbitmqctl -p vhost_ch set_parameter shovel queue-simple "$JSON_VAR_QUEUE"
##sudo rabbitmqctl -p vhost_ch set_parameter shovel exchange-simple "$JSON_VAR_EXCANGE"
