package com.ssafy.adventsvr.service;

import com.ssafy.adventsvr.entity.Advent;
import com.ssafy.adventsvr.entity.AdventBox;
import com.ssafy.adventsvr.payload.request.AdventCertifyRequest;
import com.ssafy.adventsvr.payload.request.AdventDayRequest;
import com.ssafy.adventsvr.payload.request.AdventPrivateRequest;
import com.ssafy.adventsvr.payload.request.AdventRecipientModify;
import com.ssafy.adventsvr.payload.response.*;
import com.ssafy.adventsvr.repository.AdventBoxRepository;
import com.ssafy.adventsvr.repository.AdventRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdventServiceImpl implements AdventService{

    private final AdventRepository adventRepository;
    private final AdventBoxRepository adventBoxRepository;

    // Todo: POST 1,3,7 클릭시 게시글 생성 - ok
    @Transactional
    @Override
    public AdventDayResponse inputDayAdvent(AdventDayRequest adventDayRequest) {
        Advent advent = Advent.adventBuilder(adventDayRequest);

        return AdventDayResponse.builder()
                .adventId(adventRepository.save(advent).getId())
                .build();
    }

    @Transactional
    // Todo: POST 비밀번호, 힌트, 기념일 설정 페이지 작성
    @Override
    public AdventUrlResponse modifyPrivateInfoAdvent(AdventPrivateRequest adventPrivateRequest) {
        Optional<Advent> optionalAdvent = adventRepository.findById(adventPrivateRequest.getAdventId());
        Advent advent = optionalAdvent.orElseThrow(NoSuchElementException::new);
        // 기존에 디비에 있는지 확인해야함
        String url = RandomStringUtils.randomAlphanumeric(15);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate localDate = LocalDate.parse(adventPrivateRequest.getEndAt(),formatter);
        System.out.println();
        advent.setAdventPrivateInfoModify(adventPrivateRequest,url,localDate);

        Optional<List<AdventBox>> optionalAdventBoxes  = adventBoxRepository.findAllByAdventId(advent.getId());
        List<AdventBox> adventBoxList = optionalAdventBoxes.orElseThrow(NoSuchElementException::new);
        for (AdventBox adventbox :adventBoxList) {
            adventbox.setAdventBoxActiveAtModify(localDate,advent.getDay(),adventbox);
        }
        return AdventUrlResponse.builder()
                .url(url)
                .build();
    }

    @Transactional
    // Todo: POST 비밀번호 인증시 게시글 조회 - no
    @Override
    public AdventReceiveResponse findReceiveUrlAdvent(AdventCertifyRequest adventCertifyRequest) {
        Optional<Advent> optionalAdvent = adventRepository.findByUrl(adventCertifyRequest.getUrl());
        Advent advent = optionalAdvent.orElseThrow(NoSuchElementException::new);

        if(advent.getPassword().equals(adventCertifyRequest.getPassword())){
           Optional<List<AdventBox>> optionalAdventBoxes  =adventBoxRepository.findAllByAdventId(advent.getId());
           List<AdventBox> adventBoxList = optionalAdventBoxes.orElseThrow(NoSuchElementException::new);
           List<AdventBoxListResponse> adventBoxListResponse = AdventBoxListResponse.adventBoxListBuilder(adventBoxList);
           advent.setAdventIsReceivedModify();

           // 날짜됐을시 활성화
            for (AdventBox adventbox:adventBoxList) {
                LocalDate localDate = LocalDate.now();
                if(adventbox.getActiveAt() != null && adventbox.getActiveAt() == localDate){
                    adventbox.setAdventIsActiveModify();
                }
            }

            return AdventReceiveResponse.builder()
                    .adventId(advent.getId())
                    .recipientName(advent.getRecipientName())
                    .adventBoxList(adventBoxListResponse)
                    .build();
        }
        // 널 말고 예외 처리
        return null;
    }

    // Todo: 패스워드 설정 안돼있을시에 - ok
    @Override
    public AdventReceiveResponse findReceiveNotPasswordUrlAdvent(String url) {
        Optional<Advent> optionalAdvent = adventRepository.findByUrl(url);
        Advent advent = optionalAdvent.orElseThrow(NoSuchElementException::new);

        Optional<List<AdventBox>> optionalAdventBoxes  =adventBoxRepository.findAllByAdventId(advent.getId());
        List<AdventBox> adventBoxList = optionalAdventBoxes.orElseThrow(NoSuchElementException::new);
        List<AdventBoxListResponse> adventBoxListResponse = AdventBoxListResponse.adventBoxListBuilder(adventBoxList);
        advent.setAdventIsReceivedModify();

        // 날짜됐을시 활성화
        for (AdventBox adventbox:adventBoxList) {
            LocalDate localDate = LocalDate.now();
            if(adventbox.getActiveAt() != null && adventbox.getActiveAt() == localDate){
                adventbox.setAdventIsActiveModify();
            }
        }

        return AdventReceiveResponse.builder()
                .adventId(advent.getId())
                .recipientName(advent.getRecipientName())
                .adventBoxList(adventBoxListResponse)
                .build();

    }

    // Todo: 보관함 페이지에서 수정 눌렀을때 조회 - ok
    @Override
    public AdventReceiveResponse findAdvent(Integer adventId) {
        Optional<Advent> optionalAdvent = adventRepository.findById(adventId);
        Advent advent = optionalAdvent.orElseThrow(NoSuchElementException::new);

        Optional<List<AdventBox>> optionalAdventBoxes  =adventBoxRepository.findAllByAdventId(adventId);
        List<AdventBox> adventBoxList = optionalAdventBoxes.orElseThrow(NoSuchElementException::new);
        List<AdventBoxListResponse> adventBoxListResponse = AdventBoxListResponse.adventBoxListBuilder(adventBoxList);

        return AdventReceiveResponse.builder()
                .adventId(adventId)
                .recipientName(advent.getRecipientName())
                .adventBoxList(adventBoxListResponse)
                .build();
    }

    // Todo: GET 보관함 페이지 - ok
    @Override
    public Page<AdventStorageResponse> findMyStorageAdvent(Pageable pageable, Integer userId) {
        Optional<List<Advent>> optionalAdvent = adventRepository.findAllByUserId(pageable,userId);
        List<Advent> advent = optionalAdvent.orElseThrow(NoSuchElementException::new);
        List<AdventStorageResponse> advents = AdventStorageResponse.storageBuilder(advent);

        return new PageImpl<>(advents,pageable,advent.size());
    }

    @Override
    public void modifyRecipientAdvent(AdventRecipientModify adventRecipientModify) {
        Optional<Advent> optionalAdvent = adventRepository.findById(adventRecipientModify.getAdventId());
        Advent advent = optionalAdvent.orElseThrow(NoSuchElementException::new);
        advent.setAdventRecipientNameModify(adventRecipientModify.getRecipientName());
        System.out.println();
    }

    // Todo: DELETE 게시글 삭제 - no
    @Transactional
    @Override
    public void deleteAdvent(Integer userId, Integer adventId) {
        Optional<Advent> optionalAdvent = adventRepository.findById(adventId);
        Advent advent = optionalAdvent.orElseThrow(NoSuchElementException::new);

        if(advent.getUserId().equals(userId)){
            adventRepository.deleteById(advent.getId());
        }
    }
}