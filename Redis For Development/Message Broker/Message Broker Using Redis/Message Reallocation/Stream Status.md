-----
### stream 상태 확인
-----
1. XINFO 커맨드를 이용해 stream의 여러 상태를 확인할 수 있으며, 이 때 사용할 수 있는 기능은 아래 help 커맨드로 확인 가능
```redis
> XINFO HELP
1) XINFO <subcommand> [<arg> [value] [opt] ...]. Subcommands are:
2) CONSUMERS <key> <groupname>
3)     Show consumers of <groupname>.
4) GROUPS <key>
5)     Show the stream consumer groups.
6) STREAM <key> [FULL [COUNT <count>]
7)     Show information about the stream.
8) HELP
9)     Print this help.
```

2. 다음과 같은 커맨드를 이용해 특정 소비자 그룹에 속한 소비자 정보를 알 수 있음
```redis
XINFO cosumer <stream key> <소비자 그룹 이름>
```
```redis
> XINFO consumers Email EmailServiceGroup
1) 1) "name"
   2) "es1"
   3) "pending"
   4) (integer) 1
   5) "idle"
   6) (integer) 650129"
2) 1) "name"
   2) "es2"
   3) "pending"
   4) (integer) 0
   5) "idle"
   6) (integer) 437738623
3) 1) "name" 
   2) "es3"
   3) "pending"
   4) (integer) 7
   5) "idle"
   6) (integer) 858725
```

3. ```XINFO GROUPS <stream key>``` 커맨드를 이용해 stream에 속한 전체 소비자 그룹 List를 볼 수 있음
```redis
> XINFO GROUPS Email
...
```
<div align="center">
<img src="https://github.com/user-attachments/assets/c0f1ba5d-9d72-4d4c-9d2f-507296ce4914">
<img src="https://github.com/user-attachments/assets/ed0b0a43-a7e7-468e-8322-1681d7853486">
</div>

4. ```XINFO STREAM <stream key>``` 커맨드를 이용하면 stream 자체 정보를 알 수 있음
   - stream이 내부적으로 어떻게 인코딩되고 있는지 그리고 첫 번째와 마지막 메세지의 ID를 표시
<div align="center">
<img src="https://github.com/user-attachments/assets/65a91278-a6b0-4a34-a02f-afa57e2a8a95">
<img src="https://github.com/user-attachments/assets/38f6d241-c1b5-41c6-8841-ead4babddd95">
</div>

