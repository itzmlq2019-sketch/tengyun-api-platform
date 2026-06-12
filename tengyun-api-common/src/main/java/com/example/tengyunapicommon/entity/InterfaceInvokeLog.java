package com.example.tengyunapicommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "interface_invoke_log")
public class InterfaceInvokeLog implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "interface_info_id")
    private Long interfaceInfoId;

    @TableField(value = "request_path")
    private String requestPath;

    @TableField(value = "request_method")
    private String requestMethod;

    @TableField(value = "request_params")
    private String requestParams;

    @TableField(value = "response_status")
    private Integer responseStatus;

    @TableField(value = "response_message")
    private String responseMessage;

    /**
     * 0-success, 1-failed
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
