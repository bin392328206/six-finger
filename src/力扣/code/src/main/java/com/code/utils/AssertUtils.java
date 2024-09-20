package main.java.com.code.utils;

import com.yy.zbase.external.yyworld.to.domain.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

public final class AssertUtils {

    private AssertUtils() {
        // private
    }

    /**
     * 断言一个条件为True, 如果条件为False就抛BusinessException(message)
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言一个条件为True, 如果条件为False就抛BusinessException(message, args)
     */
    public static void isTrue(boolean expression, String message, Object... args) {
        if (!expression) {
            throw new BusinessException(message, args);
        }
    }

    /**
     * 断言一个条件为True, 如果条件为False就抛BusinessException(code, message)
     */
    public static void isTrue(boolean expression, int code, String message) {
        if (!expression) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言一个条件为True, 如果条件为False就抛BusinessException(code, message, args)
     */
    public static void isTrue(boolean expression, int code, String message, Object... args) {
        if (!expression) {
            throw new BusinessException(code, message, args);
        }
    }

    /**
     * 断言一个条件为False, 如果条件为True就抛BusinessException(message)
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言一个条件为False, 如果条件为True就抛new BusinessException(message, args)
     */
    public static void isFalse(boolean expression, String message, Object... args) {
        if (expression) {
            throw new BusinessException(message, args);
        }
    }

    /**
     * 断言一个条件为False, 如果条件为True就抛BusinessException(code, message)
     */
    public static void isFalse(boolean expression, int code, String message) {
        if (expression) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言一个条件为False, 如果条件为True就抛BusinessException(code, message, args)
     */
    public static void isFalse(boolean expression, int code, String message, Object... args) {
        if (expression) {
            throw new BusinessException(code, message, args);
        }
    }

    /**
     * 断言一个对象为空, 如果非空就抛BusinessException(message)
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言一个对象为空, 如果非空就抛BusinessException(message, args)
     */
    public static void isNull(Object object, String message, Object... args) {
        if (object != null) {
            throw new BusinessException(message, args);
        }
    }

    /**
     * 断言一个对象为空, 如果非空就抛BusinessException(code, message)
     */
    public static void isNull(Object object, int code, String message) {
        if (object != null) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言一个对象为空, 如果非空就抛BusinessException(code, message, args)
     */
    public static void isNull(Object object, int code, String message, Object... args) {
        if (object != null) {
            throw new BusinessException(code, message, args);
        }
    }

    /**
     * 断言一个对象不为空, 如果空就抛BusinessException(message)
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言一个对象不为空, 如果空就抛BusinessException(message, args)
     */
    public static void notNull(Object object, String message, Object... args) {
        if (object == null) {
            throw new BusinessException(message, args);
        }
    }

    /**
     * 断言一个对象不为空, 如果空就抛BusinessException(code, message)
     */
    public static void notNull(Object object, int code, String message) {
        if (object == null) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言一个对象不为空, 如果空就抛BusinessException(code, message, args)
     */
    public static void notNull(Object object, int code, String message, Object... args) {
        if (object == null) {
            throw new BusinessException(code, message, args);
        }
    }

    /**
     * 断言一个字符串为空, 如果不为空就抛BusinessException(message)
     */
    public static void isBlank(String content, String message) {
        if (StringUtils.isNotBlank(content)) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言一个字符串为空, 如果不为空就抛BusinessException(message, args)
     */
    public static void isBlank(String content, String message, Object... args) {
        if (StringUtils.isNotBlank(content)) {
            throw new BusinessException(message, args);
        }
    }

    /**
     * 断言一个字符串为空, 如果不为空就抛BusinessException(code, message)
     */
    public static void isBlank(String content, int code, String message) {
        if (StringUtils.isNotBlank(content)) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言一个字符串为空, 如果不为空就抛BusinessException(code, message, args)
     */
    public static void isBlank(String content, int code, String message, Object... args) {
        if (StringUtils.isNotBlank(content)) {
            throw new BusinessException(code, message, args);
        }
    }

    /**
     * 断言一个字符串不为空, 如果为空就抛BusinessException(message)
     */
    public static void notBlank(String content, String message) {
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言一个字符串不为空, 如果为空就抛BusinessException(message, args)
     */
    public static void notBlank(String content, String message, Object... args) {
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(message, args);
        }
    }

    /**
     * 断言一个字符串不为空, 如果为空就抛BusinessException(code, message)
     */
    public static void notBlank(String content, int code, String message) {
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言一个字符串不为空, 如果为空就抛BusinessException(code, message, args)
     */
    public static void notBlank(String content, int code, String message, Object... args) {
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(code, message, args);
        }
    }


    /**
     * 断言一个集合不为空, 如果为空就抛ServiceException(message)
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言一个集合不为空, 如果为空就抛ServiceException(message, args)
     */
    public static void notEmpty(Collection<?> collection, String message, Object... args) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(StrFormatter.format(message, args));
        }
    }

    /**
     * 断言一个集合不为空, 如果为空就抛ServiceException(code, message)
     */
    public static void notEmpty(Collection<?> collection, int code, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言一个集合不为空, 如果为空就抛ServiceException(code, message, args)
     */
    public static void notEmpty(Collection<?> collection, int code, String message, Object... args) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(code, StrFormatter.format(message, args));
        }
    }


    /**
     * 断言一个Map不为空, 如果为空就抛ServiceException(message)
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(message);
        }
    }

    /**
     * 断言一个Map不为空, 如果为空就抛ServiceException(message, args)
     */
    public static void notEmpty(Map<?, ?> map, String message, Object... args) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(StrFormatter.format(message, args));
        }
    }

    /**
     * 断言一个Map不为空, 如果为空就抛ServiceException(code, message)
     */
    public static void notEmpty(Map<?, ?> map, int code, String message) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 断言一个Map不为空, 如果为空就抛ServiceException(code, message, args)
     */
    public static void notEmpty(Map<?, ?> map, int code, String message, Object... args) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(code, StrFormatter.format(message, args));
        }
    }


}
