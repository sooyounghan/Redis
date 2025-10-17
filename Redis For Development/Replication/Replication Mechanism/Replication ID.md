-----
### 복제 ID
-----
1. 모든 레디스 인스턴스는 복제 ID(Replication ID)를 가지고 있음
2. 복제 기능을 사용하지 않는 인스턴스라도 모두 랜덤 스트림 값의 복제 ID를 가짐
   - 복제 ID는 오프셋과 쌍으로 존재
   - 레디스 내부의 데이터가 수정되는 모든 커맨드를 수행할 때마다 오프셋이 증가

3. INFO REPLICATION 커맨드를 사용하면 복제 연결 상태 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/7db8127f-68e0-4944-8c6e-04bdd4f60c63">
</div>

  - 인스턴스의 역할을 마스터
  - 연결된 복제본은 없음
  - 복제 ID는 e3b06d3eba5228894a240a8a9ce3e808dd5ccfd7a
  - 오프셋은 700
  - 이 마스터 노드에 복제본을 연결한 뒤, 다시 INFO REPLICAATION 커맨드 사용해 정보 확인
<div align="center">
<img src="https://github.com/user-attachments/assets/59762478-d27b-43e9-8e04-f2534f617a28">
</div>

   - connected_slaves가 1로 변경됐으며, slave0에 신규 연결된 노드의 정보가 추가된 것 확인 가능
   - 새로 연결된 복제본에서 INFO REPLICATION 커맨드를 사용해 복제 정보를 보면 다음과 같음
<div align="center">
<img src="https://github.com/user-attachments/assets/84b78007-1bfd-4bfd-8e0f-c653590c3807">
</div>

  - 현재 role은 slave이며, 마스터 정보 추가
  - 주목해야 할 점은 master_replid : 복제 연결을 시작하면 복제본의 replication_id는 마스터의 replication_id로 변경
  - 오프셋은 복제본에서 마지막으로 수행된 마스터 오프셋 의미
<div align="center">
<img src="https://github.com/user-attachments/assets/ef32e420-7a37-485a-bc5a-8b878fc50f83">
</div>

   - 복제본 노드는 마스터의 replication_id와 동일한 replication_id를 가짐
   - 현재 마스터 노드의 오프셋은 807, 복사본 노드의 오프셋은 801 : 현재 복제본은 마스터와 정확하게 일치하지 않으며, 802 ~ 807 까지의 커맨드를 수행했을 때, 마스터와 정확히 일치됨을 알 수 있음

3. 레디스에서 replication_id와 오프셋이 같을 때, 두 노드는 정확히 일치된 상태를 의미
   - 이 한 쌍의 정보를 이용해 복제본이 마스터의 어디까지 복제됐는지 파악 가능
   - 위 마스터에 복제본 노드를 하나 더 추가한 뒤, 마스터 노드에서 복제 정보 확인
<div align="center">
<img src="https://github.com/user-attachments/assets/56d2408f-1644-4324-84db-42cd9749985d">
</div>

   - slave0의 오프셋은 901, slave1의 오프셋은 915인 것으로 보아, slave1의 데이터는 마스터와 정확히 동일하지만, slave0의 데이터는 마스터 노드의 데이터를 일부 전달받지 못한 상태
