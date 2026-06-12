# 腾云 API 调用平台（项目展示主线）

## 一句话定位
一个面向开发者的 API 调用平台：提供 SDK 调用、网关签名鉴权、配额计费、调用日志与管理后台。

## 最值得讲的主线（面试主叙事）
`SDK 发请求 -> Gateway 验签鉴权 -> Dubbo 调 backend 查用户/接口/额度 -> 转发到 interface -> 成功后回写调用次数`

这条链路覆盖了：
- 业务闭环：调用前校验、调用后计费、全程可追踪
- 技术深度：签名算法、防重放、网关过滤器、Dubbo RPC、数据库原子扣减
- 工程性：分层清晰（controller/facade/service/mapper）、可测试、可运维

## 关键实现文件（建议面试时点名）
- SDK 加签与请求发送  
  [TengyunApiClient.java](/G:/aaabbb/sky-take-out/tengyun-api-parent/tengyun-api-client-sdk/src/main/java/com/example/tengyunapiclientsdk/client/TengyunApiClient.java)  
  [SignUtils.java](/G:/aaabbb/sky-take-out/tengyun-api-parent/tengyun-api-client-sdk/src/main/java/com/example/tengyunapiclientsdk/utils/SignUtils.java)
- Gateway 鉴权与计费入口  
  [CustomGlobalFilter.java](/G:/aaabbb/sky-take-out/tengyun-api-parent/tengyun-api-gateway/src/main/java/com/example/tengyunapigateway/filter/CustomGlobalFilter.java)
- Dubbo 内部服务实现  
  [InnerUserServiceImpl.java](/G:/aaabbb/sky-take-out/tengyun-api-parent/tengyun-api-backend/src/main/java/com/example/tengyunapibackend/service/impl/InnerUserServiceImpl.java)  
  [InnerInterfaceInfoServiceImpl.java](/G:/aaabbb/sky-take-out/tengyun-api-parent/tengyun-api-backend/src/main/java/com/example/tengyunapibackend/service/impl/InnerInterfaceInfoServiceImpl.java)  
  [InnerUserInterfaceInfoServiceImpl.java](/G:/aaabbb/sky-take-out/tengyun-api-parent/tengyun-api-backend/src/main/java/com/example/tengyunapibackend/service/impl/InnerUserInterfaceInfoServiceImpl.java)  
  [InnerInterfaceInvokeLogServiceImpl.java](/G:/aaabbb/sky-take-out/tengyun-api-parent/tengyun-api-backend/src/main/java/com/example/tengyunapibackend/service/impl/InnerInterfaceInvokeLogServiceImpl.java)
- 配额原子扣减 SQL  
  [UserInterfaceInfoMapper.xml](/G:/aaabbb/sky-take-out/tengyun-api-parent/tengyun-api-backend/src/main/resources/com/example/tengyunapibackend/mapper/UserInterfaceInfoMapper.xml) (`consumeInvokeCount`)
- 管理端调用入口  
  [InterfaceInfoFacadeServiceImpl.java](/G:/aaabbb/sky-take-out/tengyun-api-parent/tengyun-api-backend/src/main/java/com/example/tengyunapibackend/service/impl/InterfaceInfoFacadeServiceImpl.java) (`invokeInterface`)

## 你可以写进简历的要点
- 设计并实现 API 调用平台核心链路，完成签名鉴权、防重放、Dubbo 内部鉴权与计费闭环。  
- 在网关层实现请求级安全控制，并将成功调用与配额扣减严格绑定，避免重复扣费。  
- 将后台管理聚合查询下沉至 Mapper SQL，支撑接口/用户维度统计与分页。  
- 引入管理员操作审计日志与最小回归测试，提升可追踪性与交付质量。

