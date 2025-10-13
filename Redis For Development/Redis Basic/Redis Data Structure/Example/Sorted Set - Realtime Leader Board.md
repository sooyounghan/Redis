-----
### Sorted Set을 이용한 실시간 리더보드
-----
1. 리더보드 : 경쟁자들의 순위와 현재 점수를 보여주는 순위표
   - 주로 게임에서 스코어로 정렬되어, 상위 경쟁자의 순위를 보여주는 용도로 사용
   - 게임 외 서비스에서도 여러 데이터들을 게임화해 리더보드로 나타내는 방식으로 자주 사용
   - 듀오링고(Duolingo)와 같은 학습 애플리케이션에서도 학습 데이터를 바탕으로 리더보드를 만들어 제공하고 있으며, 같은 리그 내 사람들과 순위에 대한 건전한 경쟁을 유도해 참여도를 향상시키고 있음
<div align="center">
<img src="https://github.com/user-attachments/assets/cf5ed1fe-2877-4285-b490-5d5e588febe7">
</div>

2. 리더보드의 유형
   - 절대적 리더보드 (Absolute Leaderboard) : 서비스의 모든 유저를 정렬시켜 상위권의 목록만을 표시
   - 상대적 리더보드 (Relative Leaderboard) : 사용자마다 다른 데이터를 보여줌
     + 사용자의 스코어를 기반으로 그들을 다른 사용자와 비교해 순위를 결정하는 리더보드
     + 사용자가 속한 그룹 내에서 또는 특정 경쟁자와의 스코어 대결에서 상대적 순위를 제공하여, 절대적인 모든 사용자를 대상으로 한 리더보드와 다름
     + 주로 사용자 간 경쟁과 상대적 성과를 강조하는 리더보드 유형으로 사용
   - 상대적 리더보드의 예
<div align="center">
<img src="https://github.com/user-attachments/assets/9f62e2a5-839e-4521-97a5-3fc67fcfafb1">
</div>

   - 첫 번째 리더보드 : 전체 리더보드에서 사용자를 기준으로 인접해있는 다른 유저의 데이터를 보여줌
   - 두 번째 리더보드 : 전체 사용자가 아닌 유저가 속한 특정 그룹 내에서의 순위를 보여줌

3. 리더보드는 기본적으로 사용자의 스코어를 기반으로 데이터를 정렬하는 서비스
   - 사용자의 증가에 따라 가공해야 할 데이터가 몇 배로 증가
   - 또한, 실시간으로 반영해야 하는 데이터로서, 유저의 스코어가 100에서 110으로 변경되면, 이 데이터는 실시간으로 계산되어 자신의 순위가 상승한 것을 바로 확인 가능
  
4. 상대적 리더보드를 사용한다면, 다양한 그룹의 관점에서 데이터를 계산하고 통계를 내야함
   - 주간 리더보드에서는 한 주간의 사용자 스코어를 합산해 순위를 매겨야 하며, 특정 그룹별로 유저의 다른 순위를 계산해야 할 수 있기 때문에, 여러 가지 수학적 계산이 빠르게 수행되어야 함

5. 레디스의 sorted set에서 데이터는 저장될 때부터 정렬되어 삽입
   - 만약 유저 스코어를 sorted score의 가중치로 설정한다면, 스코어 순으로 유저가 정렬되므로 리더보드 데이터를 읽어오기 위해 매번 데이터를 정렬할 필요가 없음

6. 서비스에 일별 리더보드를 도입하기 위해 ```daily-source:<날짜>```를 이용해 sorted set 키를 만들고, 사용자의 스코어를 가중치로 사용해서 데이터 입력
<div align="center">
<img src="https://github.com/user-attachments/assets/62381536-f59e-4651-8020-88b19438fa69">
</div>

   - sorted set에 데이터를 저장할 때는 ZADD 커맨드 사용
```redis
> ZADD daily-score:220817 28 player:286
(integer) 1

> ZADD daily-score:220817 400 player:234
(integer) 1

> ZADD daily-score:220817 45 player:101
(integer) 1

> ZADD daily-score:220817 357 player:24
(integer) 1

> ZADD daily-score:220817 199 player:143
(integer) 1
```
   - 데이터를 저장하더라도 sorted set에는 데이터가 스코어 순으로 정렬
   - ZRANGE 커맨드를 이용하면 스코어로 오름차순 정렬된 데이터 확인 가능
```redis
> ZRANGE daily-score:220817 0 -1 WITHSCORES
 1) "player:286"
 2) "28"
 3) "player:101"
 4) "45"
 5) "player:143"
 6) "199"
 7) "player:24"
 8) "357"
 9) "player:234"
10) "400"
```
   - ZRANGE는 스코어가 낮은 순서로 출력
   - 만약 겡미의 첫 화면으로 오늘 상위 스코어 세 명의 유지만 출력하고 싶다면, ZREVRANGE 커맨드 사용
     + ZERVRANGE 커맨드 : sorted set에 저장된 데이터를 내림차순 반환
```redis
> ZREVRANGE daily-score:220817 0 2 WITHSCORES
1) "player:234"
2) "400"
3) "player:24"
4) "357"
5) "player:143"
6) "199"
```
   - 0번 인덱스인 첫 번째 데이터부터 2번 인덱스인 세 번째 데이터까지 출력 : 스코어가 가장 높은 3개의 데이터 출력

7. 데이터 업데이트
   - 만약 player:286이 게임을 해서 데이터를 업데이트를 해야할 경우 ZADD 커맨드를 이용해 변경
```redis
> ZADD daily-score:220817 200 player:286
(integer) 0
```
   - sorted set은 기본적으로 set이므로 데이터를 중복해서 저장하지 않으며, 같은 아이템을 저장하고자 할 때 스코어가 다르면, 기존 데이터 스코어만 신규 입력한 스코어로 업데이트
     + 스코어가 업데이트되면, 그에 맞춰 데이터 순서도 다시 정렬
<div align="center">
<img src="https://github.com/user-attachments/assets/0a90f117-94e0-40c4-b745-d2ae9c13df0d">
</div>

   - 직접 스코어 값을 지정해서 변경하지 않고도 ZINCRBY 커맨드를 이용해 sorted set 내 스코어를 증감시킬 수 있음
     + ZINCRBY 커맨드는 string에서 INCRBY 커맨드와 비슷하게 동작하며, 아이템의 스코어를 입력한 값만큼 증가시키는 커맨드
```redis
> ZINCRBY daily-score:220817 100 player:24
"457"
```
   - 기존 스코어가 357이었던 아이템 player:24의 스코어를 100 증가시키면 아이템은 457로 변경되어 재정렬

8. 관계형 데이터베이스만을 이용해 실시간 차트 서비스를 구현하는 것은 까다로운 작업
   - 모든 유저의 변경 데이터는 실시간으로 업데이트되어야 하며, 점수별로 데이터를 정렬해서 가져오는 작업 자체가 관계형 데이터베이스에 상당한 부하가 될 수 있음
   - 유저가 증가할수록 계산해야 하는 데이터 크기는 배로 늘어나며, 이에 따른 처리 시간이 점점 길어질 수 있음

9. 랭킹 합산
    - 주간 리더보드를 매주 월요일마다 초기화한다고 가정
    - 관계형 데이터베이스에서 이런 주간 누적 랭킹을 구현하려면, 하나의 테이블에서 일자에 해당하는 데이터를 모두 가져온 뒤 선수별로 합치고, 이를 다시 Sorting하는 작업이 진행되어야 함
    - 레디스에서는 ZUNIONSTORE 커맨드를 사용해 간단하게 구현 가능
      + ZUNIONSTORE 커맨드 : 지정한 키에 연결된 각 아이템의 스코어를 합산하는 커맨드
      + 따라서, 해당하는 일자의 키를 지정하기만 한다면 손쉽게 주간 리더보드 데이터를 얻을 수 있음
<div align="center">
<img src="https://github.com/user-attachments/assets/826e28c6-d93c-40b4-b296-d31cc655b3eb">
</div>

   - ZUNIONSTORE는 ```<생성할 키 이름> <합산할 키 개수> <합산할 키> ...```와 같이 사용 가능
```redis
> ZADD daily-score:220815 100 player:101
(integer) 1

> ZADD daily-score:220815 250 player:24
(integer) 1

> ZADD daily-score:220816 200 player:286
(integer) 1

> ZADD daily-score:220816 350 player:24
(integer) 1

> ZADD daily-score:220816 400 player:234
(integer) 1

> ZADD daily-score:220817 50 player:24
(integer) 1

> ZADD daily-score:220817 100 player:101
(integer) 1

> ZADD daily-score:220817 250 player:234
(integer) 1
```
```redis
127.0.0.1:6379> ZUNIONSTORE weekly-score:2208-3 3 daily-score:220815 daily-score:220816 daily-score:220817
(integer) 4
```
```redis
> ZREVRANGE weekly-score:2208-3 0 -1 WITHSCORES
 1) "player:24"
 2) "650"
 3) "player:234"
 4) "650"
 5) "player:286"
 6) "200"
 7) "player:101"
 8) "150"
```
   - 신규로 생성한 sorted set weekly-score:2208-3을 확인해보면 합산된 데이터 순으로 정렬되어있음을 알 수 있음
   - sorted set은 스코어가 같을 때 사전순으로 정렬되므로, player:234가 player:24보다 우선순위가 더 높으며, ZREVRANGE는 우선순위의 역순으로 보여주는 커맨드이므로 player:24가 더 먼저 출력
   - ZUNIONSTORE를 이용해 데이터를 합칠 때 스코어에 가중치를 줄 수 있음
<div align="center">
<img src="https://github.com/user-attachments/assets/a669776c-1458-4be6-a644-96de330a73b1">
</div>

```redis
> ZUNIONSTORE weekly-score:2208-3 3 daily-score:220815 daily-score:220816 daily-score:220817 weights 1 2 1
(integer) 4

> ZREVRANGE weekly-score:2208-3 0 -1 WITHSCORES
 1) "player:234"
 2) "1050"
 3) "player:24"
 4) "1000"
 5) "player:286"
 6) "400"
 7) "player:101"
 8) "150"
```
   - WEIGHTS 옵션을 이용해 가중치를 줄 수 있음
   - 위 예제에서는 15일 / 16일 / 17일에 각각 1, 2, 1을 곱한 값으로 합산된 랭킹을 구할 수 있음
