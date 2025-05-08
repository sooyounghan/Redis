-----
### Pub, Sub 기능
-----
1. Redis를 활용해 메세지를 발행하고 구독하는 서비스
<div align="center">
<img src="https://github.com/user-attachments/assets/beb18ab9-1c9f-43f8-8afd-cdee7c6ab139">
</div>

  - Redis 서버가 존재하고, 웹 서버들이 Redis를 참조하는데, Redis에게 특정 키(Channel)를 기준으로 메세지를 발행(Publish)
  - 또한, 어떤 웹 서버는 Redis를 구독(Subscribe) 할 수 있음
  - 즉, Pub / Sub 기능은 어떠한 웹 서버가 메세지(Channel)를 발행(Publish)했을 때, 이 Redis를 이를 구독(Subscribe)한 채널에게 다른 웹 서버들이 Redis로부터 실시간으로 메세지를 받을 수 있음

2. 특징
   - Redis Pub/Sub 시스템에서 동일한 채널을 여러 구독자가 구독하면, 해당 채널로 발행된 메세지가 모든 구독자에게 발송
   - 💡 한 번 발송된 메세지는 저장되지 않음

3. 실습 예시
   - 터미널 1, 2 : SUBSCRIBE test_channel
```shell
127.0.0.1:6379> SUBSCRIBE test_channel
1) "subscribe"
2) "test_channel"
3) (integer) 1
```
```shell
127.0.0.1:6379> SUBSCRIBE test_channel
1) "subscribe"
2) "test_channel"
3) (integer) 2
```
   - 터미널 3 : PUBLISH test_channel "Hello, This is a test Message."
```shell
127.0.0.1:6379> publish test_channel "Hello, This is a test Message."
(integer) 2
```

  - 터미널 1, 2
```shell
1) "message"
2) "test_channel"
3) "Hello, This is a test Message."
```
```shell
1) "message"
2) "test_channel"
3) "Hello, This is a test Message."
```

4. 활용
   - 기본적으로 채팅과 같은 서비스의 경우 특정 서버에 서비스가 의존적이므로 다수 서버를 운영하면서 채팅 서비스(또는 알림 서비스)를 운영할 때 Pub / Sub 구조로 많이 활용 가능
     + 일반적으로 기업에서는 웹 서버를 1대를 운영하는 것이 아닌 여러 대를 운영
     + 만약, 채팅 서비스를 제공하는데, 웹 서버 1에는 사용자 1의 IP 주소 등의 위치 정보를 저장, 웹 서버 2에는 사용자 2의 IP 주소 등의 위치 정보를 저장한다면, 서로 메세지를 보낼 수 없음
     + 이 때, Redis의 Pub / Sub 기능들이 유용하게 활용 가능
     + 두 웹 서버 사이에 Redis를 위치시키고, 각 웹 서버는 Pub / Sub 기능을 사용해 구독하고 있다면, 사용자 1이 사용자 2에게 메세지를 전송할 때 주소를 가지고 있다면 바로 전송하지만, 없다면 Redis에게 메세지를 발행
       * 즉, 사용자 2 정보를 가지고 있는 서버에게 이 메세지를 발행하여 해당 사용자에게 전송하도록 발행
     + 즉, 메세지가 들어오면 그 메세지를 구독하고 있는 서버들에게 전파하는 목적으로 활용 가능
