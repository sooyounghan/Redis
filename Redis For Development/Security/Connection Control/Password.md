-----
### 패스워드
-----
1. 레디스에서는 패스워드를 설정하는 두 가지 방법 사용
   - 노드에 직접 패스워드를 지정하는 방식
   - 버전 6.0에 새롭 추가된 ACL(Access Control List) 기능 사용하는 방식

2. 이전 버전에서는 requirepass를 사용해 레디스 서버에 하나의 패스워드 설정 가능
3. 버전 6부터는 ACL를 사용해 패스워드 설정 권장
   - 하지만, 기존 방식도 여전히 사용 가능
```redis
127.0.0.1:6379> CONFIG SET requirepass password
OK
```
   - requirepass 커맨드를 이용하면 레디스에 기본 패스워드 설정 가능
   - 패스워드는 redis.conf에서 지정한 뒤 실행시키거나, 다음 커맨드를 이용해 운영 중 변경 가능
     + redis-cli를 이용해 패스워드가 설정된 노드에 접속하려면 접속 시 -a 옵션을 이용해 패스워드를 직접 지정할 수 있음
     + 해당 옵션을 사용하지 않고, 접속한 뒤 AUTH 커맨드를 이용해 패스워드 입력 가능
```redis
$ redis-cli -a password
Warining: Using a password with '-a' or '-u' option on the command line
interface may not be safe
```
   - 커맨드라인에서 직접 패스워드를 입력할 경우 안전하지 않을 수 있다는 경고 출력
   - --no-auth-warning 옵션을 사용하면 위 경고가 노출되지 않도록 설정 가능
```redis
$redis-cli
127.0.0.1:6379> PING
(error) NOAUTH Aauthentication required.

127.0.0.1:6379> AUTH password
OK

127.0.0.1:6379> PING
PONG
```
   - 패스워드가 설정되어 있는 인스턴스에 접속한 뒤 인증을 하지 않으면 아무런 커맨드를 사용할 수 없음
   - AUTH 커맨드를 이용해 패스워드를 입력해야 다른 커맨드 사용 가능
