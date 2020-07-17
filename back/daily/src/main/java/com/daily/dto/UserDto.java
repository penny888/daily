package com.daily.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@ApiModel(value = "用户dto", description = "用户dto")
public class UserDto extends Request {

    private static final long serialVersionUID = -5712824587005305102L;

    @ApiModelProperty(value = "用户id", name = "id", example = "1")
    private Integer id;
    @ApiModelProperty(value = "姓名", name = "name", example = "张三")
    private String name;
    @ApiModelProperty(value = "账户", name = "account", example = "jack")
    private String account;
    @ApiModelProperty(value = "是否管理员", name = "isAdmin", example = "0:否，1:是")
    private String isAdmin;
    @ApiModelProperty(value = "所属部门", name = "department", example = "技术部")
    private String department;
    @ApiModelProperty(value = "开始日期", name = "startDt", example = "2020-05-10 00:00:00")
    private String startDt;
    @ApiModelProperty(value = "结束日期", name = "endDt", example = "2020-05-11 23:59:59")
    private String endDt;

}
