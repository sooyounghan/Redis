-----
### AOF 파일 복원
-----
1. 시점 복원(Point-In-Time Recovery)에 사용한 redis-check-aof 프로그램은 AOF 파일이 손상됐을 때도 사용 가능
2. 의도치 않은 서버 장애 발생 시, AOF 파일 작성 도중 레디스가 중지됐을 가능성 존재
<div align="center">
<img src="https://github.com/user-attachments/assets/7d1c11db-8a7d-4c27-8021-39ddf10ffe5e">
</div>

3. 레디스가 의도치 않은 장애로 중단됐을 때 redis-check-aof 프로그램을 사용하면 AOF 파일의 상태가 정상적인지 확인 가능
   - 위와 같은 경우 RDB 파일은 정상이며, AOF 파일은 비정상이므로 fix 옵션을 사용해 해결하라는 문구가 나옴
<div align="center">
<img src="https://github.com/user-attachments/assets/7ea49379-aa38-489f-8122-7f830db426e5">
</div>

   - fix 옵션을 사용한 복구 또한 원본 파일을 변경하므로 이전 데이터를 보호하고 싶다면, 원본 데이터를 다른 곳에 복사해두는 것이 안전
