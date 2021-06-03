#!/bin/bash

sudo rabbitmqctl -p vhost_ch set_parameter shovel proba-kostya-migration-not-ssl \
'{"src-uri": "amqp://for_ch_root:1@localhost:5672/vhost_ch", "src-queue": "itx_ch_gm_queue_shovel", "dest-uri": "amqp://shovel_user:1@192.168.1.45:5672/vhost_shovel", "dest-queue": "itx_ch_gm_queue_shovel"}'


## amqps://server-name?cacertfile=/path/to/cacert.pem&certfile=/path/to/cert.pem&keyfile=/path/to/key.pem&verify=verify_peer&fail_if_no_peer_cert=true&auth_mechanism=external
## connect to server-name, with SSL and EXTERNAL authentication
