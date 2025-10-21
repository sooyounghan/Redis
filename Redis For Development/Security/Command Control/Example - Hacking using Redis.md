-----
### 레디스를 이용한 해킹 사례
-----
1. ```203.0.113.1```이라는 IP 주소를 가진 서버 A에서 레디스를 실행하고, 레디스의 protected-mode 설정값은 no로 지정되어 있으며, 패스워드는 설정되지 않았다고 가정
<div align="center">
<img src="https://github.com/user-attachments/assets/2d675ff7-6ae4-4aed-82be-68ce64f395f2">
</div>

   - 서버 B에서 서버 A의 레디스에 접근이 가능한지 확인하기 위해 telnet 커멘드 사용
<div align="center">
<img src="https://github.com/user-attachments/assets/6b186e3e-a052-48f4-8827-fcb81dbe032d">
</div>

   - Telnet 연결이 가능하다는 것을 확인 : 네트워크 통신이 가능하며, 또한 redis-cli를 사용해 패스워드 없이 연결 가능

2. 서버 B에서 SSH 키를 생성하고, 이 키의 데이터를 데시르르 통해 서버 A로 전송해 파일 저장 후, 이 키를 사용해 서버 B에서 서버 A로의 접근을 가능하게 설정
<div align="center">
<img src="https://github.com/user-attachments/assets/60feac1e-5fb8-49bf-a7fd-7cec16206e34">
</div>

   - 서버 B에서 키 생성
<div align="center">
<img src="https://github.com/user-attachments/assets/d8176295-2874-461c-83a2-99310dd8eb8b">
</div>

   - ssh-keyhen을 이용해 생성한 키는 id_rsa.pub 이라는 이름으로 B 서버에 저장
   - 파일의 앞뒤에 공백 문자를 넣어 key.tx라는 텍스트 파일 생성
<div align="center">
<img src="https://github.com/user-attachments/assets/2abfd75c-1c3f-43d7-961b-0d50be0645ab">
</div>

   - 이제 서버 B에서 A의 레디스로 접근해서 레디스의 내용을 삭제한 뒤, 방금 생성한 텍스트 파일을 데이터로 넣어줄 것
   - 다음과 같이 수행하면, B에서 생성한 키의 데이터를 서버 A의 레디스에 삽입 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/e29ac18e-3446-4f78-a358-9a91f30f9798">
</div>

   - 이제 서버 A의 레디스에 직접 접근하기 위해 데이터가 저장되는 경로와 파일명 변경
<div align="center">
<img src="https://github.com/user-attachments/assets/127309ae-020c-4bb1-ace8-1f3f99b9f70e">
</div>

   - dir와 dbfilename 설정값을 변경한 뒤 SAVE 커맨드 사용하면, /home/centos/.ssh 경로에 authorized_keys 파일명으로 RDB 파일 저장

3. 서버 B에서 생성한 SSH 키를 서버 A에 직접 복사하는 대신, 레디스를 이용해 데이터를 간접적으로 전달함으로써, 서버 B에서 생성한 SSH 키를 이용해 서버 A에 직접 접근 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/3569eff7-c46a-4076-8ab0-1f589586515a">
</div>

4. 위와 같은 방법을 이용하면 보안이 취약한 레디스를 이용해 서버에 직접 접근 가능
5. 따라서, 보안 강화를 위해 protect-mode를 yes로 설정하고, 패스워드 설정해 사용하는 것 권장
   - 그러나 패스워드를 사용하지 않고, 레디스 인스턴스를 사용하는 경우, enable-protected-configs 옵션을 local 또는 no로 설정해 외부에서 레디스의 중요 설정 파일을 변경할 수 없도록 하는 것이 좋음
