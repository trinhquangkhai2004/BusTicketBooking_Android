package com.busticket.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDebugResponseDTO {
    private Integer tokenLength;
    private Integer segmentCount;
    private String segmentLengths;
    private String headerAlgorithm;
    private String headerKeyId;
    private String tokenAudience;
    private String tokenIssuer;
    private String tokenSubject;
    private String expectedProjectId;
    private boolean projectMatches;
}
