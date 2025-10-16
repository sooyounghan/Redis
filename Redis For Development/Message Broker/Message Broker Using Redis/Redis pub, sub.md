-----
### 레디스의 pub / sub
-----
1. 레디스는 아주 가벼운 pub / sub 기능 제공
   - 레디스 노드에 접근할 수 있는 모든 클라이언트는 발행자와 구독자가 될 수 있음
   - 발행자는 특정 채널에 메세지를 보낼 수 있으며, 구독자는 특정 채널을 리스닝하다가 메세지를 읽어갈 수 있음

2. 레디스에서 pub / sub은 매우 가볍기 때문에 최소한의 메세지 전달 기능만 제공
   - 발행자는 메세지를 채널로 보낼 수 있을 뿐, 어떤 구독자가 메세지를 읽어가는지, 정상적으로 모든 구독자에게 메세지가 전달됐는지 확인할 수 없음
   - 구독자 또한 메세지를 받을 수 있지만 해당 메세지가 언제 어떤 발행자에 의해 생성됐는지 등 메타데이터는 알 수 없음
<div align="center">
<img src="https://github.com/user-attachments/assets/f0db8a43-fa96-4f01-a8e1-ad962938ebc8">
</div>

   - 한 번 전파된 데이터는 레디스에 저장되지 않으며, 단순히 메세지 통로 역할을 함
   - 만약, 특정 구독자에 장애가 생겨 메세지를 받지 못했다 하더라도 그 사실을 알 수 없으므로 정합성이 중요한 데이터를 전달하기에 적합하지 않음
   - 이럴 경우, 애플리케이션 레벨에서 메세지의 송수신과 관련한 로직을 추가해야 할 수 있음

3. 메세지 Publish
   - 레디스에서는 PUBLISH 커맨드를 이용해 데이터를 전파할 수 있음
```redis
> PUBLISH hello world
(integer) 0
```
   - 위의 커맨드를 수행하려면 hello라는 채널을 수신하고 있는 모든 서버들에게 world라는 메세지가 전파
   - 메세지가 전파된 후 메세지를 수신한 구독자의 수가 반환

4. 메세지 구독하기
   - SUBSCRIBE 커맨드를 이용하면 특정 채널을 구독할 수 있음
```redis
> SUBSCRIBE event1 event2
Reading messages... (press Ctrl-C to quit or any key to type command)
1) "subscribe"
2) "event1"
3) (integer) 1
1) "subscribe"
2) "event2"
3) (integer) 2
```
   - 클라이언트가 위 커맨드를 수행하면 event1과 event2 채널을 동시에 구독하기 시작
   - 클라이언트가 구독자로 동작할 때는 새로운 채널을 구독할 수 있지만, pub / sub과 관련되지 않은 다른 커맨드는 수행 불가

5. 구독자가 수행할 수 있는 커맨드는 SUBSCRIBE, SSUBSCRIBE, SUNSUBSCRIBE, PSUBSCRIBE, UNSUBSCRIBE, PUNSUBSRCIBE, PING, RESET, QUIT
6. PSUBSRCIBE 커맨드를 사용하면 일치하는 패턴에 해당하는 채널을 한 번에 구독할 수 있으며, 이 때 레디스는 Glob-Style 패턴 지원
```redis
> PSUBSCRIBE mail-*
1) "psubscribe"
2) "mail-*"
3) (integer) 1
Reading messages... (press Ctrl-C to quit or any key to type command)
```
   - PSUBSCRIBE mail-*라는 커맨드를 사용하면 mail-track, mail-album 등 앞 부분이 mail로 시작하는 모든 채널에 전파된 메세지를 모든 수신 가능
   - SUBSCRIBE와 마찬가지로 동시에 여러 문자열을 구독하는 것도 가능
   - 이 때, 메세지는 message가 아닌 pmessage 타입으로 전달되며, SUBSCRIBE 커맨드를 이용해 메세지를 구독하는 방식과 구분
<div align="center">
<img src="https://github.com/user-attachments/assets/3fe5b571-ab14-47e6-85bb-5590d0eae15f">
</div>

   - 만약 구독자가 SUBSCRIBE mail-1과 PSUBSCRIBE mail-*을 동시에 구독하고 있을 때, mail-1 채널에 메세지가 발행되면 구독자는 2개의 메세지를 받게 됨
