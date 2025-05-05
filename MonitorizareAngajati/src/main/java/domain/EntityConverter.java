package domain;

public abstract class EntityConverter<T extends Entity> {
    public abstract String toString(T shape);

    public abstract T fromString(String string);
}
