-----
### 자동 AOF 재구성
-----
```redis
auto-aof-rewrite-percentage 100
auto-aof-rewrite-size 64mb
```

1. auto-aof-rewrite-percentage : AOF 파일을 다시 쓰기 위한 시점을 정하기 위한 옵션
   - 마지막으로 재구성했던 AOF 파일의 크기와 비교해, 현재의 AOF 파일이 지정된 퍼센트만큼 커졌을 때 재구성을 시도
   - 마지막으로 저장된 AOF 파일의 크기는 레디스에서 INFO Persistence 커맨드로 확인할 수 있는 aof_base_size 값
```redis
> INFO Persistence
# Persistence
...
aof_current_size:186830
aof_base_size:145802
...
```
   - aof_base_size는 145802이고, aof_current_size는 186830
   - auto-aof-rewrite-percentage이 100이면, aof_current_size가 aof_base_size의 100%만큼 커진 291604가 되면 자동으로 재구성 시도

2. 데이터가 아무것도 없는 상태로 인스턴스가 처음 부팅됐을 때 aof_base_size는 0이므로, 이럴 때에는 auto_aof_rewrite_size를 기준으로 데이터 재구성
3. auto-aof-rewrite-min-size 옵션 : 재구성된 이후 AOF 파일의 최소 크기를 지정 가능
   - 사용자가 데이터를 생성하고 삭제하는 작업을 반복했다고 가정
   - 실제 재구성을 시도해 새로 저장된 RDB의 크기, 즉 aof_base_size가 1KB로 줄어드는 경우가 발생할 수 있음
   - 이 경우 aof_current_size가 1KB의 100%에 도달할 때마다 재구성을 시도할 수 있으며, 비효율적인 작업을 트리거할 수 있음

4. 따라서, 마지막으로 작성된 AOF 파일 크기의 기준으로 재구성하되, 적어도 AOF 파일이 특정 크기 이상일 때에만 재구성 하도록 지정해 비효율적 작업을 최소화해야 함
