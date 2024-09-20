package main.java.com.code.utils;

import com.yy.zbase.external.yyworld.to.domain.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

public final class AsyncUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncUtils.class);

    private AsyncUtils() {
        // private
    }

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return runAsync(runnable, ForkJoinPool.commonPool());
    }

    public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor) {
        Map<?, ?> contextMap = MDC.getCopyOfContextMap();
        return CompletableFuture.runAsync(() -> {
            MDC.setContextMap(contextMap != null ? contextMap : Collections.emptyMap());
            try {
                runnable.run();
            } catch (Exception e) {
                LOGGER.error("执行异步方法出错.", e);
            } finally {
                MDC.clear();
            }
        }, executor);
    }

    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return supplyAsync(supplier, ForkJoinPool.commonPool());
    }

    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor) {
        Map<?, ?> contextMap = MDC.getCopyOfContextMap();
        return CompletableFuture.supplyAsync(() -> {
            MDC.setContextMap(contextMap != null ? contextMap : Collections.emptyMap());
            try {
                return supplier.get();
            } catch (BusinessException be) {
                throw be;
            } catch (Exception e) {
                LOGGER.error("执行异步方法出错.", e);
                throw new BusinessException("系统异常，请稍后再试");
            } finally {
                MDC.clear();
            }
        }, executor);
    }

    public static <U> U get(CompletableFuture<U> future) {
        try {
            return future.get();
        } catch (ExecutionException e) {
            throw (RuntimeException) e.getCause();
        } catch (Exception e) {
            LOGGER.error("执行异步方法出错.", e);
            throw new BusinessException("系统异常，请稍后再试");
        }
    }

    public static <U> U join(CompletableFuture<U> future) {
        try {
            return future.join();
        } catch (CompletionException e) {
            throw (RuntimeException) e.getCause();
        }
    }

    public static void join(CompletableFuture<?>... futures) {
        try {
            CompletableFuture.allOf(futures).join();
        } catch (CompletionException e) {
            throw (RuntimeException) e.getCause();
        }
    }

}
