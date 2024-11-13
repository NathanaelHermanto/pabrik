package com.nathancorp.pabrik.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaddyRequest {
    private Double price;
    private Double quantity;
    private String supplier;
}