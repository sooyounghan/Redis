-----
### AOF 타임스탬프
-----
1. 버전 7 이상부터는 AOF를 저장할 때 타임스탬프를 남길 수 있음
```redis
aof-timestamp-enabled no
```

2. 설정 파일에서 aof-timestamp-enabled 옵션을 활성화시키면 AOF 데이터가 저장될 때 타임스탬프로 함께 저장
```redis
#TS:1669532240
*2
$6
SELECT
$1
0
*3
$3
SET
$1
a
$1
b
```

3. 이를 이용하면 수동으로 AOF 파일을 조작하지 않아도 시스템상에서 시점 복원(Point-In-Time Recovery)이 가능
   - 만약 사용자 실수 FLAUSHALL 커맨드를 사용해 레디스 모든 데이터를 삭제했다고 가정
   - AOF 파일 로그
```redis
#TS:1669532240
*2
$6
SELECT
$1
0
*3
$3
SET
$1
a
$1
b
#TS:1669532845
*2
$6
SELECT
$1
0
*1
$8
FLUSHALL
```
   - 레디스에서 제공하는 redis-check-aof 프로그램을 사용해 FLUSHALL이 실행되기 전까지 데이터 복구 가능
   - 예제는 리눅스 타임스탬프를 1669532845까지 복구
<div align="center">
<img src="https://github.com/user-attachments/assets/d60fbf17-0bca-4c8f-b7d3-9c27d5c76b1d">
</div>

   - 위 과정이 끝난 후, 실제 appendonly.aof.15.incr.aof 파일을 확인하면 다음과 같이 FLUSHALL 커맨드가 수행되기 전 까지 데이터만 AOF 파일에 남게됨
```redis
$ cat appendonly.aof.15.incr.aof
#TS:1669532240
*2
$6
SELECT
$1
0
*3
$3
SET
$1
a
$1
b
```

   - 이 때, truncate-to-timestamp 옵션을 사용해 AOF 파일을 복구하면 원본 파일이 변경
   - 작업을 수행하기 이전 AOF 파일을 보호하고 싶다면 위 옵션을 사용하기 전 원본 파일을 미리 다른 곳에 복사하는 것이 좋음

4. 타임스탬프 기능은 레디스 버전 7 이후부터 지원되므로, 이 옵션을 켜서 저장한 AOF 파일은 이전 버전 레디스와 호환되지 않음
