-----
### AOF 방식의 데이터 백업
-----
1. AOF는 레디스 인스턴스에서 수행된 모든 쓰기 작업 로그를 차례로 기록
   - 실수로 FLUSHALL 커맨드로 데이터를 모두 날려버렸다 하더라도, AOF 파일을 직접 열어 FLUSHALL 커맨드만 삭제한 뒤 레디스를 재시작시킨다면 커맨드를 실행하기 직전까지 데이터 바로 복구 가능
   
2. 설정 파일에서 appendonly 옵션을 yes로 설정하면 AOF 파일에 주기적 데이터 저장
   - AOF 파일은 appenddirname에서 지정한 경로와 appenddirname에서 지정한 경로와 appendfilename 옵션에 설정한 이름으로 생성
```redis
appendonly yes
appendfilename "appendonly.aof"
appenddirname "appendonlydir"
```

   - appendfilename 옵션을 이용해 AOF 파일명을 변경하지 않는다면, 기본적으로 AOF 파일은 appendonly.aof라는 이름으로 저장
   - 버전 7.0 이상부터는 AOF 파일은 여러 개로 저장되며, 이는 appenddirname 옵션에서 지정된 디렉토리 하위에 저장
   - appenddirname 옵션에는 경로가 아닌 디렉토리 이름만 지정할 수 있으며, dir 옵션 하위에 생성

3. 예제
```redis
> SET key1 apple
OK

> SET key1 beer
OK

> DEL key1
(integer) 1

> DEL non_existing_key
(integer) 0
```
   - 1 ~ 3번째 커맨드는 레디스 서버의 메모리에 영향을 끼치는 작업
   - 4번째 커맨드는 존재하지 않는 키를 삭제하는 작업이므로 메모리가 수정되는 작업이 아님
   - AOF 파일에는 메모리 상의 데이터가 변경되는 커맨드만 기록되므로 마지막 DEL non_existing_key 작업은 기록되지 않음

4. AOF에서 모든 커맨드의 실행 내역은 레디스 프로토콜(RESP) 형식으로 저장
```
*3
$3
SET
$4
key1
$5
apple
*3
$3
SET
$4
key1
$5
beer
*2
$3
DEL
$4
key1
```
   - 하지만 항상 AOF 파일이 사용자가 실행한 커맨드 그대로 저장되는 것은 아님
   - 예를 들어, list에 블로킹 기능을 지원하는 BRPOP 커맨드는 AOF 파일에 저장될 때 RPOP으로 기록 : AOF 파일에서 블로킹 기능을 굳이 명시해줄 필요가 없기 때문임
```redis
> RPUSH mylist a b c d e
(integer) 5

> BRPOP mylist 1
1) "mylist"
2) "e"
```

   - 위 커맨드가 실행됐을 때 AOF 파일
```
*7
$5
RPUSH
$6
mylist
$1
a
$1
b
$1
c
$1
d
$1
e
*2
$4
RPOP
$6
mylist
```

   - 기존 string 값에 사용자가 입력한 부동소수점 값을 더해주는 INCRBYFLOAT 커맨드도 AOF 파일에 그대로 기록되지 않음
```redis
> SET counter 100
OK

> INCRBYFLOAT counter 50
"150"
```
  - 레디스가 실행되는 아키텍쳐에 따라 부동소수점을 처리하는 방식이 다를 수 있으므로 AOF 파일에 증분 후 값을 직접 SET하는 커맨드로 변경되어 처리
```
*3
$3
SET
$7
counter
$3
100
*4
$3
SET
$7
counter
$3
150
$7
KEEPTTL
```

5. AOF는 Append-Only File이라는 뜻 이름 그대로 실행되는 커맨드가 파일 뒤쪽에 계속 추가되는 방식으로 동작
   - 따라서 인스턴스가 실행되는 시간에 비례해서 AOF 파일 크기는 계속 증가
   - INCR 커맨드를 사용해 counter 키를 100번 증가시킨다면, 실제 레디스 메모리에서 counter 라는 키는 100이 증가된 값을 저장
   - AOF 파일에는 키를 증가시킨 100번의 실행 내역이 그대로 남아있게 됨
