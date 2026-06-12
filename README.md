# 腾云 API 调用平台

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Gateway](https://img.shields.io/badge/Spring%20Cloud-Gateway-blue)
![Dubbo](https://img.shields.io/badge/Dubbo-RPC-red)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

## 项目简介

腾云 API 调用平台是一个基于 Spring Boot、Spring Cloud Gateway、Dubbo 和 MySQL 构建的接口开放调用平台。

项目围绕接口管理、用户密钥、网关鉴权、调用转发、成功后计费、调用日志统计等功能构建完整业务闭环。用户可以通过 SDK 发起接口调用，请求经过网关统一鉴权、签名校验和防重放校验后转发至目标接口服务，调用成功后进行额度扣减与日志记录。

本项目主要用于实践后端系统中的网关鉴权、接口安全、RPC 调用、计费一致性、日志审计和后台统计等典型业务场景。

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 后端框架 | Spring Boot 3 |
| 网关 | Spring Cloud Gateway |
| RPC 调用 | Apache Dubbo |
| 注册中心 | Nacos |
| 数据库 | MySQL |
| ORM | MyBatis-Plus |
| 接口文档 | Knife4j |
| 构建工具 | Maven |

## 核心功能

- 用户注册、登录与密钥管理
- 接口信息管理与上下线控制
- SDK 发起接口调用
- Gateway 统一请求拦截与鉴权
- AK/SK 签名校验
- timestamp + nonce 防重放校验
- 下游接口转发
- 接口调用成功后扣减调用额度
- 调用日志记录与后台统计
- 管理员接口管理与调用数据查看

## 系统模块

| 模块 | 说明 |
| --- | --- |
| tengyun-api-backend | 后台管理与核心业务服务 |
| tengyun-api-gateway | 网关服务，负责鉴权、验签、转发 |
| tengyun-api-interface | 模拟开放接口服务 |
| tengyun-api-client-sdk | 调用方 SDK |
| tengyun-api-common | 公共实体、工具类与通用响应 |
| tengyun-api-frontend | 前端页面 |

## 核心调用流程

1. 用户通过 SDK 携带 accessKey、timestamp、nonce、sign 等参数发起接口调用。
2. 请求首先进入 Spring Cloud Gateway。
3. 网关校验用户身份、接口状态、签名合法性和防重放参数。
4. 校验通过后，网关将请求转发至接口服务。
5. 接口服务返回调用结果。
6. 调用成功后，平台扣减用户接口调用额度。
7. 系统记录调用日志并将结果返回给调用方。

## 项目亮点

### 1. 基于 AK/SK 的接口签名校验

平台为用户分配 accessKey 和 secretKey。调用方请求接口时，需要基于请求路径、请求方法、请求体、时间戳、随机数等参数生成签名，网关侧重新计算签名并进行比对，从而降低非法调用风险。

### 2. timestamp + nonce 防重放机制

网关对请求时间戳进行有效期校验，并结合 nonce 随机数识别重复请求，避免同一请求被恶意重复提交。

### 3. 成功后计费

平台仅在接口成功调用后扣减用户调用额度，并记录调用日志，避免接口调用失败但仍扣费的问题。

### 4. 网关统一鉴权与转发

所有接口调用统一经过 Gateway 处理，业务服务不直接暴露给调用方，降低接口安全风险，同时便于后续扩展限流、监控、黑白名单等能力。

### 5. 后台调用统计

平台支持按照接口维度、用户维度统计调用数据，便于管理员查看接口调用情况和用户使用情况。

## 快速启动

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Nacos
- Node.js

### 启动顺序

1. 启动 Nacos
2. 启动 MySQL
3. 启动 tengyun-api-backend
4. 启动 tengyun-api-interface
5. 启动 tengyun-api-gateway
6. 启动 tengyun-api-frontend

### 配置说明

启动前需要根据本地环境修改数据库、Nacos 等配置。

公开仓库中不建议提交真实数据库账号、密码、密钥等敏感信息。可以使用 application-example.yml 或 README 说明配置项。

## 后续优化方向

- 引入 Redis 存储 nonce，提升防重放校验性能
- 增加接口限流与熔断机制
- 增加用户调用额度充值与套餐管理
- 增加更细粒度的接口权限控制
- 接入 Prometheus / Grafana 进行调用监控
- 补充单元测试与接口自动化测试
