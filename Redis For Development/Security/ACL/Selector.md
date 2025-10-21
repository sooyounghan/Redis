-----
### 셀렉터
-----
1. 버전 7에서 새로 추가된 개념으로, 좀 더 유연한 ACL 규칙을 위해 도입
```redig
ACL SETUSER loguser ~log:* %R-mail:* %R-sms:*
```
  - 위와 같은 권한이 있을 때, loguser는 mail:* 프리픽스 키에 대한 메타데이터도 가지고 올 수 있음
  - 예를 들어, mail:1 키에 대한 만료 시간이 얼마나 남았는지 알 수 있음
```redis
> TTL mail:1
(integer) 95
```
2. 하지만 loguser라는 유저는 mail:* 프리픽스 커맨드에 대해 다른 읽기 커맨드가 아닌 오직 GET 커맨드만 사용하도록 강제하고 싶을 수 있는데, 이럴 경우 사용할 수 있는 것이 바로 셀렉터
```redis
> ACL SETUSER loguser resetkeys ~log:* (+GET ~mail:*)
```
   - 위 규칙에서 괄호 안에 지정된 것이 바로 셀렉터
   - 위 명령어는 loguser에 정의된 모든 키를 리셋하고(resetkeys), log:에 대한 모든 접근 권한을 부여한 뒤, mail:에 대해 get만 가능하도록 설정하는 것을 의미

3. 위와 같이 설정함으로 인해 loguser는 더 이상 mail: 프리픽스 키에 대해 다른 기능은 사용할 수 없고, 오직 get 커맨드만 수행할 수 있게 됨
```redis
> TTL mail:1
(error) NOPERM this user has no permissions to access one of the keys used
as arguments
```
