# Demo Runbook（10 分钟演示脚本）

## 0. 演示目标
证明平台主链路真实可用：鉴权正确、调用可达、计费准确、日志可查。

## 1. 启动准备
1. 启动 Nacos（你已手动维护）。
2. 启动三个服务：
   - `tengyun-api-interface`（8123）
   - `tengyun-api-backend`（7529）
   - `tengyun-api-gateway`（8090）
3. 前端可选启动（用于页面演示）。

## 2. 演示路径（按顺序）
1. **登录后台**
   - 账号：`tengyun`
   - 密码：`12345678`
2. **管理员发放额度**
   - 对某个在线接口给当前用户发放 `+2`
3. **执行一次真实调用**
   - 调用 `interfaceInfo/invoke`
4. **展示结果闭环**
   - 用户剩余额度：净变化应为 `+1`（发放 +2，调用 -1）
   - 调用日志：新增一条成功记录（`status=0`, `responseStatus=200`）
   - 审计日志：新增一条 `AUTH_GRANT_QUOTA`

## 3. 讲解点（每步一句）
1. 登录后通过 Session 建立用户上下文，后续鉴权统一走 `AuthContextService`。
2. 发放额度写 `user_interface_info` + `user_interface_quota_record`，管理员动作写审计表。
3. 调用时 backend 不直连业务接口，而是调用 SDK，强制走网关安全入口。
4. 网关校验签名与 nonce 后，Dubbo 查用户/接口/额度，成功才转发。
5. 返回 200 才扣费并回写日志，避免失败调用误扣。

## 4. 常见失败场景（顺手展示）
- 缺失鉴权头 -> `401`
- nonce 重放 -> 第二次 `401`
- 接口未上线 -> 拒绝调用
- 额度不足 -> `403`

## 5. 一键联调脚本
- 脚本路径：`scripts/smoke-e2e.ps1`
- 运行方式：
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\smoke-e2e.ps1
```
