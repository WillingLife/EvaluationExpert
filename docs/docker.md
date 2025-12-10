docker run -d --hostname rabbitmq-expert --name rabbitmq -p 5672:5672 -p 15672:15672 -v rabbitmq-data:/var/lib/rabbitmq rabbitmq:4.2.1-management

docker run -d \
--name expert-mongodb \
-p 27017:27017 \
-v mongo_data:/data/db \
-e MONGO_INITDB_ROOT_USERNAME=admin \
-e MONGO_INITDB_ROOT_PASSWORD=password \
mongo:8.2-noble