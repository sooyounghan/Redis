-----
### 프로메테우스와 그라파나를 이용한 레디스 모니터링
-----
1. 레디스 모니터링 구조
   - 프로메테우스와 그라파나를 이용해 레디스 대시보드 구축하는 모니터링 시스템
<div align="center">
<img src="https://github.com/user-attachments/assets/84fb68a4-8f6d-4c81-b4ca-7f6502764cf9">
</div>

   - 익스포터(Exporter) : 시스템의 상태를 실시간으로 스크랩해서 메트릭을 수집하는 프로그램
   - 레디스 익스포터는 지정한 레디스의 인스턴스의 실시간 정보를 수집
   - 레디스 익스포터는 레디스가 실행되는 서버의 하드웨어와 OS 관련 메트릭 수집

2. 프로메테우스 : 메트릭 기반 오픈 소스 모니터링 시스템
   - 데이터는 시계열 형태로 저장되며, 간단하고 빠르게 데이터 수집 가능
   - 지정한 타깃으로 직접 접근해 데이터를 Pull 방식으로 수집
   - 타깃으로는 모니터링하고자 하는 시스템의 익스포터 저장

3. 그라파나 : 오픈 소스 메트릭 데이터 시각화 도구
   - 데이터를 시각화해서 시스템의 분석과 모니터링을 용이하게 해주는 플랫폼
   - 그라파나에서는 여러 데이터 소스를 연동할 수 있으므로, 프로메테우스를 데이터 소스로 추가하면, 각 레디스 서버의 메트릭을 수집한 프로메테우스의 정보를 시각화해서 볼 수 있음

4. 프로메테우스를 실행할 때 알람 규칙(Alerting Rule) 설정 가능
   - 이를 이용하면, 프로메테우스에서 수집한 메트릭을 그라파나로 볼 수 있을 뿐만 아니라, 메트릭별 임계치 지정 가능
   - 모니터링하는 대상이 특정 임계치에 도달했을 때, 이를 사용자에게 통지하기 위해서는 얼럿 매니저(Alert Manager)라는 프로그램 사용 가능
   - 얼럿 매니저는 서비스의 상태를 전달받을 엔트포인트를 SMS / 이메일 / 슬랙 등으로 지정 가능

5. 만약 하나의 서버에 하나의 레디스 인스턴스가 실행되는 경우, 레디스 익스포터 / 노드 익스포터를 실행시키면 됨
   - 하나의 서버에 2개의 레디스 인스턴스를 실행할 경우, 레디스 익스포터는 2개, 노드 익스포터는 1개만 실행시킴
   - 서버 2대를 이용해 모니터링 구축하는 방법
     + ```10.0.0.1``` 서버에는 6379 포트로 레디스가 띄워져 있으며, 9121포트로 레디스 익스포터를, 9100 포트로 노드 익스포터를 실행시킬 것
     + ```10.0.0.2``` 서버에 프로메테우스와 그라파나를 각각 설치한 뒤, ```10.0.0.1```에 띄워놓은 익스포터를 이용해 프로메테우스에서 데이터를 수집
     + 그라파나를 이용해 대시보드 확인
<div align="center">
<img src="https://github.com/user-attachments/assets/53ed8c59-cee3-4997-9e83-fe5781adacce">
</div>

6. 노드 익스포터 설치 : 다음 커맨드를 이용해 노드 익스포터 설치 파일 다운로드 후 압축 풀기
<div align="center">
<img src="https://github.com/user-attachments/assets/78f91c97-4882-4ac4-89b4-d4351174c795">
</div>

   - 백그라운드로 노드 익스포터 실행
<div align="center">
<img src="https://github.com/user-attachments/assets/08ad3ee2-6f21-495e-a69f-10f3c8c89206">
</div>

   - 아무런 설정 없이 노드 익스포터를 실행시키면 익스포터는 기본 옵션으로 실행
   - ```10.0.0.1:9100```에서 수집된 정보 확인 가능

7. 레디스 익스포터 설치 : 다음 커맨드를 이용해 노드 익스포터 설치 파일 다운로드 후 압축 풀기
<div align="center">
<img src="https://github.com/user-attachments/assets/5d6ad2dd-db22-4322-ab49-14566c6dcf5b">
</div>

   - 백그라운드로 레디스 익스포터 실행
<div align="center">
<img src="https://github.com/user-attachments/assets/25ced38e-5a51-47d5-b73f-5fd6f491a53a">
</div>

   - 아무런 설정 없이 레디스 익스포터를 실행시키면 익스포터는 기본 옵션으로 실행
   - ```10.0.0.1:9121```에서 수집된 정보 확인 가능
   - 만약, 레디스에 패스워드를 지정했거나, 레디스가 실행되는 포트를 변경했거나, 수집되는 엔드포인트를 변경하고 싶다면, 레디스 실행 시 다음과 같은 플래그 추가
<div align="center">
<img src="https://github.com/user-attachments/assets/51208257-cc00-4345-a950-3e854422e868">
</div>

   - 만약 레디스 실행 포트가 7379이고 패스워드가 password라면 다음과 같이 익스포터 실행 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/d4b61361-a85c-45e2-97d2-6373c1c34691">
</div>

8. 얼럿 매니저 설치 : 모니터링 서버에서 얼럿 매니저 설치
   - 다음 커맨드를 이용해 얼럿 매니저 다운로드 한 뒤, 압축 풀기
<div align="center">
<img src="https://github.com/user-attachments/assets/cace2401-dd37-4e57-9975-861a19ede2d1">
</div>

   - 이 예제에서는 특정 임계치테 도달했을 때, 디스코드를 이용해 알림을 받도록 설정
   - 얼럿 매니저의 수정 파일 수정 : webhook_url에는 디스코드에서 확인한 웹훅 주소 입력
<div align="center">
<img src="https://github.com/user-attachments/assets/7c64465e-99e0-4049-ac66-e2ba22078c9d">
</div>

   - 알람을 받을 정보에 대한 규칙 생성 및 룰을 설정
<div align="center">
<img src="https://github.com/user-attachments/assets/a24d67f2-2e45-47e8-8692-7a2a75b20914">
</div>

   - 다음 커맨드를 수행해 백그라운드로 얼럿 매니저 실행 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/b0bca32c-ec6d-4d0c-9911-e4d0bf9cb4bc">
</div>

   - 얼럿 매니저를 실행하는 서버인 ```10.0.0.2:9093```에 접속
<div align="center">
<img src="https://github.com/user-attachments/assets/93010ea4-54fc-4c9c-9ebe-208f60dc6bfc">
</div>

9. 프로메테우스 설치
   - 모니터링 서버에서 프로메테우스 설치
   - 다음 커맨드를 이용해 프로메테우스 다운로드 후, 압축 풀기
<div align="center">
<img src="https://github.com/user-attachments/assets/cfa05f88-0905-4600-9362-79fbb791c26b">
</div>

   - 프로메테우스가 모니터링할 타깃은 prometheus.yml에 yaml 형태로 지정 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/ed17d6f3-f6dc-427c-8e65-8bbcfffba472">
</div>

   - 파일을 열어 다음과 같이 데이터 입력
<div align="center">
<img src="https://github.com/user-attachments/assets/eca06749-176f-4037-b23f-861305c566b5">
</div>

   - 다음 커맨드를 수행해 백그라운드로 프로메테우스 실행 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/1bcc083b-7514-44fd-81d4-92c87ebde071">
</div>

   - ```10.0.0.2:9090```에 접속하면 프로메테우스 실행 화면을 볼 수 있음
   - Alerts 탭에서는 alert.rules에서 지정한 설정 값이 나오는 것 화인 가능
     + 만약 alert.rules에서 설정한 임계가 다다르면, 앞서 alertmanager.yaml에 설정한 디스코드의 웹 훅 주소로 인스로 레디스 인스턴스에 대한 알람을 받을 수 있음
<div align="center">
<img src="https://github.com/user-attachments/assets/3ffd4714-658a-4ad9-aa06-c2d56f46c1a7">
</div>

10. 그라파나 설치
    - 모니터링 서버에 그라파나 설치
    - 다음 커맨드를 이용해 프로메테우스 다운로드 후, 압축 풀기
<div align="center">
<img src="https://github.com/user-attachments/assets/d5eb0323-3f8f-4013-bbc3-6ac918c53501">
</div>

   - ```10.0.0.2:3000```에 접속하면 그라파나 첫 화면이 나옴
   - 기본 username과 password : admin / admin
     + 입력하면 그라파나에 접속 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/1c340bc0-00ed-4cc9-a168-82ef9aaf13af">
</div>

   - 앞서 생성해둔 프로메테우스를 그라파나에서 데이터 소스로 추가함으로써 프로메테우스에 수집된 데이터를 그라파나에서 확인 가능
   - 추가할 데이터 소스에 프로메테우스를 선택한 뒤 추가해주며, HTTP URL에 ```http://10.0.0.2:9090/```을 추가해 프로메테우스 URL를 입력하여 설치한 프로메테우스 정보 입력 가능

11. 대시보드 생성
    - 그 뒤, 데이터를 보여주는 화면인 대시보드를 추가해줘야 함
    - 그라파나 공식 홈페이지에는 다양한 대시보드를 제공하므로 필요 대시보드를 간단히 추가 가능
    - 레디스 서버를 모니터링 하기위해 Node Exporter Full 대시보드 추가

<div align="center">
<img src="https://github.com/user-attachments/assets/b5827b2f-79ad-496a-8670-fe219a4f7c2a">
</div>

   - 공식 홈페이지에서 제공하는 대시보드 ID를 복사해서 붙여 넣은 뒤 로드하면, 추가된 대스보드 확인 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/75aa7d8d-0fdc-4094-a462-654ec7acf16f">
</div>

   - 데이터는 프로메테우스라는 외부 저장소에 일정 기간 동안 보관되므로 원하는 시점 그래프를 확인 가능
