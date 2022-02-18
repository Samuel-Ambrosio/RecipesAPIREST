package domain.utils;

import domain.entities.User;
import play.libs.typedmap.TypedKey;

public class Attrs {
    public static final TypedKey<User> USER = TypedKey.create("user");
}