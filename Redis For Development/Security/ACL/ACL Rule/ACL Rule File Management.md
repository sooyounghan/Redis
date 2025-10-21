-----
### ACL 규칙 파일
-----
1. ACL 규칙은 파일로 관리할 수 있음
2. 기본적으로 일반 설정 파일인 redis.conf에 저장되며, ACL 파일을 따로 관리해 유저 정보만 저장하는 것도 가능
3. 만약 /etc/redis/user.acl 파일로 ACL 파일을 관리하고 싶다면, redis.conf에 다음 커맨드 추가
   - ACL 데이터가 redis.conf에 저장되든, 다른 ACL 파일에 저장되든 저장되는 형태는 동일하며, 다만 저장되는 위치가 달라질 뿐임
```redis
aclfile /etc/redis/users.acl
```

4. ACL 파일을 사용하지 않을 때에는 CONFIG REWRITE 커맨드를 이용해 레디스 모든 설정값과 ACL 룰을 한 번에 redis.conf에 저장 가능
5. 다만 ACL 파일을 따로 관리할 경우 ACL LOAD나 ACL SAVE 커맨드를 이용해 유저 데이터를 레디스로 로드하거나 저장하는 것이 가능해지므로 운영 측면에서 조금 더 유용하게 사용할 수 있음
   - ACL 파일을 따로 사용한다고 지정해뒀을 때 CONFIG REWRITE 커맨드를 사용하면 ACL 정보는 저장되지 않음을 유의
   
