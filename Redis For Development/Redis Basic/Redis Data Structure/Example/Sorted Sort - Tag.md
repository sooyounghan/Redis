-----
### Sorted Set을 이용한 태그 기능
-----
1. 태그 기능
   - 블로그에 게시물을 작성할 때 태그를 추가할 수 있음
<div align="center">
<img src="https://github.com/user-attachments/assets/ea4985af-dace-44d3-ad70-c83f62633df6">
</div>

   - 관계형 데이터베이스에서 태그 기능을 사용하려면 적어도 2개의 테이블이 추가되어야 함 : 태그 테이블 / 태그-게시물 테이블

2. 레디스에서는 set을 사용하면 간단하게 게시물의 태그 기능 사용 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/2f8446da-b964-4102-9e41-4925ccaba8e0">
</div>

   - 각 포스트가 사용하는 태그를 레디스의 set을 이용해 저장한 내용을 나타냄
<div align="center">
<img src="https://github.com/user-attachments/assets/efc76802-e335-4d6c-8586-3c028dde00ef">
</div>

   - id가 47인 포스트에서 사용하는 태그는 IT, REDIS, DataStore 3개라는 것을 뜻함
   - set에서는 데이터는 중복해서 저장되지 않음
```redis
> SADD post:47:tags IT REDIS DataStore
(integer) 3

> SADD post:22:tags IT Python
(integer) 2
```

3. 태그 기능을 사용하는 이유 중 하나 : 특정 게시물이 어떤 태그와 연관되어 있는지 확인하는 것 뿐만 아니라 특정 태그를 포함한 게시물들만 확인하기 위해서도 존재
   - 데이터를 저장할 때 포스트를 기준으로 하는 set과 태그를 기준으로 하는 set에 각각 데이터를 넣어주면 쉽게 구현 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/efc76802-e335-4d6c-8586-3c028dde00ef">
</div>

   - tag를 기준으로 하는 set의 데이터
      + SMEMBERS 커맨드를 이용해 특정 태그를 갖고 잇는 포스트를 쉽게 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/fa795f06-7fe9-401e-b348-febbaa776c8c">
</div>

```redis
> SADD post:47:tags IT REDIS DataStore
(integer) 3

> SADD post:22:tags IT Python
(integer) 2

> SADD post:53:tags DataStore IT MySQL
(integer) 3

> SADD tag:DataStore:posts 53
(integer) 1

> SADD tag:IT:posts 53
(integer) 1

> SADD tag:MySQL:posts: 53
(integer) 1
```
```redis
> SMEMBERS tag:IT:posts
1) "53"
2) "47"
3) "22"
```

4. SINTER 커맨드를 이용하면, 특정 set의 교집합을 확인할 수 있음
   - 만약, IT와 DataStore 태그를 모두 포함하는 게시물을 확인하고 싶다면 SINTER 커맨드 사용 가능
```redis
> SINTER tag:IT:posts tag:DataStore:posts
1) "47"
2) "53"
```
   - tag:IT:posts와 tag:DataStore:posts 집합의 교집합을 확인함으로써 두 태그를 공통으로 가지고 있는 포스트 ID 확인 가능
   - 만약 이 기능을 관계형 데이터베이스를 이용해 구현하면, 까다로울 수 있음 : 태그 기능을 사용하려면 태그-포스트 관계형 테이블을 만드는 것이 일반적
<div align="center">
<img src="https://github.com/user-attachments/assets/4c9eef25-6142-49eb-86eb-bd7e24a5c54f">
</div>

   - 이 때, 다음과 같은 쿼리를 이용하면 TAG ID 1, 3을 모두 포함하고 있는 모든 포스트 정보 확인 가능
     + 관계형 데이터베이스에서 GROUP BY ~ HAVING 절을 사용하면 검색하는 테이블의 크기에 따라 데이터베이스 자체에 부하 발생시킬 수 있음
  ```sql
SELECT post_id
FROM tag_post
WHERE tag_id IN (1, 3)
GROUP BY post_id
HAVING COUNT(tag_id) <= 2;
```
