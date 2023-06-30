package org.aujee.com.shared.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class UtilBucket {

    private static final Map<String, Class<?>> PRIM_WRAP_MAP = HashMap.newHashMap(16);

    private UtilBucket() {}

    static {
        PRIM_WRAP_MAP.put("short", Short.TYPE);
        PRIM_WRAP_MAP.put("Short", Short.class);
        PRIM_WRAP_MAP.put("byte", Byte.TYPE);
        PRIM_WRAP_MAP.put("Byte", Byte.class);
        PRIM_WRAP_MAP.put("int", Integer.TYPE);
        PRIM_WRAP_MAP.put("Integer", Integer.class);
        PRIM_WRAP_MAP.put("long", Long.TYPE);
        PRIM_WRAP_MAP.put("Long", Long.class);
        PRIM_WRAP_MAP.put("float", Float.TYPE);
        PRIM_WRAP_MAP.put("Float", Float.class);
        PRIM_WRAP_MAP.put("double", Double.TYPE);
        PRIM_WRAP_MAP.put("Double", Double.class);
        PRIM_WRAP_MAP.put("boolean", Boolean.TYPE);
        PRIM_WRAP_MAP.put("Boolean", Boolean.class);
        PRIM_WRAP_MAP.put("char", Character.TYPE);
        PRIM_WRAP_MAP.put("Character", Character.class);
    }

    public static Throwable getRootCause(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    public static Object getPrimitiveVal(String primitive, String value) {

        return switch (primitive) {
            case "Short", "short" -> Short.parseShort(value);
            case "Byte", "byte" -> Byte.parseByte(value);
            case "Integer", "int" -> Integer.parseInt(value);
            case "Long", "long" -> Long.parseLong(value);
            case "Float", "float" -> Float.parseFloat(value);
            case "Double", "double" -> Double.parseDouble(value);
            case "Boolean", "boolean" -> Boolean.parseBoolean(value);
            case "Character", "char" -> value.charAt(0);
            default -> throw new IllegalStateException(
                    "Unexpected value: " + primitive);
        };
    }

    public static Class<?> getPrimOrWrapClass(String primOrWrap) {
        return PRIM_WRAP_MAP.get(primOrWrap);
    }

    public static boolean isString(String typeName) {
        return "String".equals(typeName);
    }
}
