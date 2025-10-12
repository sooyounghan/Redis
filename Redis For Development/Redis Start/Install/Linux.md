-----
### 리눅스
-----
1. CentOS 7에서 레디스를 설치하는 방법
2. 대부분 리눅스 배포판에서 최신의 레디스 버전을 제공하지 않으므로, remi-repository를 이용해 다운로드
3. remi repository를 설치한 뒤 활성화
```bash
$ sudo yum install http://rpms.remirepo.net/enterprise/remi-release-7.rpm
$ sudo yum-config-manager --enable remi
```

4. 설치하고자하는 레디스 버전 확인
```bash
$ sudo yum info redis | egrep 'Name|Arch|Version'
Name      : redis
Arch      : x86_64
Version   : 7.0.8
```

5. 설치하고자 하는 버전이 맞다면 yum repository를 이용해 설치
```bash
$ sudo yum install redis
```

6. 레디스를 실행하고, 서버가 부팅될 때 자동으로 시작되도록 설정
   - 커맨드를 실행함과 동시에 레디스는 백그라운드로 실행
```bash
$ sudo systemctl --noew enable redis
Create symlink from /etc/systemd/system/multi-user.target.wants/redis.
service to /usr/lib/systemd/system/redis.service
```

7. /etc/system/system/redis.service 파일을 확인하면 자동으로 등록된 레디스 정보 확인 가능
```bash
$ sudo cat /user/lib/systemd/system/redis.service
[Unit]
Description=Redis persistent key-value database
After=network.target
After=network-online.target
After=network-online.target

[Service]
ExecStart=/usr/bin/redis-server /etc/redis/redis.conf --daemonize no
--supervised systemd
ExecStop=/usr/libexec/redis-shutdown
Type=notify
User=redis
Group=redis
RuntimeDirectory=redis
RuntimeDirectoryMode=0755

[Install]
WantedBy=multi-user.target
```
