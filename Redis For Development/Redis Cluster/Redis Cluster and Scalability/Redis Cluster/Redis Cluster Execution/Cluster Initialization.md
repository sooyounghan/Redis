-----
### 클러스터 초기화
-----
1. redis.conf에서 cluster-enabled 설정을 yes로 변경해 레디스를 클러스터 모드로 변경
```redis
cluster-enabled yes
```

2. 레디스를 클러스터 모드로 변경한 다음 각기 다른 서버 6대에 레디스를 실행
```redis
redis-cli -cluster create [host:post] --cluster-replicas 1
```
   - redis-cli를 이용하면 클러스터 생성 가능
   - --cluster create 옵션 : 새로운 클러스터 생성을 명시
   - 클러스터에 추가할 레디스의 ip:port 쌍을 나열
   - --cluster-replicas 1 옵션 : 각 마스터마다 1개의 복제본을 추가할 것임을 의미
<div align="center">
<img src="https://github.com/user-attachments/assets/f838c6dd-3557-4ed5-9442-7f956dea0814">
</div>

  - 입력한 순서대로 3개의 노드는 마스터, 나머지 노드는 복제본이 되도록 구성될 것이라는 정보 제공
  - 각 마스터별로 어떤 해시슬롯을 할당받게 되는지, 각 마스터 노드에 어떤 복제본이 복제되는지 등의 정보를 알 수 있음
  - yes를 입력하면 다음 단계로 넘어감
<div align="center">
<img src="https://github.com/user-attachments/assets/2e685e36-5331-43da-93dd-3498c62e5d14">
</div>


3. 정상적인 초기화가 완료되면 앞에서와 같이 [OK] All 16384 slots covered. 커맨드가 떨어지며 생성 종료
4. 레디스 클러스터의 구성
<div align="center">
<img src="https://github.com/user-attachments/assets/f248afb9-6d46-48ca-b980-c752438b7171">
</div>

   - 앞선 클러스터 구성 단계에서의 로그로 해시슬롯은 마스터에서만 할당되며, 복제본 노드에는 할당되지 않음
   - 복제본 노드는 마스터 노드와 동일한 데이터를 저장하므로 해시슬롯 내부의 데이터를 동일하게 저장하긴 하지만, 해시슬롯을 할당받진 않음
```redis
slots:[0- 5460] (5461 slots) master <- 마스터 노드
slots: (0 slots} slave <- 복제본 노드
```
