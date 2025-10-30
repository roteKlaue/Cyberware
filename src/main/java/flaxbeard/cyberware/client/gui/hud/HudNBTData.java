package flaxbeard.cyberware.client.gui.hud;

import flaxbeard.cyberware.api.hud.IHudSaveData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.nbt.CompoundNBT;

@Getter
@AllArgsConstructor
public class HudNBTData implements IHudSaveData {
    private final CompoundNBT tag;

    @Override
    public void setString(String key, String s) {
        tag.putString(key, s);
    }

    @Override
    public void setInteger(String key, int i) {
        tag.putInt(key, i);
    }

    @Override
    public void setBoolean(String key, boolean b) {
        tag.putBoolean(key, b);
    }

    @Override
    public void setFloat(String key, float f) {
        tag.putFloat(key, f);
    }

    @Override
    public String getString(String key) {
        return tag.getString(key);
    }

    @Override
    public int getInteger(String key) {
        return tag.getInt(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return tag.getBoolean(key);
    }

    @Override
    public float getFloat(String key) {
        return tag.getFloat(key);
    }
}
