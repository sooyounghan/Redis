-----
### 센티널의 자동 페일오버 과정
-----
1. 센티널은 최소 3대의 노드가 함께 동작하는 분산 시스템으로, 여러 개의 센티널 노드가 레디스 인스턴스를 함께 검사하므로 레디스 상태에 대한 오탐을 줄일 수 있게 됨
2. 마스터의 장애 상황 감지
   - 센티널은 down-after-milliseconds 파라미터에 지정된 값 이상 동안 마스터에 보낸 PING에 대해 유효한 응답을 받지 못하면 마스터가 다운되었다고 판단
   - PING에 대한 유욯하 응답은 +PONG, -LOADING, -MASTERDOWN이며, 다른 응답이나 응답을 아예 받지 못할 경우 모두 유효하지 않다고 판단
   - 만약 down-after-milliseconds 값이 30이고, 마스터는 29초마다 응답한다면 센티널은 해당 마스터를 정상적이라고 인지

3. sdown, odown 실패 상태로 전환
<div align="center">
<img src="https://github.com/user-attachments/assets/20d98190-b44b-49b7-9fa0-a495ec429f4a">
</div>

   - 하나의 센티널 노드에서 레디스 마스터 인스턴스에 대한 응답을 늦게 받으면 그 센티널은 마스터의 상태를 sdown으로 플래깅
     + sdown : subjectly down, 즉 주관적인 다운 상태를 의미
<div align="center">
<img src="https://github.com/user-attachments/assets/17984cdb-6aec-4771-8278-2881c7bbece5">
</div>

   - 이후 센티널 노드는 다른 센티널 노드에게 다음과 같은 커맨드를 보내 다른 센티널에게 장애 사실 전파
```redis
SENTINEL is-master-down-by-addr <master-ip> <master-port> <currnet-epoch> <*>
```
   - 위 커맨드를 받은 센티널들은 해당 마스터 서버의 장애를 인지했는지 여부를 응답
     + 자기 자신을 포함해 쿼럼 값 이상의 센티널 노드에서 마스터 장애를 인지했다면, 센티널 노드는 마스터 상태를 odown으로 변경
     + odown : objectly down, 즉 객관적인 다운 상태를 의미

   - 센티널은 마스터에 대해서만 odown 상태를 가짐
     + 센티널은 모든 레디스 노드를 모니터링하므로 복제본 노드에 장애가 발생한 경우, 이를 인지한 뒤, 해당 복제본 노드를 sdown으로 플래깅
     + 하지만, 해당 사실을 다른 센티널 노드로 전파하는 등의 작업을 진행해 복제본을 odown 상태로 변경하지 않음
     + 💡 장애 전파는 오직 마스터 노드에 대해서만 이뤄짐
     + 다만, 페일 오버를 진행할 때 sdown 상태의 복제본은 마스터로 승격되도록 선택하지 않음
     + sentinel.log의 상태
```redis
sentinel.log
+sdown
+odown
```

4. 에포크 증가
   - 처음으로 마스터 노드를 odown으로 인지한 센티널 노드가 페일오버 과정을 시작
   - 센티널은 페일오버를 싲가하기 전 우선 에포크(Epoch) 값을 하나 증가
   - 센티널은 에포크라는 개념을 이용해 각 마스터에서 발생한 페일오버의 버전을 관리
     + 에포크 : 증가하는 숫자값으로, 처음으로 페일오버가 일어날 때의 에포크 값은 1이 됨
     + 새로운 페일오버가 발생할 때마다 에포크 값은 하나씩 증가하며, 동일한 에포크 값을 이용해 페일오버가 진행되는 동안 모든 센티널 노드가 같은 작업을 시도하고 있다는 것 보장 가능
```redis
sentinel.log
+new-epoch
+try-failover
```

5. 센티널 리더 산출
<div align="center">
<img src="https://github.com/user-attachments/assets/5def02e0-1ad1-472d-a11f-dc8ed59062d8">
</div>

   - 에포크를 증가시킨 센티널은 다른 센티널에게 센티널 리더를 선출하기 위해 투표하라는 메세지 전송
   - 이 때, 증가시킨 에포크와 함께 전달하늗네, 해당 메세지를 받은 다른 센티널 노드가 현재 자신의 에포크보다 전달받은 클 경우, 자신의 에포크를 증가시킨 뒤, 센티널 리더에게 투표하겠다는 응답을 보냄
   - 만약 센티널 노드가 투표 요구를 받았을 때, 함께 전달받은 에포크 값이 자신의 에포크 값과 동일할 때에는 이미 리더로 선출한 센티널의 id를 응답
     + 하나의 에포크에서 센티널은 하나의 센티널에게만 투표할 수 있으며, 투표 결과는 변경할 수 없음
```redis
sentinel.log
+vote-for-leader
+elected-leader
```

6. 💡 과반수와 쿼럼
   - 센티널이 마스터 노드를 sdown → odown으로 변경하기 위해서는 쿼럼 값 이상 센티널 동의가 필요
   - 하지만, 페일 오버를 실제로 시도하기 위해 센티널 리더를 선출할 때에는 쿼럼 값이 아니라 실제 센티널 개수 중 과반수 이상의 센티널의 동의를 얻어야만 센티널 리더가 산출
   - 쿼럼 값보다 큰 센티널이 동의를 했음에도 그 수가 과반수보다 작다면 페일오버는 발생하지 않음
   - 💡 예) 센티널이 5개이고, 쿼럼이 2일 떄 마스터의 상태는 sdwon에서 odown으로 변경되어 페일오버가 트리거될 수 있으나, 2개의 센티널만 동의한 경우에는 센티널 리더를 선출할 수 없어, 페일오버가 발생하지 않음

7. 복제본 선정 후 마스터로 승격
   - 과반수 이상의 센티널이 페일오버에 동의했다면, 리더 센티널은 페일오버를 시도하기 위해 마스터가 될 수 있는 적당한 복제본 선택
   - 이 때, 마스터로부터 오랜 기간 연결이 끊겼던 복제본은 승격될 자격이 없음
   - 자격이 있는 복제본은 다음과 같은 순서로 선출
     + redis.conf 파일에 명시된 replica-priority가 낮은 복제본
     + 마스터로부터 더 많은 데이터를 수신한 복제본(master_repl_오프셋)
     + 두 개의 조건까지 동일하다면, runID가 사전 순으로 작은 복제본 : 작은 runID를 선택하는 것에 특별한 의미는 없음 (단지, 임의의 하나의 노드를 선택하는 방식)

   - 선정한 복제본에서는 slaveof no one 커맨드를 수행 : 기존 마스터로부터 복제를 끊음
```redis
sentinel.log
+failove-state-select-flave
+selected-slave
+failover-state-send-slaveof-none
+failover-state-wait-promotion
+promoted-slave
```

8. 복제 연결 변경
   - 기존 마스터에 연결되었던 다른 복제본이 새로 승격된 마스터의 복제본이 될 수 있도록 복제본 마다 ```replica new-ip new-port``` 커맨드를 수행해 복제 연결 변경
   - 복제 그룹의 모든 센티널 노드에서도 레디스 구성 정보 변경
```redis
sentinel.log
+failover-state-reconf-slaves
+slave-reconf-sent
+slave-reconf-inprog
+slave-reconf-done
+config-update-from sentinel
```

9. 장애 조치 완료 : 모든 과정이 완료된 뒤 센티널은 새로운 마스터를 모니터링
```redis
sentinel.log
+failover-end
+switch-master
```
