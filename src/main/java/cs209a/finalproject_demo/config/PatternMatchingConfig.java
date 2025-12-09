package cs209a.finalproject_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.regex.Pattern;

@Configuration
public class PatternMatchingConfig {
    public static class PitfallPattern {
        public String normalizedName; // e.g., "resource_exhaustion"
        public String category;       // e.g., "ROOT_CAUSE"
        public List<Pattern> regexes;

        public PitfallPattern(String normalizedName, String category, List<String> regexStrings) {
            this.normalizedName = normalizedName;
            this.category = category;
            this.regexes = new ArrayList<>();
            for (String regex : regexStrings) {
                this.regexes.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
            }
        }
    }

    @Bean
    public List<PitfallPattern> concurrencyPatterns() {
        List<PitfallPattern> patterns = new ArrayList<>();
        // ROOT CAUSE
        patterns.add(new PitfallPattern(
                "resource_exhaustion", "ROOT_CAUSE", Arrays.asList(
                // 1. 内存耗尽 (Memory/Heap/Native)
                "out of memory", "java.lang.OutOfMemoryError", "OOME", // 经典报错
                "heap space", "heap.*full",             // 堆内存满
                "metaspace",                            // 元空间满
                "direct buffer", "off-heap",            // 堆外内存/DirectBuffer (对应 Direct buffer memory gets exhausted)
                "malloc",                               // Native 内存分配 (对应 The process calls malloc)
                "memory.*exhaust",                      // 通用耗尽描述
                "run.*out of memory",                   // 对应 eventually runs out of memory
                "using more and more memory",           // 对应 LLM 发现的经典描述
                "drain.*memory",                        // 对应 draining some memory
                "high.*memory", "high.*usage",          // 对应 peak JVM usage / memory high
                "store.*to memory",                     // 对应 store the entire stream to memory (设计缺陷)

                // 2. 连接池与网络资源耗尽 (Connection Pool/TCP)
                "pool.*empty",                          // 对应 Connection pool is empty
                "pool.*exhaust",                        // 对应 exhausting the pool
                "connection.*limit",                    // 连接数限制
                "max.*connection",                      // 对应 max connections
                "active connection",                    // 对应 observing more than 100 active connections
                "unable to obtain.*connection",         // 对应 unable to obtain isolated JDBC connection
                "connection refused",                   // 对应 get connection refused (通常是端口/backlog满了)
                "tcp connection",                       // 对应 Can't open more than 8000 TCP connections

                // 3. 线程资源耗尽 (Thread Starvation/Limit)
                "pthread_create",                       // 对应 pthread_create failed (EAGAIN) - 最底层的线程耗尽
                "failed to start.*thread",              // Java 无法创建线程
                "native thread",                        // 对应 Failed to start the native thread
                "too many.*threads",                    // 对应 too many waiting threads
                "thread.*limit",                        // 线程限制

                // 4. 其他系统资源 (OS/Disk/Cache)
                "os limit",                             // 对应 hitting some OS limit
                "file descriptor", "open files",        // 文件句柄耗尽 (隐含在 OS limit 中，建议加上)
                "capacity",                             // 对应 cache size increased to near capacity
                "no space left"                         // 磁盘满
        )
        ));

        // === ROOT_CAUSE: 可见性与上下文传递 (Visibility & Context Propagation) ===
        patterns.add(new PitfallPattern(
                "visibility_issue", "ROOT_CAUSE", Arrays.asList(
                // [核心技术术语 - 几乎不会误判]
                "volatile",
                "happens-before", "HB ",
                "memory model", "JMM",
                "memory barrier", "store fence", "load fence",
                "atomic reference", // AtomicInteger 等通常涉及可见性/原子性

                // [收紧后的症状描述 - 必须带上下文]
                "changes.*not reflect",  // 替换原来的 "reflect" (太宽泛)
                "stale data", "stale value",
                "cpu cache", "L1 cache", "L2 cache", // 替换原来的 "cache" (太宽泛，会匹配 maven cache)
                "shared variable",       // 共享变量通常是可见性问题的核心

                "loops forever",         // while(!flag) 死循环

                // [微服务/框架上下文丢失 - 保持原样，这些通常很准]
                "MDC.*lost", "MDC.*empty",
                "trace.*lost", "trace.*null",
                "context.*propagat",
                "thread local.*lost"
        )
        ));

        // === 2. THREAD_STARVATION (收紧：区分“主动等待”和“被动饿死”) ===
        patterns.add(new PitfallPattern(
                "thread_starvation", "ROOT_CAUSE", Arrays.asList(
                // [明确的阻塞状态]
                "blocked thread",           // 替换原来的 "block" (太宽泛)
                "thread.*blocked",
                "blocking operation",       // 阻塞操作导致饥饿
                "indefinite",               // 无限期等待
                "unresponsive",
                "stuck",                    // Stuck 通常指非预期的卡住

                // [明确的资源等待]
                "waiting for lock",         // 替换原来的 "waiting for"
                "waiting for connection",
                "waiting for db",
                "pool.*exhaust",            // 池满了导致在此等待
                "queue.*full",              // 队列满了进不去

                // [硬核堆栈特征 - 绝对准确]
                "LockSupport.park",
                "Thread.State.WAITING",
                "Thread.State.BLOCKED",     // 注意：BLOCKED 状态是饥饿的铁证

                // [竞争描述]
                "starvation",               // 显式术语
                "thread contention",        // 线程竞争
                "overwhelm"                 // 系统被压垮
        )
        ));

        // === ROOT CAUSE: 竞态条件与可见性 ===
        patterns.add(new PitfallPattern(
                "race_condition", "ROOT_CAUSE", Arrays.asList(
                // 1. 显式技术术语 (Explicit Terms)
                "race condition", "race issue", "data race",
                "racing",                   // 对应 "racing to create" / "resource racing"

                // 2. 核心代码模式 (Code Patterns)
                "check-then-act",           // 经典的非原子操作模式
                "read-modify-write",        // 计数器非原子操作模式
                "atomicity", "not atomic",  // 原子性破坏
                "singleton",                // 对应 Singleton失效 (Double-checked locking问题)
                "lazy init",                // 懒加载竞态

                // 3. "海森堡 Bug" 特征 (Heisenbug / Timing Symptoms)
                // 这是竞态条件最独特的指纹：Debug 模式下行为改变
                "works.*debug",             // 对应 "works in DEBUG"
                "fail.*run",                // 对应 "crashes in RUN"
                "timing issue", "timing dependency",
                "sleep",                    // 对应 "adding a call to sleep... guarantees" (用sleep测出来的bug)
                "flaky", "flakiness",       // 工业界对不稳定测试/Bug 的标准称呼

                // 4. 非确定性行为 (Non-deterministic Behavior)
                "non-deterministic",        // 直接点题
                "sometimes",                // 对应 "matches sometimes", "sometimes processed"
                "occasionally",             // 对应 "occasional duplicates"
                "intermittently",           // 对应 "intermittently lost"
                "spasmodically",            // 偶发
                "randomly",                 // 随机失败
                "not always",               // 对应 "not always be the same"

                // 5. 并发结果异常 (Outcome of Races)
                "duplicate",                // 对应 "getting duplicates" (并发重复处理)
                "ordering guarantee",       // 对应 "no ordering guarantee"
                "out of order"              // 乱序执行
        )
        ));

        // === ROOT_CAUSE: 死锁与永久卡死 (Deadlock & Hangs) ===
        patterns.add(new PitfallPattern(
                "deadlock", "ROOT_CAUSE", Arrays.asList(
                // 1. 显式术语 (Explicit Terms)
                "deadlock", "dead-lock",    // 对应 "lead to deadlock"
                "dead lock",                // 变体
                "livelock",                 // 活锁 (虽然不同，但通常归为一类活性故障)
                "circular dependency",      // 死锁的核心成因 (环路等待)
                "dining philosophers",      // 哲学家就餐问题 (死锁经典案例)

                // 2. 行为描述：卡死/冻结 (Behavior: Frozen)
                "thread.*stuck", "thread.*hang", "application.*hang",
                "app.*freeze",         // 替换 "freeze"
                "indefinite.*wait",    // 无限等待
                "stuck in waiting",         // 对应 "stuck in waiting"
                "indefinite",               // 对应 "hangs indefinitely" (这是死锁与慢的区别)
                "waits forever",            // e.g., "waits forever"

                // 3. 锁与等待特征 (Locking & Waiting)
                "wait.*lock",               // 对应 "no query got lock" / "waiting for lock"
                "lock.*timeout",            // 对应 "inno_db lock timeout" (数据库死锁的常见报错)
                "unable to acquire lock",
                "blocked thread",           // 线程被阻塞
                "join.*deadlock"
        )
        ));

        // === ROOT_CAUSE: 配置错误与环境不匹配 (Configuration & Environment Issues) ===
        patterns.add(new PitfallPattern(
                "configuration_issue", "ROOT_CAUSE", Arrays.asList(
                // 1. 配置不生效/被忽略 (Ignored/Ineffective Settings)
                "default.*configuration",           // 对应 "no longer uses the default... configuration"
                "fail.*deserialize",        // 对应 "fails to deserialize YAML config" (格式错误)

                // 2. 默认值陷阱 (Default Value Pitfalls)
                "default is false",         // 对应 "automaticReconnect default is false"
                "default configuration",    // 默认配置问题

                // 3. 框架绑定与验证错误 (Framework Binding/Validation)
                "failed to bind properties",// 对应 Spring Boot "Failed to bind properties"
                "invalid.*config",          // 通用配置无效
                "invalid_redirect_uri",     // 对应 OAuth/Security 配置错误
                "unrecognized vm option",   // 对应 JVM "Unrecognized VM option" (参数过时/拼写错误)
                "maxpermsize",              // 对应 PermGen (Java 8+ 已移除，经典配置错误)
                "support this anymore",     // 对应 "JVM that doesn't support this anymore"

                // 4. 配置与实际行为不符 (Mismatch)
                "configured.*duration",     // 对应 "beyond the configured 1-hour duration"
                "configured.*time",         // 同上
                "misconfigured"             // 直接点题
        )
        ));

        patterns.add(new PitfallPattern(
                "data_loss", "SYMPTOM", Arrays.asList(
                // 1. 核心数据/消息丢失 (Core Data/Message Loss)
                "data loss", "message.*lost", "packet.*loss", "lost update",
                "never reach",            // 对应 never reach @MessageMapping
                "not receiv",             // 对应 I only receive a chunk / not received
                "not consum",             // 对应 did not consume
                "empty result",           // 本该有数据却为空
                "data.*loss", "data.*lost",
                "field.*missing",              // 字段丢失
                "record.*missing",

                // 3. 上下文与元数据丢失 (Context/Metadata Loss - 微服务/MDC常见问题)
                "context.*lost",          // 对应 lose trace context
                "trace.*null",            // 对应 traceId=null
                "span.*null",             // 对应 spanId=null
                "header.*null",           // 对应 Headers found... null
                "header.*missing",
                "MDC",                    // MDC (Mapped Diagnostic Context) 丢失是多线程经典问题
                "identity.*not appear",   // 对应 identity field... does not appear
                "not propagat",           // 对应 context is not propagating

                // 4. 连接中断/未完成 (Interruption/Abort)
                "disconnect.*before",     // 对应 disconnects before response finished
                "premature close",              // 过早断开 (premature close)

                // 5. 错误/日志被吞没 (Error Swallowing - 诊断数据丢失)
                "swallow",                // 对应 AssertionError is swallowed
                "suppress",               // 异常被抑制
                "not report",             // 对应 doesn't report any error
                "not log",                // 对应 doesn't log any errors
                "fail silently",          // 静默失败
                "no error log"            // 对应 no error logs on sender side
        )
        ));

        patterns.add(new PitfallPattern(
                "data_inconsistency", "SYMPTOM", Arrays.asList(
                // 1. 直接描述不一致的通用词
                "data inconsistency",
                "inconsistency",
                "inconsistency",
                "inconsistent",
                "integrity violation",
                "checksum fail",          // 对应 checksum failed

                // 2. 描述“重复” (Duplicates)
                "duplicate",              // 对应 getting duplicates
                "double entry",
                "processed twice",

                // 3. 描述“不同步/不匹配” (Mismatch/Different)
                "mismatch",
                "value.*different",       // 对应 showing different value
                "not match",
                "out of sync",
                "not reflect",            // e.g., changes are not reflected

                // 4. 描述“丢失更新/未更新/旧数据” (Stale/Lost Update)
                "never update",           // 对应 never updated
                "not update",
                "old version",            // 对应 old versions... not removed
                "stale data",
                "stale entry",
                "cache.*not removed",     // 对应 cache entry not removed

                // 5. 描述“顺序错乱” (Ordering Issues)
                "out of order",
                "lose.*order",            // 对应 lose the ordering
                "wrong order",

                // 6. 描述“可见性/通知失效” (Visibility/Notification)
                "not see.*value",         // 对应 thread will see values
                "visibility",             // 对应 observability/visibility
                "did not receive.*notification" // 对应 ListView did not receive notification
        )
        ));


        // === SYMPTOM: 性能问题与高延迟 (Performance & Latency) ===
        patterns.add(new PitfallPattern(
                "performance_issue", "SYMPTOM", Arrays.asList(
                "high latency", "response time",
                "bottleneck",

                "too slow",
                "very slow",
                "taking long time",
                "runs long",

                "timeout", "timed out",
                "jitter",

                "high cpu", "cpu usage",
                "gc pressure", "garbage collection", "stop-the-world",
                "low rate"
        )
        ));

        // === 5. EXCEPTION: 具体异常类 ===
        // 算法逻辑：精确匹配 Java 异常类名
        patterns.add(new PitfallPattern("InterruptedException", "EXCEPTION", Arrays.asList("InterruptedException")));
        patterns.add(new PitfallPattern("IOException", "EXCEPTION", Arrays.asList("IOException")));
        patterns.add(new PitfallPattern("ConcurrentModificationException", "EXCEPTION", Arrays.asList("ConcurrentModificationException")));
        patterns.add(new PitfallPattern("NullPointerException", "EXCEPTION", Arrays.asList("NullPointerException")));
        patterns.add(new PitfallPattern("SSLHandshakeException", "EXCEPTION", Arrays.asList("SSLHandshakeException")));
        patterns.add(new PitfallPattern("IllegalStateException", "EXCEPTION", Arrays.asList("IllegalStateException")));

        return patterns;
    }
}