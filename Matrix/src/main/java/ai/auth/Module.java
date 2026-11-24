package ai.auth;

import com.matrix.client.config.BooleanSetting;
import com.matrix.client.config.ColorSetting;
import com.matrix.client.config.ConfigSetting;
import com.matrix.client.config.DoubleSetting;
import com.matrix.client.config.EnumSetting;
import com.matrix.client.config.IntegerSetting;
import com.matrix.client.config.KeybindSetting;
import com.matrix.client.config.ModuleConfigAutosave;
import com.matrix.client.config.ModuleIdentities;
import com.matrix.client.config.RangeSetting;
import com.matrix.client.config.StringBase;
import com.matrix.client.gui.Button;
import com.matrix.client.gui.TickProgress;
import com.matrix.client.gui.ZoomHud;
import com.matrix.client.render.QueueCommand;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import com.matrix.client.utils.Cooldown;
import com.matrix.client.utils.AutoVanish;
import com.matrix.client.utils.InventoryPlayer;
import com.matrix.client.utils.PanicDestructUtils;
import com.matrix.client.utils.HudShowUtils;
import com.matrix.client.utils.AutoYourUtils;
import com.matrix.client.utils.DeltaAngle;
import com.matrix.client.utils.IconsPngType;
import com.matrix.client.utils.PlayerScaffold;
import com.matrix.client.utils.ScaffoldTrade;
import com.matrix.client.utils.SwordBot;

public class Module implements PanicDestructUtils.Target, HudShowUtils.View {
    public static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String category;
    private final IconsPngType pageType;
    private boolean enabled = false;
    private final List<ConfigSetting> settings = new ArrayList<>();
    private int bindKey = -1;

    public Module(String category, IconsPngType name) {
        this.category = category != null ? category : "Unknown";
        this.pageType = name != null ? name : IconsPngType.MISC;
    }

    public void toggle() {
        this.setEnabled(!this.enabled);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
        ModuleConfigAutosave.updateFlag2();
    }

    public void loadExtraData(JsonElement element) {
    }

    public final String getName() {
        return ModuleIdentities.getProperty(this.getClass());
    }

    protected void onZoomHud(ZoomHud event) {
    }

    protected void onRender3D(QueueCommand render3DEvent) {
    }

    protected void onPlayerScaffold(PlayerScaffold event) {
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public void onConfigLoaded() {
    }

    public boolean isHidden() {
        return this.isHiddenModule();
    }

    public boolean isHiddenModule() {
        return false;
    }

    public Identifier getIconId() {
        return this.getPageType().getIdentifier();
    }

    protected void onPacketReceive(AutoVanish packetReceiveEvent) {
    }

    public IconsPngType getPageType() {
        return AutoYourUtils.canUpdatePvp(this) ? IconsPngType.BOTS : this.pageType;
    }

    public void syncSettingHandlers() {
        for (ConfigSetting configSetting : this.settings) {
            if (configSetting instanceof BooleanSetting booleanSetting) {
                booleanSetting.handle(booleanSetting.isFlag3());
            } else if (configSetting instanceof IntegerSetting integerSetting) {
                integerSetting.handleMax(integerSetting.getValue3());
            } else if (configSetting instanceof DoubleSetting doubleSetting) {
                doubleSetting.handleMaxAmount(doubleSetting.getAmount3());
            } else if (configSetting instanceof EnumSetting enumSetting) {
                enumSetting.handle(enumSetting.getValue());
            } else if (configSetting instanceof StringBase stringBase) {
                stringBase.handleText(stringBase.getText());
            } else if (configSetting instanceof ColorSetting colorSetting) {
                colorSetting.update();
            } else if (configSetting instanceof RangeSetting rangeSetting) {
                rangeSetting.handleMaxAmount2(rangeSetting.getAmount2());
                rangeSetting.handleMaxAmount(rangeSetting.getAmount3());
            } else if (configSetting instanceof KeybindSetting keybindSetting) {
                keybindSetting.setValue2(keybindSetting.getValue());
            }
        }
        ModuleConfigAutosave.updateFlag();
    }

    protected void onPostItemUse(Cooldown postItemUseEvent) {
    }

    public BindMode getBindMode() {
        return BindMode.TOGGLE;
    }

    public boolean isVisible() {
        return !this.isHiddenModule();
    }

    public boolean requiresContainerBindPolling() {
        return false;
    }

    public JsonElement saveExtraData() {
        return null;
    }

    protected void onCameraAngle(DeltaAngle cameraEvent) {
    }

    public boolean isVisibleInModuleList() {
        return this.pageType != IconsPngType.SETTING;
    }

    protected void onReset() {
    }

    protected void onRender2D(TickProgress render2DEvent) {
    }

    public boolean hasExtraData() {
        return false;
    }

    protected void onPreItemUse(ScaffoldTrade preItemUseEvent) {
    }

    protected void onSwordBot(SwordBot event) {
    }

    protected void onButtonClick(Button event) {
    }

    public List<ConfigSetting> getSettings() {
        return this.settings;
    }

    public void addSetting(ConfigSetting setting) {
        this.settings.add(setting);
    }

    public String getCategory() {
        return this.category;
    }

    public void addSettings(ConfigSetting... settings) {
        for (ConfigSetting configSetting : settings) {
            this.addSetting(configSetting);
        }
    }

    protected void onPacketSend(InventoryPlayer packetSendEvent) {
    }

    public int getBindKey() {
        return this.bindKey;
    }

    public void setBindKey(int bindKey) {
        this.bindKey = bindKey;
        ModuleConfigAutosave.updateFlag2();
    }

    public enum BindMode {
        TOGGLE, HOLD
    }
}
