package com.smartcourse.utils;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Signal;

import java.time.Duration;
import java.util.function.Function;

public class FluxUtils {

    /**
     * 这是一个 Reactor Operator 封装。
     * 作用：如果上游在指定时间内没有发出元素，则发送心跳元素；一旦有新元素，重置计时器。
     *
     * @param timeout       超时时间（如 2秒）
     * @param heartbeatItem 超时时发送的心跳对象
     * @param <T>           流的数据类型
     * @return 转换函数，用于 .transform()
     */
    public static <T> Function<Flux<T>, Flux<T>> withHeartbeat(Duration timeout, T heartbeatItem) {
        return upstream -> {
            // 内部包装类，用于区分 初始标记 vs 真实信号
            class WrappedSignal {
                final Signal<T> signal;
                final boolean isInit;

                WrappedSignal(Signal<T> signal, boolean isInit) {
                    this.signal = signal;
                    this.isInit = isInit;
                }
            }

            return upstream
                    // 1. 将所有信号（数据、报错、完成）都实体化为 Signal 对象
                    .materialize()
                    // 2. 包装为 WrappedSignal，标记这不是初始占位符
                    .map(s -> new WrappedSignal(s, false))
                    // 3. 开头塞入一个初始占位符，用于启动第一次等待
                    .startWith(new WrappedSignal(null, true))
                    // 4. 核心逻辑
                    .switchMap(wrapper -> {
                        // 场景 A: 如果是初始占位符 -> 只启动心跳计时，不发数据
                        if (wrapper.isInit) {
                            return Flux.interval(timeout)
                                    .map(i -> Signal.next(heartbeatItem)); // 包装成 Signal 以便下游还原
                        }
                        Signal<T> signal = wrapper.signal;
                        // 场景 B: ★关键修复★ 如果是“完成”或“错误”信号
                        // 直接发送该信号并结束，不再连接心跳流！
                        if (signal.isOnComplete() || signal.isOnError()) {
                            return Flux.just(signal);
                        }
                        // 场景 C: 如果是正常数据 -> 发送数据 + 启动延迟心跳
                        return Flux.just(signal)
                                .concatWith(
                                        Flux.interval(timeout)
                                                .map(i -> Signal.next(heartbeatItem))
                                );
                    })
                    // 5. 还原回正常数据流 (把 Signal 变回 T)
                    .dematerialize();
        };
    }
}