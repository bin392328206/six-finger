package main.java.com.code.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public final class BeanConvertUtils {

    private BeanConvertUtils() {
        // private
    }

    public static <K, T> K convert(T source, Class<K> targetClass) {
        return convert(source, targetClass, null);
    }

    public static <K, T> K convert(T source, Class<K> targetClass, Handler<T, K> handler) {
        K target = null;
        if (source != null) {
            try {
                target = targetClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, target);
                if (handler != null) {
                    handler.handle(source, target);
                }
            } catch (Exception e) {
                log.error("error convert bean[{}] to bean[{}]. ", source.getClass().getSimpleName(), targetClass.getSimpleName(), e);
            }
        }
        return target;
    }

    public static <K, T> List<K> convertList(List<T> sources, Class<K> targetClass) {
        return convertList(sources, targetClass, null);
    }

    public static <K, T> List<K> convertList(List<T> sources, Class<K> targetClass, Handler<T, K> handler) {
        List<K> targets = Collections.emptyList();
        if (sources != null && sources.size() > 0) {
            targets = sources.stream().map(source -> convert(source, targetClass, handler)).filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return targets;
    }

    @FunctionalInterface
    public interface Handler<T, K> {
        void handle(T source, K target);
    }

}



