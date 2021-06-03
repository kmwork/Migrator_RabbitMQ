#!/bin/bash

rabbitmqctl set_parameter shovel proba-kostya-migration \
'{"src-uri": "amqps://for_ch_root:1@localhost:5671/vhost_ch", "src-queue": "itx_ch_gm_queue_shovel", "dest-uri": "amqps://shovel_user:1@192.168.1.45:5671/vhost_shovel", "dest-queue": "itx_ch_gm_queue_shovel"}'
