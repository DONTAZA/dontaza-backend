package com.dontaza.dontazabackend.riding.application;

import com.dontaza.dontazabackend.global.exception.BusinessViolationException.AlreadyRidingException;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.DailyRidingLimitException;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.TooFarFromStationException;
import com.dontaza.dontazabackend.global.exception.ResourceException.RidingNotFoundException;
import com.dontaza.dontazabackend.member.domain.Member;
import com.dontaza.dontazabackend.member.domain.MemberRepository;
import com.dontaza.dontazabackend.riding.domain.Riding;
import com.dontaza.dontazabackend.riding.domain.RidingBaselineStation;
import com.dontaza.dontazabackend.riding.domain.RidingBaselineStationRepository;
import com.dontaza.dontazabackend.riding.domain.RidingRepository;
import com.dontaza.dontazabackend.riding.domain.RidingStatus;
import com.dontaza.dontazabackend.riding.dto.RentRequest;
import com.dontaza.dontazabackend.riding.dto.RentResponse;
import com.dontaza.dontazabackend.riding.dto.RidingCurrentResponse;
import com.dontaza.dontazabackend.riding.dto.VerifyResponse;
import com.dontaza.dontazabackend.station.domain.Station;
import com.dontaza.dontazabackend.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RidingServiceTest {

    @Autowired
    private RidingService ridingService;

    @Autowired
    private RidingRepository ridingRepository;

    @Autowired
    private RidingBaselineStationRepository baselineStationRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Long memberId;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(
                new Member(12345L, "테스터", "https://img.kakao.com/test.jpg"));
        memberId = member.getId();
    }

    @Test
    void 근처_정류장이_있으면_대여에_성공한다() {
        // given
        stationRepository.save(new Station("ST-001", "강남역 1번출구", 37.4979, 127.0276, 10));
        RentRequest request = new RentRequest(37.4979, 127.0276);

        // when
        RentResponse response = ridingService.rent(memberId, request);

        // then
        assertThat(response.ridingId()).isNotNull();
        assertThat(response.status()).isEqualTo("WAITING_VERIFICATION");
        assertThat(response.nearbyStationIds()).contains("ST-001");
    }

    @Test
    void 대여_시_주변_정류장의_자전거_수가_기록된다() {
        // given
        stationRepository.save(new Station("ST-001", "강남역 1번출구", 37.4979, 127.0276, 10));
        RentRequest request = new RentRequest(37.4979, 127.0276);

        // when
        RentResponse response = ridingService.rent(memberId, request);

        // then
        List<RidingBaselineStation> baselines =
                baselineStationRepository.findByRidingId(response.ridingId());
        assertThat(baselines).hasSize(1);
        assertThat(baselines.get(0).getBaselineBikeCount()).isEqualTo(10);
    }

    @Test
    void 근처에_정류장이_없으면_대여에_실패한다() {
        // given
        stationRepository.save(new Station("ST-001", "강남역 1번출구", 37.4979, 127.0276, 10));
        RentRequest request = new RentRequest(35.0000, 125.0000);

        // when & then
        assertThatThrownBy(() -> ridingService.rent(memberId, request))
                .isInstanceOf(TooFarFromStationException.class);
    }

    @Test
    void 이미_라이딩_중이면_대여에_실패한다() {
        // given
        stationRepository.save(new Station("ST-001", "강남역 1번출구", 37.4979, 127.0276, 10));
        RentRequest request = new RentRequest(37.4979, 127.0276);
        ridingService.rent(memberId, request);

        // when & then
        assertThatThrownBy(() -> ridingService.rent(memberId, request))
                .isInstanceOf(AlreadyRidingException.class);
    }

    @Test
    void 당일_완료된_라이딩이_있으면_대여에_실패한다() throws Exception {
        // given
        stationRepository.save(new Station("ST-001", "강남역 1번출구", 37.4979, 127.0276, 10));
        Riding completed = Riding.rent(memberId);
        setRentedAt(completed, LocalDateTime.now().minusMinutes(10));
        completed.verify();
        completed.returnBike("ST-001", 100);
        ridingRepository.save(completed);

        RentRequest request = new RentRequest(37.4979, 127.0276);

        // when & then
        assertThatThrownBy(() -> ridingService.rent(memberId, request))
                .isInstanceOf(DailyRidingLimitException.class);
    }

    @Test
    void 자전거_수가_감소하면_인증에_성공한다() {
        // given
        Station station = stationRepository.save(
                new Station("ST-001", "강남역 1번출구", 37.4979, 127.0276, 10));
        ridingService.rent(memberId, new RentRequest(37.4979, 127.0276));

        station.updateFrom(new Station("ST-001", "강남역 1번출구", 37.4979, 127.0276, 9));
        stationRepository.save(station);

        // when
        VerifyResponse response = ridingService.verify(memberId);

        // then
        assertThat(response.verified()).isTrue();
    }

    @Test
    void 자전거_수가_그대로면_인증에_실패한다() {
        // given
        stationRepository.save(new Station("ST-001", "강남역 1번출구", 37.4979, 127.0276, 10));
        ridingService.rent(memberId, new RentRequest(37.4979, 127.0276));

        // when
        VerifyResponse response = ridingService.verify(memberId);

        // then
        assertThat(response.verified()).isFalse();
    }

    @Test
    void 대기_중인_라이딩이_없으면_인증_시_예외가_발생한다() {
        // given — 라이딩 없음

        // when & then
        assertThatThrownBy(() -> ridingService.verify(memberId))
                .isInstanceOf(RidingNotFoundException.class);
    }

    @Test
    void 활성_라이딩을_취소하면_상태가_CANCELLED로_변경된다() {
        // given
        stationRepository.save(new Station("ST-001", "강남역 1번출구", 37.4979, 127.0276, 10));
        RentResponse rentResponse = ridingService.rent(memberId, new RentRequest(37.4979, 127.0276));

        // when
        ridingService.cancelRiding(memberId);

        // then
        Riding cancelled = ridingRepository.findById(rentResponse.ridingId()).orElseThrow();
        assertThat(cancelled.getStatus()).isEqualTo(RidingStatus.CANCELLED);
    }

    @Test
    void 활성_라이딩이_여러개면_모두_취소된다() {
        // given
        Riding riding1 = ridingRepository.save(Riding.rent(memberId));
        Riding riding2 = ridingRepository.save(Riding.rent(memberId));

        // when
        ridingService.cancelRiding(memberId);

        // then
        assertThat(ridingRepository.findById(riding1.getId()).orElseThrow().getStatus())
                .isEqualTo(RidingStatus.CANCELLED);
        assertThat(ridingRepository.findById(riding2.getId()).orElseThrow().getStatus())
                .isEqualTo(RidingStatus.CANCELLED);
    }

    @Test
    void 활성_라이딩이_없어도_취소_시_예외_없이_정상_처리된다() {
        // given — 라이딩 없음

        // when & then — 예외 없이 정상 종료
        ridingService.cancelRiding(memberId);
    }

    @Test
    void 활성_라이딩이_있으면_라이딩_정보를_반환한다() {
        // given
        stationRepository.save(new Station("ST-001", "강남역 1번출구", 37.4979, 127.0276, 10));
        ridingService.rent(memberId, new RentRequest(37.4979, 127.0276));

        // when
        RidingCurrentResponse response = ridingService.getCurrentRiding(memberId);

        // then
        assertThat(response.active()).isTrue();
        assertThat(response.ridingId()).isNotNull();
        assertThat(response.status()).isEqualTo("WAITING_VERIFICATION");
    }

    @Test
    void 활성_라이딩이_없으면_빈_응답을_반환한다() {
        // given — 라이딩 없음

        // when
        RidingCurrentResponse response = ridingService.getCurrentRiding(memberId);

        // then
        assertThat(response.active()).isFalse();
        assertThat(response.ridingId()).isNull();
    }

    private void setRentedAt(Riding riding, LocalDateTime time) throws Exception {
        Field field = Riding.class.getDeclaredField("rentedAt");
        field.setAccessible(true);
        field.set(riding, time);
    }
}
