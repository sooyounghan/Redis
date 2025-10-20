-----
### 클러스터 리샤딩
-----
1. 마스터 노드가 가지고 있는 해시슬롯 중 일부를 다른 마스터로 이동하는 것 : 리샤딩(Resharding)
   - 리샤딩은 redis-cli에서 CLUSTER RESHARD 옵션을 이용해 수행
   - 클러스터에서 일부 해시 슬롯 이동
<div align="center">
<img src="https://github.com/user-attachments/assets/3671afcc-3d30-42bf-b8c8-68f90d1356bc">
</div>

   - 이 때, 클러스터에 속한 여러 노드 중 하나의 노드를 지정하면 해당 노드가 속한 클러스터 구조를 파악한 뒤, 다른 연결된 다른 노드의 정보를 찾아와 다음과 같이 보여줌
     + 이 때, 마스터 노드 뿐만 아니라 Replica 노드 중 하나를 지정하더라도 리샤딩 동작은 동일하게 수행

2. 첫 번째로, 이동시킬 슬롯의 개수를 지정 : 100개의 키 이동
<div align="center">
<img src="https://github.com/user-attachments/assets/2a0e22c6-ef91-4000-92c6-61cd4e80b355">
</div>

   - 이 해시슬롯을 받을 노드의 ID를 입력 : 노드의 ID는 리샤딩을 시작했을 때 파악한 구성에서 쉽게 파악 가능
   - ```192.168.0.33```으로 실행된 마스터 노드에 100개의 해시슬롯을 이동시키고 싶다면, 다음과 같이 입력
<div align="center">
<img src="https://github.com/user-attachments/assets/64485dd1-5e6c-4910-9017-dfad2b34f269">
</div>

   - 해시슬롯을 이동시킬 노드의 ID를 묻는 메세지가 표시
     + all 입력 : 모든 마스터 노드에서 조금씩 이동
     + 해시슬롯을 가져올 마스터 ID를 지정하고 싶다면, 하나씩 입력한 뒤 done을 입력

   - all을 입력할 경우 다른 노드에서 조금씩 슬롯을 가져오는 상황
<div align="center">
<img src="https://github.com/user-attachments/assets/8685a283-3ab4-4670-9123-b1154db358e9">
</div>

<div align="center">
<img src="https://github.com/user-attachments/assets/6274633b-b39f-4bfb-8482-292db149f400">
</div>

   - 이제 해시슬롯을 이동시킬 노드의 입력이 끝나면, 리샤딩이 진행될 소스와 데스티네이션의 마스터 노드 정보를 확인할 수 있으며, 리샤딩 플랜을 보여줌
     + yes를 입력할 경우, 모든 작업이 끝난 뒤 CLUSTER CHECK 커맨드를 이용해 클러스터 정보를 자세히 확인 가능
     + CLUSTER CHECK 커맨드는 CLUSTER NODES보다 조금 더 자세한 구성 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/ec0fef03-2c0c-4ed4-8f15-ab0d61fe1078">
</div>

   - 앞선 리샤딩 과정을 통해 ```192.168.0.33``` IP 노드는 0부터 48, 5461부터 5511, 10923부터 168383까지 총 5561개의 해시슬롯을 갖게 됨을 알 수 있음
