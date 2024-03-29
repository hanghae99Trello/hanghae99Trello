# 주특기 프로젝트 트렐로
![image](https://github.com/hanghae99Trello/hanghae99Trello/assets/152241642/ca0df1a1-6824-40c8-9871-f6f3a970390a)
![image](https://github.com/hanghae99Trello/hanghae99Trello/assets/152241642/5427ed1c-d380-4499-ae3b-be4cb8c9e5a8)


## 구현 기능
- **사용자 관리 기능**
    - [x]  로그인 / 회원가입 기능
    - [x]  사용자 정보 수정 및 삭제 기능
- **보드 관리 기능**
    - [x]  보드 생성
    - [x]  보드 수정
        - 보드 이름
        - 배경 색상
        - 설명
    - [x]  보드 삭제
        - 생성한 사용자만 삭제를 할 수 있습니다.
    - [x]  보드 초대
        - 특정 사용자들을 해당 보드에 초대시켜 협업을 할 수 있어야 합니다.
- **컬럼 관리 기능**
    - [x]  컬럼 생성
        - 보드 내부에 컬럼을 생성할 수 있어야 합니다.
        - 컬럼이란 위 사진에서 Backlog, In Progress와 같은 것을 의미해요.
    - [x]  컬럼 이름 수정
    - [x]  컬럼 삭제
    - [x]  컬럼 순서 이동
        - 컬럼 순서는 자유롭게 변경될 수 있어야 합니다.
            - e.g. Backlog, In Progress, Done → Backlog, Done, In Progress
- **카드 관리 기능**
    - [x]  카드 생성
        - 컬럼 내부에 카드를 생성할 수 있어야 합니다.
    - [x]  카드 수정
        - 카드 이름
        - 카드 설명
        - 카드 색상
        - 작업자 할당
        - 작업자 변경
    - [x]  카드 삭제
    - [x]  카드 이동
        - 같은 컬럼 내에서 카드의 위치를 변경할 수 있어야 합니다.
        - 카드를 다른 컬럼으로 이동할 수 있어야 합니다.
- **카드 상세 기능**
    - [x]  댓글 달기
        - 협업하는 사람들끼리 카드에 대한 토론이 이루어질 수 있어야 합니다.
    - [x]  날짜 지정
        - 카드에 마감일을 설정하고 관리할 수 있어야 합니다.

## 추가 구현 사항
- [x]  Github Action을 통한 CI/CD 구현
- [x]  Beanstalk와 연결하여 자동 무중단 배포
- [x]  Redisson을 통한 분산락 구현
- [x]  Docker를 사용하여 컨테이너화
- [x]  Thymeleaf를 사용하여 화면과 서버 통신
- [x]  Spring Security를 사용하여 권한 제어
---
