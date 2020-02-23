package io.github.thatsmusic99.headsplus.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumUtil {

    public static Object[] getEnumResults(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getMethod("values");
        return (Object[]) method.invoke(clazz);
    }
}
