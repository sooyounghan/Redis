-----
### Geospatial
-----
1. Geospatial 자료 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/e85efe82-91b5-4c81-ab5c-b8469a667682">
</div>

   - 경도, 위도 데이터 쌍의 집합으로 간편하게 지리 데이터를 저장할 수 있는 방법
   - 내부적으로 데이터는 sorted set으로 저장
   - 하나의 자료 구조 안에는 키는 중복되어 저장되지 않음

2. GEOADD ```<key>``` 경도 위도 member 순서로 저장
   - sorted set과 마찬가지로 XX 옵션을 사용하면 이미 아이템이 있는 경우에만 저장
   - NX 옵션을 사용하면 아이템이 없는 경우에만 데이터 저장
```redis
> GEOADD travel 14.399698913595286 50.09924276349484 prague
(integer) 1

> GEOADD travel 127.0016985 37.5642135 seoul -122.4345476225572 37.78530362582044 SanFrancisco
(integer) 2
```

3. GEOPOS 커맨드를 이용하면 저장된 위치 데이터를 조회할 수 있음
4. GETDIST 커맨드를 사용하면 두 아이템 사이 거리 반환
```redis
> GEOPOS travel prague
1) 1) "14.39969927072525024"
   2) "50.09924150927290043"

> GEODIST travel seoul prague KM
"8252.9957"
```

5. GEOSEARCH 커맨드 : 특정 위치를 기준으로 원하는 거리 내에 있는 아이템을 검색할 수 있음
   - BYRADIUS 옵션을 사용하면 반경 거리 기준
   - BYBOX 옵션을 사용하면 직사각형 거리를 기준으로 데이터 조회
   
