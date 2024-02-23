package it.polimi.ingsw.Common;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Serializer for Gson Interfaces
 *
 * @param <T> effective type
 */
public final class GsonInterfaceSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private final Class<T> implementationClass;

    private GsonInterfaceSerializer(final Class<T> implementationClass) {
        this.implementationClass = implementationClass;
    }

    /**
     * Implementation of Gson Interface Serializer
     *
     * @param implementationClass effective type
     * @param <T>                 object type
     * @return serializer
     */
    public static <T> GsonInterfaceSerializer<T> interfaceSerializer(final Class<T> implementationClass) {
        return new GsonInterfaceSerializer<>(implementationClass);
    }

    @Override
    public JsonElement serialize(final T value, final Type type, final JsonSerializationContext context) {
        final Type targetType = value != null
                ? value.getClass()
                : type;
        return context.serialize(value, targetType);
    }

    @Override
    public T deserialize(final JsonElement jsonElement, final Type typeOfT, final JsonDeserializationContext context) {
        return context.deserialize(jsonElement, implementationClass);
    }
}