-----
### 메세지의 재할당
-----
1. 레디스는 소비자에게 장애가 날 경우를 대비해 소비자 별 보류 리스트를 유지함
2. 만약 소비자 서버에 장애가 발생해 복구되지 않는다면, 해당 소비자가 처리하던 보류 중인 메세지들은 다른 소비자가 대신 처리해야 함
   - XCLAIM 커맨드를 이용하면 메세지 소유권을 다른 소비자에게 할당 가능
```redis
XCLAIM <key> <group> <consumer> <min-idle-time> <ID-1> <ID-2> ... <ID-N>
```
   - XCLAIM 커맨드를 사용할 때는 최소 대기 시간(Min-Idle-Time)을 지정해야 함
   - 이는 메세지가 보류 상태로 머무른 시간이 최소 대기 시간을 초과한 경우에만 소유권 변경을 할 수 있도록 해서 같은 메세지가 2개의 다른 소비자에게 중복으로 할당되는 것을 방지할 수 있음

3. EmailService3이라는 소비자에 문제가 생겨, 이 소비자가 처리하던 메세지를 다른 소비자인 EmailService 1, 2가 가져가기 위해 XCLAIM 커맨드를 실행하는 상황 가정
```redis
EmailService 1: XCLAIM Email EmailServiceGroup EmailService3 3600000
16265569498055-0
EmailService 2: XCLAIM Email EmailServiceGroup EmailService3 3600000
16265569498055-0
```
  - 2개의 소비자가 모두 보류 중인 메세지에 XCLAIM 커맨드를 실행했지만, 위의 예제처럼 EmailService1의 커맨드가 먼저 실행되면 보류 시간이 즉시 0으로 재설정
  - EmailServicd2에서 실행한 XCLAIM 커맨드에서의 최소 대깃 ㅣ간보다 메세지 보류 시간이 짧으므로, 이 커맨드는 무시되며, 이를 통해 중복 메세지 할당 방지 가능
