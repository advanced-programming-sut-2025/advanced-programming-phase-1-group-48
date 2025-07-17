package model.Plant;

import model.items.Item;

public interface PlantInstance {
    /** پیشرفت یک روز (رشد یا شمارش دوره) */
    void growOneDay();

    /** ریستِ پرچم آب‌دهی برای روز جدید */
    void resetWaterFlag();

    /** آب دادن: ست کردن پرچم wateredToday */
    void water();

    /** آیا این گیاه آمادهٔ برداشت است؟ */
    boolean isReadyToHarvest();

    /** برداشت: تولید آیتم یا null */
    Item harvest();
}
