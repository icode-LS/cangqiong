package com.sky.dto;

import lombok.Data;

@Data
public class CouponsDTO {

    private Long id ;

    private String name;

    private Integer num;

    private Integer fullMinus;

    private Integer reducePrice;

}
