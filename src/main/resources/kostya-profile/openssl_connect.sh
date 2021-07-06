#!/bin/sh
local_ssl_dir="/home/kmwork/work_kac/kFly/src/main/resources/rabbitmq_keys"
local_cacertfile="$local_ssl_dir/ca_certificate.pem"
local_certfile="$local_ssl_dir/client_certificate.pem"
local_keyfile="$local_ssl_dir/client_key.pem"
local_options="verify=verify_peer&fail_if_no_peer_cert=true&auth_mechanism=external"
local_password=12345678
openssl s_client -connect localhost:5671 -cert $local_certfile -key $local_keyfile -CAfile $local_cacertfile -pass pass:"$local_password"

