package com.ararita.game.items;

import com.ararita.game.Global;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Weapon extends Item {

    final Map<String, Integer> attributesAffection;
    String weaponType;

    /**
     * Weapon's constructor.
     *
     * @param name The weapon's unique name.
     * @param price The weapon's price.
     * @param description A simple description.
     * @param attributesAffection A map of the attributes' affection.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public Weapon(String name, int price, String description, Map<String, Integer> attributesAffection, String weaponType) throws IOException {
        super(name, price, "Weapon", description);
        this.attributesAffection = attributesAffection;
        if (Global.isPresentInJSONList(Global.globalSets, weaponType, "weaponTypesSet")) {
            this.weaponType = weaponType;
            Global.addWeapon(this);
        } else {
            try {
                throw new InvalidNameException("ERROR: The given weaponType doesn't match any type inside the global " + "manager. Check your spelling" + ".");
            } catch (InvalidNameException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Map<String, Integer> getAttributesAffection() {
        return attributesAffection;
    }

    public String getWeaponType() {
        return weaponType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Weapon weapon = (Weapon) o;
        return Objects.equals(attributesAffection, weapon.attributesAffection) && Objects.equals(weaponType, weapon.weaponType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), attributesAffection, weaponType);
    }
}
