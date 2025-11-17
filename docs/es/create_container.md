```
docker run -d \
  --name elasticsearch-expert \
  --network elastic-expert-net \
  -p 9200:9200 \
  -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  -v esdata:/usr/share/elasticsearch/data \
  -v es-plugins:/usr/share/elasticsearch/plugins \
  -v es-config:/usr/share/elasticsearch/config \
  elasticsearch:9.2.0
```
