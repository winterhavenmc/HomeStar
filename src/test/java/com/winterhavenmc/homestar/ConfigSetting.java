package com.winterhavenmc.homestar;


public enum ConfigSetting {

    LANGUAGE("language", "en-US"),
    ENABLED_WORLDS("enabled-worlds", "[]"),
    DISABLED_WORLDS("disabled-worlds", "[disabled_world1, disabled_world2]"),
    ITEM_MATERIAL("item-material", "NETHER_STAR"),
    MINIMUM_DISTANCE("minimum-distance", "10"),
    CENTER_ON_BLOCK("center-on-block", "true"),
    TELEPORT_COOLDOWN("teleport-cooldown", "60"),
    TELEPORT_WARMUP("teleport-warmup", "5"),
    SOUND_EFFECTS("sound-effects", "true"),
    PARTICLE_EFFECTS("particle-effects", "true"),
    LEFT_CLICK("left-click","false"),
    SHIFT_CLICK("shift-click", "true"),
    REMOVE_FROM_INVENTORY("remove-from-inventory", "on-success"),
    ALLOW_IN_RECIPES("allow-in-recipes", "false"),
    CANCEL_ON_DAMAGE("cancel-on-damage", "false"),
    CANCEL_ON_MOVEMENT("cancel-on-movement", "false"),
    CANCEL_ON_INTERACTION("cancel-on-interaction", "false"),
    MAX_GIVE_AMOUNT("max-give-amount", "-1"),
    FROM_NETHER("from-nether", "true"),
    FROM_END("from-end", "true"),
    LIGHTNING("lightning", "false"),
    LOG_USE("log-use", "true");


    private final String key;
    private final String value;

    ConfigSetting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }
    public String getValue() {
        return this.value;
    }

}
