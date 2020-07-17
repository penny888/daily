package com.daily.dto;

import com.daily.entity.DailyContent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@ApiModel(value = "日报汇总dto", description = "日报汇总dto")
public class TotalDailyDto {

    @ApiModelProperty(value = "员工姓名", name = "name", example = "张三")
    private String name;

    @ApiModelProperty(value = "员工日报任务列表", name = "content", example = "{}")
    private List<DailyContent> content;

}
