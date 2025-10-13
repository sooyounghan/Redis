-----
### Hyperloglog
-----
1. Hyperloglog 자료 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/719c09d5-80e1-4990-bf74-6e3abf4a254e">
</div>

   - hyperloglog는 집합 원소 개수인 카디널리티를 추정할 수 있는 자료 구조
   - 대량 데이터에서 중복되지 않는 고유한 값을 집계할 때 유용하게 사용할 수 있는 데이터 구조

2. 일반적으로 set과 같은 데이터 구조에서는 중복을 피하기 위해 저장된 데이터를 모두 기억하고 있으며, 따라서 저장되는 데이터가 많아질수록 그만큼 메모리를 많이 사용
   - hyperloglog는 입력되는 데이터 그 자체를 저장하지 않고, 자체적인 방법으로 데이터를 변경해 처리
   - 따라서, hyperloglog 자료 구조는 저장되는 데이터 개수에 구애받지 않고, 계속 일정한 메모리를 유지할 수 있으며, 중복되지 않는 유일한 원소의 개수를 계산할 수 있음
  
3. 하나의 hyperloglog 자료 구조는 최대 12KB 크기를 가지며, 레디스에서 카디널리티의 추정 오차는 0.81%로, 비교적 정확하게 데이터 추정 가능
   - 하나의 hyperloglog에는 최대 $2^{64}$개의 아이템을 저장할 수 있음

4. PFADD 커맨드 : hyperloglog에 아이템을 저장할 수 있음
5. PFCOUNT 커맨드 : 저장된 아이템의 개수, 즉, 카디널리티 추정 가능
```redis
> PFADD members 123
(integer) 1

> PFADD members 500
(integer) 1

> PFADD members 12
(integer) 1

> PFCOUNT members
(integer) 3
```
