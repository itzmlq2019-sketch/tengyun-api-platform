package com.example.tengyunapicommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "user_interface_quota_record")
public class UserInterfaceQuotaRecord implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "interface_info_id")
    private Long interfaceInfoId;

    @TableField(value = "change_num")
    private Integer changeNum;

    @TableField(value = "before_left_num")
    private Integer beforeLeftNum;

    @TableField(value = "after_left_num")
    private Integer afterLeftNum;

    @TableField(value = "operate_type")
    private String operateType;

    @TableField(value = "description")
    private String description;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
