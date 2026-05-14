#!/bin/bash
mkdir -p /usr/share/nginx/html/images

curl -sL "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=artificial%20intelligence%20neural%20network%20deep%20learning%20technology%20futuristic%20digital%20brain&image_size=landscape_16_9" -o /usr/share/nginx/html/images/channel-ai.jpg
echo "AI image done: $?"

curl -sL "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=big%20data%20analytics%20visualization%20dashboard%20technology%20charts%20graphs&image_size=landscape_16_9" -o /usr/share/nginx/html/images/channel-bigdata.jpg
echo "BigData image done: $?"

curl -sL "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cloud%20computing%20server%20infrastructure%20technology%20data%20center&image_size=landscape_16_9" -o /usr/share/nginx/html/images/channel-cloud.jpg
echo "Cloud image done: $?"

curl -sL "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=internet%20digital%20network%20global%20connectivity%20web%20technology&image_size=landscape_16_9" -o /usr/share/nginx/html/images/channel-internet.jpg
echo "Internet image done: $?"

curl -sL "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=computer%20chip%20semiconductor%20hardware%20technology%20circuit%20board&image_size=landscape_16_9" -o /usr/share/nginx/html/images/channel-hardware.jpg
echo "Hardware image done: $?"

curl -sL "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=startup%20business%20entrepreneurship%20innovation%20team%20meeting&image_size=landscape_16_9" -o /usr/share/nginx/html/images/channel-startup.jpg
echo "Startup image done: $?"

ls -la /usr/share/nginx/html/images/
