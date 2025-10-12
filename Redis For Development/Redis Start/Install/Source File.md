-----
### 소스 파일을 이용해 레디스 설치
-----
1. 다음 커맨드를 이용해 원하는 버전의 레디스 패키지 다운로드
   - 가장 최신 버전을 다운로드하려면 redis-stable.tar.gz 파일 다운로드
```bash
-- 버전 지정
$ wget http://download.redis.io/releases/redis-7.0.8.tar.gz

-- 최신 버전 다운로드
$ wget https://download.redis.io/redis-stable.tar.gz
```

2. 압축 파일을 풀고 빌드
   - 빌드하기 위해서는 gcc 버전 4.6 이상이 필요
```bash
-- gcc 설치
$ yum install -y gcc

-- 압축 파일 해제 후 빌드
$ tar -zxvf redis-7.0.8.tar.gz
$ mv redis-7.0.8 redis
$ cd redis
$ make
```
<div align="center">
<img src="https://github.com/user-attachments/assets/02972591-8ef7-436b-8b0a-17b69f1a7dbd">
</div>

3. make가 끝났다면, 기본 디렉토리 내 bin 디렉토리에 실행 파일을 복사하기 위해 make install 커맨드를 프리픽스 지정과 함께 수행
```bash
$ make PREFIX=/home/centos/redis install
```

4. /home/centos/redis 디렉토리에 bin 디렉토리가 생성되며, 해당 디렉토리 내에는 다음과 같은 파일 생성
```bash
$ls
redis-benchamrk  redis-check-aof  redis-check-rdb  redis-cli  redis-sentinel  redis-server
```

5. 레디스를 포그라운드(Foreground) 모드로 시작
   - redis.conf 파일은 레디스 설정 파일 의미
<div align="center">
<img src="https://github.com/user-attachments/assets/f7d68b71-dbdd-44b2-be0d-8e14ab2dc33d">
</div>

   - 위와 같이 Redis is starting 부터 Ready to accept connections 로그가 실행된다면, 레디스가 정상적으로 실행됨을 의미
   - 다만, 로그에 적힌 내용을 확인해보면 몇 가지 시스템 변수를 변경하라는 안내가 출력
