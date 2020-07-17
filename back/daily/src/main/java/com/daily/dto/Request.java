package com.daily.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Request implements Serializable {

    @ApiModelProperty(value = "pageNum", name = "pageNum", example = "1")
    private Integer pageNum;
    @ApiModelProperty(value = "pageSize", name = "pageSize", example = "10")
    private Integer pageSize;

}
