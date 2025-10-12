-----
### 레디스 환경 구성 - 서버 환경 설정
-----
1. Open files 확인
   - 레디스의 기본 maxclients 설정값은 10000 : 레디스 프로세스를 받아들일 수 있는 최대 클라이언트 개수를 의미
     + 하지만 이 값은 레디스를 실행하는 서버의 파일 디스크럽터 수에 영향을 받음
   - 레디스 프로세스 내부적으로 사용하기 위해 예약한 파일 디스크럽터 수는 32개로, maxclients 값에 32를 더한 값보다 서버의 최대 파일 디스크럽터 수가 작으면 레디스가 실행될 때 자동으로 그 수에 맞게 조정
   - 따라서, 만약 레디스의 최대 클라이언트 수를 기본값인 10000으로 지정하고 싶으면, 서버의 파일 디스크럽터 수를 10032 이상으로 지정해야 함
   - 현재 서버 파일 디스크럽터 수 확인
```bash
$ ulimit -a | grep open
```

   - 만약 위 커맨드로 확인한 open files의 값이 10032보다 작다면 /etc/security/limits.conf 파일에 다음과 같은 구문 추가
```
*   hard   nofile    100000
*   soft   nofile    100000
```
   - 서버를 재접속해서 ulimit를 확인하면 위에서 설정한 값이 반영되어있음을 확인 가능
```bash
$ ulimit -a | grep open
open files        (-n)   100000
```

2. THP 활성화
   - 리눅스는 메모리를 페이지 단위로 관리하며, 기본 페이지는 4096바이트(4KB)로 고정
   - 메모리 크기가 커질수록 페이지를 관리하는 테이블인 TLB의 크기도 커져, 메모리를 사용해 오버헤드가 발생하는 이슈로 인해 페이지를 크게 만든 뒤 자동으로 관리하는 THP(Transparent Huge Page) 기능 도입
   - 하지만, 레디스와 같은 데이터베이스 애플리케이션에서는 오히려 이 기능을 사용할 때 퍼포먼스가 떨어지고 Latency가 올라가는 현상이 발생하므로 레디스를 사용할 때는 이 기능을 추천하지 않음
   - THP 비활성화
```bash
$ echo > never > /sys/kernel/mm/transparent_hugepage/enabled
```
   - 이는 일시적으로 hugepage를 비활성화하는 것이고, 영구적으로 이 기능을 비활성화 하고 싶다면 /etc/rc.local 파일에 다음 구문 추가
```bash
if test -f /sys/kernel/mm/transparent_hugepage/enable; then
   echo never > /sys/kernel/mm/transparent_hugepage/enabled
fi
```
   - 다음 커맨드를 수행하면 부팅 중 rc.local 파일이 자동으로 실행되도록 설정 가능
```bash
chmod +x /etc/rc.d/rc.local
```

3. vm.overcommit.memory=1로 변경
   - 레디스는 디스크에 파일을 저장할 때 fork()를 이용해 백그라운드 프로세스를 만드는 데, 이 때 COW(Copy-On-Write)라는 메커니즘 동작
   - 이 메커니즘에서는 부모 프로세스와 자식 프로세스가 동일한 메모리 페이지를 공유하다가 레디스의 데이터가 변경될 때마다 메모리 페이지를 복사하므로 데이터 변경이 많이 발생하면 메모리 사용량이 빠르게 증가할 수 있음
   - 따라서, 레디스 프로세스가 실행되는 도중 메모리를 순간적으로 초과해 할당해야 하는 상황이 발생할 수 있으며, 이를 위해 vm.overcommit_memory를 1로 설정하는 것이 좋음
   - 기본적으로 vm.overcommit_memory 값은 0으로 설정되어 있어, 필요한 메모리를 초과해 할당하는 것이 제한됨
     + 그러나 레디스를 사용할 때에는 이 값을 조절해 메모리의 과도한 사용이나 잘못된 동작을 예방하고, 백그라운드에서 데이터를 저장하는 과정에서의 성능 저하나 오류를 방지할 수 있게 설정해야 함
   - /etc/sysctl.conf 파일에 vm.overcommit_memory=1 구문을 추가하면 영구적으로 해당 설정을 적용할 수 있으며, 재부팅 없이 바로 설정을 적용하려면 sysctl vm.overcommit_memory=1을 수행

4. somaxconn과 syn_backlog 설정 변경
   - 레디스 설정 파일의 tcp-backlog 파라미터는 레디스 인스턴스가 클라이언트와 통신할 때 사용하는 tcp backlog 큐의 크기를 지정
   - 이 때, redis.conf에서 지정한 tcp-backlog 값은 somaxconn(socket max connection)과 syn_backlog 값보다 클 수 없음
   - 기본 tcp-backlog 값은 511이므로, 서버 설정이 최소 이 값보다 크도록 설정해야 함
   - 서버의 현재 설정 값은 다음 커맨드로 확인 가능
```bash
$ sysctl -a | grep syn_backlog
net.ipv4.tcp_max_syn_backlog = 128
```
```bash
$ sysctl -a | grep somaxconn
net.core.somaxconn = 128
```
   - /etc/sysctl.conf 파일에 다음 구문을 추가하면 영구적으로 해당 설정 적용 가능
```bash
net.ipv4.tcp_max_syn_backlog = 1024
net.core.somaxconn = 1024
```
   - 재부팅 없이 바로 설정을 적용하려면 다음 커맨드 수행
```bash
sysctl net.ipv4.tcp_max_syn_backlog=1024
sysctl net.core.somaxconn = 1024
```
