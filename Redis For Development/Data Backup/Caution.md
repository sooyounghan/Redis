-----
### 백업을 사용할 때 주의할 점
-----
1. RDB와 AOF 파일을 사용하는 경우 인스턴스의 maxmemory 값은 실제 서버 메모리보다 여유를 갖고 설정하는 것이 좋음
2. BGSAVE 커맨드로 RDB 파일을 저장하거나 AOF 재구성을 진행할 때는 레디스는 fork()를 이용해 자식 프로세스를 생성
   - 생성된 자식 프로세스는 레디스의 메모리 그대로 파일에 저장해야 하며, 기존의 부모 프로세스는 다른 메모리의 데이터를 이용해 다른 클라이언트의 연결을 처리해야 함
   - 이 때, 레디스는 Copy-On-Write 방식을 이용해 메모리 상 데이터를 하나 더 복사하는 방법을 이용해 백업을 진행하면서, 클라이언트 요청 사항을 받아 메모리의 데이터를 읽고 수정하는 작업 진행
<div align="center">
<img src="https://github.com/user-attachments/assets/f7389ecf-74fe-48ed-8837-a8cacb70d2e2">
</div>

   - 물리적 메모리에 있는 실제 메모리 페이지가 그대로 복제되므로, 최악의 경우 레디스는 기존 메모리 용량의 2배를 사용하게 될 수 있음
   - 레디스의 maxmemory 값을 너무 크게 설정하면, 레디스의 Copy-On-Write 동작으로 인해 OS 메모리가 가득 차는 상황 발생 가능 : OOM(Out-Of-Memory) 문제로 서버 다운 가능

3. 따라서, 레디스의 maxmemory 옵션은 실제 메모리보다 여유를 갖고 설정하는 것이 안정적
   - 예를 들어, 다음 표와 같이 서버의 메모리 유형에 따라 적절한 maxmemory 값을 설정하는 것이 좋음
<div align="center">
<img src="https://github.com/user-attachments/assets/a3152485-9b42-4828-aa2c-306934775035">
</div>

4. RDB 스냅샷을 저장하는 도중엔 AOF 재구성 기능을 사용할 수 없고, AOF 재구성이 진행될 때는 BGSAVE 실행 불가
