-----
### Redis (Remote Dictionary Server)
-----
1. 고성능의 키-값(Key-Value) 저장소
2. 거대한 맵(Map) 데이터 저장소 형태를 가지고, 데이터를 메모리에 저장하여 빠른 읽기와 쓰기 지원
3. 주로 캐싱, 인증 관리, DB 동시성 제어 등에서 다양한 목적으로 사용
4. 주요 특징
   - Key-Value로 구성된 단순화된 데이터 구조로 SQL 쿼리 사용 불필요
   - 💡 빠른 성능
     + In-Memory NoSQL 데이터베이스로서 빠른 성능 (NoSQL : 관계형 데이터베이스를 사용하지 않음)
       * 기본적으로 DISK에 저장하며, 필요 시 메모리에 캐싱하는 것이므로, RDB보다 훨씬 빠른 성능
       * Redis의 메모리 상 데이터는 주기적으로 스냅샷 DISK에 저장하여 안정성을 높임
     + Key-Value는 구조적으로 해시 테이블을 사용함으로, 매우 빠른 속도로 데이터 검색 가능
       * 해시 테이블을 이용하여 동일 알고리즘으로 암호화한다면, 항상 그 값은 동일하다는 특성 이용
       * 이 값을 Redis의 메모리 주소로 설정하여 저장된 위치를 쉽게 찾기 가능 (시간 복잡도 : O(1))
    
  - 💡 Single Thread 구조로 동시성 이슈가 발생하지 않음 (기본적으로 고성능이므로, 처리 속도가 빠른 것)
    + 일반적인 RDB의 경우 Multi Thread 구조로 구성되어 있어서, 동시성 이슈가 발생할 수 밖에 없음
    + 따라서, 동시성 이슈를 해결하기 위해 사용 (DB 동시성 제어)
  - 윈도우 서버에서는 지원하지 않으며, Linux 서버 및 MacOS 등에서 사용 가능

-----
### Redis 설치
-----
: 설치 및 기본 명령어
   - 윈도우 / MacOS
     + Docker Desktop 설치
     + 터미널 창에서 'docker run -d -p 6379:6379 --name redis-container redis' (6379 : Redis Port)

   - 리눅스에서의 설치
     + sudo apt-get update
     + sudo apt-get install -y redis-server
       * redis-server --version
     + 서버 시작 : sudo systemctl start redis-server

   - 접속
     + Docker로 설치한 접속
       * docker ps : Container ID 확인
       * docker exec -it ```<ContainerID>``` redis-cli : Redis 프로그램 접속 (-it : 명령어 전달, redis-cli : Redis 프로그램에 접속 명령어)

     + 리눅스 : redis-cli
