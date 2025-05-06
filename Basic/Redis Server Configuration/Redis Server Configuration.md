-----
### Redis 서버 구성
-----
1. 복제 (Replication) - Master / Slave
<div align="center">
<img src="https://github.com/user-attachments/assets/cbfed71e-c40a-46a0-8bd5-0f7186e71f3a">
</div>

  - Redis DB 기본 구성으로, Master에는 데이터를 저장하는 목적으로만 사용하며, 데이터베이스의 안정성을 위해 DB의 문제 발생 시, Slave를 설정해 Master에서 나오는 데이터베이스를 실시간 복제하여 문제 발생 시 사용하도록 설정
  - 하나의 Master 서버가 쓰기 작업 처리, 즉 Master는 데이터 저장 목적
  - 여러 Slave 서버가 Master의 데이터를 복제하여 읽기 작업 처리, 즉 Slave는 읽기 목적

2. 클러스터 구성
<div align="center">
<img src="https://github.com/user-attachments/assets/02932746-8d25-490b-8b39-17c7e9e0e4e4">
</div>

   - 추가적 설정과 구성 필요
   - Redis 클러스터는 데이터를 여러 노드에 분산하여 저장함으로 고가용성과 확장성 제공
   - 클러스터는 자동으로 데이터 샤딩(Sharding - 데이터를 조각 내 분산 저장하는 데이터 처리 기법)을 수행하고, 노드 간 복제를 통해 장애 복구 지원
     + 데이터베이스 샤딩(Database Sharding) : 대규모 데이터베이스를 여러 머신에 저장하는 프로세스 
