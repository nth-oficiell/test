package ai.auth;

import com.vortex.client.entity.ClientThreadUtils;
import com.vortex.client.gui.Button;
import com.vortex.client.gui.TickProgress;
import com.vortex.client.gui.ZoomHud;
import com.vortex.client.render.QueueCommand;
import com.vortex.client.render.VertexBuilderUtils;
import com.vortex.client.world.MathClampUtils;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.util.math.MatrixStack;
import com.vortex.client.categories.bots.AutoElytraPilot;
import com.vortex.client.categories.bots.AutoTpaHere;
import com.vortex.client.categories.bots.CrystalPvpBot;
import com.vortex.client.utils.PlayerInputOverride;
import com.vortex.client.categories.bots.SwordPvpBot;
import com.vortex.client.categories.combat.AimAssist;
import com.vortex.client.categories.combat.AnchorMacro;
import com.vortex.client.categories.combat.AutoAggroPearl;
import com.vortex.client.categories.combat.AutoCart;
import com.vortex.client.categories.combat.AutoClicker;
import com.vortex.client.categories.combat.AutoHitCrystal;
import com.vortex.client.categories.combat.AutoHotbar;
import com.vortex.client.categories.combat.AutoInventoryTotem;
import com.vortex.client.categories.combat.AutoJumpReset;
import com.vortex.client.categories.combat.AutoShieldBreaker;
import com.vortex.client.categories.combat.AutoTotem;
import com.vortex.client.categories.combat.AutoTrap;
import com.vortex.client.categories.combat.AutoXP;
import com.vortex.client.categories.combat.Criticals;
import com.vortex.client.categories.combat.CrystalAura;
import com.vortex.client.categories.combat.CrystalOptimizer;
import com.vortex.client.categories.combat.HitBoxes;
import com.vortex.client.categories.combat.HitSelect;
import com.vortex.client.categories.combat.HitSwap;
import com.vortex.client.categories.combat.KeyPearl;
import com.vortex.client.categories.combat.MaceBomber;
import com.vortex.client.categories.combat.ObsidianOptimizer;
import com.vortex.client.categories.combat.PerfectWindCharge;
import com.vortex.client.categories.combat.Reach;
import com.vortex.client.categories.combat.SafeAnchor;
import com.vortex.client.categories.combat.SilentAim;
import com.vortex.client.categories.combat.TridentAssist;
import com.vortex.client.categories.combat.TriggerBot;
import com.vortex.client.categories.combat.WTap;
import com.vortex.client.categories.bots.AnchorHunter;
import com.vortex.client.categories.donutsmp.AntiVanish;
import com.vortex.client.categories.bots.AutoAreaMiner;
import com.vortex.client.categories.bots.AutoAuctionFlipper;
import com.vortex.client.categories.bots.AutoBuilder;
import com.vortex.client.categories.bots.AutoElytraFinder;
import com.vortex.client.categories.donutsmp.AutoMine;
import com.vortex.client.categories.donutsmp.AutoSell;
import com.vortex.client.categories.donutsmp.AutoSellChest;
import com.vortex.client.categories.donutsmp.Bloodhound;
import com.vortex.client.categories.donutsmp.BoneDropper;
import com.vortex.client.categories.donutsmp.ChunkFinder;
import com.vortex.client.categories.donutsmp.ChunkFinderV2;
import com.vortex.client.categories.donutsmp.DeepPlayerFinder;
import com.vortex.client.categories.donutsmp.FakePay;
import com.vortex.client.categories.donutsmp.FakeStats;
import com.vortex.client.categories.donutsmp.GamblingRigger;
import com.vortex.client.categories.donutsmp.NetheriteFinder;
import com.vortex.client.categories.donutsmp.RegionMap;
import com.vortex.client.categories.donutsmp.Relog;
import com.vortex.client.categories.bots.RtpBaseAlert;
import com.vortex.client.categories.donutsmp.SeedAnalyzer;
import com.vortex.client.categories.donutsmp.SilentHome;
import com.vortex.client.categories.bots.SpawnerProtect;
import com.vortex.client.categories.donutsmp.SpecialItemBuyer;
import com.vortex.client.categories.donutsmp.StaffList;
import com.vortex.client.categories.donutsmp.SusChunkFinder;
import com.vortex.client.categories.donutsmp.SusChunkFinderV2;
import com.vortex.client.categories.bots.VillagerTradeBot;
import com.vortex.client.categories.donutsmp.WanderingTraderFinder;
import com.vortex.client.categories.misc.ACAlert;
import com.vortex.client.categories.misc.AutoPot;
import com.vortex.client.categories.misc.AutoPromo;
import com.vortex.client.categories.misc.AutoTool;
import com.vortex.client.categories.misc.BoatFly;
import com.vortex.client.categories.misc.Capes;
import com.vortex.client.utils.Cooldown;
import com.vortex.client.categories.misc.VortexRadio;
import com.vortex.client.categories.misc.CreamyKeys;
import com.vortex.client.categories.misc.CustomCrosshair;
import com.vortex.client.categories.misc.CustomSkin;
import com.vortex.client.categories.misc.FOVSlider;
import com.vortex.client.categories.misc.FakeCoordinates;
import com.vortex.client.categories.misc.FakePlayer;
import com.vortex.client.categories.misc.FastPlace;
import com.vortex.client.categories.misc.Fastbreak;
import com.vortex.client.categories.misc.InventoryCleaner;
import com.vortex.client.categories.misc.NameProtect;
import com.vortex.client.categories.misc.PotionRefill;
import com.vortex.client.categories.misc.SkinProtect;
import com.vortex.client.categories.misc.TimeChanger;
import com.vortex.client.categories.misc.WeatherChanger;
import com.vortex.client.categories.misc.WeatherNotifier;
import com.vortex.client.categories.misc.Zoom;
import com.vortex.client.categories.movement.AutoSpear;
import com.vortex.client.categories.movement.AutoWalk;
import com.vortex.client.categories.movement.Flight;
import com.vortex.client.categories.movement.Freecam;
import com.vortex.client.categories.movement.KeepSprint;
import com.vortex.client.categories.movement.PearlCatchAssist;
import com.vortex.client.categories.movement.Scaffold;
import com.vortex.client.categories.movement.SpeedHack;
import com.vortex.client.categories.movement.Sprint;
import com.vortex.client.utils.AutoVanish;
import com.vortex.client.categories.network.BackTrack;
import com.vortex.client.categories.network.Blink;
import com.vortex.client.utils.ConnectionTimeUtils;
import com.vortex.client.utils.EntityDamageUtils;
import com.vortex.client.categories.network.FakeLag;
import com.vortex.client.utils.InventoryPlayer;
import com.vortex.client.categories.network.KnockbackDelay;
import com.vortex.client.categories.settings.ClientSounds;
import com.vortex.client.categories.configs.Configs;
import com.vortex.client.categories.socials.Friends;
import com.vortex.client.categories.settings.GUISettings;
import com.vortex.client.categories.settings.License;
import com.vortex.client.categories.settings.MiddleClickFriend;
import com.vortex.client.categories.settings.Notifications;
import com.vortex.client.categories.settings.Panic;
import com.vortex.client.categories.settings.SelfDestruct;
import com.vortex.client.categories.theme.ThemeCustomizer;
import com.vortex.client.categories.visuals.AtmosphericParticles;
import com.vortex.client.categories.visuals.BlockESP;
import com.vortex.client.categories.visuals.BlockHighlight;
import com.vortex.client.categories.visuals.BlockOverlay;
import com.vortex.client.categories.visuals.CustomFlame;
import com.vortex.client.categories.visuals.CustomSky;
import com.vortex.client.categories.visuals.CustomWorld;
import com.vortex.client.categories.visuals.FireFly;
import com.vortex.client.categories.visuals.Freelook;
import com.vortex.client.categories.visuals.Fullbright;
import com.vortex.client.categories.visuals.HandCustomer;
import com.vortex.client.categories.visuals.HeatColor;
import com.vortex.client.categories.visuals.HoleESP;
import com.vortex.client.categories.visuals.JumpCircles;
import com.vortex.client.categories.visuals.LootESP;
import com.vortex.client.categories.visuals.MobESP;
import com.vortex.client.categories.visuals.NameTags;
import com.vortex.client.categories.visuals.NoExplosions;
import com.vortex.client.categories.visuals.NoFarmLag;
import com.vortex.client.categories.visuals.NoFlame;
import com.vortex.client.categories.visuals.Particles;
import com.vortex.client.categories.visuals.PearlCatch;
import com.vortex.client.categories.visuals.PearlPrediction;
import com.vortex.client.categories.visuals.PlayerESP;
import com.vortex.client.categories.visuals.RealisticWater;
import com.vortex.client.categories.visuals.ShowHUD;
import com.vortex.client.categories.visuals.SpawnerFinder;
import com.vortex.client.categories.visuals.SpotifyHUD;
import com.vortex.client.categories.visuals.StorageESP;
import com.vortex.client.categories.visuals.SwordInspect;
import com.vortex.client.categories.visuals.TargetESP;
import com.vortex.client.categories.visuals.TotemCounter;
import com.vortex.client.categories.visuals.Trails;
import com.vortex.client.categories.visuals.Waypoints;
import ai.auth.Module;
import com.vortex.client.utils.DeltaAngle;
import com.vortex.client.utils.DirExtractionUtils;
import com.vortex.client.utils.ExtUtils;
import com.vortex.client.utils.IconsPngType;
import com.vortex.client.utils.InstanceTimeUtils;
import com.vortex.client.utils.PlayerScaffold;
import com.vortex.client.utils.ScaffoldTrade;
import com.vortex.client.utils.SwordBot;

public class ModuleRegistry {
    private static final Map<Class<? extends Module>, Module> modulesByClass;
    private static final List<Module> modules;
    private static final Map<IconsPngType, List<Module>> modulesByCategory;

    static {
        modules = new ArrayList<>();
        modulesByCategory = new EnumMap<>(IconsPngType.class);
        modulesByClass = new IdentityHashMap<>();
    }

    private static void registerModule(Module module) {
        modules.add(module);
        modulesByClass.putIfAbsent(module.getClass(), module);
        modulesByCategory.get(module.getPageType()).add(module);
    }

    public static Module getModule(Class<? extends Module> clazz) {
        return modulesByClass.get(clazz);
    }

    public static List<Module> getModules() {
        return modules;
    }

    public static List<Module> getModulesByCategory(IconsPngType category) {
        return modulesByCategory.getOrDefault(category, new ArrayList<>());
    }

    public static void registerAllModules() {
        IconsPngType[] categories = IconsPngType.values();
        for (int i = 0; i < categories.length; ++i) {
            modulesByCategory.put(categories[i], new ArrayList<>());
        }
        registerModule(new ACAlert());
        registerModule(new AimAssist());
        registerModule(new AnchorHunter());
        registerModule(new AnchorMacro());
        registerModule(new AntiVanish());
        registerModule(new AtmosphericParticles());
        registerModule(new AutoAggroPearl());
        registerModule(new AutoAreaMiner());
        registerModule(new AutoAuctionFlipper());
        registerModule(new AutoBuilder());
        registerModule(new AutoCart());
        registerModule(new AutoClicker());
        registerModule(new AutoElytraFinder());
        registerModule(new AutoElytraPilot());
        registerModule(new AutoHitCrystal());
        registerModule(new AutoHotbar());
        registerModule(new AutoInventoryTotem());
        registerModule(new AutoJumpReset());
        registerModule(new AutoMine());
        registerModule(new AutoPot());
        registerModule(new AutoPromo());
        registerModule(new AutoSell());
        registerModule(new AutoSellChest());
        registerModule(new AutoShieldBreaker());
        registerModule(new AutoSpear());
        registerModule(new AutoTool());
        registerModule(new AutoTotem());
        registerModule(new AutoTpaHere());
        registerModule(new AutoTrap());
        registerModule(new AutoWalk());
        registerModule(new AutoXP());
        registerModule(new BackTrack());
        registerModule(new Blink());
        registerModule(new BlockESP());
        registerModule(new BlockHighlight());
        registerModule(new BlockOverlay());
        registerModule(new Bloodhound());
        registerModule(new BoatFly());
        registerModule(new BoneDropper());
        registerModule(new Capes());
        registerModule(new ChunkFinder());
        registerModule(new ChunkFinderV2());
        registerModule(new ClientSounds());
        registerModule(new Configs());
        registerModule(new VortexRadio());
        registerModule(new CreamyKeys());
        registerModule(new Criticals());
        registerModule(new CrystalAura());
        registerModule(new CrystalOptimizer());
        registerModule(new CrystalPvpBot());
        registerModule(new CustomCrosshair());
        registerModule(new CustomFlame());
        registerModule(new CustomSkin());
        registerModule(new CustomSky());
        registerModule(new CustomWorld());
        registerModule(new DeepPlayerFinder());
        registerModule(new FakeCoordinates());
        registerModule(new FakeLag());
        registerModule(new FakePay());
        registerModule(new FakePlayer());
        registerModule(new FakeStats());
        registerModule(new Fastbreak());
        registerModule(new FastPlace());
        registerModule(new FireFly());
        registerModule(new Flight());
        registerModule(new FOVSlider());
        registerModule(new Freecam());
        registerModule(new Freelook());
        registerModule(new Friends());
        registerModule(new Fullbright());
        registerModule(new GamblingRigger());
        registerModule(new GUISettings());
        registerModule(new HandCustomer());
        registerModule(new HeatColor());
        registerModule(new HitBoxes());
        registerModule(new HitSelect());
        registerModule(new HitSwap());
        registerModule(new HoleESP());
        registerModule(new InventoryCleaner());
        registerModule(new JumpCircles());
        registerModule(new KeepSprint());
        registerModule(new KeyPearl());
        registerModule(new KnockbackDelay());
        registerModule(new License());
        registerModule(new LootESP());
        registerModule(new MaceBomber());
        registerModule(new MiddleClickFriend());
        registerModule(new MobESP());
        registerModule(new NameProtect());
        registerModule(new NameTags());
        registerModule(new NetheriteFinder());
        registerModule(new NoExplosions());
        registerModule(new NoFarmLag());
        registerModule(new NoFlame());
        registerModule(new Notifications());
        registerModule(new ObsidianOptimizer());
        registerModule(new Panic());
        registerModule(new Particles());
        registerModule(new PearlCatch());
        registerModule(new PearlCatchAssist());
        registerModule(new PearlPrediction());
        registerModule(new PerfectWindCharge());
        registerModule(new PlayerESP());
        registerModule(new PotionRefill());
        registerModule(new Reach());
        registerModule(new RealisticWater());
        registerModule(new RegionMap());
        registerModule(new Relog());
        registerModule(new RtpBaseAlert());
        registerModule(new SafeAnchor());
        registerModule(new Scaffold());
        registerModule(new SeedAnalyzer());
        registerModule(new SelfDestruct());
        registerModule(new ShowHUD());
        registerModule(new SilentAim());
        registerModule(new SilentHome());
        registerModule(new SkinProtect());
        registerModule(new SpawnerFinder());
        registerModule(new SpawnerProtect());
        registerModule(new SpecialItemBuyer());
        registerModule(new SpeedHack());
        registerModule(new SpotifyHUD());
        registerModule(new Sprint());
        registerModule(new StaffList());
        registerModule(new StorageESP());
        registerModule(new SusChunkFinder());
        registerModule(new SusChunkFinderV2());
        registerModule(new SwordInspect());
        registerModule(new SwordPvpBot());
        registerModule(new TargetESP());
        registerModule(new ThemeCustomizer());
        registerModule(new TimeChanger());
        registerModule(new TotemCounter());
        registerModule(new Trails());
        registerModule(new TridentAssist());
        registerModule(new TriggerBot());
        registerModule(new VillagerTradeBot());
        registerModule(new WanderingTraderFinder());
        registerModule(new Waypoints());
        registerModule(new WeatherChanger());
        registerModule(new WeatherNotifier());
        registerModule(new WTap());
        registerModule(new Zoom());
    }

    public static void onGameTick() {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        ExtUtils.updateFlagAction();
        ClientThreadUtils.updateOptionsTime();
        PlayerInputOverride.updateFlagTick();
        EntityDamageUtils.updateTimeInstance();
        ConnectionTimeUtils.update();
        Module autoBuilderModule = getModule(AutoBuilder.class);
        AutoBuilder autoBuilder = autoBuilderModule instanceof AutoBuilder ab ? ab : null;
        if (autoBuilder != null) {
            autoBuilder.updateTeamLeaving();
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onReset();
        }
        if (autoBuilder != null) {
            autoBuilder.updateRestockDestroyed();
            autoBuilder.updateCancelledWorld();
        }
        Module villagerModule = getModule(VillagerTradeBot.class);
        if (villagerModule instanceof VillagerTradeBot villagerTradeBot) {
            villagerTradeBot.updateVillagerTrader();
        }
        InstanceTimeUtils.updateDequeArray();
        MathClampUtils.updateFlagClamp();
    }

    public static void onEndClientTick() {
    }

    public static void dispatchPacketSend(InventoryPlayer packetSendEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onPacketSend(packetSendEvent);
        }
    }

    public static void dispatchPacketReceive(AutoVanish packetReceiveEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onPacketReceive(packetReceiveEvent);
        }
    }

    public static void dispatchRender3D(QueueCommand render3DEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        VertexBuilderUtils.handleCameraMatrix((MatrixStack) render3DEvent.matrices);
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onRender3D(render3DEvent);
        }
        Module autoBuilderModule = getModule(AutoBuilder.class);
        if (autoBuilderModule instanceof AutoBuilder autoBuilder) {
            autoBuilder.handleStateEnum(render3DEvent);
            autoBuilder.handleWriteCamera(render3DEvent);
        }
        Module areaMinerModule = getModule(AutoAreaMiner.class);
        if (areaMinerModule instanceof AutoAreaMiner autoAreaMiner && !autoAreaMiner.isEnabled()) {
            autoAreaMiner.handleProgressTick(render3DEvent);
        }
        Module villagerModule = getModule(VillagerTradeBot.class);
        if (villagerModule instanceof VillagerTradeBot villagerTradeBot) {
            villagerTradeBot.handlePickCamera(render3DEvent);
        }
        VertexBuilderUtils.updateDrawImmediate();
    }

    public static void dispatchRender2D(TickProgress render2DEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onRender2D(render2DEvent);
        }
        Module autoBuilderModule = getModule(AutoBuilder.class);
        if (autoBuilderModule instanceof AutoBuilder autoBuilder) {
            autoBuilder.handleChestPlayer(render2DEvent);
            autoBuilder.handleBaseLeaving(render2DEvent);
        }
        Module villagerModule = getModule(VillagerTradeBot.class);
        if (villagerModule instanceof VillagerTradeBot villagerTradeBot) {
            villagerTradeBot.handlePickRight(render2DEvent);
        }
    }

    public static void dispatchZoomHud(ZoomHud event) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onZoomHud(event);
        }
    }

    public static void dispatchPostItemUse(Cooldown postItemUseEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onPostItemUse(postItemUseEvent);
        }
    }

    public static void dispatchPreItemUse(ScaffoldTrade preItemUseEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onPreItemUse(preItemUseEvent);
        }
        Module villagerModule = getModule(VillagerTradeBot.class);
        if (villagerModule instanceof VillagerTradeBot villagerTradeBot) {
            villagerTradeBot.handleVillagerTrader(preItemUseEvent);
        }
    }

    public static void dispatchButtonClick(Button event) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onButtonClick(event);
        }
        Module autoBuilderModule = getModule(AutoBuilder.class);
        if (autoBuilderModule instanceof AutoBuilder autoBuilder) {
            autoBuilder.handleKeybindScreen(event);
        }
    }

    public static void dispatchSwordBot(SwordBot event) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onSwordBot(event);
        }
        Module autoBuilderModule = getModule(AutoBuilder.class);
        if (autoBuilderModule instanceof AutoBuilder autoBuilder) {
            autoBuilder.handleKeybindScreen(event);
        }
        Module villagerModule = getModule(VillagerTradeBot.class);
        if (villagerModule instanceof VillagerTradeBot villagerTradeBot) {
            villagerTradeBot.handleKeybindScreen(event);
        }
    }

    public static void dispatchCameraAngle(DeltaAngle cameraEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onCameraAngle(cameraEvent);
        }
    }

    public static void dispatchPlayerScaffold(PlayerScaffold event) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onPlayerScaffold(event);
        }
    }
}
