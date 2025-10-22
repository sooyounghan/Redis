-----
### 센티널 구성의 레디스 버전 업그레이드
-----
1. 센티널 구성은 다음과 같은 방법으로 다운타임 없이 전체 인스턴스를 업그레이드 가능
   - 신규 버전 레디스 바이너리 파일 다운로드
   - 3대의 센티널 인스턴스 모두 중단
   - 신규 버전 폴더에 기존 sentinel.conf 복사
   - 신규 바이너리 파일을 이용해 3대의 센티널 인스턴스 시작
   - 복제본 인스턴스 중단
   - 신규 버전 폴더에 기존 redis.conf 복사
   - 신규 바이너리 파일을 이용해 복제본 인스턴스 시작
   - 센티널에서 수동 페일오버 수행
   - 기존 마스터 인스턴스 중단
   - 신규 버전 폴더에 기존 redis.conf 복사
   - 신규 바이너리 파일을 이용해 기존 마스터 인스턴스 시작
   - 센티널에서 수동 페일오버 수행 (Fail-Back)

2. 예시) 레디스 버전 7.0.5에서 7.0.7로 업그레이드 하는 방법
   - 첫 번째 단계 : 신규 버전의 레디스 바이너리 파일 다운로드
<div align="center">
<img src="https://github.com/user-attachments/assets/ced2a860-f32f-4597-9738-59160d8f241a">
</div>

   - 신규버전의 레디스를 다운로드받은 뒤, 압축을 풀고 빌드
   - 기존 버전 레디스 폴더는 redis.old로, 신규 다운로드한 버전은 redis로 변경

   - 두 번째 단계 : 3대의 레디스 인스턴스 모두 중단
<div align="center">
<img src="https://github.com/user-attachments/assets/7c04dc21-efe2-4514-8189-7f387138f7e0">
</div>

   - 센티널 포트 모두 중단
   - 세 번째 단계 : 신규 버전 폴더에 기존 sentinel.conf 복사
<div align="center">
<img src="https://github.com/user-attachments/assets/59e726ee-7b79-481a-93a9-8870700e80f2">
</div>

   - 기존에 사용하던 sentinel.conf를 새로운 폴더에 복사
   - 네 번쨰 단계 : 신규 바이너리 파일을 이용해 3대의 센티널 인스턴스 시작
<div align="center">
<img src="https://github.com/user-attachments/assets/3525b0f9-7b5d-4508-8ba6-2e41a5d04c79">
</div>

   - 새로운 바이너리 파일을 이용해 센티널 인스턴스 업그레이드
<div align="center">
<img src="https://github.com/user-attachments/assets/569b9648-153b-43eb-9ed3-a7d43d76c9d4">
</div>

   - sentinel.log를 확인하면 센티널 버전이 7.0.7로 정상 실행된 것 확인 가능
   - 로그의 에러 확인 이후, 직접 센티널에 접속해 마스터 노드를 잘 모니터링하고 있는지 확인 후 다음 단계로 넘어가는 것이 좋음

   - 다섯 번째 단계 : 복제본 인스턴스 중단
<div align="center">
<img src="https://github.com/user-attachments/assets/5a598014-997b-4041-8c4b-13f211262d8f">
</div>

   - 복제본 인스턴스가 중단하기 전에는 우선 현재 실행 중인 레디스 인스턴스 정보가 설정 파일에 반영될 수 있도록 config rewrite를 수행하는 것이 좋음

   - 여섯 번째 단계 : 신규 버전 폴더에 기존 redis.conf 복사
<div align="center">
<img src="https://github.com/user-attachments/assets/593c3410-d878-46f2-87fa-0c08b6ae101e">
</div>

   - 기존 사용하던 redis.conf를 새로운 폴더로 복사
   - 일곱 번쨰 단계 : 신규 바이너리 파일을 이용해 3대의 센티널 인스턴스 시작
<div align="center">
<img src="https://github.com/user-attachments/assets/b661ce6b-ffe8-4afd-ac4a-903429da412f">
</div>

   - 새로운 바이너리 파일을 이용해 레디스 인스턴스 실행
   - 마찬가지로 로그 파일에서 신규 버전인 7.0.7로 업그레이드된 것 확인 가능
   - 서버에 접속한 뒤 INFO server 컴내드를 이용해도 실행 중인 인스턴스 서버 버전 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/014b3f30-715b-4142-a5fc-c4de79a4ff17">
</div>

   - 여덟 번쨰 단계 : 센티널에서 수동 페일오버 수행
<div align="center">
<img src="https://github.com/user-attachments/assets/c5f01610-2719-41ad-8b83-13ecaee022f8">
</div>

   - 센티널 서버에 접속한 뒤 커맨드를 수행하면 센티널을 수동으로 페일오버 시킬 수 있음
   - 마스터 서버에서 INFO replication 커맨드를 이용해 정상적으로 slave 상태로 변경된 것 확인해야 함
<div align="center">
<img src="https://github.com/user-attachments/assets/0d354be9-ac3d-4c7c-83e7-4fe6ea368026">
</div>

   - 앞선 5 ~ 7 단계와 동일하게 복제본이 된 기존 마스터 인스턴스를 중단하고, redis.conf 파일을 복사해서 신규 바이너리 파일을 이용해 레디스 실행
   - 만약, 운영 편의를 위해 기존 마스터를 다시 마스터 인스턴스를 변경하고 싶다면, sentinel failover를 한 번 더 수행해 다시 Fail-Back하는 작업을 거칠 수 있음
