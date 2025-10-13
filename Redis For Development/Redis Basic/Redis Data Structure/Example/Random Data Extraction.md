-----
### 랜덤 데이터 추출
-----
1. 관계형 데이터베이스에서 랜덤 데이터 추출을 사용할 때는 ORDER BY RAND() 함수를 많이 사용
   - 쿼리 결과값을 랜덤하게 정렬하지만, 조건 절에 맞는 모든 행을 읽은 뒤, 임시 테이블에 넣어 정렬한 다음 랜덤으로 limit에 해당할 떄까지 데이터를 추출
   - 데이터가 1만 건 이상일 경우 이와 같은 쿼리는 성능이 나빠지게 되어 굉장히 부하가 많이 가는 방법일 수 있음

2. 레디스를 사용하면 O(1)의 시간 복잡도를 이용해 랜덤한 데이터를 추출할 수 있음
   - RANDOMKEY 커맨드는 레디스에 저장된 전체 키 중 하나를 무작위로 반환
<div align="center">
<img src="https://github.com/user-attachments/assets/6f295898-471e-4da6-b728-7c471d2c71fd">
</div>

   - 레디스에 다음과 같이 5개의 키가 저장된 경우라면, 이 중 무작위로 하나의 키가 반환
   - 하지만 보통 하나의 레디스 인스턴스에 이와 같이 한 가지 종류의 데이터만 저장하지는 않으므로, 이와 같은 랜덤 키 추출은 별로 의미가 없음

3. HRANDFIELD, SRANDMEMBER, ZRANDMEMBER는 각각 hash, set, sorted set에 저장된 아이템 중 랜덤한 아이템을 추출할 수 있음
<div align="center">
<img src="https://github.com/user-attachments/assets/ad730ebf-d2d6-4e8a-bf22-59cf0400416d">
</div>

   - 전체 유저를 user:hash라는 키의 hash 자료 구조에 저장했다고 가정
     + 이 때 필드를 유저의 id, 값을 유저명이라고 저장한다면 위와 같이 저장
```redis
> HSET user:hash Id:4615 Jinnji
(integer) 1

> HRANDFIELD user:hash
"id:4615"
```
   - HRANDFIELD 커맨드를 사용하면 지정한 hash 내 임의로 선택된 하나의 아이템을 추출할 수 있음
   - 이 때, COUNT 옵션을 사용하면 원하는 개수만큼 랜덤 아이템이 반환되며, WITHVALUES 옵션을 사용하면 필드에 연결된 값도 함께 반환 가능
   - 이 때, COUNT 옵션을 양수로 설정하면 중복되지 않는 랜덤 데이터가 반환되고, 음수로 설정하면 데이터가 중복해서 반환될 수 있음
```redis
> HSET user:hash id:4615 Jinnji
(integer) 1

> HSET user:hash id:38274 garimoo
(integer) 1

> HSET user:hash id:134 Ssoorryry
(integer) 1

> HRANDFIELD user:hash 2
1) "id:4615"
2) "id:134"

> HRANDFIELD user:hash -2
1) "id:134"
2) "id:134"
```
   - SRANDMEMBER, ZRANDMEMBER 커맨드도 마찬가지로 COUNT 옵션을 양수로 설정하면 중복되지 않는 데이터가, 음수로 설정하면 중복될 수 있는 랜덤 데이터가 반환
   - WITHSCORE 옵션을 사용하면 필드의 연결된 값도 함께 반환
