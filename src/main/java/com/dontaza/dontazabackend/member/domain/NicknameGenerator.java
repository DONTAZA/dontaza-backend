package com.dontaza.dontazabackend.member.domain;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NicknameGenerator {

    private static final List<String> ADJECTIVES = List.of(
            "신나는", "용감한", "느긋한", "행복한", "씩씩한",
            "졸린", "배고픈", "심심한", "엉뚱한", "삐진",
            "흥분한", "당황한", "억울한", "찡찡대는", "투덜대는",
            "헐레벌떡", "어리둥절", "오지랖넓은", "눈치없는", "과몰입",
            "쪼렙", "만렙", "풀충전", "방전된", "버프받은",
            "킹왕짱", "극한의", "럭키비키", "핵인싸", "세상억울한",
            "빡친", "쿨한척하는", "감성터진", "텐션높은", "무념무상",
            "지각한", "칼퇴하는", "야근하는", "재택하는", "출근거부",
            "다이어트중인", "폭식하는", "헬스하는", "런닝하는", "뒹굴뒹굴",
            "하품하는", "몰래숨은", "눈치보는", "도망치는", "돌진하는"
    );

    private static final List<String> NOUNS = List.of(
            "프렌치불독", "치와와", "시바견", "골든리트리버", "웰시코기",
            "판다", "고양이", "먼치킨", "토끼", "다람쥐",
            "펭귄", "코알라", "수달", "햄스터", "여우",
            "카피바라", "알파카", "미어캣", "나무늘보", "레서판다",
            "오리너구리", "라쿤", "고슴도치", "두더지", "비버",
            "쿼카", "친칠라", "페릿", "기니피그", "슈가글라이더",
            "호랑이", "사자", "곰돌이", "치타", "하마",
            "기린", "얼룩말", "코끼리", "캥거루", "북극곰",
            "돌고래", "해파리", "문어", "복어", "물개",
            "앵무새", "올빼미", "플라밍고", "까마귀", "너구리"
    );

    private NicknameGenerator() {
    }

    public static String generate() {
        String adjective = ADJECTIVES.get(ThreadLocalRandom.current().nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(ThreadLocalRandom.current().nextInt(NOUNS.size()));
        return adjective + " " + noun + " 라이더";
    }
}
