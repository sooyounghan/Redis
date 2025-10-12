-----
### 레디스 실행하기
-----
1. 프로세스 시작과 종료
   - 레디스 실행
```powershell
$ bin/redis-server redis.conf
```
   - 레디스 프로세스 종료
```powershell
$ bin/redis-cli shutdown
```

2. 레디스 접속하기
   - 레디스를 설치하면 함께 설치되는 cli(comman line interface)를 이용해 레디스에 접속 가능
   - redis-cli는 bin 디렉토리 내 존재해 bin/redis-cli와 같이 실행시켜야 하지만, PATH를 추가하면 어느 위치에서든 redis-cli에 바로 접근 가능
```powershell
$ export PATH=$PATH:/home/centos/redis/bin
```
   - 레디스 서버에서 다음과 같은 방법으로 접근 가능
```powershell
redis-cli -h <IP주소> -p <port> -a <패스워드>
```
   - IP 주소를 생략할 경우 기본값은 127.0.0.1이며, 포트를 생략할 경우 6379
   - requireness에 패스워드 설정해준 경우 접속 시 -a 옵션을 이용해 패스워드를 함께 입력해주거나, 접속한 뒤 AUTH 커맨드와 함께 패스워드를 입력해야만 정상적으로 레디스 사용 가능
   - redis-cli를 입력해 레디스 접속
```powershell
$ redis-cli
127.0.0.1:6379>
```
   - 대화형 모드로 레디스 접속한 것을 의미
   - 커맨드를 입력하면 레디스 프로세스가 처리한 뒤 응답해주는 방식으롣 ㅗㅇ작
```powershell
127.0.0.1:6379> PING
PONG
```
   - 위와 같은 방식에서는 사용자가 연결을 끊을 때까지 계속 레디스 서버에 접속된 상태 유지
   - 레디스 서버에 특정 커맨드를 수행시킨 뒤 종료하고 싶다면 레디스를 커맨드라인 모드로 사용할 수 있음
```powershell
$ redis-cli PING
PONG
$
```
   - redis-cli는 응답을 반환하고 종료
   - help 옵션을 입력해 redis-cli에 대한 더 자세한 사용 방법을 알 수 있음`
```powershell
$ redis-cli --helop
```

3. 데이터 저장과 조회
   - redis-cli를 이용해 데이터 저장 및 삭제 작업
```powershell
$ redis-cli
127.0.0.1:6379> SET hello world
OK
```
   - 레디스는 Key-Value로 된 저장소
   - 위 커맨드는 hello 라는 키에 world라는 값이 저장되는 것을 나타냄
```powershell
> GET hello
"world"
```
   - GET 커맨드를 이용하면 저장된 키에 대한 값을 확인할 수 있음

4. 애플리케이션에서 레디스에 연결하는 것 또한 간단
   - 다양한 언어에서 레디스로 연결할 수 있는 클라이언트를 제공
   
