package org.bxwbb.spigotplugincreatertool.WindowLabel.AnimationEngine;

import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 动画控制引擎：统一管理所有Animation实例的生命周期
 */
public class AnimationEngine {
    private static final Logger log = LoggerFactory.getLogger(AnimationEngine.class);
    // 核心存储：Key=动画UUID，Value=Animation实例（ConcurrentHashMap保证线程安全）
    private final Map<UUID, Animation> animationMap;
    // 锁对象：保证多线程操作安全
    private final Object lock = new Object();

    // 单例模式（可选，推荐全局唯一引擎）
    private static volatile AnimationEngine instance = new AnimationEngine();

    // 私有构造方法
    private AnimationEngine() {
        // 并发HashMap，适合多线程场景
        this.animationMap = new ConcurrentHashMap<>();
    }

    /**
     * 获取单例实例（全局唯一的动画引擎）
     */
    public static AnimationEngine getInstance() {
        if (instance == null) {
            synchronized (AnimationEngine.class) {
                if (instance == null) {
                    instance = new AnimationEngine();
                }
            }
        }
        return instance;
    }

    // -------------------- 基础管理方法 --------------------

    /**
     * 注册并启动动画
     * @param animation 要启动的动画实例
     */
    public void startAnimation(Animation animation) {
        if (animation == null) {
            throw new IllegalArgumentException("动画实例不能为null");
        }

        synchronized (lock) {
            UUID animationId = animation.getId();
            // 避免重复注册
            if (animationMap.containsKey(animationId)) {
                // 如果已存在且正在运行，先取消旧动画
                cancelAnimation(animationId);
            }

            // 注册动画（设置结束回调，自动清理）
            animation.setEndRunnable(() -> removeAnimation(animationId));
            animationMap.put(animationId, animation);

            // 启动动画
            animation.run();

            log.debug("动画[{}]已注册并启动，当前管理的动画数：{}", animationId, animationMap.size());
        }
    }

    /**
     * 取消指定ID的动画
     * @param animationId 动画UUID
     */
    public void cancelAnimation(UUID animationId) {
        if (animationId == null) {
            return;
        }

        synchronized (lock) {
            Animation animation = animationMap.get(animationId);
            if (animation != null) {
                animation.cancel();
                // 移除动画（结束回调也会移除，这里双重保障）
                animationMap.remove(animationId);
                log.debug("动画[{}]已取消并移除", animationId);
            }
        }
    }

    /**
     * 暂停指定ID的动画
     * @param animationId 动画UUID
     */
    public void pauseAnimation(UUID animationId) {
        if (animationId == null) {
            return;
        }

        synchronized (lock) {
            Animation animation = animationMap.get(animationId);
            if (animation != null && animation.isRunning() && !animation.isPaused()) {
                animation.pause();
                log.debug("动画[{}]已暂停", animationId);
            }
        }
    }

    /**
     * 恢复指定ID的动画
     * @param animationId 动画UUID
     */
    public void resumeAnimation(UUID animationId) {
        if (animationId == null) {
            return;
        }

        synchronized (lock) {
            Animation animation = animationMap.get(animationId);
            if (animation != null && animation.isPaused()) {
                animation.resume();
                log.debug("动画[{}]已恢复", animationId);
            }
        }
    }

    /**
     * 移除指定动画（仅移除管理，不触发取消）
     * @param animationId 动画UUID
     */
    public void removeAnimation(UUID animationId) {
        if (animationId == null) {
            return;
        }

        synchronized (lock) {
            animationMap.remove(animationId);
            log.debug("动画[{}]已从引擎中移除，当前管理的动画数：{}", animationId, animationMap.size());
        }
    }

    // -------------------- 批量控制方法 --------------------

    /**
     * 取消所有动画
     */
    public void cancelAllAnimations() {
        synchronized (lock) {
            if (animationMap.isEmpty()) {
                log.debug("无正在管理的动画，无需取消");
                return;
            }

            // 批量取消
            animationMap.values().forEach(Animation::cancel);
            // 清空Map
            animationMap.clear();
            log.debug("已取消并移除所有动画，当前管理的动画数：0");
        }
    }

    /**
     * 暂停所有运行中的动画
     */
    public void pauseAllAnimations() {
        synchronized (lock) {
            long pausedCount = animationMap.values().stream()
                    .filter(anim -> anim.isRunning() && !anim.isPaused())
                    .count();
            log.debug("已暂停{}个动画", pausedCount);
        }
    }

    /**
     * 恢复所有暂停的动画
     */
    public void resumeAllAnimations() {
        synchronized (lock) {
            long resumedCount = animationMap.values().stream()
                    .filter(Animation::isPaused)
                    .count();
            log.debug("已恢复{}个动画", resumedCount);
        }
    }

    // -------------------- 查询方法 --------------------

    /**
     * 获取指定ID的动画实例
     * @param animationId 动画UUID
     * @return 动画实例（null表示不存在）
     */
    public Animation getAnimation(UUID animationId) {
        if (animationId == null) {
            return null;
        }
        return animationMap.get(animationId);
    }

    /**
     * 获取指定组件的所有动画
     * @param node 目标BaseLabel组件
     * @return 该组件的动画列表
     */
    public List<Animation> getAnimationsByNode(BaseLabel node) {
        if (node == null) {
            return Collections.emptyList();
        }

        synchronized (lock) {
            return animationMap.values().stream()
                    .filter(anim -> node.equals(anim.getNode()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取所有运行中的动画
     * @return 运行中的动画列表
     */
    public List<Animation> getRunningAnimations() {
        synchronized (lock) {
            return animationMap.values().stream()
                    .filter(Animation::isRunning)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取引擎管理的动画总数
     */
    public int getAnimationCount() {
        return animationMap.size();
    }

    /**
     * 检查指定动画是否正在运行
     * @param animationId 动画UUID
     */
    public boolean isAnimationRunning(UUID animationId) {
        Animation animation = getAnimation(animationId);
        return animation != null && animation.isRunning();
    }
}