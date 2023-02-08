package ac.artemis.anticheat.api.check.type;


import ac.artemis.anticheat.api.material.NMSMaterial;

public enum Category {
    COMBAT(NMSMaterial.DIAMOND_SWORD,
            "&7Combat checks are such an important ones",
            "&7They provide full protection against the",
            "&7most dangerous types of cheaters"),
    MOVEMENT(NMSMaterial.FEATHER,""),
    PLAYER(NMSMaterial.PLAYER_HEAD,""),
    EXPLOIT(NMSMaterial.TNT,""),
    MISC(NMSMaterial.PEONY,"");

    private final NMSMaterial material;
    private final String[] description;


    public String[] getDescription() {
        return description;
    }

    Category(NMSMaterial material, String... description) {
        this.material = material;
        this.description = description;
    }
}
