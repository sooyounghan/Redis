-----
### 커맨드 권한 제어
-----
1. ACL 기능을 이용해 유저가 사용할 수 있는 커맨드 제어 가능
   - 운영의 편의성을 위해 일부 커맨드는 그룹화되어 카테고리로 정리되어 있으므로 운영자는 커맨드를 일일히 직접 제어할 필요가 없음
   - 물론 개별 커맨드도 제어가 가능하며, 서브 커맨드가 있는 경우 특정한 서브 커맨드를 제어하는 것도 가능

2. +@all 혹은 allcommands 키워드 : 모든 커맨드의 수행 권한을 부여한다는 것을 의미
3. -@all 혹은 nocommands 키워드 : 아무런 커맨드도 수행할 수 없다는 것을 뜻함
4. 커맨드 권한에 언급 없이 유저를 만들면 -@all 권한의 유저가 생성
5. 특정 카테고리의 권한을 추가하려면 ```+@<category>```, 제외하려면 ```-@<category>```를 사용할 수 있음
6. 개별 커맨드의 권한을 추가, 제외하려면 @ 없이 바로 ```+<command>```나 ```-<command>```를 사용
7. 예) user1이라는 유저에 권한 부여
```redis
> ACL SETUSER user1 +@all -@admin +bgsave +slowlog|get
```
   - ACL 룰은 왼쪽부터 오른쪽으로 순서대로 적용
   - 따라서, 앞서 나온 커맨드를 실행시키면 user1에 모든 커맨드의 수행 권한을 부여한 뒤, admin 카테고리의 커맨드 수행 권한은 제외
   - 그 뒤, bgsave 커맨드와 slowlog 커맨드 중 get이라는 서브 커맨드에 대한 수행 권한만 추가로 다시 부여하게 됨

8. ACL CAT 커맨드를 이용하면 레디스에 미리 정의되어 있는 카테고리 커맨드 list 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/349c51e5-9ea5-4d84-9441-784e9cbeb20f">
</div>

   - 카테고리 이름만 봐도 어떤 커맨드들이 속해 있는지 짐작 가능
   - 이를테면 pubsub 카테고리는 pub / sub 기능과 관련된 커맨드, list는 list 자료 구조에 대한 커맨드가 포함
   - 각 카테고리에 포함된 상세 커맨드 확인 : ```ACL CAT <카테고리명>```으로 확인 가능

9. 주목해야 할 카테고리와 포함된 커맨드 일부
    - dangerous : 아무나 사용하면 위험할 수 있는 커맨드를 포함
      + 레디스 구성을 변경하는 커맨드, 혹은 한 번 수행하면 오래 수행할 수 있는 가능성이 있어 장애를 발생시킬 수 있는 커맨드, 혹은 운영자가 아니면 사용하지 않아도 되는 커맨드 포함
      + 구성 변경 커맨드
        * replconf
        * replicaonf
        * migrate
        * failover

      + 장애 유발 커맨드
        * sort
        * flushdb
        * flushall
        * keys

      + 운영 커맨드
        * shutdown
        * monitor
        * acl|log, acl|deluser, acl|list, acl|setuser
        * bgsave, bgrewriteaof
        * info
        * config|get, config|set, config|rewrite, config|resetstat
        * debug
        * cluster|addslots, cluster|forget, cluster|failover
        * latency|graph, latency|docter, latency|reset, latency|history
        * client|list, client|kill, client|pause
        * module|loadex, module|list, module|unload

      + replicaof와 같은 커맨드는 마스터의 정보를 변경하므로 운영자가 의도하지 않은 구성으로 변경할 수 있는 가능성 존재
      + sort, key와 같은 커맨드는 메모리에 있는 모든 키들에 접근하므로 데이터가 많이 저장되어 있는 경우 오랜 기간 수행되며, 다른 커맨드들의 수행을 막을 가능성이 존재
      + client list 혹은 info, config get과 같은 커맨드는 장애를 유발하지 않지만, 레디스 인스턴스를 운영하는 사람이 아니라면 굳이 알지 않아도 되는 정보까지 노출될 수 있으므로, 모든 사용자에게 노출할 필요가 없음
      + 만약 레디스가 운영하는 팀이 따로 있고, 개발자에게 레디스를 제공한다면 dangerous 카테고리 커맨드만 수행하지 못하도록 강제하더라도 의도치 않은 많은 장애 상황을 줄일 수 있음

   - admin : dangerous 카테고리에서 장애를 유발할 수 있는 커맨드를 제외한 커맨드들 존재
     + key 혹은 sort, flushasll과 같은 커맨드는 구성을 변경하거나 운영과 관련된 커맨드는 아니고, 잘 모르고 사용했을 때 장애를 유발할 수 있는 커맨드이므로 상황에 따라 개발자가 사용할 수 있도록 제공해줄 경우가 필요할 수 있음
     + 예를 들어, 개발 용도의 레디스 인스턴스를 제공할 때는 위와 같은 커맨드를 사용할 수 있도록 admin 카테고리만 제외시킨 권한을 전달할 수 있음

   - fast : O(1)로 수행되는 커맨드를 모아놓은 카테고리로, get / spop / hset 등 커맨드 포함
   - slow : fast 카테고리에 속하지 않은 커맨드가 존재하며, scan / set / setbit / sunion 등 커맨드 포함
   - keyspace : 키와 관련된 커맨드가 포함된 카테고리로, scan / keys를 포함해 rename / type / expire / exists 등 키의 이름을 변경하거나 키의 종류를 파악하거나, 키의 TTL 값을 확인하거나 혹은 키가 있는지 확인하는 등 커맨드 포함
   - read : 데이터를 읽어오는 커맨드가 포함된 카테고리로, 각 자료 구조별 읽기 전용으로 키를 읽어오는 커맨드 포함 (get / hget / xtrange 등)
   - write : 메모리에 데이터를 쓰는 커맨드가 포함된 카테고리로, set / lset / setbit / hmset 등 포함하며, 키의 만료 시간 등의 메타데이터를 변경하는 expire / pexpire와 같은 커맨드 포함
