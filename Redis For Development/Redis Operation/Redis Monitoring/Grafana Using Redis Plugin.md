-----
### 레디스 플러그인을 이용한 그라파나 대시보드
-----
1. 프로메테우스라는 중간 저장소 없이 그라파나에서 레디스 데이터를 확인하고, 실시간으로 변경
   - 그라파나에서 레디스 플러그인을 설치하면 RedisGrafana에서 제공하는 대시보드를 이용해 실시간으로 레디스 상태 확인 가능
   - 레디스 익스포터를 설치할 필요가 없으므로 온프레미스 또는 클라우드 상품의 레디스에서도 실시간 대시보드 확인 가능

2. Redis와 Redis Application 플러그인을 설치한 뒤, 데이터 소스에 직접 레디 주소 입력해 레디스로부터 직접 데이터 수집할 수 있도록 설정
<div align="center">
<img src="https://github.com/user-attachments/assets/73b5520a-010a-477a-a06b-4fc846edc99d">
<img src="https://github.com/user-attachments/assets/dc9c0577-d207-4dd8-89d1-93244c5ab450">
</div>

3. Redis 대시보드에서는 실시간 레디스 정보 확인 가능
   - 현재의 슬로우 로그와 어떤 클라이언트가 어떤 커맨드를 수행했는지 하나의 창에서 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/2672988a-6433-460a-a831-5c5e43c0e731">
</div>

   - Redis Application 대시보드에서는 사용자가 원하는 대시보드를 손쉽게 추가 가능
   - redis-cli 패널을 제공하므로 그라파나에서 레디스 커맨드 바로 수행 가능
   - 그라파나에서 직접 레디스로 커맨드를 수행할 수 있게 해주는 redis-cli : 서버에 접근할 필요 없이 바로 그라파나에서 간단하게 쿼리 수행 가능
