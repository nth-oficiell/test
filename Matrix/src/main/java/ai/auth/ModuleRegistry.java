package ai.auth;

import com.matrix.client.entity.ClientThreadUtils;
import com.matrix.client.gui.Button;
import com.matrix.client.gui.TickProgress;
import com.matrix.client.gui.ZoomHud;
import com.matrix.client.render.QueueCommand;
import com.matrix.client.render.VertexBuilderUtils;
import com.matrix.client.world.MathClampUtils;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.util.math.MatrixStack;
import com.matrix.client.categories.bots.AutoElytraPilot;
import com.matrix.client.categories.bots.AutoTpaHere;
import com.matrix.client.categories.bots.CrystalPvpBot;
import com.matrix.client.utils.PlayerInputOverride;
import com.matrix.client.categories.bots.SwordPvpBot;
import com.matrix.client.categories.combat.AimAssist;
import com.matrix.client.categories.combat.AnchorMacro;
import com.matrix.client.categories.combat.AutoAggroPearl;
import com.matrix.client.categories.combat.AutoCart;
import com.matrix.client.categories.combat.AutoClicker;
import com.matrix.client.categories.combat.AutoHitCrystal;
import com.matrix.client.categories.combat.AutoHotbar;
import com.matrix.client.categories.combat.AutoInventoryTotem;
import com.matrix.client.categories.combat.AutoJumpReset;
import com.matrix.client.categories.combat.AutoShieldBreaker;
import com.matrix.client.categories.combat.AutoTotem;
import com.matrix.client.categories.combat.AutoTrap;
import com.matrix.client.categories.combat.AutoXP;
import com.matrix.client.categories.combat.Criticals;
import com.matrix.client.categories.combat.CrystalAura;
import com.matrix.client.categories.combat.CrystalOptimizer;
import com.matrix.client.categories.combat.HitBoxes;
import com.matrix.client.categories.combat.HitSelect;
import com.matrix.client.categories.combat.HitSwap;
import com.matrix.client.categories.combat.KeyPearl;
import com.matrix.client.categories.combat.MaceBomber;
import com.matrix.client.categories.combat.ObsidianOptimizer;
import com.matrix.client.categories.combat.PerfectWindCharge;
import com.matrix.client.categories.combat.Reach;
import com.matrix.client.categories.combat.SafeAnchor;
import com.matrix.client.categories.combat.SilentAim;
import com.matrix.client.categories.combat.TridentAssist;
import com.matrix.client.categories.combat.TriggerBot;
import com.matrix.client.categories.combat.WTap;
import com.matrix.client.categories.bots.AnchorHunter;
import com.matrix.client.categories.donutsmp.AntiVanish;
import com.matrix.client.categories.bots.AutoAreaMiner;
import com.matrix.client.categories.bots.AutoAuctionFlipper;
import com.matrix.client.categories.bots.AutoBuilder;
import com.matrix.client.categories.bots.AutoElytraFinder;
import com.matrix.client.categories.donutsmp.AutoMine;
import com.matrix.client.categories.donutsmp.AutoSell;
import com.matrix.client.categories.donutsmp.AutoSellChest;
import com.matrix.client.categories.donutsmp.Bloodhound;
import com.matrix.client.categories.donutsmp.BoneDropper;
import com.matrix.client.categories.donutsmp.ActivityInspector;
import com.matrix.client.categories.donutsmp.ChunkFinder;
import com.matrix.client.categories.donutsmp.ChunkFinderV2;
import com.matrix.client.categories.donutsmp.DeepPlayerFinder;
import com.matrix.client.categories.donutsmp.FakePay;
import com.matrix.client.categories.donutsmp.FakeStats;
import com.matrix.client.categories.donutsmp.GamblingRigger;
import com.matrix.client.categories.donutsmp.NetheriteFinder;
import com.matrix.client.categories.donutsmp.RegionMap;
import com.matrix.client.categories.donutsmp.Relog;
import com.matrix.client.categories.bots.RtpBaseAlert;
import com.matrix.client.categories.donutsmp.SeedAnalyzer;
import com.matrix.client.categories.donutsmp.SilentHome;
import com.matrix.client.categories.bots.SpawnerProtect;
import com.matrix.client.categories.donutsmp.SpecialItemBuyer;
import com.matrix.client.categories.donutsmp.StaffList;
import com.matrix.client.categories.donutsmp.SusChunkFinder;
import com.matrix.client.categories.donutsmp.SusChunkFinderV2;
import com.matrix.client.categories.bots.VillagerTradeBot;
import com.matrix.client.categories.donutsmp.WanderingTraderFinder;
import com.matrix.client.categories.misc.ACAlert;
import com.matrix.client.categories.misc.AutoPot;
import com.matrix.client.categories.misc.AutoPromo;
import com.matrix.client.categories.misc.AutoTool;
import com.matrix.client.categories.misc.BoatFly;
import com.matrix.client.categories.misc.Capes;
import com.matrix.client.utils.Cooldown;
import com.matrix.client.categories.misc.MatrixRadio;
import com.matrix.client.categories.misc.CreamyKeys;
import com.matrix.client.categories.misc.CustomCrosshair;
import com.matrix.client.categories.misc.CustomSkin;
import com.matrix.client.categories.misc.FOVSlider;
import com.matrix.client.categories.misc.FakeCoordinates;
import com.matrix.client.categories.misc.FakePlayer;
import com.matrix.client.categories.misc.FastPlace;
import com.matrix.client.categories.misc.Fastbreak;
import com.matrix.client.categories.misc.InventoryCleaner;
import com.matrix.client.categories.misc.NameProtect;
import com.matrix.client.categories.misc.StreamMode;
import com.matrix.client.categories.misc.UpdateChecker;
import com.matrix.client.categories.misc.PotionRefill;
import com.matrix.client.categories.misc.SkinProtect;
import com.matrix.client.categories.misc.TimeChanger;
import com.matrix.client.categories.misc.WeatherChanger;
import com.matrix.client.categories.misc.WeatherNotifier;
import com.matrix.client.categories.misc.Zoom;
import com.matrix.client.categories.movement.AutoSpear;
import com.matrix.client.categories.movement.AutoWalk;
import com.matrix.client.categories.movement.Flight;
import com.matrix.client.categories.movement.Freecam;
import com.matrix.client.categories.movement.KeepSprint;
import com.matrix.client.categories.movement.PearlCatchAssist;
import com.matrix.client.categories.movement.Scaffold;
import com.matrix.client.categories.movement.SpeedHack;
import com.matrix.client.categories.movement.Sprint;
import com.matrix.client.utils.AutoVanish;
import com.matrix.client.categories.network.BackTrack;
import com.matrix.client.categories.network.Blink;
import com.matrix.client.utils.ConnectionTimeUtils;
import com.matrix.client.utils.EntityDamageUtils;
import com.matrix.client.categories.network.FakeLag;
import com.matrix.client.utils.InventoryPlayer;
import com.matrix.client.categories.network.KnockbackDelay;
import com.matrix.client.categories.settings.ClientSounds;
import com.matrix.client.categories.configs.Configs;
import com.matrix.client.categories.socials.Friends;
import com.matrix.client.categories.settings.GUISettings;
import com.matrix.client.categories.settings.License;
import com.matrix.client.categories.settings.MiddleClickFriend;
import com.matrix.client.categories.settings.Notifications;
import com.matrix.client.categories.settings.Panic;
import com.matrix.client.categories.settings.PinLock;
import com.matrix.client.categories.settings.SelfDestruct;
import com.matrix.client.categories.theme.ThemeCustomizer;
import com.matrix.client.categories.visuals.AtmosphericParticles;
import com.matrix.client.categories.visuals.BlockESP;
import com.matrix.client.categories.visuals.BlockHighlight;
import com.matrix.client.categories.visuals.BlockOverlay;
import com.matrix.client.categories.visuals.CustomFlame;
import com.matrix.client.categories.visuals.CustomSky;
import com.matrix.client.categories.visuals.CustomWorld;
import com.matrix.client.categories.visuals.FireFly;
import com.matrix.client.categories.visuals.Freelook;
import com.matrix.client.categories.visuals.Fullbright;
import com.matrix.client.categories.visuals.HandCustomer;
import com.matrix.client.categories.visuals.HeatColor;
import com.matrix.client.categories.visuals.HoleESP;
import com.matrix.client.categories.visuals.JumpCircles;
import com.matrix.client.categories.visuals.LootESP;
import com.matrix.client.categories.visuals.MobESP;
import com.matrix.client.categories.visuals.NameTags;
import com.matrix.client.categories.visuals.NoExplosions;
import com.matrix.client.categories.visuals.NoFarmLag;
import com.matrix.client.categories.visuals.NoFlame;
import com.matrix.client.categories.visuals.Particles;
import com.matrix.client.categories.visuals.PearlCatch;
import com.matrix.client.categories.visuals.PearlPrediction;
import com.matrix.client.categories.visuals.PlayerESP;
import com.matrix.client.categories.visuals.RealisticWater;
import com.matrix.client.categories.visuals.ShowHUD;
import com.matrix.client.categories.visuals.SkinChanger;
import com.matrix.client.categories.visuals.SpawnerFinder;
import com.matrix.client.categories.visuals.SpotifyHUD;
import com.matrix.client.categories.visuals.StorageESP;
import com.matrix.client.categories.visuals.SwordInspect;
import com.matrix.client.categories.visuals.TargetESP;
import com.matrix.client.categories.visuals.TotemCounter;
import com.matrix.client.categories.visuals.Trails;
import com.matrix.client.categories.visuals.Waypoints;
import com.matrix.client.categories.visuals.DeathMarker;
import ai.auth.Module;
import com.matrix.client.utils.DeltaAngle;
import com.matrix.client.utils.DirExtractionUtils;
import com.matrix.client.utils.ExtUtils;
import com.matrix.client.utils.IconsPngType;
import com.matrix.client.utils.InstanceTimeUtils;
import com.matrix.client.utils.PlayerScaffold;
import com.matrix.client.utils.ScaffoldTrade;
import com.matrix.client.utils.SwordBot;

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
        registerModule(new MatrixRadio());
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
        registerModule(new StreamMode());
        registerModule(new UpdateChecker());
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
        registerModule(new SkinChanger());
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
        registerModule(new ActivityInspector());
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
        registerModule(new DeathMarker());
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
