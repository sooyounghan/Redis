-----
### hash
-----
1. hash 자료 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/630ef6db-610c-45fb-aacd-b4ba849ce781">
</div>

   - 레디스에서 hash는 필드-값 쌍을 가진 아이템의 집합
   - 레디스에서 데이터가 key-value 쌍으로 저장되는 것처럼, 하나의 hash 자료 구조 내에서 아이템은 필드-값 쌍으로 저장
     + 필드는 하나의 hash 내에서 유일하며, 필드와 값 모두 문자열 데이터로 저장

2. hash는 객체를 표현하는 적절한 자료 구조이므로 관계형 데이터베이스 테이블 데이터로 변환하는 것도 간편함
<div align="center">
<img src="https://github.com/user-attachments/assets/400e78d9-6ad4-41f7-88a2-4db27a6c0d2c">
</div>

   - 컬럼이 고정된 관계형 데이터베이스와 달리, hash에서 필드를 추가하는 것은 간단
   - hash에서는 각 아이템마다 다른 필드를 가질 수 있으며, 동적으로 다양한 필드를 추가할 수 있음
   - 관계형 데이터베이스 테이블에 데이터를 저장할 때는 미리 합의된 컬럼 데이터를 저장할 수 밖에 없는데, hash에는 새로운 필드에 데이터를 저장할 수 있으므로 조금 더 유연한 개발 가능
   - 따라서, 같은 객체 데이터를 저장하더라도 서비스 특성을 파악해서 적절한 데이터 저장소를 선택하는 것이 중요

3. HSET 커맨드를 사용하면 hash에 아이템을 저장할 수 있으며, 한 번에 여러 필드-값 쌍을 저장할 수 있음
```redis
> HSET Product:123 Name "Happy Hacking"
(integer) 1

> HSET Product:123 TypeID 35
(integer) 1

> HSET Product:123 Version 2002
(integer) 1

> HSET Product:234 Name "Track Ball" TypeID 32
(integer) 2
```
  
4. hash에 저장된 데이터는 HGET으로 가져올 수 있으며, 이 때 hash 자료 구조의 키와 아이템의 필드를 함께 입력해야 함
   - HMGET 커맨드를 이용하면 하나의 hash 내에서 다양한 필드 값을 가져올 수 있음
   - HGETALL 커맨드는 hash 내 모든 필드-값 쌍을 차례로 반환
```redis
> HGET Product:123 TypeID
"35"

> HMGET Product:234 Name TypeID
1) "Track Ball"
2) "32"

> HGETALL Product:234
1) "Name"
2) "Track Ball"
3) "TypeID"
4) "32"
```
