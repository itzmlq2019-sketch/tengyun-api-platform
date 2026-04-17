package com.example.tengyunapicommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "admin_operate_log")
public class AdminOperateLog implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "operator_user_id")
    private Long operatorUserId;

    @TableField(value = "action")
    private String action;

    @TableField(value = "target_type")
    private String targetType;

    @TableField(value = "target_id")
    private Long targetId;

    @TableField(value = "request_path")
    private String requestPath;

    @TableField(value = "request_method")
    private String requestMethod;

    @TableField(value = "request_ip")
    private String requestIp;

    @TableField(value = "detail")
    private String detail;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
