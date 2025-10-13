-----
### list
-----
1. 레디스에서 list는 순서를 가지는 문자열의 목록
<div align="center">
<img src="https://github.com/user-attachments/assets/c12ede2f-a5c2-43a9-94ce-9f40f6ce4415">
</div>

   - 하나의 list에는 최대 42억여 개의 아이템을 저장할 수 있음
   - 일반적으로 알고 있는 다른 배열처럼 인덱스를 이용해 데이터에 접근할 수 있으며, 일반적으로 list는 서비스에서 스택과 큐로서 사용

2. LPUSH 커맨드 : list의 왼쪽(Head)에 데이터를 추가
3. RPUSH 커맨드 : list의 오른쪽(Tail)에 데이터 추가
4. LRANGE 커맨드 : list에 들어있는 데이터 조회 가능
```redis
> LPUSH mylist E
(integer) 1

> RPUSH mylist B
(integer) 2

> LPUSH mylist D A C B A
(integer) 7

> LRANGE mylist 0 -1
1) "A"
2) "B"
3) "C"
4) "A"
5) "D"
6) "E"
7) "B"

> LRANGE mylist 0 3
1) "A"
2) "B"
3) "C"
4) "A"
```
   - mylist라는 키에 연결된 list 자료 구조에 데이터를 저장하는 과정
   - 하나의 LPUSH 커맨드로 여러 아이템을 저장하는 것도 가능하며, 이 때 아이템은 나열된 순서대로 하나씩 list에 저장
   - LRANGE는 시작과 끝 아이템의 인덱스를 각각 인수로 받아 출력
     + 이 때, 인덱스는 음수가 될 수 있으며, 가장 오른쪽(Tail)에 있는 아이템의 인덱스는 -1, 그 앞의 인덱스는 -2
     + 위의 예제와 같이 0부터 -1까지 아이템을 출력하는 것 : 전체 데이터를 출력

5. LPOP 커맨드 : list에 저장된 첫 번쨰 아이템을 반환하는 동시에 list에서 삭제
   - 숫자와 함께 사용하면 지정한 숫자만큼 아이템 반복해서 반환
<div align="center">
<img src="https://github.com/user-attachments/assets/5d253eba-13c5-47a9-8ae8-51903f0ee24b">
</div>

```redis
> LPOP mylist
"A"

> LPOP mylist 2
1) "B"
2) "C"
```

6. LTRIM 커맨드 : 시작과 끝 아이템의 인덱스를 인자로 전달받아 지정한 범위에 속하지 않은 아이템은 모두 삭제
   - LPOP과 같이 삭제되는 아이템을 반환하지 않음
<div align="center">
<img src="https://github.com/user-attachments/assets/3ae52c7e-8cea-48ca-acd2-312ba41ec279">
</div>

```redis
> LRANGE mylist 0 -1
1) "A"
2) "D"
3) "E"
4) "B"

> LTRIM mylist 0 1
OK

> LRANGE mylist 0 -1
1) "A"
2) "D"
```

7. LPUSH와 LTRIM 커맨드를 함께 사용하면, 고정된 길이의 큐를 쉽게 유지할 수 있음
   - list에 로그를 저장하는 상황에 대해 가정
     + 로그는 계속해서 쌓이는 데이터이므로 주기적으로 로그 데이터를 삭제해 저장 공간을 확보하는 것이 일반적
     + 레디스의 list에 최대 1,000개 로그 데이터를 보관하고 싶다면, 데이터를 저장할 때 LPUSH와 LTRIM 커맨드를 함께 사용 가능
```redis
LPUSH logdata <data>

LTRIM logdata 0 999
```
<div align="center">
<img src="https://github.com/user-attachments/assets/f6eec6bc-ec62-449a-956b-30a393caf489">
</div>

   - list에 데이터를 저장하면서 매 번 1,000번째 이상의 인덱스를 삭제하는 과정을 보여줌
   - 데이터의 개수가 1,001개가 되기 전까지 1,000번 인덱스가 없으므로 LTRIM 커맨드를 사용해도 아무런 동작이 일어나지 않음
   - 하지만, 데이터가 1,0001개가 되는 순간부터는 1,000번째 인덱스, 즉, 가장 처음 들어온 데이터를 삭제

8. 로그 데이터를 일단 쌓은 뒤 주기적으로 배치 처리를 이용해 삭제하는 것보다 위와 같은 방식으로 삭제하는 것이 효율적
   - 위와 같은 로직에서는 매번 큐의 마지막 데이터만 삭제되기 때문임
   - list에서 tail의 데이터를 삭제하는 작업은 O(1)로 동작하므로, 굉장히 빠르게 처리되며, 배치 처리 시마다 삭제할 데이터를 검색하는 것보다 훨씬 효율적

9. list의 양 끝에 데이터를 넣고 빼는 LPUSH, RPUSH, LPOP, RPOP 커맨드는 O(1)로 처리할 수 있어 매우 빠른 실행이 가능
    - 하지만, 인덱스나 데이터를 이용해 list 중간 데이터에 접근할 때는 O(n)으로 처리되며, list에 저장된 데이터가 늘어남에 따라 성능은 저하

10. LINSERT 커맨드 : 원하는 데이터의 앞이나 뒤에 데이터를 추가 가능
    - 데이터의 앞에 추가하려면 BEFORE 옵션을, 뒤에 추가하려면 AFTER 옵션을 추가하면 됨
    - 만약, 지정한 데이터가 없으면 오류 반환
    - 예) LINSERT 커맨드를 BEFORE 옵션과 함께 B 앞에 E 추가
<div align="center">
<img src="https://github.com/user-attachments/assets/0b3be46e-7ef1-4878-a1d5-2f49023a8baa">
</div>

```redis
> LRANGE mylist 0 -1
1) "A"
2) "B"
3) "C"
4) "D"

> LINSERT mylist BEFORE B E
(integer) 5

> LRANGE mylist 0 -1
1) "A"
2) "E"
3) "B"
4) "C"
5) "D"
```

11. LSET 커맨드 : 지정한 인덱스의 데이터를 신규 입력하는 데이터로 덮어씀
    - 만약 list 범위를 벗어난 인덱스를 입력하면 에러 반환
    - 인덱스 2에 F라는 데이터를 저장하는 예
```redis
> LSET mylist 2 F
OK

> LRANGE mylist 0 -1
1) "A"
2) "E"
3) "F"
4) "C"
5) "D"
```

12. LINDEX 커맨드 : 원하는 인덱스의 데이터 확인 가능
```redis
> LINDEX mylist 3
"C"
```
