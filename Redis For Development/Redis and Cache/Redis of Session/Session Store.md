-----
### 세션 스토어가 필요한 이유
-----
1. 서비스 초창기, 혹은 프로토타입용 서비스에서는 굳이 세션 스토어가 필요하지 않음
   - 각 웹 서버에 세션 스토어를 두고 자체적으로 세션을 관리할 수 있기 떄문임
   - 하지만 서비스가 확장돼 웹 서버가 여러 대로 늘어나는 상황을 가정 : 웹 서버를 늘리면 여러 개 웹 서버에 트래픽을 분배할 수 있으므로 더 많은 유저를 수용할 수 있게 됨
<div align="center">
<img src="https://github.com/user-attachments/assets/4348f8ff-6c17-4fd7-be19-1f7882c5ff72">
</div>

2. Sticky Session
<div align="center">
<img src="https://github.com/user-attachments/assets/e1e6c7e5-8816-4dd1-b922-048ed9058a23">
</div>

   - 이 떄 각 웹 서버별로 세션 스토어를 따로 관리한다면, 유저의 세션 정보를 갖고 있는 웹 서버에 종속되어야 함
   - 그렇지 않으면, 유저 데이터 정합성에 문제가 발생
     + 쇼핑 카트에 아이템을 저장했으나, 서버가 재접속할 때마다 아이템이 사라졌다 생겼다 한다면 정상적인 서비스 이용이 불가능
   - 특정 웹 서버에 유저가 몰려 트래픽이 집중되는 상황이 발생하더라도 유저는 다른 서버를 사용할 수 없어, 결국 트래픽을 분산시킬 수 없는 상황이 발생하는데 이를 Sticky Session

3. All-to-All 방법
<div align="center">
<img src="https://github.com/user-attachments/assets/4ed11f49-38fa-43ed-a63a-2c47fb441000">
</div>

   - 유저의 세션 정보를 모든 웹 서버에 복제해서 저장하는 방법을 All-to-All 방법이라 함
     + 이 방법은 유저를 여러 웹 서버에 분산시킬 수 있지만, 유저의 세션 데이터는 여러 서버로 복사되어 저장되므로 불필요한 저장 공간을 차지하게 됨
     + 하나의 유저는 한 번에 하나의 웹 서버에만 접속하므로 다른 웹 서버에 저장된 유저의 세션 정보는 무의미함
     + 또한, 데이터를 복제하는 과정에서 불필요한 네트워크 트래픽이 다수 발생

4. 세션 스토어 - 데이터베이스 이용
<div align="center">
<img src="https://github.com/user-attachments/assets/19310e8b-2ad5-45dc-a263-7be1faaf70a0">
</div>

   - 각 유저는 세션이 활성화되어 있는 동안 세션 스토어에 활발하게 액세스함
   - 만약, 세션 스토어의 응답 속도가 느려지면 이는 곧바로 클라이언트의 응답 속도 저하로 이어질 수 있음
   - 서비스가 커져 유저가 많아질수록, 데이터베이스를 세션 스토어로 사용하는 것은 서비스 전반적인 응답 속도를 저하시키는 요인이 될 수 있음

5. 세션 스토어 - 레디스 이용
<div align="center">
<img src="https://github.com/user-attachments/assets/0c467bf6-2e49-43d1-9f4a-e963001b4dcb">
</div>

   - 레디스를 세션 스토어로 사용해 서버, 데이터베이스와 분리시켜 놓은 뒤 여러 서버에서 세션 스토어를 바라보도록 구성한다면 앞선 모든 이슈 해결 가능
   - 유저는 세션 스토어에 구애받지 않고 어떤 웹 서버에 연결되더라도 동일한 세션 데이터를 조회할 수 있어 트래픽을 효율적으로 분산시킬 수 있으며, 데이터의 일관성도 고려할 필요가 없음
   - 레디스는 관계형 데이터베이스보다 훨씬 빠르고 접근하기도 간편하므로 데이터를 가볍게 저장 가능

6. 레디스의 hash 자료 구조는 세션 데이터를 저장하기 알맞은 형태
<div align="center">
<img src="https://github.com/user-attachments/assets/a28e8e64-6f84-47a1-a604-7ec970f7cfc9">
</div>

```redis
> HMSET usersession:1 Name Garimoo IP 10:20:104:30 Hits 1
OK

> HINCRBY usersession:1 Hits 1
(integer) 2
```
