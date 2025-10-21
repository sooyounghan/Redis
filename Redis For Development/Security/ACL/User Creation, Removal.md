-----
### 유저의 생성과 삭제
-----
1. ACL SETUSER와 ACL DELUSER 커맨드로 유저를 생성하거나 삭제할 수 있음
```redis
> ACL SETUSER garimoo on >password ~cache:* &+ +@all -@dangerous
OK
```

2. 특정 유저를 확인하고 싶으면 ACL GETUSER 커맨드 사용
<div align="center">
<img src="https://github.com/user-attachments/assets/acf23636-ac15-49c0-a041-c053b18c8acf">
</div>

3. 만약 여기서 생성한 garimoo 유저가 user:로 시작하는 Prefix를 가진 키에도 접근할 수 있는 권한을 부여하고 싶으면, ACL SETUSER를 한 번 더 수행
<div align="center">
<img src="https://github.com/user-attachments/assets/e899fc1e-6724-43b5-a27b-4028762cb5cd">
</div>

4. ACL DELSUER를 이용하면 생성한 유저 삭제 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/8ccf2c67-b50e-46fa-ba31-76e07f96dac4">
</div>

5. 레디스를 설치한 뒤 아무런 패스워드와 유저를 생성하지 않았다면, 다음과 같은 기본 유저가 존재
   - ACL LIST 커맨드를 이용하면 레디스에 생성된 모든 유저 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/45c94e31-e044-485b-a26c-9bc52d1444cd"">
</div>

   - 기본 유저의 특징과 권한
     + 유저 이름 : defualt
     + 유저 상태 : on (활성 상태)
     + 유저 패스워드 : nopass (패스워드 없음)
     + 유저가 접근할 수 있는 키 : ```~*``` (전체 키)
     + 유저가 접근할 수 있는 채널 : ```&*``` (전체 채널)
     + 유저가 접근할 수 있는 커맨드 : ```+@all``` (전체 커맨드)

6. 💡 레디스에서 ACL 규칙은 항상 왼쪽에서 오른쪽으로 적용되므로 권한을 적용하는 순서가 중요
