-----
### sharded pub / sub
-----
1. 클러스터의 pub / sub 비효율을 해결하기 위해 레디스 7.0에서는 sharded pub / sub 기능 도입
2. sharded pub / sub 환경에서 각 채널은 슬롯에 매핑
   - 클러스터에서 키가 슬롯에 할당하는 것과 동일한 방식으로 채널이 할당되며, 같은 슬롯을 가지고 있는 노드 간에만 pub / sub 메세지 전파
<div align="center">
<img src="https://github.com/user-attachments/assets/b0aec9c0-8ecb-443b-b22b-7ae6c9601edb">
</div>

3. SPUBLISH 커맨드 :  발행된 메세지는 모든 노드에 전파되지 않으며, 노드의 복제본에만 전달
```redis
10.0.0.1:6379> SPUBLISH apple a
-> Redirected to slot [7092] located at 10.0.0.2:6379
(integer) 1

10.0.0.1:6379>
```
   - 로컬에서 redis-cli 클라이언트를 이용해 데이터를 전파하려고 할 때, 연결된 노드에서 지정한 채널에 전파할 수 없다는 메세지와 함께 연결된 노드로 리다이렉트

4. SSUBSCRIBE도 마찬가지로 특정 서버에서만 수행될 수 있음
```redis
10.0.0.1:6379> SSUBSCRIBE apple
Reading messages... (press Ctrl+C to quit)
-> Redirected to slot [7092] located at 10.0.0.2:6379
Reading messages... (press Ctrl+C to quit)
1) "ssubscribe"
2) "apple"
3) (integer) 1
1) "smessage"
2) "apple"
3) "a"
```
   - apple 채널은 apple 키 값을 할당받을 수 있는 슬롯을 포함한 마스터 노드에 연결될 수 있도록 리다이렉트 됨
   - Sharded pub / sub을 이용한다면 클러스터 구조에서 pub / sub되는 모든 노드로 전파되지 않기 때문에 불필요한 복제를 줄여 자원을 절약할 수 있음
