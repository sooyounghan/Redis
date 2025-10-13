-----
### 레디스에서 키를 관리하는 법
-----
1. 키의 자동 생성과 삭제
   - stream이나 set, sorted set, hash와 같이 하나의 키가 여러 개의 아이템을 가지고 있는 자료 구조에서는 명시적으로 키를 생성하거나 삭제하지 않아도 키는 알아서 생성되고 삭제
   
2. 키의 생성과 삭제의 세 가지 공통적인 규칙
   - 첫 번째 : 키가 존재하지 않을 때 아이템을 넣으면 아이템을 삽입하기 전에 빈 자료구조 생성
```redis
> DEL mylist
(integer) 1

> LPUSH mylist 1 2 3
(integer) 3
```
   - 키가 존재하지 않을 때, LPUSH 커맨드를 사용해 데이터를 입력하면 명시적으로 키를 생성하는 작업을 하지 않아도 mylist라는 이름의 list 자료 구조 생성
   - 저장하고자 하는 키에 다른 자료 구조가 이미 생성되어 있을 때, 아이템을 추가하는 작업은 에러 반환
```redis
> SET hello world
OK

> LPUSH hello 1 2 3
(error) WRONGTYPE Operation against a key holding the wrong kind of value

> TYPE hello
string
```

   - 두 번째 : 모든 아이템을 삭제하면 키도 자동 삭제 (stream은 예외)
```redis
> LPUSH mylist 1 2 3
(integer) 3

> EXISTS mylist
(integer) 1

> LPOP mylist
"3"

> LPOP mylist
"2"

> LPOP mylist
"1"

> EXISTS mylist
(integer) 0
```
   - mylist에 들어간 아이템을 모두 pop해서 삭제시켰을 때에는 mylist라는 키 자체가 없어짐
   - 세 번째 : 키가 없는 상태에서 키 삭제 / 아이템 삭제 / 자료 구조 크기 조회 같은 읽기 전용 커맨드를 수행하면 에러 반환 대신 키가 있으나 아이템이 없는 것 처럼 동작
```redis
> DEL mylist
(integer) 0

> LLEN mylist
(integer) 0

> LPOP mylist
(nil)
```
   - DEL 커맨드로 mylist 키를 지웠지만, LLEN 커맨드로 길이를 확인하고자하면 키가 없음에도 불구하고 에러를 반환하지 않음
   - list 내 아이템을 삭제하는 LPOP 커맨드를 수행할 때도 동일하게 작동
