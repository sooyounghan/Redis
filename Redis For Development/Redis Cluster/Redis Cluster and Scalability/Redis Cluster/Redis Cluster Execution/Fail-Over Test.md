-----
### 페일오버 테스트
-----
1. 클러스터를 구성하는 작업을 완료했다면, 정상적으로 페일오버가 동작하는지 확인하는 작업을 거치는 것이 좋음
2. 클러스터 내부 노드 간 통신이 정상적으로 이뤄지고 있는지, 일부 노드 간 네트워크 단절은 없는지 등 놓친 부분 확인 가능
3. 커맨드를 이용한 페일오버 발생 (수동 페일오버)
   - 수동으로 페일오버시키려면 페일오버시키고자 하는 마스터에 1개 이상의 복제본이 연결되어 있어야 함
   - 페일오버를 발생시킬 복제본 노드에서 CLUSTER FAILOVER 커맨드를 실행하면 페일오버 발생
   - 현재 구성
<div align="center">
<img src="https://github.com/user-attachments/assets/a036a005-6f4c-4e3c-917f-6e1de804d745">
</div>

   - ```192.168.0.55``` IP의 노드에서 커맨드 수행하여 페일오버 발생
```redis
192.168.0.55:6379> INFO REPLICATION
# Replication
role:slave
master_host:192.168.0.11
master_port:6379
...
192.168.0.55:6379> CLUSTER FAILOVER
OK
```
   - 다시 INFO REPLICATION 커맨드를 이용 복제 연결 상태 확인
```redis
192.168.0.55:6379> INFO REPLICATION
# Replication
role:master
connected_slaves:1
slave0:ip=192.168.0.11,port=6379,state=online,offset=613998,lag=0
```
   - 수동 페일오버가 진행되는 동안 기존 마스터에 연결된 클라이언트는 잠시 블럭됨
   - 페일오버를 시작하기 전 복제 딜레이를 기다린 뒤, 마스터의 복제 오프셋을 복제본이 따라잡는 작업이 완료되면 페일오버가 시작
   - 페일오버가 완료되면 클러스터 정보를 변경하고, 모든 작업이 완료되면 클라이언트는 새로운 마스터로 리다이렉션

4. 마스터 동작을 중지시켜 페일오버 발생 (자동 페일오버)
   - 직접 마스터 노드에 장애를 발생시킨 뒤, 페일오버가 잘 발생하는지 확인함으로써, 마스터 상태가 정상이 아닐 경우, 다른 노드에 이를 인지할 수 있는지 확인 가능
   - 레디스 프로세스를 직접 shutdown
```redis
$ redis-cli -h <master-host> -p <master-port> shutdown
```
   - 클러스터 구조에서 복제본은 redis.conf에 지정한 cluster-node-timeout 시간 동안 마스터에서 응답이 오지 않으면 마스터 상태가 정상적이지 않다고 판단해 페일오버를 트리거
   - 해당 옵션의 기본값은 15,000ms, 즉 15초이므로 복제본은 15초 동안 마스터에서 응답을 받지 못했을 때, 마스터의 상태가 비정상이라 판단해 페일오버 진행
   - 15초가 지난 뒤 CLUSTER NODES 커맨드를 이용해 클러스터 상태 확인
<div align="center">
<img src="https://github.com/user-attachments/assets/923e9c18-9a47-4912-a396-a920dc751947">
</div>
