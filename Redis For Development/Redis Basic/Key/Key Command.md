-----
### 키와 관련된 커맨드
-----
1. 모든 키에 공통적으로 사용할 수 있는 커맨드

2. 키의 조회
   - EXISTS : 키가 존재하는지 확인하는 커맨드 (존재하면 1, 존재하지 않으면 0을 반환)
```redis
EXISTS key [key ...]
```
```redis
> SET hello world
OK

> EXISTS hello
(integer) 1

> EXISTS world
(integer) 0
```

   - KEYS : 레디스에 저장된 모든 키를 조회하는 커맨드
     + 매칭되는 패턴에 해당하는 모든 키의 list 반환
     + 패턴은 글롭 패턴(Glob Pattern) 스타일로 동작
       * h?llo : hello, hallo가 매칭될 수 있음
       * h*llo : hllo, heeeello가 매칭될 수 있음
       * h[ae]llo : hello, hallo가 매칭될 수 있지만, hillo는 매칭되지 않음
       * h[^e]llo : hallo, hbllo가 매칭될 수 있지만, hello는 매칭되지 않음
       * h[a-b]llo : hallo, hbllo만 매칭될 수 있음
     + 💡 KEYS는 굉장히 위험한 커맨드 : 레디스에 100만개 키가 저장되었다면, 모든 키의 정보를 반환
       * 레디스는 싱글 스레드로 동작하므로 실행 시간이 오래 걸리는 커맨드를 수행하는 동안 다른 클라이언트에서 들어오는 다른 모든 커맨드는 차단
       * 레디스가 KEYS 명령을 수행하기 위해 메모리에 저장된 모든 키를 읽어오는 동안 다른 클라이언트가 수행하는 모든 SET, GET 커맨드는 수행되지 않고 대기
       * 메모리에서 저장된 데이터를 읽어오는 작업은 얼마나 수행될지 예상할 수 없음
       * 레디스는 굉장히 빠른 수행 속도를 가지므로, 수초 내 완료될 수 있지만, 그 수초 간 마스터는 아무 동작을 할 수 없음
       * 다른 클라이언트에서 레디스로 데이터를 저장할 수 없어 그동안 대기열이 늘어날 수 있으며, 모니터링 도구가 마스터 노드로 보낸 Health Check에 응답할 수 없어 의도하지 않은 Fail-Over 발생 가능

```redis
KEYS pattern
```

   - SCAN : KEYS를 대체해 키를 조회할 때 사용할 수 있는 커맨드
     + KEYS : 한 번에 모든 키를 반환하는 커맨드로 잘못 사용하면 문제 발생
     + SCAN 커맨드는 커서를 기반으로 특정 범위의 키만 조회하므로 비교적 안전하게 사용 가능
```redis
SCAN CURSOR [MATCH pattern] [COUNT count] [TYPE type]
```
```redis
> SCAN 0
1) "19"
2)  1) "members"
    2) "Product:234"
    3) "SSet"
    4) "hello"
    5) "a"
    6) "Product:123"
    7) "set:111"
    8) "score:220817"
    9) "myset"
   10) "mybitmap"

> SCAN 19
1) "0"
2) 1) "counter"
   2) "c"
   3) "set:222"
   4) "travel"
   5) "b"
   6) "mySortedSet:0"
```
   - 처음 SCAN 커맨드를 사용해 키를 조회할 때는 커서에 0을 입력
     + 첫 번째로 반환되는 값 : 다음 SCAN 커맨드를 사용할 때 인수로 사용해야 하는 커서 위치
     + 다음으로 반환되는 데이터 : 저장된 키의 list
     + 위 예에서는 19이라는 커서 위치를 이용하면 다음 범위의 키 list를 조회할 수 있음

   - 두 번째 SCAN 커맨드는 다음 커서 값으로 0을 반환 : 레디스에 저장된 모든 키를 반환해서 더 이상 검색할 키가 없음을 의미
     + 클라이언트는 반환되는 첫 번째 인자가 0이 될 때까지 SCAN을 반복적으로 사용해 레디스에 저장된 모든 키를 확인할 수 있음

   - 기본적으로 한 번에 반환되는 키의 개수는 10개 정도지만, COUNT 옵션을 사용하면 이 개수를 조절할 수 있음
     + 하지만, 데이터는 정확하게 지정된 개수만큼 출력되지는 않음 : 레디스는 메모리를 스캔하며 저장된 형상에 따라 몇 개의 키를 더 읽는 것이 효율적이라고 판단되면 1 ~ 2개의 키를 더 읽은 뒤 함께 반환되기도 함
     + 하지만, 마찬가지로 SCAN에서 COUNT 옵션을 너무 크게 설정해 한 번에 반환되는 값이 많아져서 출력에 오랜 시간이 걸리면, 이 작업 또한 서비스에 영향을 줄 수 있음

   - MATCH 옵션을 사용하면 KEYS에서처럼 입력한 패턴에 맞는 키 값을 조회할 수 있음
     + 하지만, 반환되는 값은 사용자가 의도하는 것과는 다를 수 있음
  
   - 예) 레디스에는 key:0부터 key:200까지 키가 저장되어있으며, 11이라는 문자열이 포함된 키를 조회하고 시음
     + KEYS 커맨드로 키를 조회하는 경우에는 한 번에 원하는 키 조회 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/12713801-6359-4b8a-b8b2-f9ab48df5cdf">
</div>

   - 하지만 SCAN 커맨드와 MATCH 옵션을 이용해 키 갑을 조회할 때는 한 번에 패턴에 매칭된 여러 개의 키 값이 반환되지 않음 (적은 수 결과가 반환되거나 혹은 빈 값 반환될 수 있음)
<div align="center">
<img src="https://github.com/user-attachments/assets/7ae5881d-8742-45b5-aeea-b615d8cadd1a">
</div>

   - SCAN 커맨드에서 MATCH 옵션을 사용할 때는 우선 데이터를 필터링 없이 스캔한 다음, 데이터를 반환하기 직전에 필터링하는 방식으로 동작
<div align="center">
<img src="https://github.com/user-attachments/assets/af013a02-37be-41fa-98a0-1448ded662a0">
</div>

   - TYPE 옵션을 이용하면 지정한 타입의 키만 조회 가능
     + 이 또한 MATCH 옵션처럼 사용자에게 반환되기 전에 필터되는 방식으로 동작하므로 타입을 조회하기까지 오래걸릴 수 있음

<div align="center">
<img src="https://github.com/user-attachments/assets/5217df02-7d9a-4dbc-b39e-3ba3e7cfdb9e">
</div>

   - SCAN과 비슷한 커맨드로 SSCAN, HSCAN, ZSCAN이 존재
     + 각각 set, hash, sorted set에서 아이템을 조회하기 위해 사용되는 SMEMBERS, HGETALL, WITHSCORE를 대체해서 서버에 최대한 영향을 끼치지 않고 반복해서 호출할 수 있도록 사용할 수 있는 커맨드
