package model.Plant;

import java.util.HashMap;
import java.util.Map;
// اگر تایپ‌های دیگه‌ای هم داری اینجا import کن
// import model.Plant.types.TreeType;
// import model.Plant.types.FlowerType;

public class PLantRegistry {

    private static final Map<String, PlantType> byName = new HashMap<>();

    static {
        // ثبت همه‌ی محصولات کشاورزی (CropType)
        for (CropType crop : CropType.values()) {
            byName.put(crop.getName().toLowerCase(), crop);
        }

        // ثبت همه‌ی محصولات قابل جمع‌آوری از طبیعت (ForageCropType)
        for (ForCropType forage : ForCropType.values()) {
            byName.put(forage.getName().toLowerCase(), forage);
        }

        // در صورت نیاز: تایپ‌های دیگر
        /*
        for (TreeType tree : TreeType.values()) {
            byName.put(tree.getName().toLowerCase(), tree);
        }

        for (FlowerType flower : FlowerType.values()) {
            byName.put(flower.getName().toLowerCase(), flower);
        }
        */
    }

    /**
     * با نام گیاه، نمونه‌ی مربوط به آن را برمی‌گرداند.
     * اگر پیدا نشد، null برمی‌گردد.
     */
    public static PlantType lookup(String name) {
        if (name == null) return null;
        return byName.get(name.toLowerCase());
    }

    /**
     * بررسی اینکه آیا گیاه با نام خاصی در رجیستری وجود دارد یا نه
     */
    public static boolean exists(String name) {
        return name != null && byName.containsKey(name.toLowerCase());
    }

    /**
     * ثبت دستی یک گیاه جدید در رجیستری (مثلاً اگر از بیرون داینامیک بارگذاری شود)
     */
    public static void register(PlantType plant) {
        if (plant != null) {
            byName.put(plant.getName().toLowerCase(), plant);
        }
    }
}
