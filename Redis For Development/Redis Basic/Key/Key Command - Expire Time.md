-----
### 키의 만료 시간
-----
1. EXPIRE
   - 키가 만료될 시간을 초 단위로 정의할 수 있음
   - 옵션
     + NX : 해당 키에 만료 시간이 정의되어있지 않을 경우에만 커맨드 수행
     + XX : 해당 키에 만료 시간이 정의되어있을 때만 커맨드 수행
     + GT : 해당 키가 가지고 있는 만료 시간보다 새로 입력한 초가 더 클 때만 수행
     + LT : 해당 키가 가지고 있는 만료 시간보다 새로 입력한 초가 더 작을 때에만 수행
```redis
EXPIRE KEY seconds [NX | XX | GT | LT]
```

2. EXPIREAT
   - 키가 특정 유닉스 타임스탬프에 만료될 수 있도록 키의 만료 시간을 직접 지정
   - 사용할 수 있는 옵션은 EXPIRE와 동일
```redis
EXPIREAT KEY unix-time-seconds [NX | XX | GT | LT]
```

3. EXPIRETIME
   - 키가 삭제되는 유닉스 타임스탬프를 초 단위로 반환
   - 키가 존재하지만 만료 시간이 설정되어있지 않은 경우에는 -1을, 키가 없을 때에는 -2를 반환
```redis
EXPIRETIME KEY
```

4. TTL
   - 키가 몇 초 뒤 만료되는지 반환
   - 키가 존재하지만 만료 시간이 설정되어 있지 않다면 -1, 키가 없을 때에는 -2 반환
```redis
TTL KEY
```

5. PEXPIRE, PEXPIREAT, PEXPIRETIME, PTTL은 ms 단위로 계산되는 점만 다르며, 그 외 동일하게 동작
