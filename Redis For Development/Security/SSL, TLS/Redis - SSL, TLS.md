-----
### 레디스에서 SSL/TLS 사용
-----
1. 기본적으로 레디스에서 SSL/TLS 설정은 비활성화 : SSL/TLS 프로토콜을 사용하기 위해서 레디스를 처음 빌드할 때 다음과 같이 정의
```redis
make BULID_TLS=yes
```

2. 일반적으로 레디스에서 SSL/TLS 프로토콜을 사용할 때에는 레디스 인스턴스와 클라이언트 간 동일한 인증서를 사용
   - 따라서 다음 설정에서 정의한 key, cert, ca-cert 파일은 레디스를 실행할 클라이언트에 동일하게 복사해둬야 함
   - 이미 certification/keys 파일이 준비되어있다는 가정 하 이를 이용해 레디스를 구성하는 방법
     + redis.conf 파일에서 tls-port 값을 추가하면 SSL/TLS 연결을 사용할 것이라는 것을 의미하며, 연결에 필요한 인증 파일들에 대한 정의 필요
```redis
tls-port <포트 번호>
tls-cert-file /path/to/redis.crt
tls-key-file /path/to/redis.key
tls-ca-cert-file /path/to/ca.crt
```
   - 만약 기본 설정인 port와 tls-port 모두 지정했다면, 레디스 인스턴스는 두 가지 설정을 모두 받아들일 수 있음
     + 예를 들어, port를 6379, tls-port를 16379로 설정했다면, 6379는 일반적 통신을, 16379 포트로는 인증서를 기반으로 한 TLS 통신이 가능

3. 만약 보안을 강화하기 위해 인증서 없이는 레디스 인스턴스 접근을 할 수 없도록 막고 싶다면, port 0을 명시해 기본 포트를 비활성화함으로써 SSL/TLS를 사용하지 않고 레디스에 접근할 수 없도록 할 수 있음
4. redis-cli를 이용해 SSL/TLS 프로토콜을 활성화한 인스턴스에 접속할 때에는, 연결 시 다음과 같은 인증서를 입력해야 함
   - 이 때, 저장하는 인증서는 redis.conf에서 지정한 파일과 동일해야 함
<div align="center">
<img src="https://github.com/user-attachments/assets/503a3f29-0b5f-4315-878b-dd4d6b22a24b">
</div>

5. 애플리케이션에서 레디스를 접속할 때도 마찬가지이며, 레디스 인스턴스가 설정한 파일과 동일한 인증서를 클라이언트도 가지고 있어야 함
   - 예) 파이썬으로 레디스에 접속하기 위해 다음과 같이 설정
<div align="center">
<img src="https://github.com/user-attachments/assets/1295c9e9-a774-4f9d-aa52-13d2bc2a42a3">
</div>
