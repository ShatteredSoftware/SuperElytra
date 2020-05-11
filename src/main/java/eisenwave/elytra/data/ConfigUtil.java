package eisenwave.elytra.data;

import com.google.common.base.CaseFormat;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.bukkit.Material;

public final class ConfigUtil {

    private ConfigUtil() {}

    /**
     * Uses reflection to write a serializer. Works well with
     * {@link #getInEnum(Map, String, Class, Enum)}, and serializes things in such a way that its use
     * is expected.
     *
     * @param instance The instance to serialize.
     * @param clazz The class being serialized.
     * @param <T> The type of the class being serialized.
     * @return A serialized map of the class.
     */
    public static <T> Map<String, Object> reflectiveSerialize(T instance, Class<T> clazz) {
        Map<String, Object> map = new LinkedHashMap<>();
        for(Field field : clazz.getFields()) {
            try {
                if(Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                if(field.getType().isEnum()) {
                    map.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.getName()), ((Enum) field.get(instance)).name());
                    continue;
                }
                map.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.getName()), field.get(instance));
            }
            catch (IllegalAccessException ignored) { }
        }
        return map;
    }

    /**
     * Gets something from a map, defaulting if it's not found or is of the wrong type.
     *
     * @param map The map to look through.
     * @param key The key to lookup.
     * @param clazz The class of the expected result.
     * @param def The default value.
     * @param <T> The type of the expected result.
     * @return The value if it exists, or the default otherwise.
     */
    public static <T> T getIfValid(Map<String, Object> map, String key, Class<T> clazz, T def) {
        if (!map.containsKey(key)) {
            return def;
        }
        Object obj = map.get(key);
        if (obj == null) {
            return def;
        }
        if(!obj.getClass().isAssignableFrom(clazz)) {
            System.err.println("Found " + key +
                " in config with wrong type " + obj.getClass().getName() + " (expected " +
                clazz.getName() + "). Using default " + def + ".");
            return def;
        }
        return clazz.cast(map.get(key));
    }

    /**
     * By making a few assumptions, we can pick the default in multiple cases.
     *
     * The assumptions are:<br/>
     * 1. We're picking from an enum<br/>
     * 2. The value is a string<br/>
     * 3. A no-match should default.<br/>
     *
     * @param map The map to look through.
     * @param key The key to use.
     * @param clazz The enum type.
     * @param def The default value.
     * @param <T> The enum type.
     * @return The enum choice if it exists, the default otherwise.
     */
    public static <T extends Enum<T>> T getInEnum(Map<String, Object> map, String key, Class<T> clazz, T def) {
        String val = getIfValid(map, key, String.class, def.name());
        if(Stream.of(clazz.getEnumConstants()).parallel().anyMatch(it -> it.name().equalsIgnoreCase(val))) {
            return T.valueOf(clazz, val.toUpperCase());
        }
        return def;
    }

}
