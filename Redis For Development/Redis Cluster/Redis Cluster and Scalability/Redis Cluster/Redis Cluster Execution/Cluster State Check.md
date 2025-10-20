-----
### 클러스터 상태 확인
-----
1. CLUSTER NODES 커맨드 : 현재 클러스터 상태 확인 가능
   - 랜덤으로 클러스터 내의 노드들을 수서 없이 출력
<div align="center">
<img src="https://github.com/user-attachments/assets/9417aa07-ff04-4c59-b2ba-925a2d4766f6">
</div>

   - 출력되는 라인은 각각 다음과 같은 필드를 갖고 있음
```redis
<ip> <ip:port@cport> <flags> <master> <ping-sent> <pong-recv> <config-epoch> <link-state> <slot> <slot> ... <slot>
```
<div align="center">
<img src="https://github.com/user-attachments/assets/bde43669-e76e-4f5d-b7e2-00edaf51a8df">
<img src="https://github.com/user-attachments/assets/824b9882-bd6a-4062-8e10-59ab512d1699">
</div>

2. 첫 번쨰 라인은 id가 73abfbb3872609862c9fcc229cdf1c3a3c0f2d05
   - 레디스 인스턴스가 실행되는 IP와 포트가 각각 ```192.168.0.22```와 6379라는 것을 의미
   - 클러스터 포트는 인스턴스 포트에 10000을 더한 값이므로 16379라는 것을 나타냄
   - Flag가 master이므로 마스터 상태 인스턴스
   - 활성화된 PING은 없고, 마지막으로 PONG을 수신한 시간은 16704298990051라는 문구
   - 구성 에포크는 2
   - 이 인스턴스로 클러스터 버스 연결을 잘 활성화되어 있음을 알 수 있음
   - 또한, 해당 인스턴스가 보유하고 있는 해시슬롯은 5461 ~ 10922
   
