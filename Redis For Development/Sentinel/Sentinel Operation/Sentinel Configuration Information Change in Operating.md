-----
### 운영 중 센티널 구성 정보 변경
-----
1. 센티널은 실행 도중 모니터링할 마스터를 추가 / 제거 / 변경 가능
2. 이 때, 마스터를 모니터링하는 센티널이 여러 대이면, 각각의 센티널에 모두 설정을 적용해야 하며, 설정을 변경했다고 해서 그 정보들이 다른 센티널로 전파되지 않음
   - SENTINEL MONITOR 커맨드 : 센티널이 새로운 마스터를 모니터링 할 수 있도록 함
```redis
SENTINER MONITOR <master-name> <ip> <port> <quorum>
```
   - SENTINEL REMOVE 커맨드 : 더 이상 지정하지 마스터를 모니터링 하지 않도록 지시
     + 센티널의 내부 상태에서 완전히 제거되며, SENTINEL masters 등의 컴내드에서 나열되지 않음
```redis
SENTINEL REMOVE <master-name>
```
   - SENTINEL SET 커맨드 : 특정 마스터에 대해 지정한 파라미터를 변경할 수 있음
     + 예를 들어, 마스터가 다운됐다는 것을 판단하는 시간인 down-after-milliseconds 값을 변경하고 싶다면, 다음과 같은 커맨드 입력
```redis
sentinel> SENTINEL SET mymater down-after-milliseconds 1000
OK
```
   - 쿼럼 값도 간단하게 변경 가능
```redis
sentinel> SENTINEL SET mymater quorum 1
OK
```
   - 레디스 버전 6.2 이상부터는 각 마스터에 종속되지 않는 센티널의 고유한 설정값도 런타임 중 변경 가능
```redis
SENTINEL CONFIG GET <configuration-name>
SENTINEL CONFIG SET <configuration-name> <value>
```
   - 레디스 노드에서 CONFIG GET / SET으로 파라미터를 변경한 것과 유사하게 사용 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/89099c69-a768-44b8-ae0c-e9a01c04597a">
</div>
