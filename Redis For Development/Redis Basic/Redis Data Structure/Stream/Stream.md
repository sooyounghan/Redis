-----
### stream
-----
1. stream 자료 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/c5826626-3444-45f7-bdc3-f92c2b980a7c">
</div>

   - 레디스를 메세지 브로커로서 사용할 수 있게 하는 자료 구조
   - 전체적인 구조는 카프카에서 영향을 받아 만들어졌으며, 카프카에서처럼 소비자 그룹 개념을 도입해 데이터를 분산 처리할 수 있는 시스템

2. stream 자료 구조는 데이터를 계속해서 추가하는 방식(Append-Only)으로 저장
   - 실시간 이벤트 혹은 로그성 데이터 저장을 위해 사용할 수 있음
   
