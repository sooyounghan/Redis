-----
### Redis Streams
-----
1. Pub / Sub과 다르게 메세지가 저장되어 소비자(Consumer)가 나중에라도 읽을 수 있음 (Kafka와 자료 구조 유사)
2. 실습 예시
   - XADD test_stream * message "Hello, This is a test Message."
     + XADD : Redis Stream에서 데이터를 추가할 떄 사용
     + test_stream : 스트림 이름
     + ```*``` : 메세지고유 ID를 Redis가 자동 생성
```
127.0.0.1:6379> XADD test_stream * message "Hello, This is a test Message."
"1746445553728-0"
```      
   - XREAD BLOCK 20000 STREAMS test_stream $
     + BLOCK 20000 : 최대 20초 (20000ms) 동안 대기
     + ```$``` : 현재 마지막 메세지 이후 새 메세지를 기다림
```
127.0.0.1:6379> XREAD BLOCK 20000 STREAMS test_stream $
1) 1) "test_stream"
   2) 1) 1) "1746445553728-0"
         2) 1) "message"
            2) "Hello, This is a test Message."
(4.36s)
```
```
127.0.0.1:6379> XREAD BLOCK 20000 STREAMS test_stream $
1) 1) "test_stream"
   2) 1) 1) "1746445553728-0"
         2) 1) "message"
            2) "Hello, This is a test Message."
(3.56s)
```
   - keys *로 조회도 가능 : 키 값으로 남아있음 (stream은 하나의 자료 구조로 남아있음)
```
127.0.0.1:6379> keys *
1) "test_stream"
```

   - XRANGE test_stream - +
     + XRANGE : Redis Stream에서 메세지를 조회할 때 사용
     + ```-``` : 시작 범위 (처음부터)
     + ```+``` : 끝 범위 (끝까지)
```
127.0.0.1:6379> XRANGE test_stream - +
1) 1) "1746445553728-0"
   2) 1) "message"
      2) "Hello, This is a test Message."
```

3. 활용
   - 이벤트 기반 시스템 (비동기 프로그래밍) [동기적 처리 = 처리할 때 까지 기다림 / 비동기적 처리 = 처리될 때까지 기다리지 않음]
      + 상품과 주문과 관련하여, 클라이언트가 재고 조회를 요청하게 되면 주문은 상품에게 재고를 조회해서 확인 후 그 결과를 받아 응답 (동기적)
      + 하지만, 상품 주문의 경우에는 클라이언트가 주문을 한 상태에서 주문에서 상품에 대한 재고를 감소시키고, 이를 상품에서 받아 처리한 다음, 다시 주문에게 전달한 후 클라이언트에게 응답하는 동기적 처리를 하게 된다면, 오랜 시간 소요
      + 따라서, 주문 서버는 이러한 주문이 들어왔을 때, 상품에게 상품에 대한 재고를 수정하도록 하는 메세지를 Redis 서버에 발행하고, 클라이언트에게 응답 처리 (비동기적 처리)
      + 이를 구독하고 있는 상품은 이를 구독하여 확인하여 처리한다면 성능이 더 빨라지며, 안정성도 높아짐 (만약, 상품 서버가 다운되더라도 메세지를 넣어놨으므로 다시 복구되면 읽을 수 있음)
      + 하지만 Redis는 기본적으로 In-Memory 기반 데이터베이스이므로 데이터 안정성 확보가 어렵기에 Kafka를 사용 (기능도 훨씬 다양)
      + 즉, 이벤트 기반 비동기 아키텍쳐에서 일반적으로 Kafka 사용
   - 채팅 및 알림 시스템
