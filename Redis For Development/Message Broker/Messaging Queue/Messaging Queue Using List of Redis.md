-----
### 레디스의 list를 메세징 큐로 사용하기
-----
1. 레디스의 자료 구조 중 하나인 list는 큐로 사용하기 적절한 자료 구조
2. 레디스에서는 큐의 tail과 head에 데이터를 넣고 뺄 수 있는 LPUSH, LPOP, RPUSH, RPOP 커맨드가 존재하므로 애플리케이션 특성에 맞는 메세징 큐를 직접 구현할 수 있다는 장점 존재

3. list의 EX 기능
   - 인스타그램, 트위터, 페이스북, 유튜브와 같은 SNS에는 각 유저별 타임라인이 존재하며, 각자의 타임라인에 자신이 팔로우한 계정의 게시물, 혹은 자신과 관련 있는 게시물 등 표시
   - 모든 유저는 즉, 제각기 다른 타임라인을 가짐
   - 트위터는 각 유저의 타임라인 캐시 데이터를 레디스에서 list 자료 구조로 관리
<div align="center">
<img src="https://github.com/user-attachments/assets/c84aaecd-93fb-4b88-8b22-ee882d72e77a">
</div>

   - 유저 A가 새로운 트윗을 작성하면, 그 데이터는 A를 팔로우하는 유저의 타임라인 캐시에 저장
   - A를 팔로우하는 유저 B의 타임라인 캐시는 위와 같음
   - A가 쓴 트윗의 데이터는 유저 B와 C의 타임라인 캐시 list에 새로운 아이템으로 추가
   - 이 때, 각 타임라인 캐시에 데이터를 저장할 때 RPUSH 커맨드가 아닌 RPUSHX 커맨드를 사용
     + RPUSHX는 데이터를 저장하고자 하는 list가 이미 존재할 때만 아이템을 추가하는 커맨드
     + 이 커맨드를 이용하면 이미 캐시된 (이미 키가 존재하는) 타임라인에만 데이터를 추가할 수 있음
     + 자주 들어오지 않는 D 유저에 대해서 타임라인 캐시를 굳이 관리해야 할 필요가 없기 때문임
```redis
> RPUSHX Timelinecache:userB data3
(integer) 26
> RPUSHX Timelinecache:userC data3
(integer) 5
> RPUSHX Timelinecache:userD data3
(integer) 0
```
   - 사용자의 캐시가 이미 존재하는지의 유무를 애플리케이션에서 확인하는 과정 없이, 모든 로직을 레디스에서만 제어할 수 있으므로 불필요한 확인 과정을 줄여 성능을 향상시킬 수 있음

4. list의 블로킹 기능
   - 레디스를 이벤트 큐로 사용할 경우 블로킹 기능 또한 유용하게 사용 가능
   - 이벤트 기반(Event-Driven) 구조에서 시스템은 이벤트 루프를 돌며, 신규로 처리할 이벤트가 있는지 확인
<div align="center">
<img src="https://github.com/user-attachments/assets/bb4010eb-ca10-4b2b-a4d3-ab4f4fb39d2a">
</div>

   - 이벤트 루프는 이벤트 큐에 새 이벤트가 있는지 체크하며, 새로운 이벤트가 없을 경우 정해진 시간(폴링 인터벌, Polling Interval) 동안 대기한 뒤 다시 이벤트 큐에 데이터가 있는지 확인하는 과정 반복하는데, 이를 폴링(Polling)이라고 함
   - 폴링 프로세스가 진행되는 동안 애플리케이션과 큐의 리소스가 불필요하게 소모될 수 있으며, 또한, 이벤트 큐에 이벤트가 들어왔을 수 있지만, 폴링 인터벌 시간 동안은 대기한 뒤 다시 확인하는 과정을 거쳐야 하므로 이벤트는 즉시 처리할 수 없는 단점 존재
   - 이 때, list의 블로킹 기능을 사용하면 이와 같은 불편함을 줄일 수 있음
     + BRPOP과 BLPOP은 각각 RPOP과 LPOP에 블로킹을 추가한 커맨드
     + 클라이언트가 BLPOP을 사용해 데이터를 요청했을 때, list에 데이터가 있으면 즉시 반환
     + 만약, 데이터가 없을 경우 list에 데이터가 들어올 때까지 기다린 후 들어온 값을 반환하거나, 클라이언트가 설정한 타임아웃시간만큼 대기한 후 nil 값을 반환
```redis
> BRPOP quque:a 5
```
   - quque:a에 데이터가 입력될 때까지 최대 5초 동안 대기하고, 5초가 경과하면 nil을 반환하라는 의미
   - 타임아웃 값을 0으로 설정하면 데이터가 리스트에 들어올 때까지 제한 없이 기다리라는 의미로 사용
   - 하나의 리스트에 대해 여러 클라이언트가 동시에 블로킹될 수 있으며, 리스트에 데이터가 입력되면 가장 먼저 요청을 보낸 클라이언트가 데이터를 가져감
   - BRPOP은 RPOP과 다르게 2개의 데이터를 반환
     + 첫 번째는 팝된 리스트의 키 값을 반환
     + 두 번째에 반환된 데이터의 값을 반환
     + 이렇게 설계된 이유는 동시에 여러 개의 리스트를 대기할 수 있게 하기 위함
<div align="center">
<img src="https://github.com/user-attachments/assets/2a5edc3c-194d-4f6a-bce8-bbe41fc3303c">
</div>

   - BRPOP은 1,000초 동안 quque:a, queue:b, quque:c 중 어느 하나라도 데이터가 들어올 때까지 기다린 뒤, 그 중 하나의 리스트에 데이터가 들어오면 해당 값을 읽어옴
```redis
> BRPOP queue:a queue:b queue:c timeout 1000
1) "queue:b"
2) "DATA"
(19.89s)
```
   - 클라이언트는 19.89초 동안 세 리스트에 입력된느 것을 기다리다가 queue:b에 신규로 들어온 DATA라는 값을 반환받음

5. list를 이용한 원형 큐
   - 만약 특정 아이템을 계속해서 반복 접근해야 하는 클라이언트, 혹은 여러 개의 클라이언트가 병렬적으로 같은 이이템에 접근해야 하는 클라이언트에서는 원형 큐(Circular Queue)를 이용해 아이템을 처리하고 싶을 수 있음
   - list에서 RPOPLPUSH 커맨드를 사용하면 간편하게 원형 큐 사용 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/7bc5d292-f9b0-447c-abd2-42d896af01f6">
</div>

```redis
> LPUSH clist A
(integer) 1

> LPUSH clist B
(integer) 2

> LPUSH clist C
(integer) 3

> LRANGE clist 0 -1
1) "C"
2) "B"
3) "A"

> RPOPLPUSH clist clist
"A"

> LRANGE clist 0 -1
1) "A"
2) "C"
3) "B"
```
