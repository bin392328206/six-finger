package main.java.com.code.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * 批量处理工具类
 */
public class BatchUtils {

    private BatchUtils() {
        // private
    }

    /**
     * 内存分批处理数据 无返回值
     * @param list 数据列表
     * @param batchSize 分页大小
     * @param consumer 处理函数
     */
    public static <T> void run(List<T> list, int batchSize, Consumer<List<T>> consumer) {
        run(list, batchSize, consumer, false);
    }


    /**
     * 内存分批处理数据 无返回值
     * @param list 数据列表
     * @param batchSize 分页大小
     * @param consumer 处理函数
     * @param concurrent 是否并行处理（*不保证顺序）
     */
    public static <T> void run(List<T> list, int batchSize, Consumer<List<T>> consumer, boolean concurrent) {
        run(list, batchSize, consumer, concurrent, ForkJoinPool.commonPool());
    }

    /**
     * 多线程内存分批处理数据 无返回值
     * @param list 数据列表
     * @param batchSize 分页大小
     * @param consumer 处理函数
     * @param executor 线程池
     */
    public static <T> void run(List<T> list, int batchSize, Consumer<List<T>> consumer, Executor executor) {
        run(list, batchSize, consumer, true, executor);
    }

    /**
     * 内存分批处理数据 无返回值
     * @param list 数据列表
     * @param batchSize 分页大小
     * @param consumer 处理函数
     * @param concurrent 是否并行处理（*不保证顺序）
     */
    private static <T> void run(List<T> list, int batchSize, Consumer<List<T>> consumer, boolean concurrent, Executor executor) {
        if (list == null || list.isEmpty()) {
            return;
        }
        int total = list.size();
        int fromIdx = 0;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        while (fromIdx < total) {
            int toIdx;
            if (total - fromIdx >= batchSize) {
                toIdx = fromIdx + batchSize;
            } else {
                toIdx = total;
            }
            List<T> subList = list.subList(fromIdx, toIdx);
            if (concurrent) {
                futures.add(AsyncUtils.runAsync(() -> consumer.accept(subList), executor));
            } else {
                consumer.accept(subList);
            }
            fromIdx = toIdx;
        }
        if (futures.size() > 0) {
            AsyncUtils.join(futures.toArray(new CompletableFuture[0]));
        }
    }

    /**
     * 分页拉取全部数据
     *
     * @param batchSize 分页大小
     * @param function 查询函数
     * @return 全部数据集合
     */
    public static <R> List<R> list(int batchSize, BiFunction<Integer, Integer, List<R>> function) {
        List<R> result = new ArrayList<>();
        R pre = null;
        for (int page = 1; ; page++) {
            List<R> pageData = function.apply(page, batchSize);
            if (pageData == null || pageData.isEmpty() || pageData.get(0).equals(pre)) {
                break;
            }
            // 对比第一条数据 防止一直返回重复数据导致死循环
            pre = pageData.get(0);
            result.addAll(pageData);
            if (pageData.size() < batchSize) {
                break;
            }
        }
        return result;
    }


}
