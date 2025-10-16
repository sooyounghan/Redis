-----
### 스트림 생성과 데이터 입력
-----
1. 카프카에서 각 스트림은 토픽이라는 이름으로 관리
   - 생성자는 데이터를 토픽에 푸시하며, 소비자는 토픽에서 데이터를 읽어감
   - 카프카에서는 데이터를 저장하기 위해 토픽을 먼저 생성한 뒤, 프로듀서를 이용해 메세지를 보낼 수 있음
```kafka
-- 토픽 생성
$ kafa-topics --zookeeper 127.0.0.1:6000 --topic Email --create partitions
1 --replication-factor 1

-- 데이터 추가
$ kafka-console-consumer --brokers-list 127.0.0.1:7000 --topic Email
> "I am first email"
> "I am second email"
```

2. 레디스에서는 따로 strea을 생성하는 과정은 필요하지 않으며, XADD 커맨드를 이용해 새로운 이름의 stream에 데이터를 저장하면 데이터의 저장과 동시에 stream 자료구조 생성
```redis
> XADD Email * subject "first" body "hello?"
"1760598988104-0"
```
   - 위의 커맨드를 실행하면 Email이라는 이름의 stream이 생성
     + 만약 기존에 같은 이름의 키가 존재한다면 이 커맨드는 기존 stream에 새로운 메세지를 추가
     + 존재하지 않았을 때에는 Email이라는 이름의 키를 가진 새로운 stream 자료 구조 생성
     + 이 때 사용한 * 필드는 저장되는 데이터의 ID를 의미하며, 이 값을 *로 입력할 경우 레디스에서 자동 생성되는 타임스탬프 ID를 사용하겠다는 의미
     + XADD를 사용했을 때 반환되는 값이 바로 저장되는 데이터 ID

   - 메세지는 키-값 쌍으로 저장되며, 위의 예제에서는 subject라는 키에 first 값을, body라는 키에 hello?라는 값이 저장
   - 반환되는 값은 메세지를 유니크하게 식별할 수 있는 ID
```redis
> XADD Push * userid 1000 ttl 3 body Hey
"1760599130831-0"

> XADD Email * subject "second" body "hi?"
"1760599143895-0"
```
   - 첫 번째 커맨드를 수행하면 Push라는 stream 자료 구조가 생성되며, 해당 stream ID가 1760599130831-0인 메세지가 신규로 저장
   - 두 번쨰 커맨드를 수행했을 때, 이미 Email stream이 존재하므로 ID가 1760599143895-0인 새로운 메세지가 Email stream에 저장
   - 레디스 stream에 데이터가 저장된 그림
<div align="center">
<img src="https://github.com/user-attachments/assets/361c57e0-9a8d-4676-a2f6-5e02ea42b5f5">
</div>

   - 데이터는 hash 자료 구조처럼 필드-값 쌍으로 저장되므로 각 메세지마다 유동적인 데이터를 저장할 수 있음

3. 만약, 자동으로 생성되는 ID가 아니라 서비스에서 기존에 사용하던 ID를 이용해 메세지를 구분하고 싶을 수 있는데, 이럴 때에는 ID를 입력하는 필드에 *가 아니라 직접 ID 값을 지정
```redis
> XADD mystream 0-1 "hello" "world"
"0-1"

> XADD mystream 0-2 "hi" "redis"
"0-2"
```
   - 이 경우 지정할 수 있는 최소 ID 값은 0-1이며, 이후 저장되는 stream ID는 이전에 저장됐던 ID 값보다 작은 값으로 지정 불가
