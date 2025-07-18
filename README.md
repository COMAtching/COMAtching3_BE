<img src="https://github.com/COMAtching/.github/blob/main/assets/github_banner.png?raw=true" alt="banner_image">

<table align="center">
  <tr>
    <td> <img src="https://github.com/COMAtching/.github/blob/main/assets/icon.png?raw=true" width="30px"> </td>
    <td> <a href="https://comatching.site"> <h1>COMAtching</h1> </a> </td>
    <td> <img src="https://github.com/COMAtching/.github/blob/main/assets/icon.png?raw=true" width="30px"></td>
  </tr>
</table>

<div align="center">
  가톨릭대학교 중앙 IT동아리 COMA "대학 축제 AI 이성매칭 서비스"
</div>

<br><br><br>

# 🎈 프로젝트 소개

## 만들게된 계기

코딩 동아리 활동 중 대학교 축제를 준비하면서, 기존에 사람이 수기로 직접 운영하던 이성 매칭 행사를 편리하게 웹 서비스로 개선하고자 했습니다.

## 인식한 문제

- 대학교 축제때 수기로 받던 이성 매칭 신청이 사람이 많아지면서 너무 많은 자료가 존재함
- 많은 사람들을 직접 분류하는 과정이 시간이 많이 들었음
- 분류한 사람들을 특정 기준으로 사람이 직접 매칭하는 시간이 너무 오래 걸렸음
- 다양한 기준으로 이성을 매칭하기엔 너무 복잡도가 올라감

# 📜 사용법 & 사용 시나리오

## 회원가입 및 정보 입력

1. **카카오 인증을 통해 회원가입**합니다.
2. 회원가입 후, 자신의 정보를 입력합니다:

## 포인트 충전

1. **포인트 충전 요청**:
   - 충전할 금액을 입력하고, 계좌번호로 송금합니다.
2. **운영자 확인**:
   - 운영자는 충전 요청을 확인하고, 입금을 확인 후 승인을 진행합니다.
3. 승인 완료 시, 포인트 충전이 완료됩니다.

## 포인트 사용

충전한 포인트로 다음 2가지 상품을 구매할 수 있습니다:

### 1. **Pick Me**

- 상대방이 나를 선택할 기회를 제공합니다.
- Pick Me를 통해 선택되더라도 상대방의 정보를 알 수 없습니다.

### 2. **매칭하기**

- 원하는 이성과 매칭할 수 있습니다.
- 상대방의 정보는 "뽑은 내역"에 저장됩니다.

### 매칭하기 기본 기능

- 원하는 상대방의 **MBTI 4개 중 2개**를 고를 수 있습니다.

### 추가 옵션 (포인트 추가 소모)

1. **나이 옵션**:
   - 연상, 동갑, 연하 중 선택.
2. **취미 옵션**:
   - 16개의 취미 중 최대 4개 선택 가능.
3. **연락 빈도 옵션**:
   - 자주, 보통, 가끔 중 선택.
4. **같은 과 안 뽑기**:
   - 동일한 전공을 가진 사람 제외.

#### 포인트 계산법

- 기본 매칭하기 포인트 + (추가 옵션 포인트 \* 선택한 추가 옵션 개수)

## 매칭 내역 조회

- **조회하기** 기능을 통해 내가 뽑은 매칭 내역을 확인할 수 있습니다.

## 🎈 주요 기능

### [가입 및 신청 폼 작성]

<table>
  <tr>
    <td>
      <img src="https://github.com/COMAtching/.github/blob/main/assets/%EC%BD%94%EB%A7%A4%EC%B9%AD_%EA%B0%80%EC%9E%85.gif?raw=true" width="300">
    </td>
    <td>
      Kakao OAuth2 인증을 통해 가입할 수 있습니다. <br>
      가입시 나이, MBTI, 전공 등 사용자 정보를 입력받습니다 <br>
      AI 모델은 사용자 정보 학습합니다  <br>
    </td>
  </tr>
</table>

### [포인트 충전]

<table>
  <tr>
    <td>
      <img src="https://github.com/COMAtching/.github/blob/main/assets/%EC%82%AC%EC%9A%A9%EC%9E%90_%EC%B6%A9%EC%A0%84.gif?raw=true" width="300">
    </td>
    <td>
      계좌이체를 통해 포인트를 충전합니다. <br>
      원하는 양의 포인트를 충전 요청할 수 있습니다 <br>
      부스에서 운영자가 확인후 승인하면 충전이 완료됩니다 <br>
      충전 후 뽑힐 기회(Pick Me)로 변경 가능합니다
    </td>
  </tr>
</table>

### [매칭]

<table>
  <tr>
    <td>
      <img src="https://github.com/COMAtching/.github/blob/main/assets/%EC%82%AC%EC%9A%A9%EC%9E%90_%EB%A7%A4%EC%B9%AD.gif?raw=true" width="300">
    </td>
    <td>
      포인트를 소모하여 매칭을 할 수 있습니다. <br>
      원하는 이성의 MBTI를 2개를 고를 수 있습니다. <br>
      추가 옵션으로 나이, 취미, 연락빈도를 선택할 수 있습니다.<br>
      해당 옵션과 최대한 비슷한 이성친구를 AI가 추천해줍니다. <br>
      한번 더 뽑기를 통해서 같은 옵션으로 추가 매칭이 가능합니다
    </td>
  </tr>
</table>

### [매칭 내역 확인]

<table>
  <tr>
    <td>
      <img src="https://github.com/COMAtching/.github/blob/main/assets/%EC%82%AC%EC%9A%A9%EC%9E%90_%EB%A7%A4%EC%B9%AD%EB%82%B4%EC%97%AD_%EC%A1%B0%ED%9A%8C.gif?raw=true" width="300">
    </td>
    <td>
      포인트를 소모하여 매칭을 할 수 있습니다. <br>
      원하는 이성의 MBTI를 2개를 고를 수 있습니다. <br>
      추가 옵션으로 나이, 취미, 연락빈도를 선택할 수 있습니다.<br>
      해당 옵션과 최대한 비슷한 이성친구를 AI가 추천해줍니다. <br>
      한번 더 뽑기를 통해서 같은 옵션으로 추가 매칭이 가능합니다
    </td>
  </tr>
</table>

## 기술 스택 & 아키텍처

<img src="https://github.com/COMAtching/.github/blob/main/assets/comatching_diagram.png?raw=true">

## ERD 설계

<img src="https://github.com/COMAtching/.github/blob/main/assets/erd.png?raw=true">
