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
   - 이벤트 기반 시스템 (비동기 프로그래밍)
   - 채팅 및 알림 시스템
