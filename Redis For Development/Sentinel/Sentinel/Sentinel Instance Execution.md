-----
### 센티널 인스턴스 실행하기
-----
1. 센티널 배치
<div align="center">
<img src="https://github.com/user-attachments/assets/75fa8362-7019-46cb-8c08-812ddd3d6043">
</div>

   - 2대의 서버 ```192.168.0.11```, ```192.168.0.22```에는 레디스와 센티널을 모두 띄움
   - ```192.168.0.33``` 서버에는 센티널 프로세스만 띄운 구성
   - 쿼럼 값은 2로 구성
   - 모든 레디스 프로세스는 6379 포트를, 센티널 프로세스는 26379 포트를 사용

2. 센티널 프로세스 실행
   - 센티널 프로세스를 실행하기 전 마스터와 복제본 노드 간 복제 연결이 된 상태
   - 복제본 노드에서 다음 커맨드를 실행해 복제 연결 시작 가능
```redis
REPLICAOF 192.168.0.11 6379
```
   - 센티널 프로세스를 띄우기 위해서는 sentinel.conf 라는 별도의 구성 파일이 필요
     + sentinel.conf에 다음과 같은 내용 추가 후, 센티널 프로세스 시작
```redis
PORT 26379
SENTINEL monitor master-test 192.168.0.11 6379 2
```
   - PORT : 센티널 프로세스가 실행될 포트를 의미 (26379 포트 사용한다고 지정)
   - SENTINEL monitor : 모니터링할 마스터의 이름을 지정하고, 마스터에 이름을 부여하며, 쿼럼 값 지정
     + 마스터 이름에는 특수 문자나 공백은 포함될 수 없으며, 알파벳 / 숫자 / . / - / _ 만 사용 가능

3. 센티널은 마스터와 복제본을 포함한 모든 레디스 프로세스를 모니터링하지만, 구성 파일에는 복제본 정보를 입력하지 않아도 됨
   - 센티널 프로세스가 시작하면, 마스터에 연결된 복제본을 자동으로 찾아내는 과정을 거침
   - 위 예제에서는 모니터링 할 이름을 master-test로 지정한 뒤 마스터 정보 기입, 쿼럼은 2로 설정

4. 이제 이 sentinel.conf 파일을 이용해 센티널 인스턴스 시작
   - 센티널 인스턴스를 실행시킬 모든 서버에서 해당 파일을 작성한 뒤, 각 인스턴스를 시작시켜야 함
   - 센티널은 두 가지 방법을 이용해 실행 가능
```redis
# redis-sentinel을 이용하는 방법
redis-sentinel /path/to/sentinel.conf

# redis-server를 이용하는 방법
redis-server /path/to/sentinel.conf --sentinel
```
   - 2개의 방법은 동일하게 동작하며, 모두 명시된 sentinel.conf 파일을 이용해 센티널 인스턴스 시작
   - 지정된 위치에 sentinel.conf 파일이 없거나 해당 경로에 데이터를 쓸 수 없는 경우 인스턴스는 시작되지 않음

5. 레디스 프로세스에 접근할 떄와 같이, 레디스 커맨드라인 클라이언트인 redis-cli를 이용해 센티널 인스턴스에 직접 접근할 수 있음
   - 센티널을 기본 포트인 26379 포트에서 실행시켰다면, 다음과 같은 방법으로 센티널에 접속 가능
```redis
redis-cli -p 26379
```
   - 센티널 인스턴스에 접속하면 센티널이 모니터링하고 있는 마스터와 복제본 노드 정보 그리고 복제본을 함께 모니터링하고 있는 다른 센티널 인스턴스에 대한 정보 확인 가능
   - 레디스 인스턴스가 가지고 있는 데이터는 확인할 수 없음
```redis
SENTINEL master <master-name>
```
   - SENTINEL master 커맨드를 이용하면 원하는 마스터의 IP, 포트, 연결된 복제본의 개수 등 다양한 정보 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/3323d653-bec3-4e47-8e4a-7b38299804b2">
</div>

   - 센티널을 구성한 뒤 위 커맨드를 이용하면 센티널이 정상적으로 구성됐는지 알아볼 수 있음

6. 확인하면 좋을 몇가지 플래그
   - num-other-sentinels 값 : 마스터를 모니터링하고 있는 다른 센티널의 정보를 나타냄
     + 위 예제에서는 해당 값은 2로, 현재 master-test 마스터를 모니터링하고 잇는 다른 센티널 노드가 추가로 2대가 더 존재한다는 것을 인지하고 있음을 의미

   - flags 값 : 마스터의 상태를 나타냄
     + 정상적이지 않다고 판단되면 해당 값이 s_down 또는 o_down 등의 값으로 변경

   - num-slaves 값 : 현재 마스터에 연결된 복제본의 개수
     + 위의 예제에서는 1로, 마스터에 연결된 복제본0이 1개 존재한다는 것을 센티널이 인지하고 있음을 의미

7. SENTINEL replicas 커맨드 : 마스터에 연결된 복제본의 자세한 정보 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/57bfa7f4-4337-4ef8-8200-593a8eaf6884">
</div>

8. SENTINEL sentinels 커맨드 : 마스터에 연결된 복제본의 자세한 정보 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/6590415b-4531-4b31-9677-413ed313da35">
</div>

9. SENTINEL ckquorum 커맨드 : 마스터를 바라보고 있는 센티널 인스턴스가 설정한 쿼럼 값보다 큰 지 확인
    - 예를 들어, 정상 상태 센티널이 3대, 쿼럼이 2일 경우, 센티널 3대 모두 정상이라면 다음과 같은 값 반환
<div align="center">
<img src="https://github.com/user-attachments/assets/5b148dc9-a99f-46a3-9080-33e928bb5ac8">
</div>

   - 1대의 센티널에 문제가 생겨 센티널이 2대가 됐을 경우 : 정상적 센티널 대수가 쿼럼 값이 2 이상이므로, 전체 센티널 구성은 정상적이라 판단 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/dfc41633-b333-4cdf-8610-5290f095ec42">
</div>

   - 만약 이 상황에서 다른 1대의 센티널이 또 비정상적 상태가 되면, 정상적 센티널 수는 1대로, 설정한 쿼럼 값보다 작아지게 됨
     + 이런 상황에서는 레디스 마스터에 장애가 발생해도 쿼럼 이상의 센티널 인스턴스에게 동의를 받을 수 없으므로 비정상적인 센티널 상태
<div align="center">
<img src="https://github.com/user-attachments/assets/7db1b135-95c6-44cb-a705-8ff0587192db">
</div>

   - 정상적인 센티널의 대수가 쿼럼보다 작으므로 마스터 노드에 장애가 발생해도 투표를 진행할 수 없어, 페일오버를 자동으로 실행할 수 없음
