# 面试问答清单（围绕主链路）

## Q1：为什么要做 SDK + Gateway，而不是前端直接调接口？
答：为了统一安全入口。SDK负责规范签名，Gateway集中做验签、防重放、配额校验和日志计费，避免每个业务接口重复实现安全逻辑。

## Q2：签名怎么做？防止哪些攻击？
答：签名串由 `path + method + body + timestamp + nonce + secretKey` 组成，SHA-256。  
可防参数篡改与伪造请求；`timestamp` 限时窗口防过期，`nonce` 缓存防重放。

## Q3：为什么 Gateway 不直接查数据库？
答：Gateway定位流量与安全层，不承载业务数据访问。通过 Dubbo 调 backend 的内部服务，保持职责清晰，也方便后续扩展 backend 逻辑而不改网关。

## Q4：如何保证“只在成功调用后扣费”？
答：网关拿到下游响应后仅在 HTTP 200 分支扣减额度。  
并先 `claimInvokeLogForBilling` 抢占计费状态，再执行扣减，避免重复扣费。

## Q5：配额扣减并发下怎么保证安全？
答：Mapper 层使用原子 SQL：`left_num = left_num - 1` 且条件 `left_num > 0`。  
数据库层保证不会扣成负数，业务层再写额度流水记录。

## Q6：你做了哪些工程质量动作？
答：  
- 控制器薄化，业务下沉至 facade/service。  
- 聚合统计查询下沉 mapper SQL。  
- 增加管理员操作审计日志。  
- 增加最小回归测试（网关鉴权/重放、索引初始化、管理员操作审计）。

## Q7：如果线上出现“用户说被多扣费”，你怎么排查？
答：先按 `userId + interfaceInfoId + time` 查 `interface_invoke_log` 与 `user_interface_quota_record`。  
再核对网关日志中 `claimInvokeLogForBilling` 记录与请求 nonce，确认是否重放或重复计费。

