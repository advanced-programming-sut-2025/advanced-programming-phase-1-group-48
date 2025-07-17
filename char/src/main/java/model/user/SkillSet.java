package model.user;

import model.enums.SkillType;

import java.util.HashMap;
import java.util.Map;

public class SkillSet {
    private Map<SkillType, Integer> points;

    public SkillSet() {
    }

    public void gainSkill(SkillType type, int value) {
        points.put(type, points.get(type) + value);
    }

    public int getLevel(SkillType type) {
        return 0;
    }
}


