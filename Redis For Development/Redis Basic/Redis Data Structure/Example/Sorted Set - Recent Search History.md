-----
### Sorted Set을 이용한 최근 검색 기록
-----
1. 쇼핑몰에서 사용자가 최근에 검색한 내역을 확인할 수 있는 기능
<div align="center">
<img src="https://github.com/user-attachments/assets/6282dbd0-d50c-438b-9d42-c95cb6786afe">
</div>

2. 관계형 데이터베이스의 경우
   - 요구사항
     + 유저별로 다른 키워드 노출
     + 검색 내역은 중복 제거
     + 가장 최근 검색한 5개의 키워드만 사용자에게 노출
   - 쿼리문 (유저가 최근에 검색했던 테이블에서 최근 5개의 데이터 조회)
```sql
SELECT *
FROM keyword
WHERE user_id = 123
ORDER BY reg_date DESC
LIMIT 5;
```
   - 테이블에 데이터를 저장할 때에는 기존에 사용자가 같은 키워드를 검색했었는지 확인한 뒤 업데이트하는 작업을 추가해야 함
   - 또한, 테이블에 데이터가 무기한으로 쌓이는 것을 방지하기 위해 주기적으로 배치 작업을 돌려 오래된 검색 기록은 삭제하는 작업이 필요
   - 데이터를 가져올 때는 검색한 시점을 기준으로 Sorting을 해야하므로 사용자와 검색 기록이 늘어날수록 많은 데이터를 테이블에서 관리해야 하는 문제 발생

3. Sorted Set은 set이므로 저장할 때부터 중복을 허용하지 않으며, 스코어로 시간을 사용한다면 검색 기록으로 정렬될 수 있음
   - 예) user_id가 123인 유저의 검색 기록을 search-keyword:123이라는 키에 sorted set으로 저장한 내용
<div align="center">
<img src="https://github.com/user-attachments/assets/eaa3986f-320d-4e38-a0c1-7e6164e7323d">
</div>

   - 데이터를 저장할 때 유저가 검색한 시간을 스코어로 지정하면, 검색 시간 순으로 정렬된 데이터 저장
```redis
> ZADD search-keyword:123 20221106143501 코듀로이
(integer) 1
```
   - ZREVRANGE 커맨드를 이용해 가장 최근에 검색한 순서대로 데이터를 가져올 수 있으며, 인덱스를 지정해 최근 5개 데이터만 조회 가능
```redis
> ZADD search-keyword:123 20221106143501 코듀로이
(integer) 1
> ZADD search-keyword:123 20221106152734 기모후드
(integer) 1
> ZADD search-keyword:123 20221105221002 반지갑
(integer) 1
> ZADD search-keyword:123 20221105220954 에나멜
(integer) 1
>  ZADD search-keyword:123 20221105220913 실버
(integer) 1
```
```redis
127.0.0.1:6379> ZREVRANGE search-keyword:123 0 4 WITHSCORES
 1) "기모후드"
 2) "20221106152734"
 3) "코듀로이"
 4) "20221106143501"
 5) "반지갑"
 6) "20221105221002"
 7) "에나멜"
 8) "20221105220954"
 9) "실버"
10) "20221105220913"
```

4. sorted set을 이용해 데이터를 저장하므로 같은 키워드를 다시 검색했을 때 별다른 중복 체크를 진행하지 않아도 됨
   - 각 아이템은 중복되지 않게 저장되므로 같은 아이템의 데이터를 입력한다면, 자동으로 스코어만 업데이트 되어 재정렬
<div align="center">
<img src="https://github.com/user-attachments/assets/51b39df1-a726-45c2-bbb4-79b5ca630ce1">
</div>

```redis
> ZADD search-keyword:123 20221106160104 반지갑
(integer) 0
```
```redis
127.0.0.1:6379> ZREVRANGE search-keyword:123 0 -1 WITHSCORES
 1) "반지갑"
 2) "20221106160104"
 3) "기모후드"
 4) "20221106152734"
 5) "코듀로이"
 6) "20221106143501"
 7) "에나멜"
 8) "20221105220954"
 9) "실버"
10) "20221105220913"
```
   - 관계형 데이터베이스를 이용해 해당 기능을 개발했다면 주기적으로 테이블의 데이터를 삭제하는 배치 작업을 통해 특정 일자 이전 데이터를 삭제하는 작업을 추가하거나, 유저별로 최근 5개 검색어를 제외하고 삭제하는 작업을 진행해야 함
   - sorted set을 이용하면 이런 작업을 좀 더 단순화 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/42397198-895f-4c12-84d9-973a20b47238">
</div>

   - 데이터는 시간 순으로 정렬되어 가장 오래된 데이터를 지우기 위해서는 데이터가 6개째 저장될 때, 가장 오래된 데이터인 0번 인덱스 데이터를 삭제하면 됨
   - 하지만, 매번 데이터를 저장할 때 아이템 개수를 확인해야 하는 번거로움 존재
   - 이 때, sorted set의 음수 인덱스를 사용해 데이터를 삭제한다면, 번거로운 작업을 줄일 수 있음 : 음수 인덱스는 아이템의 제일 마지막 값을 -1로 시작해서 역순으로 증가하는 값
     + 데이터가 6개 저장됐을 때 가장 오래 전 저장된 데이터는 일반 인덱스로 0, 음수 인덱스로 -6이 됨
     + -6번 인덱스를 삭제하는 것은 0번 인덱스를 삭제하는 것과 동일한 작업을 의미
<div align="center">
<img src="https://github.com/user-attachments/assets/782ffb8e-260d-442e-b127-f2e5b23787a7">
</div>

   - 항상 ZADD로 데이터를 저장할 때마다 음수 인덱스 -6번째를 삭제하는 로직을 추가하면, 유저별 아이템 개수를 확인하지 않더라도 5개 이상 데이터가 저장되지 않도록 강제할 수 있음
```redis
> ZADD search-keyword:123 20221106165302 버킷햇
(integer) 1

> ZREMRANGEBYRANK search-keyword:123 -6 -6
(integer) 1
```
   - ZREMRANGEBYRANK KEY start stop 커맨드 : 인덱스 범위로 아이템을 삭제할 수 있음
     + 따라서 정확히 -6번째 인덱스의 데이터만 지우기 위해 -6부터 -6까지 지정해 처리 가능
     + 만약, 아이템의 개수가 5개보다 많지 않을 때에는 -6번째 인덱스는 존재하지 않으므로, 삭제된 데이터가 없어 영향을 주지 않음
    
