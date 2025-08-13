package io.github.some_example_name.shared.model.actions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,     // بر اساس نام زیرکلاس
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"               // در JSON منتظر فیلدی به نام "type" می‌مانیم
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MoveAction.class, name = "move"),
//    @JsonSubTypes.Type(value = FeedAction.class, name = "feed"),
    // @JsonSubTypes.Type(value = CollectAction.class, name = "collect"),
    // سایر اکشن‌های شما …
})
public abstract class Action implements Serializable {
    private static long counter = 0;
    private final long id;

    public Action() {
        this.id = ++counter;
    }

    public long getId() {
        return id;
    }
}
