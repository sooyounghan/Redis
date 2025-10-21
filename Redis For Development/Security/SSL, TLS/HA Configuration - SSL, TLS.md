-----
### SSL/TLS를 사용한 HA 구성
-----
1. 복제 구성
   - SSL/TLS를 사용하는 마스터와 TLS 연결을 이용한 복제를 하기 위해서는 복제본도 마스터와 동일하게 다음 설정을 추가
<div align="center">
<img src="https://github.com/user-attachments/assets/a53dfa57-49a8-4255-a74d-5b019f36f0d6">
</div>

   - 기본적으로 tls-replication 값은 no로 설정되어 있는데, 이는 복제본에서 마스터로의 커넥션은 SSL/TLS 연결이 아닌 일반 프로토콜로 연결됨을 의미
   - 복제 서버에서 이 값이 no로 설정되어 있을 경우, 정상적으로 복제 연결을 구성할 수 없으며, 마스터 서버는 다음과 같은 로그를 남
<div align="center">
<img src="https://github.com/user-attachments/assets/82470fe0-ab96-4ea8-ac9b-9a4bc035d9db">
</div>

   - 복제본에서 마스터로 보내는 연결 또한 SSL/TLS 프로토콜을 이용하기 위해서 tls-replication 값을 yes로 설정해야 함

2. 센티널 구성
   - 센티널에서도 SSL/TLS 연결을 사용해 레디스에 접속 가능
   - 복제 연결을 할 때와 마찬가지로, 센티널 구성 파일인 sentinel.conf에 다음 내용 추가
<div align="center">
<img src="https://github.com/user-attachments/assets/649ad07b-4801-4031-80cd-2eb78fc2c359">
</div>

3. 클러스터 구성
   - 클러스터 구성에서 SSL/TLS 연결을 사용하려면 다음과 같이 tls-cluster yes 구문 추가
<div align="center">
<img src="https://github.com/user-attachments/assets/b5e465c9-0294-4596-a0bf-c2dd331f2b1c">
</div>

   - 모든 클러스터 노드 간 연결과 클러스터 버스 통신은 SSL/TLS 프로토콜을 이용해 보호
