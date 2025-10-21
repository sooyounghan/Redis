-----
### 패스워드
-----
1. ```>패스워드``` 키워드로 패스워드 지정 가능
   - 패스워드는 1개 이상 지정할 수 있음

2. ```<패스워드``` 키워드로 지정한 패스워드 삭제 가능
3. 기본적으로 패스워드를 지정하지 않으면 유저에 접근할 수 없으나, nopass 권한을 부여하면 유저에는 패스워드 없이 접근 가능
4. 또한, 유저에 nopass 권한을 부여하면 기존 유저에 설정되어있는 모두 패스워드도 모두 삭제
5. 유저에 resetpass 권한을 부여하면 유저에 저장된 모든 패스워드가 삭제되며, 이 때 nopass 상태도 없어짐
   - 즉, 유저에 대해 resetpass 키워드를 사용하면 추가로 다른 패스워드나 nopass 권한을 부여하기 전까지 그 유저에 접근 불가
<div align="center">
<img src="https://github.com/user-attachments/assets/87a66383-990b-4845-9fa4-cd98e77d7a8a">
</div>

-----
### 패스워드 저장 방식
-----
1. ACL를 사용하지 않고 기존 requirepass를 이용해 레디스 인스턴스의 패스워드를 정의했을 때 암호화되지 않은 채로 패스워드가 저장되므로 설정 파일에 접근할 수 있거나, 혹은 CONFIG GET requirepass 커맨드를 이용하면 누구나 패스워드 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/866cdebd-52f3-4418-b0b0-a64b90328d4d">
</div>

2. 하지만 ACL를 이용해 패스워드를 저장하면 내부적으로 SHA256 방식으로 암호화 되어 저장되므로 유저의 정보를 확인하고자해도,  패스워드 정보를 바로 조회할 수 없음
<div align="center">
<img src="https://github.com/user-attachments/assets/bc057457-5b9a-4e9a-b23e-de3f87322338">
</div>

3. 다른 사용자가 레디스의 패스워드를 예측할 수 없도록 복잡한 패스워드를 사용하는 것이 좋음
   - ACL GENPASS 커맨드를 이용하면 난수 생성 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/57cd132a-5552-4b9e-95cf-d1951f056b0a">
</div>

