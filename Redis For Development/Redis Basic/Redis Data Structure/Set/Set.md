-----
### Set
-----
1. Set 자료 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/3a2f9c7e-bf15-49bc-ad84-20d79363f926">
</div>

   - 레디스에서 set은 정렬되지 않은 문자열의 모음
   - 하나의 set 자료 구조 내에서 아이템은 중복해서 저장되지 않으며, 교집합 / 합집합 / 차집합 등 집합 연산과 관련한 커맨드를 제공하므로 객체 간의 관계를 계산하거나 유일한 원소를 구해야 할 경우 사용

2. SADD 커맨드 : set에 아이템 저장 가능하며, 한 번에 여러 개의 아이템 저장 가능
```redis
> SADD myset A
(integer) 1

> SADD myset A A A C B D D E F F F F G
(integer) 6

> SMEMBERS myset
1) "A"
2) "C"
3) "B"
4) "D"
5) "E"
6) "F"
7) "G"
```
   - SADD 커맨드는 저장되는 실제 아이템 수 반환
   - SMEMBERS 커맨드 : set 자료 구조에 저장된 전체 아이템 출력
     + 데이터를 저장한 순서와 상관 없이 랜덤한 순서로 데이터 출력

3. SREM 커맨드를 이용하면 set에서 원하는 데이터 삭제 가능
4. SPOP 커맨드는 set 내부 아이템 중 랜덤으로 하나의 아이템을 반환하는 동시에, set에서 그 아이템 삭제
```redis
> SREM myset B
(integer) 1

> SPOP myset
"F"
```

5. set에서 합집합은 SUNION, 교집합은 SINTER, 차집합은 SDIFF 커맨드로 수행
<div align="center">
<img src="https://github.com/user-attachments/assets/c408203a-83d6-4f9a-8c40-4039561c8a38">
</div>

```redis
> SADD set:111 A B C D E
(integer) 5

> SADD set:222 D E F G H
(integer) 5

> SINTER set:111 set:222
1) "D"
2) "E"

> SUNION set:111 set:222
1) "A"
2) "B"
3) "C"
4) "D"
5) "E"
6) "F"
7) "G"
8) "H"

> SDIFF set:111 set:222
1) "A"
2) "B"
3) "C"
```
