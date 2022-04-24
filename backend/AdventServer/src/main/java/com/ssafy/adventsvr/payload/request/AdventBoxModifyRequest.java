package com.ssafy.adventsvr.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdventBoxModifyRequest {

    @NotNull
    private Integer adventId;

    @NotNull
    private Integer boxId;
}