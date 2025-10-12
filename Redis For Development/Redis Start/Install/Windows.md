-----
### 윈도우에 레디스 설치
-----
1. 윈도우에서도 WSL(Window Subsystem for Linux)를 이용해 레디스 설치 가능
   - WSL이 전부가 아니지만, 리눅스 기능 일부를 사용할 수 있게 하는 플랫폼으로, 가상머신이나 도커를 이용하지 않고도 윈도우에서 리눅스를 사용할 수 있게 해주는 시스템
     윈도우 10부터 설치 가능

2. WSL 설치
   - 윈도우에서 Powershell 관리자 권한으로 실행 후, WSL 활성화
```bash
> wsl --install
```
<div align="center">
<img src="https://github.com/user-attachments/assets/3c06fa95-588a-462c-aeed-e4ee856e514e">
</div>

   - 다음과 같은 안내문이 나왔다면, 윈도우 서버를 재부팅한 다음 Powershell을 이용해 우분투 사용 가능
   - Ubuntu 접속 명렁어
```bash
$ wsl -d ubuntu-22.04
```
   - 우분투 환경에서는 다음과 같이 repository를 추가하면 최신 버전 레디스 다운로드 가능
```bash
$ curl -fsSL https://packages.redis.io/gpg | sudo gpg --dearmor -o /usr/share/keyrings/redis-archive-keyring.gpg
$ echo "deb [signed-by=/usr/share/keyrings/redis-archive-keyring.gpg] https://packages.redis.io/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/redis.list
$ sudo apt-get update
```

3. 레디스 설치 (우분투)
```bash
$ sudo apt-get install redis
```

4. 레디스를 실행하고, 서버가 부팅될 떄 자동으로 시작하도록 설정
   - 커맨드를 실행함과 동시에 레디스를 백그라운드로 실행
```bash
$ sudo systemctl --now enable redis-server
Synchronizing state of redis-server.service with SysV service script with
/lib/systemd/systemd-sysv-install.
Executing: /lib/systemd/systemd-sysv-install enable redis-server
Created symlink /etc/systemd/system/redis.service → /lib/systemd/system/
redis-server.service.
Created symlink /etc/systemd/system/multi-user.target.wants/redis-server.
service → /lib/systemd/system/redis-server.service
```
