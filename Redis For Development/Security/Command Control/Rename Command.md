-----
### 커맨드 이름 변경
-----
1. rename-command : 레디스에서 특정 커맨드를 다른 이름으로 변경하거나, 커맨드를 비활성화할 수 있는 설정
   - 이 설정을 사용하면 레디스의 커맨드를 커스터마이징하거나 보안 강화에 도움이 됨
   - 이 설정은 redis.conf 파일에서 변경할 수 있으며, 실행 중에는 동적으로 변경할 수 없음
   - 따라서, CONFIG GET 커맨드를 이용해 확인하거나 CONFIG SET 커맨드로 변경하는 것은 불가능
```redis
rename-command CONFIG CONFIG_NEW
```
   - 설정 파일에서 CONFIG 명령어를 CONFIG_NEW로 변경하면, 이후 CONFIG_NEW라는 이름을 사용해 해당 명령어를 실행할 수 있게 됨
```redis
127.0.0.1:6379> CONFIG GET maxmemory
(error) ERR unknown command 'CONFIG', with args beginning with: 'GET'
'maxmemory'

127.0.0.1:6379> CONFIG_NEW GET maxmemory
1) "maxmemory"
2) "963641344"
```

2. redis.conf 파일에 접근할 수 없는 사용자는 변경된 커맨드 이름을 알 수 없어 해당 명령어를 사용할 수 없으며, 변경된 값을 아는 운영자만 새로운 이름으로 커맨드를 실행 가능
```redis
rename-command CONFG ""
```
   - rename-command 커맨드를 빈 문자열로 변경하면 해당 커맨드는 사용할 수 없게 됨

3. 만약 센티널을 사용하고 있다면, 센티널은 레디스 인스턴스를 감시하고 있다가 마스터에 장애가 발생했다고 판단ㄴ하면 직접 레디스로 REPLICAOF, CONFIG 등의 커맨드를 날려 레디스 인스턴스를 제어
   - 만약 rename-command를 이용해 레디스에서 커맨드 이름을 변경했다면, 장애 상황에서 센티널이 전송하는 커맨드를 레디스가 정상적으로 수행할 수 없어 페일오버가 정상적으로 발생하지 않게 됨
   - 따라서, redis.conf에서 변경한 커맨드는 sentinel.conf에서도 변경해야 함
   - 예를 들어, redis.conf에서 다음과 같이 커맨드를 변경했다고 가정
```redis
renmae-command CONFIG "my_config"
renmae-command SHUTDOWN "my_shutdown"
```
   - sentinel.conf에서도 마찬가지로 커맨드 이름을 변경해야 함
```redis
sentinel renmae-command mymaster CONFIG my_config
sentinel renmae-command mymaster SHUTDOWN my_shutdown
```
