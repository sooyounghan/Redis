-----
### 비트맵
-----
1. 비트맵 자료 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/15fec006-d9cc-4feb-96f4-50f853a85546">
</div>

   - 비트맵(Bitmap)은 독자적인 자료구조는 아닌, string 자료 구조에 bit 연산을 수행할 수 있도록 확장된 형태
   - string 자료 구조가 Binary-Safe하고, 최대 512MB의 값을 저장할 수 있는 구조이므로 $2^{32}$의 비트를 가지고 있는 비트맵 형태

2. 비트맵을 사용할 때의 가장 큰 장점은 저장 공간을 획기적으로 줄일 수 있음
   - 예를 들어, 각각의 유저가 정수 형태의 ID로 구분되고, 전체 유저가 40억이 넘는다고 해도 각 유저에 대한 y/n 데이터는 512MB 안에 충분히 저장 가능

3. SETBIT 비트를 저장할 수 있으며, GETBIT 커맨드로 저장된 비트 조회 가능
   - 한 번에 여러 비트를 SET하려면 BITFILED 커맨드를 사용하면 됨
4. BITCOUNT 커맨드를 이용하면 1로 설정된 비트의 개수를 카운팅할 수 있음
```redis
> SETBIT mybitmap 2 1
(integer) 0

> GETBIT mybitmap 2
(integer) 1

> BITFIELD mybitmap SET u1 6 1 SET u1 10 1 SET u1 14 1
1) (integer) 0
2) (integer) 0
3) (integer) 0

> BITCOUNT mybitmap
(integer) 4
```
