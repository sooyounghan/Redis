-----
### 패스워드 설정
-----
1. 레디스 6.0 이상부터 도입된 ACL 기능이 아닌 기본적인 패스워드를 사용해 데이터를 복제할 떄는 materauth 옵션에 패스워드를 입력해야 함
<div align="center">
<img src="https://github.com/user-attachments/assets/c7df4c73-624e-48c3-a54e-5a607062712e">
</div>

2. 레디스에서는 requirepass 옵션을 이용해 패스워드를 설정할 수 있음
   - 복제본 노드는 masterpass 옵션에 마스터의 requirepass에 설정된 패스워드 값을 입력해야 함
   - 해당 값이 없을 때는 master에 연결해 데이터를 받아갈 수 없음

3. 복제본 노드에서 requirepass를 다른 값으로 설정해 레디스 노드에 접근할 때 다른 패스워드를 사용하게 설정할 수 있지만, 하나의 복제 그룹에 속한 마스터와 복제본 노드는 같은 패스워드로 설정하는 것이 일반적
4. 복제본 인스턴스의 설정 파일을 직접 수정한 후 인스턴스를 재시작하거나, 실행 중인 복제본 인스턴스에서는 옵션을 수정한 뒤 설정 파일을 다시 작성할 수 있으
```redis
> CONFIG SET masterauth mypassword
OK

> CONFIG REWRITE
OK
```
