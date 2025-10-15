-----
### 좋아요 처리하기
-----
1. 포털 사이트 뉴스 댓글에 좋아요를 누를 수 있는 기능을 추가한다고 가정
2. 실시간 트래픽이 굉장히 많은 사이트라면 하나의 뉴스 댓글에 좋아요가 누르는 일이 다수 발생
   - 관계형 데이베이스의 경우 테이블의 특정 행에서 좋아요 개수 데이터를 증가시키는 일은 데이터베이스에 직접적 영향을 끼칠 수 있음
   - 또한, 하나의 유저는 같은 댓글에 한 번씩 좋아요루르 누를 수 있어야 하므로 단순히 좋아요의 개수를 파악하는 것이 아닌, 어떤 유저가 어떤 댓글에 좋아요를 눌렀는지 데이터 또한 처리할 수 있어야 함

3. 이를 구현하기 위해 레디스의 set을 간단히 활용
<div align="center">
<img src="https://github.com/user-attachments/assets/5f635c28-2bc4-4a94-9c5f-498bad1b07fc">
</div>

   - 댓글 ID를 기준으로 set을 생성한 뒤, 좋아요를 누른 유저의 ID를 set에 저장하면 중복 없이 데이터 저장 가능
   - 예를 들어, 댓글 ID 12554에 좋아요를 누른 유저는 345, 25, 967
   - 각 댓글별로 좋아요를 누른 수는 SCARD 커맨드로 확인 가능
```sql
> SADD comment-like:12554 967
(integer) 1

> SCARD comment-like:12554
(integer) 1

> SADD comment-like:12554 345
(integer) 1

> SADD comment-like:12554 25
(integer) 1

> SCARD comment-like:12554
(integer) 3
```
