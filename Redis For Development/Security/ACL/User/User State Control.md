-----
### 유저 상태 제어
-----
1. 유저의 활성 상태는 on과 off로 제어할 수 있음
   - on : 해당 유저로의 접근 허용
   - on이라는 구문 없이 유저를 생성하면 기본으로 off 상태 유저가 만들어지므로 생성 구문에 on을 명시하거나 ```ACL SETUSER <username> on``` 구문을 추후에 사용해 on 상태로 변경해야 함
<div align="center">
<img src="https://github.com/user-attachments/assets/5c383df3-8ded-40da-adb8-ab73b2bf810c">
</div>

2. 활성 상태였던 유저의 상태를 off로 변경한다면, 더 이상 이 유저로 접근할 수 없지만, 이미 접속해있는 유저의 연결은 여전히 유지
