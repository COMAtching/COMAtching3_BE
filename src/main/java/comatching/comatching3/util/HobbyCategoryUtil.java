package comatching.comatching3.util;

import java.util.Map;

public class HobbyCategoryUtil {

	private static final Map<String, String> HOBBY_CATEGORY_MAP = Map.ofEntries(
		Map.entry("헬스", "스포츠"),
		Map.entry("수영", "스포츠"),
		Map.entry("러닝", "스포츠"),
		Map.entry("축구", "스포츠"),
		Map.entry("농구", "스포츠"),
		Map.entry("야구", "스포츠"),
		Map.entry("배드민턴", "스포츠"),
		Map.entry("테니스", "스포츠"),
		Map.entry("클라이밍", "스포츠"),
		Map.entry("복싱", "스포츠"),
		Map.entry("골프", "스포츠"),

		Map.entry("인디음악", "문화예술"),
		Map.entry("락", "문화예술"),
		Map.entry("랩/힙합", "문화예술"),
		Map.entry("발라드", "문화예술"),
		Map.entry("RnB", "문화예술"),
		Map.entry("팝송", "문화예술"),
		Map.entry("K-팝", "문화예술"),
		Map.entry("클래식", "문화예술"),
		Map.entry("독서", "문화예술"),
		Map.entry("영화", "문화예술"),
		Map.entry("패션", "문화예술"),
		Map.entry("전시", "문화예술"),
		Map.entry("공예", "문화예술"),
		Map.entry("뮤지컬", "문화예술"),
		Map.entry("사진", "문화예술"),
		Map.entry("뷰티", "문화예술"),
		Map.entry("커피", "문화예술"),
		Map.entry("술/와인", "문화예술"),
		Map.entry("페스티벌", "문화예술"),

		Map.entry("피아노", "악기"),
		Map.entry("기타", "악기"),
		Map.entry("바이올린", "악기"),
		Map.entry("드럼", "악기"),
		Map.entry("플룻", "악기"),

		Map.entry("국내여행", "여행"),
		Map.entry("해외여행", "여행"),
		Map.entry("맛집탐방", "여행"),
		Map.entry("캠핑", "여행"),
		Map.entry("등산", "여행"),
		Map.entry("드라이브", "여행"),

		Map.entry("공부", "일상공부"),
		Map.entry("테크", "일상공부"),
		Map.entry("사랑", "일상공부"),
		Map.entry("시사/정치", "일상공부"),
		Map.entry("취업", "일상공부"),
		Map.entry("철학", "일상공부"),
		Map.entry("연구", "일상공부"),
		Map.entry("천문학", "일상공부"),
		Map.entry("과학", "일상공부"),
		Map.entry("시/문학", "일상공부"),

		Map.entry("배틀그라운드", "게임"),
		Map.entry("메이플스토리", "게임"),
		Map.entry("오버워치", "게임"),
		Map.entry("발로란트", "게임"),
		Map.entry("피파", "게임"),
		Map.entry("롤", "게임")
	);

	public static String getCategory(String hobbyName) {
		return HOBBY_CATEGORY_MAP.getOrDefault(hobbyName, "기타");
	}

	public static boolean isValidCategory(String hobbyName) {
		return HOBBY_CATEGORY_MAP.containsKey(hobbyName);
	}

}
