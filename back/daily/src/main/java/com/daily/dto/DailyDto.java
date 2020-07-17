package com.daily.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value = "日报dto", description = "日报dto")
public class DailyDto extends Request {

    private static final long serialVersionUID = 896731212833614102L;

    @ApiModelProperty(value = "日报任务id", name = "id", example = "1")
    private Integer id;
    @ApiModelProperty(value = "员工id", name = "userId", example = "1")
    private Integer userId;
    @ApiModelProperty(value = "所属产品", name = "product", example = "app")
    private String product;
    @ApiModelProperty(value = "日报任务内容", name = "content", example = "日报任务内容")
    private String content;
    @ApiModelProperty(value = "进度", name = "progress", example = "100")
    private Integer progress;
    @ApiModelProperty(value = "日报任务备注", name = "note", example = "日报任务备注")
    private String note;
    @ApiModelProperty(value = "创建时间", name = "insertDt", example = "2020-05-10")
    private String insertDt;
    @ApiModelProperty(value = "开始日期", name = "startDt", example = "2020-05-10 00:00:00")
    private String startDt;
    @ApiModelProperty(value = "结束日期", name = "endDt", example = "2020-05-11 23:59:59")
    private String endDt;


}
