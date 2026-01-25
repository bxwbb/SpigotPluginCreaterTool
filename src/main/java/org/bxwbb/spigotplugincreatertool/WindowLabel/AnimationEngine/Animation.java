package org.bxwbb.spigotplugincreatertool.WindowLabel.AnimationEngine;

import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;
import javafx.application.Platform;

import java.util.UUID;
import java.util.function.Function;

public class Animation {
    // 持续时间ms
    protected long duration;
    // 动画应用对象
    protected BaseLabel node;
    // 动画运行状态（volatile保证多线程可见性）
    protected volatile boolean isRunning;
    // 动画暂停状态
    protected volatile boolean isPaused;
    // 动画是否被打断（标记结束类型）
    protected volatile boolean isInterrupted;
    // 已运行时间（用于暂停/恢复）
    protected volatile long elapsedTimeWhenPaused;

    // 动画生命周期回调
    protected Runnable startRunnable;      // 开始回调
    protected Runnable endRunnable;        // 最终结束回调（无论正常/打断都会执行）
    protected Runnable interruptRunnable;  // 打断专属回调（仅取消时触发）
    protected Runnable pauseRunnable;      // 暂停回调
    protected Runnable resumeRunnable;     // 恢复回调
    protected Function<Double, Void> updateFunction; // 更新逻辑（带进度参数）

    // 唯一标识（封装为private，提供getter）
    private final UUID id;
    // 动画线程（保存引用，方便调试/中断）
    private Thread animationThread;

    // 构造方法：强制传入目标组件和持续时间
    public Animation(BaseLabel node, long duration) {
        if (node == null) {
            throw new IllegalArgumentException("动画目标组件node不能为null");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("动画持续时间必须大于0（当前值：" + duration + "）");
        }
        this.node = node;
        this.duration = duration;
        this.id = UUID.randomUUID(); // 初始化唯一ID
        this.elapsedTimeWhenPaused = 0;
    }

    /**
     * 启动动画（核心方法）
     */
    public void run() {
        // 防止重复启动/暂停状态下启动
        if (isRunning || isPaused) {
            return;
        }

        // 重置状态
        isRunning = true;
        isInterrupted = false;
        isPaused = false;
        long startTime = System.currentTimeMillis() - elapsedTimeWhenPaused;

        // 启动回调（UI线程执行）
        Platform.runLater(() -> {
            if (startRunnable != null) {
                startRunnable.run();
            }
        });

        // 动画线程（命名线程，方便调试）
        animationThread = new Thread(() -> {
            while (isRunning) {
                // 暂停逻辑
                while (isPaused) {
                    try {
                        Thread.sleep(50); // 暂停时低功耗轮询
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                // 计算动画进度（使用nanoTime提升精度）
                long currentTime = System.currentTimeMillis();
                long elapsedTime = (currentTime - startTime) + elapsedTimeWhenPaused;
                double progress = Math.min((double) elapsedTime / duration, 1.0);

                // 执行更新逻辑（UI线程）
                Platform.runLater(() -> {
                    if (updateFunction != null) {
                        updateFunction.apply(progress);
                    }
                });

                // 动画正常结束
                if (progress >= 1.0) {
                    isRunning = false;
                    elapsedTimeWhenPaused = 0;
                    break;
                }

                // 控制帧率（约60帧/秒，减少CPU占用）
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    // 线程被中断，标记为打断状态
                    Thread.currentThread().interrupt();
                    isRunning = false;
                    isInterrupted = true;
                    elapsedTimeWhenPaused = 0;
                    break;
                }
            }

            // 执行结束逻辑（UI线程）
            Platform.runLater(() -> {
                // 1. 先执行打断回调（仅当被打断时）
                if (isInterrupted && interruptRunnable != null) {
                    interruptRunnable.run();
                }
                // 2. 执行最终结束回调（无论正常/打断）
                if (endRunnable != null) {
                    endRunnable.run();
                }
                // 3. 重置状态
                isInterrupted = false;
                elapsedTimeWhenPaused = 0;
                animationThread = null;
            });
        }, "Animation-Thread-" + this.id.toString().substring(0, 8)); // 命名线程

        // 启动动画线程
        animationThread.start();
    }

    /**
     * 取消/打断动画
     */
    public void cancel() {
        if (!isRunning) {
            return; // 非运行状态，直接返回
        }

        isInterrupted = true;
        isRunning = false;
        isPaused = false;

        // 中断动画线程（确保线程退出）
        if (animationThread != null && animationThread.isAlive()) {
            animationThread.interrupt();
        }
    }

    /**
     * 暂停动画
     */
    public void pause() {
        if (isRunning && !isPaused) {
            isPaused = true;
            // 记录暂停时已运行的时间
            elapsedTimeWhenPaused = System.currentTimeMillis() - (System.currentTimeMillis() - elapsedTimeWhenPaused);
            // 执行暂停回调
            Platform.runLater(() -> {
                if (pauseRunnable != null) {
                    pauseRunnable.run();
                }
            });
        }
    }

    /**
     * 恢复动画
     */
    public void resume() {
        if (isPaused) {
            isPaused = false;
            // 执行恢复回调
            Platform.runLater(() -> {
                if (resumeRunnable != null) {
                    resumeRunnable.run();
                }
            });
            // 重新启动循环（无需重新创建线程）
            synchronized (this) {
                this.notify();
            }
        }
    }

    // -------------------- Getter方法 --------------------
    public UUID getId() {
        return id; // 仅提供getter，保证ID不可修改
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isInterrupted() {
        return isInterrupted;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public BaseLabel getNode() {
        return node;
    }

    public long getDuration() {
        return duration;
    }

    // -------------------- Setter方法 --------------------
    public void setStartRunnable(Runnable startRunnable) {
        this.startRunnable = startRunnable;
    }

    public void setEndRunnable(Runnable endRunnable) {
        this.endRunnable = endRunnable;
    }

    public void setInterruptRunnable(Runnable interruptRunnable) {
        this.interruptRunnable = interruptRunnable;
    }

    public void setPauseRunnable(Runnable pauseRunnable) {
        this.pauseRunnable = pauseRunnable;
    }

    public void setResumeRunnable(Runnable resumeRunnable) {
        this.resumeRunnable = resumeRunnable;
    }

    public void setUpdateFunction(Function<Double, Void> updateFunction) {
        this.updateFunction = updateFunction;
    }

    public void setDuration(long duration) {
        if (duration > 0 && !isRunning) { // 运行中不允许修改时长
            this.duration = duration;
        }
    }
}