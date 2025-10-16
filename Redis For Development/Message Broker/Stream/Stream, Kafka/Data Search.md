-----
### 데이터의 조회
-----
1. 카프카와 레디스 stream에서 데이터를 저장하는 방식은 비교적 비슷함
2. 하지만 데이터를 읽어가는 주체, 즉 소비자와 소비자 그룹이 동작하는 방식에서는 분명한 차이가 존재
3. 카프카에서 소비자는 특정 토픽을 실시간으로 리스닝하며, 새롭게 토픽에 저장되는 메세지를 반환받도록 동작하며, --from-begining 옵션을 이용하면 카프카에 저장되어있는 모든 데이터를 처음부터 읽겠다는 것을 뜻함
   - 소비자는 더 이상 토픽에서 읽어올 데이터가 없으면 새로운 이벤트가 토픽에 들어올 때까지 계속 토픽을 리스닝하면서 기다림
```kafka
$ kafa-console-producer --bootstrap-server 127.0.0.1:7000 --topic Email --from-beginning
> "I am first email"
> "I am second email"
```
   - 레디스 stream에서는 데이터를 두 가지 방식으로 읽을 수 있음
     + 첫 번째 : 카프카에서처럼 실시간 처리되는 데이터 리스닝
     + 두 번째 : ID를 이용해 필요한 데이터 검색하는 방식

4. 실시간 리스닝
```redis
XREAD [COUNT count] [BLOCK milliseconds] STREAMS key [key ...] ID [ID ...]
```
   - XREAD 커맨드를 이용하면 실시간으로 stream에 저장된 데이터를 읽어올 수 있음
   - 위의 카프카 예제처럼 Email stream에 저장된 데이터를 처음부터 읽어오고, 새로운 메세지가 들어올 때까지 계속 토핑을 리스닝하면서 기다리도록 하고 싶다면 다음과 같이 설정
```redis
> XREAD BLOCK 0 STREAMS Email 0
```
   - BLOCK 0은 더 이상 stream에서 가져올 데이터가 없더라도 연결을 끊지 않고 계속 stream을 리스닝하라는 의미
   - BLOCK 1000을 입력한다면, 들어오는 데이터가 없더라도 1000ms, 즉 최대 1초 동안 연결을 유지하며 대기하라는 의미
   - STREAMS Email 0이라는 커맨드는 Email이라는 stream에 저장된 데이터 중 ID가 0보다 큰 값을 읽어로난 의미 : 즉, stream에 처음부터 저장된 모든 데이터를 읽어오라는 것
   - 앞선 예제에서 Email이라는 stream에 몇 개의 데이터를 저장했으므로, 위 커맨드를 실행하면 다음과 같은 데이터를 반환한 뒤 대기하며, 만약 stream에 새로운 데이터가 저장되면 그 데이터를 반환
```redis
> XREAD BLOCK 0 STREAMS Email 0
1) 1) "Email"
   2) 1) 1) "1760598988104-0"
         2) 1) "subject"
            2) "first"
            3) "body"
            4) "hello?"
      2) 1) "1760599143895-0"
         2) 1) "subject"
            2) "second"
            3) "body"
            4) "hi?"
```

 5. 만약 커맨드를 실행한 이후 메세지만을 가져오고 싶다면, 0 대신 특수 ID인 $를 입력
    - 이 특수 ID를 전달하면 커맨드가 실행한 이후의 데이터부터 가져옴
    - 즉, $는 stream에 저장된 최대 ID를 의미하는 것
    - 혹은 다음 예제와 같이 직접 ID 값을 지정해서 데이터를 읽어올 수 있음
```redis
> XREAD BLOCK 0 STREAMS Email 1760599143895
1) 1) "1760599143895-0"
   2) 1) "subject"
      2) "second"
      3) "body"
      4) "hi?"
```

5. 특정한 데이터 조회
```redis
XRANGE key start end [COUNT count]
XREVRANGE key end start [COUNT count]
```
  - XRANGE 커맨드를 이용하면 ID를 이용해 원하는 시간대의 데이터 조회 가능
  - stream에 저장된 ID 중 가장 작은 ID 값을 지정하고 싶을 때에는 -, 제일 마지막 ID 값을 지정하고 싶을 때에는 + 기호 사용
  - XREVRANGE는 XRANGE의 역순으로 데이터를 조회하고 싶을 때 사용
  - 예를 들어, Email stream에 저장된 모든 데이터를 가져오고 싶다면 다음과 같이 사용
```redis
> XRANGE Email - +
1) 1) "Email"
   2) 1) 1)"1760598988104-0"
      2) 1) "subject"
         2) "first"
         3) "body"
         4) "hello?"

   2) 1) 1)"1760598988104-0"
      2) 1) "subject"
         2) "first"
         3) "body"
         4) "hello?"
```
   - 이 커맨드는 앞서 XREAD BLOCK 0 STREAMS Email 0 커맨드를 수행했을 때와 같은 결과
   - XREAD 커맨드는 사용했을 때 기존 데이터를 모두 반환한 뒤, 신규로 들어온 메세지를 계속해서 반환
   - XRANGE 커맨드는 커맨드를 수행하는 시점에 stream에 저장된 모든 데이터를 반환한 뒤 종료
   - XRANGE를 사용하면 메세지가 저장된 시점을 이용해 데이터 조회 가능
```redis
> XRANGE Email 1760599143890 1760599143899
1) 1) "1760599143895-0"
   2) 1) "subject"
      2) "second"
      3) "body"
      4) "hi?"
```
   - 입력한 타임스탬프를 포함한 데이터를 조회하는데, 만약 입력한 데이터를 포함하지 않고, 그 다음 데이터부터 조회하고 싶다면 입력한 타임스탬프 값에 ( 문자 사용
```redis
> XRANGE Email 1760598988104-0 +
1) 1) "1760598988104-0"
   2) 1) "subject"
      2) "first"
      3) "body"
      4) "hello?"
2) 1) "1760599143895-0"
   2) 1) "subject"
      2) "second"
      3) "body"
      4) "hi?"

> XRANGE Email (1760598988104-0 +
1) 1) "1760599143895-0"
   2) 1) "subject"
      2) "second"
      3) "body"
      4) "hi?"
```
   - LIMIT 옵션을 이용해 조회할 데이터 개수 제한도 가능
