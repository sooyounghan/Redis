-----
### pub / sub 채널 접근 제어
-----
1. ```&<pattern>``` 키워드로 pub / sub 채널에 접근할 수 있는 권한을 제어할 수 있음
2. all channels 또는 ```&*``` 키워드로 전체 pub / sub 채널에 접근할 수 있는 권한이 부여
3. resetchannels 권한은 어떤 채널에도 발행 또는 구독할 수 없음을 의미
4. 유저를 생성하면 기본으로 resetchannels 권한 부여
