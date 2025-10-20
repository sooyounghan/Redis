-----
### 클러스터 리샤딩 - 간단 버전
-----
1. 만약 운영상 클러스터 내에서 슬롯을 이동시킬 일이 자주 있거나, 자동화를 하고 싶을 경우 커맨드를 이용해 사용자와의 인터렉션 없이 바로 슬롯을 이동시키는 방법 존재
2. 앞의 경우는 한 스텝씩 운영자가 확인하며 단계별로 진행시킬 수 있지만, 이 방법은 커맨드를 실행하자마자 바로 데이터가 옮겨지므로 중간에 취소와 확인이 어려움
```redis
redis-cli --cluster reshard <host>:<port> --cluster-from <node-id> --cluster-to <node-id> --cluster-slots <number of slots> --cluster-yes
```

3. 이번에는 ```192.168.0.11``` IP 노드에서만 ```192.168.0.33``` IP의 노드에게로 100개의 슬롯 이동
<div align="center">
<img src="https://github.com/user-attachments/assets/5df634b1-87cb-49de-8099-a36b40bdb4e8">
</div>

   - --cluster-yes 커맨드 : 모든 프롬프트에 자동으로 yes를 입력하겠다는 것을 의미
   - 위의 커맨드를 시작하자마자 자동으로 클러스터 리샤딩 작업 진행
   - 작업이 끝난 뒤 CLUSTER NODES로 잘 수행됐는지 확인
<div align="center">
<img src="https://github.com/user-attachments/assets/652a79d2-98c0-4616-b453-370dd8ae3231">
</div>

   - ```192.168.0.33``` IP의 노드가 가지고 있는 해시슬롯이 0 ~ 148, 5461 ~ 5561, 10923 ~ 16383으로 변경된 것을 알 수 있음
