-----
### Sorted Set
-----
1. Sorted Set 자료 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/276571d9-1da9-4f78-ae85-f1fd2d86d6db">
</div>

   - 레디스에서 Sorted Set은 스코어(Score) 값에 따라 정렬되는 고유한 문자열의 집합
   - 모든 아이템은 스코어-값 쌍을 가지며, 저장될 때부터 스코어 값으로 정렬되어 저장
   - 같은 스코어를 가진 아이템은 데이터의 사전 순으로 정렬되어 저장

2. 데이터는 중복없이 유일하게 저장되므로 set과 유사하다고 볼 수 있으며, 각 아이템은 스코어라는 데이터에 연결되어 있어 hash와 유사
   - 또한 모든 아이템은 스코어 순으로 정렬되어 있어, list처럼 인덱스를 이용해 각 아이템에 접근 가능

3. 참고
   - list와 sorted set 모두 순서를 갖는 자료 구조이므로 인덱스를 이용해 아이템에 접근 가능
   - 배열에서 인덱스를 사용하는 것이 더 일반적이므로 레디스에서도 list에서 인덱스를 다루는 것이 더 빠를 것이라고 생각할 수 있지만, 인덱스를 이용해 아이템에 접근할 일이 많다면 list가 아닌 sorted set을 사용하는 것이 더 효율적
   - list에서 인덱스를 이용해 데이터를 접근하는 것은 O(n)으로 처리
   - sorted set에서는 O(log(n))으로 처리

4. ZADD 커맨드 : sorted set에 아이템을 저장할 수 있으며, 스코어-값 쌍으로 입력해야 함
   - 한 번에 여러 아이템을 입력할 수 있으며, 각 아이템은 sorted set에 저장되는 동시에 스코어 값으로 정렬
```redis
> ZADD score:220817 100 user:B
(integer) 1

> ZADD score:220817 150 user:A 150 user:C 200 user:F 300 user:E
(integer) 4
```
   - 만약 저장하고자 하는 데이터가 이미 sorted set에 속해있다면, score만 업데이트
   - 업데이트된 스코어에 의해 아이템이 재정렬
   - 지정한 키가 존재하지 않을 때에는 sorted set 자료 구조를 새로 생성
   - 키가 이미 존재하지만, sorted set이 아닐 경우에는 오류를 반환
   - 스코어는 배정밀도 부동소수점 숫자(Double Precision Floating Point Number)를 문자열로 표현한 값이어야 함
   - ZADD 커맨드의 다양한 옵션
     + XX : 아이템이 이미 존재할 때에만 스코어를 업데이트
     + NX : 아이템이 존재하지 않을 때에만 신규 삽입, 기존 아이템의 스코어를 업데이트하지 않음
     + LT : 업데이트하고자 하는 스코어가 기존 아이템의 스코어보다 작을 때에만 업데이트, 기존에 아이템이 존재하지 않을 때에는 새로운 데이터 삽입
     + GT : 업데이트하고자 하는 스코어가 기존 아이템의 스코어보다 클 때에만 업데이트, 기존에 아이템이 존재하지 않을 때에는 새로운 데이터 삽입

5. ZRANGE 커맨드 : sorted set에 저장된 데이터를 조회할 수 있으며, start와 stop이라는 범위를 항상 입력해야 함
```redis
ZRANGE key start stop [BYSCORE | BYLEX] [REV] [LIMIT offset count] [WITHSCORES]
```
  - 여러 가지 옵션을 이용해 다양한 조건으로 데이터 검색 가능

6. 인덱스로 데이터 조회
<div align="center">
<img src="https://github.com/user-attachments/assets/82067b9b-0cf9-4651-b86b-71bbe874d7a3">
</div>

  - ZRANGE 커맨드는 기본적으로 인덱스를 기반으로 데이터를 조회하므로 start와 stop 인자에는 검색하고자 하는 첫 번째와 마지막 인덱스 전달
  - WITHSCORE 옵션을 사용하면 데이터와 함께 스코어 값이 차례대로 출력
  - REV 옵션을 사용하면 데이터는 역순으로 출력
```redis
> ZRANGE score:220817 1 3 WITHSCORES
1) "user:A"
2) "150"
3) "user:C"
4) "150"
5) "user:F"
6) "200"

> ZRANGE score:220817 1 3 WITHSCORES REV
1) "user:F"
2) "200"
3) "user:C"
4) "150"
5) "user:A"
6) "150"
```
   - list에서와 마찬가지로 음수 인덱스 사용 가능
   - ```ZRANGE <key> 0 -1``` 커맨드는 sorted set에 저장된 모든 데이터를 조회하는 것을 의미

7. 스코어로 데이터 조회
<div align="center">
<img src="https://github.com/user-attachments/assets/c4d74947-2110-4296-be64-5a81e58a5734">
</div>

   - ZRANGE 커맨드에 BYSCORE 옵션을 사용하면 스코어를 이용해 데이터 조회 가능
   - start, stop 인자 값으로는 조회하고자 하는 최소, 최대 스코어를 전달해야 함
   - 전달한 스코어를 포함한 값으로 조회
   - 즉, 다음 예제와 같이 100, 150을 start, stop 값으로 전달했을 경우에는 스코어가 100 이상 150 이하인 값을 조회
```redis
> ZRANGE score:220817 100 150 BYSCORE WITHSCORES
1) "user:B"
2) "100"
3) "user:A"
4) "150"
5) "user:C"
6) "150"
```
   - 인수로 전달하는 스코어에 ( 문자를 추가하면 해당 스코어는 포함하지 않는 값만 조회 가능
```redis
> ZRANGE score:220817 (100 150 BYSCORE WITHSCORES
1) "user:A"
2) "150"
3) "user:C"
4) "150"

> ZRANGE score:220817 100 (150 BYSCORE WITHSCORES
1) "user:B"
2) "100"
```
   - 스코어의 최솟값과 최댓값을 표현하기 위해 infinity를 의미하는 -inf, +inf라는 값을 사용
   - 예제) 스코어가 200보다 큰 모든 값을 출력하는 방법
```redis
> ZRANGE score:220817 200 +inf BYSCORE WITHSCORES
1) "user:F"
2) "200"
3) "user:E"
4) "300"
```

   - 인덱스에서의 ```ZRANGE <key> 0 -1```와 마찬가지로 ```ZRANGE <key> -inf +inf BYSCORE``` 커맨드는 sorted set에 저장된 모든 데이터를 조회하겠다는 것을 의미

   - 스코어를 이용해 아이템을 역순으로 조회하고 싶다면 REV 커맨드를 쓸 수 있음
     + 다만, 최솟값과 최댓값 스코어의 전달 순서는 변경해야 함
```redis
> ZRANGE score:220817 +inf 200 BYSCORE WITHSCORES REV
1) "user:E"
2) "300"
3) "user:F"
4) "200"
```

8. 사전 순으로 데이터 조회
<div align="center">
<img src="https://github.com/user-attachments/assets/5a265980-101e-4b2c-ab8a-3f54eec696bd">
</div>

   - 앞서 sorted set에 데이터를 저장할 때 스코어가 같으면 데이터는 사전 순으로 정렬
   - 이러한 특성을 이용해 스코어가 같을 때 BYLEX 옵션을 사용하면 사전적 순서를 이용해 특정 아이템 조회 가능
```redis
> ZADD mySortedSet 0 apple 0 banana 0 candy 0 dream 0 egg 0 frog
(integer) 6

> ZRANGE mySortedSet:0 (b (f BYLEX
1) "banana"
2) "candy"
3) "dream"
4) "egg"
```
   - start와 stop에는 사전 순으로 비교하기 위한 문자열을 전달해야 하며, 이 때 반드시 (나 [ 문자를 함께 입력해야 함
     + 입력한 문자열을 포함 : [ 문자 사용
     + 입력한 문자열을 포함하지 않을 때 : ( 문자 사용
   - 사전식 문자열의 가장 처음은 - 문자로, 가장 마지막은 + 문자로 대체될 수 있음 : ```ZRANGE <key> - + BYLEX```은 sorted set에 저장된 모든 데이터 조회
   - 문자열은 ASCII 바이트 값에 따라 사전식으로 정렬되므로, 한글 문자열도 이기준에 따라 정렬하거나 사전식으로 검색 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/dce425b2-a250-4f2b-8537-9023e07279b1">
</div>
