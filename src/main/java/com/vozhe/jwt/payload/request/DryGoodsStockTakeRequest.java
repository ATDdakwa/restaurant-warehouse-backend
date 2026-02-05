package com.vozhe.jwt.payload.request;

import lombok.Data;

@Data
public class DryGoodsStockTakeRequest {
    private String itemId;
    private String productName;
    private Integer actualQuantity;
    private Integer systemQuantity;
}
