-----
### geo set
-----
1. 위치 공간 관리에 특화된 데이터 구조
2. 각 위치 데이터는 경도와 위도 쌍으로 저장
   - 내부적으로 sorted set 구조로 저장
```redis
> GEOADD user 50.07146286003341 14.414496454175485 142
(integer) 1
```
   - ID가 142인 사용자의 현재 위치 정보를 GEOADD 커맨드를 사용해 추가 가능

3. 위치를 변경할 때도 동일하게 GEOADD 커맨드 사용할 수 있으며, 기존 데이터는 새로운 데이터로 변경
```redis
> GEOADD restaurant 50.07146286003341 14.414496454175485 ukalendu
(integer) 1
```
   - 프라하의 맛집 ukalendu를 restarunt라는 키에 저장하는 예

4. 저장된 데이터는 GEOPOS 커맨드로 조회 가능
```redis
> GEOPOS restaurant ukalendu
1) 1) "50.07146447896957397"
   2) "14.41449664654903273"
```

5. 만약 호텔 근처 식당을 찾고자 한다면, 호텔의 경도와 위도 값을 가져온 뒤, GEOSEARCH 커맨드로 검색하면 1km 내 식당을 찾을 수 있음
```redis
> GEOSEARCH restaurant fromlonlat 50.06824582815170288 14.41818466583587366 byradius 1 km
1) "ukalendu"
```
   - FROMLONLAT 옵션을 이용해 직접 경도와 위도를 지정한 뒤, 해당 위치 근처의 1 km 내 데이터 검색
   - 그러나 동일 데이터 세트 내에서 검색하는 경우 FROMMEMBER 옵션을 이용하면 위도와 경도를 직접 입력하지 않고도 원하는 데이터를 찾을 수 있음
   - 일반적으로 근방 1Km라고 언급할 때, 우리는 특정 위치에서 반지름이 1km인 원의 영역을 생각
     + BYRADIUS 옵션은 사용자가 지정한 반지름 값을 기준으로 해당 위치에서 그 반지름 만큼 떨어진 범위 내 데이터 검색
     + BYBOX 옵션은 width와 height 값을 추가로 지정함으로써, 특정 위치 중심으로 한 직사각형 영역 내 장소들을 검색 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/19fb0254-ad29-4946-b1ee-81e9cfda51c0">
</div>

```redis
GEOSEARCH key FROMMEMBER member BYBOX 4 2 KM
```
   - BYBOX를 사용할 때는, width와 height를 설정하면, 검색 범위가 기준점을 중심으로 좌우로 width 만큼, 상하로 height 만큼 거리를 포함하는 직사각형 영역으로 결정된다는 점
   - 위 그림과 같이 width를 4Km로, height를 2Km로 설정하면, 검색 범위는 기준점을 중심으로 양 옆으로는 2Km, 위 / 아래로는 1km 이내 데이터를 검색한다는 의미
   - 이는 BYRADIUS 옵션에서처럼 검색 위치를 기준으로 입력한 데이터만큼 떨어진 것이 아니라는 점을 유의
