# MCJE_Tracker

Minecraft Java 版「物品追踪器」：一套 **Paper 服务端插件 + Fabric 客户端模组** 的组合。

- 服务端负责按指令扫描指定范围内的 **掉落物 / 方块 / 玩家**，把命中目标的位置下发给对应玩家，并在 actionbar 显示最近目标的方向与距离。
- 客户端模组接收服务端下发的数据，在玩家视角内为被追踪目标绘制 **高亮描边**（线框盒）。

> 服务端插件可独立运行（只提供 actionbar 指引）；要看到描边效果需要搭配客户端模组。

## 目录结构

```
.
├── server-plugin/                 # Paper 服务端插件（多模块：根工程 + server 子模块）
│   ├── settings.gradle
│   ├── build.gradle               # 根：统一 group / version
│   ├── gradle.properties
│   └── server/
│       ├── build.gradle           # Paper 依赖 + 版本注入 + jar 命名
│       └── src/main/
│           ├── java/com/mcje/tracker/
│           │   ├── TrackerPlugin.java          # 插件入口
│           │   ├── command/                     # /tracker 指令 + Tab 补全
│           │   ├── config/                      # 配置与每玩家设置
│           │   ├── handler/                     # 扫描 / 下发 / 周期任务
│           │   ├── listener/                    # 玩家退出清理
│           │   ├── registry/                    # 通道与协议常量
│           │   ├── tracking/                    # 数据模型与管理器
│           │   └── util/                        # 方向 / 材质键工具
│           └── resources/{plugin.yml, config.yml}
└── client-mod/                    # Fabric 客户端模组（split source set）
    ├── build.gradle
    ├── gradle.properties
    └── src/
        ├── main/resources/fabric.mod.json
        └── client/java/com/mcje/tracker/client/
            ├── TrackerClient.java               # 客户端入口
            ├── net/                             # 协议常量 / 数据包 / 接收
            ├── render/                          # 高亮描边渲染
            └── state/                           # 最近一次快照
```

## 版本矩阵

| 组件 | 版本 |
| --- | --- |
| JDK | 21（编译 `--release 21`） |
| Minecraft | 1.21.11 |
| Paper API | `io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT`（`compileOnly`） |
| plugin.yml `api-version` | `1.21` |
| Fabric Loader | 0.18.4 |
| Fabric API | 0.140.2+1.21.11 |
| Yarn 映射 | 1.21.11+build.6 |
| Fabric Loom | 1.17.20 |
| Gradle | 服务端 9.7.1 / 客户端 9.5.0（均使用仓库自带 Wrapper） |

## 构建

```bash
# 服务端插件（产物：server-plugin/server/build/libs/MCJE_Tracker-server-<version>.jar）
cd server-plugin && ./gradlew :server:build

# 客户端模组（产物：client-mod/build/libs/mcjetracker-<version>.jar）
cd client-mod && ./gradlew build
```

Windows 使用 `gradlew.bat`。请使用仓库自带 Wrapper，无需本机安装 Gradle。

## 安装

1. **服务端**：把 `MCJE_Tracker-server-<version>.jar` 放进服务器的 `plugins/`，重启或热加载。
2. **客户端**：把 `mcjetracker-<version>.jar` 以及 Fabric API 放进 `mods/`（客户端需为 Fabric 环境）。

## 指令

指令别名：`/tk`。默认需要 `mcjetracker.use` 权限（`op`）。

| 指令 | 说明 |
| --- | --- |
| `/tracker <item\|block\|player> <on\|off> [格数]` | 开启 / 关闭某类追踪，可选追踪半径，缺省使用 `default-range`（默认 15） |
| `/tracker <item\|block\|player> <add\|del> <目标>` | 添加 / 删除被追踪目标 |
| `/tracker reload` | 热重载 `config.yml`（需 `mcjetracker.reload`） |

说明：

- 目标含义按类别不同：`item` / `block` 填材质名（如 `diamond`、`diamond_ore`），`player` 填玩家名。
- 第三参数支持 **Tab 补全**：材质从服务端注册表补全，玩家从在线列表补全；`del` 只补全已添加的目标。
- 追踪半径范围限制在 `1~128`。

## 工作原理

1. 服务端按 `scan-interval-ticks`（默认 10 tick）周期扫描每位开启追踪的玩家：
   - `item` / `player` 通过 `getNearbyEntities` 按半径检索；
   - `block` 在半径立方体内逐块匹配，受 `max-blocks-scanned` 预算保护。
2. 命中目标编码为插件消息经通道 `mcjetracker:main` 下发（字符串采用 Minecraft `PacketByteBuf` 的 VarInt 长度前缀 + UTF-8 格式）。
3. 客户端模组反序列化为快照，在 `WorldRenderEvents.AFTER_ENTITIES` 阶段为每个目标绘制 `lines` 线框盒：
   - 掉落物 = 黄、方块 = 青、玩家 = 绿；
   - 实体（掉落物 / 玩家）按 `entityId` 每帧重新取包围盒，描边随实体移动。
4. 服务端同时用 actionbar 输出**最近目标**的方位与距离（如「追踪[物品] diamond 位于 (x,y,z) 方向 右前 距离 6.3格」）。

协议常量在两端各自定义，必须保持一致：

- 服务端 [Channels.java](file:///workspace/server-plugin/server/src/main/java/com/mcje/tracker/registry/Channels.java)
- 客户端 [Protocol.java](file:///workspace/client-mod/src/client/java/com/mcje/tracker/client/net/Protocol.java) 与 [TrackerUpdatePayload.java](file:///workspace/client-mod/src/client/java/com/mcje/tracker/client/net/TrackerUpdatePayload.java)

## 配置（`config.yml`）

| 键 | 默认 | 说明 |
| --- | --- | --- |
| `default-range` | `15` | 未在指令指定格数时使用的默认追踪半径 |
| `scan-interval-ticks` | `10` | 扫描周期（tick） |
| `max-results-per-category` | `64` | 每类单次扫描最多下发的目标数 |
| `max-blocks-scanned` | `40000` | 方块扫描单次遍历预算，防止大范围卡服 |
| `actionbar-enabled` | `true` | 是否下发 actionbar 方向指引 |

## 注意事项

- Paper API 必须为 `compileOnly`，否则会被打进产物。
- 客户端模组为纯客户端模组（`environment: client`），服务端无需安装。
- 版本以构建脚本实际值为准。