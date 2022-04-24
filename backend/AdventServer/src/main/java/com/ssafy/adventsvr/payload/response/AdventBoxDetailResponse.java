package com.ssafy.adventsvr.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdventBoxDetailResponse {

    private Integer boxId;
    private String content;

    @Builder
    public AdventBoxDetailResponse(Integer boxId, String content) {
        this.boxId = boxId;
        this.content = content;
    }
}