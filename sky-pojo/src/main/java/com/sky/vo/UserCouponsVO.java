package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponsVO {

    private Long id;

    private String name;

    private Integer fullMinus;

    private Integer reducePrice;

    private Integer status;

}
