package teamssavice.ssavice.serviceItem.constants;

import java.util.Arrays;

public enum SortType {
    LATEST(0),
    PRICE_ASC(1),
    PRICE_DESC(2),
    DISCOUNT_RATE(3),
    DISTANCE(4);

    private final int code;

    SortType(int code) {
        this.code = code;
    }

    public static SortType from(Integer code) {
        if (code == null) return LATEST;
        return Arrays.stream(values())
                .filter(s -> s.code == code)
                .findFirst()
                .orElse(LATEST);
    }
}