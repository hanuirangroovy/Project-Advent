package com.ssafy.adventsvr.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class AdventReceiveResponse {

    private String adventId;
    private String title;
    private List<AdventBoxListResponse> adventBoxList;

    @Builder
    public AdventReceiveResponse(String adventId, String title, List<AdventBoxListResponse> adventBoxList) {
        this.adventId = adventId;
        this.title = title;
        this.adventBoxList = adventBoxList;
    }
}
