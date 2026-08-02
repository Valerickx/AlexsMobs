package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.ClientLayerRegistry;
import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.alexsmobs.client.gui.GUITransmutationTable;
import com.github.alexthe666.alexsmobs.client.particle.*;
import com.github.alexthe666.alexsmobs.client.render.*;
import com.github.alexthe666.alexsmobs.client.render.item.AMItemRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.item.GhostlyPickaxeBakedModel;
import com.github.alexthe666.alexsmobs.client.render.tile.RenderCapsid;
import com.github.alexthe666.alexsmobs.client.render.tile.RenderTransmutationTable;
import com.github.alexthe666.alexsmobs.client.render.tile.RenderVoidWormBeak;
import com.github.alexthe666.alexsmobs.client.sound.SoundBearMusicBox;
import com.github.alexthe666.alexsmobs.client.sound.SoundLaCucaracha;
import com.github.alexthe666.alexsmobs.client.sound.SoundWormBoss;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.inventory.AMMenuRegistry;
import com.github.alexthe666.alexsmobs.item.*;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = AlexsMobs.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public static final Int2ObjectMap<SoundBearMusicBox> BEAR_MUSIC_BOX_SOUND_MAP = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<SoundLaCucaracha> COCKROACH_SOUND_MAP = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<SoundWormBoss> WORMBOSS_SOUND_MAP = new Int2ObjectOpenHashMap<>();
    public static final List<UUID> currentUnrenderedEntities = new ArrayList<>();
    public static int voidPortalCreationTime = 0;
    public CameraType prevPOV = CameraType.FIRST_PERSON;
    public boolean initializedRainbowBuffers = false;
    private int pupfishChunkX = 0;
    private int pupfishChunkZ = 0;
    private int singingBlueJayId = -1;
    private final ItemStack[] transmuteStacks = new ItemStack[3];

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemColors(RegisterColorHandlersEvent.ItemTintSources event) {
        AlexsMobs.LOGGER.info("loaded in item colorizer");
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        AlexsMobs.LOGGER.info("loaded in block colorizer");
        event.register(java.util.List.of(new net.minecraft.client.color.block.BlockTintSource() {
            @Override
            public int color(net.minecraft.world.level.block.state.BlockState state) {
                return RainbowUtil.calculateGlassColor(null);
            }
            @Override
            public int colorInWorld(net.minecraft.world.level.block.state.BlockState state, net.minecraft.client.renderer.block.BlockAndTintGetter level, net.minecraft.core.BlockPos pos) {
                return pos != null ? RainbowUtil.calculateGlassColor(pos) : -1;
            }
        }), AMBlockRegistry.RAINBOW_GLASS.get());
    }

    @Override
    public void init(IEventBus bus) {
        bus.addListener(ClientProxy::onBakingCompleted);
        bus.addListener(ClientProxy::onItemColors);
        bus.addListener(ClientProxy::onBlockColors);
        bus.addListener(ClientLayerRegistry::onAddLayers);
        bus.addListener(ClientProxy::setupParticles);
    }

    public void clientInit() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(new ClientEvents());
        initRainbowBuffers();
        EntityRenderers\.register\(AMEntityRegistry\.GRIZZLY_BEAR\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityGrizzlyBear>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityGrizzlyBear>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderGrizzlyBear\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ROADRUNNER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityRoadrunner>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityRoadrunner>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderRoadrunner\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.BONE_SERPENT\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityBoneSerpent>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityBoneSerpent>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderBoneSerpent\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.BONE_SERPENT_PART\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityBoneSerpentPart>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityBoneSerpentPart>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderBoneSerpentPart\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.GAZELLE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityGazelle>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityGazelle>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderGazelle\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CROCODILE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCrocodile>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCrocodile>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCrocodile\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.FLY\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityFly>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityFly>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderFly\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.HUMMINGBIRD\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityHummingbird>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityHummingbird>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderHummingbird\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ORCA\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityOrca>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityOrca>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderOrca\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SUNBIRD\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySunbird>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySunbird>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSunbird\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.GORILLA\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityGorilla>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityGorilla>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderGorilla\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CRIMSON_MOSQUITO\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCrimsonMosquito>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCrimsonMosquito>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCrimsonMosquito\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MOSQUITO_SPIT\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityMosquitoSpit>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityMosquitoSpit>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderMosquitoSpit\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.RATTLESNAKE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityRattlesnake>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityRattlesnake>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderRattlesnake\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ENDERGRADE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityEndergrade>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityEndergrade>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderEndergrade\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.HAMMERHEAD_SHARK\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityHammerheadShark>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityHammerheadShark>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderHammerheadShark\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SHARK_TOOTH_ARROW\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySharkToothArrow>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySharkToothArrow>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSharkToothArrow\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.LOBSTER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityLobster>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityLobster>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderLobster\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.KOMODO_DRAGON\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityKomodoDragon>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityKomodoDragon>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderKomodoDragon\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CAPUCHIN_MONKEY\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCapuchinMonkey>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCapuchinMonkey>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCapuchinMonkey\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.TOSSED_ITEM\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityTossedItem>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityTossedItem>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderTossedItem\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CENTIPEDE_HEAD\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCentipedeHead>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCentipedeHead>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCentipedeHead\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CENTIPEDE_BODY\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCentipedeBody>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCentipedeBody>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCentipedeBody\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CENTIPEDE_TAIL\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCentipedeTail>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCentipedeTail>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCentipedeTail\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.WARPED_TOAD\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityWarpedToad>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityWarpedToad>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderWarpedToad\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MOOSE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityMoose>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityMoose>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderMoose\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MIMICUBE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityMimicube>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityMimicube>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderMimicube\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.RACCOON\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityRaccoon>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityRaccoon>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderRaccoon\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.BLOBFISH\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityBlobfish>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityBlobfish>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderBlobfish\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SEAL\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySeal>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySeal>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSeal\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.COCKROACH\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCockroach>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCockroach>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCockroach\(context\);\ }\ }\);
        EntityRenderers.register(AMEntityRegistry.COCKROACH_EGG.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers\.register\(AMEntityRegistry\.SHOEBILL\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityShoebill>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityShoebill>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderShoebill\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ELEPHANT\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityElephant>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityElephant>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderElephant\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SOUL_VULTURE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySoulVulture>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySoulVulture>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSoulVulture\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SNOW_LEOPARD\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySnowLeopard>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySnowLeopard>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSnowLeopard\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SPECTRE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySpectre>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySpectre>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSpectre\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CROW\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCrow>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCrow>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCrow\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ALLIGATOR_SNAPPING_TURTLE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityAlligatorSnappingTurtle>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityAlligatorSnappingTurtle>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderAlligatorSnappingTurtle\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MUNGUS\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityMungus>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityMungus>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderMungus\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MANTIS_SHRIMP\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityMantisShrimp>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityMantisShrimp>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderMantisShrimp\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.GUSTER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityGuster>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityGuster>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderGuster\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SAND_SHOT\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySandShot>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySandShot>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSandShot\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.GUST\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityGust>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityGust>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderGust\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.WARPED_MOSCO\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityWarpedMosco>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityWarpedMosco>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderWarpedMosco\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.HEMOLYMPH\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityHemolymph>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityHemolymph>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderHemolymph\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.STRADDLER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityStraddler>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityStraddler>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderStraddler\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.STRADPOLE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityStradpole>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityStradpole>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderStradpole\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.STRADDLEBOARD\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityStraddleboard>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityStraddleboard>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderStraddleboard\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.EMU\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityEmu>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityEmu>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderEmu\(context\);\ }\ }\);
        EntityRenderers.register(AMEntityRegistry.EMU_EGG.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers\.register\(AMEntityRegistry\.PLATYPUS\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityPlatypus>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityPlatypus>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderPlatypus\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.DROPBEAR\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityDropBear>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityDropBear>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderDropBear\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.TASMANIAN_DEVIL\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityTasmanianDevil>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityTasmanianDevil>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderTasmanianDevil\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.KANGAROO\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityKangaroo>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityKangaroo>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderKangaroo\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CACHALOT_WHALE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCachalotWhale>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCachalotWhale>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCachalotWhale\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CACHALOT_ECHO\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCachalotEcho>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCachalotEcho>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCachalotEcho\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.LEAFCUTTER_ANT\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityLeafcutterAnt>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityLeafcutterAnt>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderLeafcutterAnt\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ENDERIOPHAGE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityEnderiophage>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityEnderiophage>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderEnderiophage\(context\);\ }\ }\);
        EntityRenderers.register(AMEntityRegistry.ENDERIOPHAGE_ROCKET.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers\.register\(AMEntityRegistry\.BALD_EAGLE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityBaldEagle>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityBaldEagle>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderBaldEagle\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.TIGER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityTiger>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityTiger>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderTiger\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.TARANTULA_HAWK\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityTarantulaHawk>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityTarantulaHawk>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderTarantulaHawk\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.VOID_WORM\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityVoidWorm>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityVoidWorm>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderVoidWormHead\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.VOID_WORM_PART\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityVoidWormPart>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityVoidWormPart>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderVoidWormBody\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.VOID_WORM_SHOT\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityVoidWormShot>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityVoidWormShot>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderVoidWormShot\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.VOID_PORTAL\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityVoidPortal>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityVoidPortal>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderVoidPortal\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.FRILLED_SHARK\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityFrilledShark>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityFrilledShark>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderFrilledShark\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MIMIC_OCTOPUS\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityMimicOctopus>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityMimicOctopus>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderMimicOctopus\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SEAGULL\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySeagull>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySeagull>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSeagull\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.FROSTSTALKER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityFroststalker>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityFroststalker>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderFroststalker\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ICE_SHARD\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityIceShard>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityIceShard>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderIceShard\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.TUSKLIN\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityTusklin>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityTusklin>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderTusklin\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.LAVIATHAN\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityLaviathan>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityLaviathan>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderLaviathan\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.COSMAW\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCosmaw>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCosmaw>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCosmaw\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.TOUCAN\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityToucan>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityToucan>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderToucan\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MANED_WOLF\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityManedWolf>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityManedWolf>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderManedWolf\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ANACONDA\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityAnaconda>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityAnaconda>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderAnaconda\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ANACONDA_PART\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityAnacondaPart>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityAnacondaPart>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderAnacondaPart\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.VINE_LASSO\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityVineLasso>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityVineLasso>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderVineLasso\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ANTEATER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityAnteater>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityAnteater>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderAnteater\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.ROCKY_ROLLER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityRockyRoller>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityRockyRoller>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderRockyRoller\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.FLUTTER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityFlutter>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityFlutter>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderFlutter\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.POLLEN_BALL\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityPollenBall>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityPollenBall>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderPollenBall\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.GELADA_MONKEY\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityGeladaMonkey>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityGeladaMonkey>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderGeladaMonkey\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.JERBOA\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityJerboa>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityJerboa>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderJerboa\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.TERRAPIN\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityTerrapin>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityTerrapin>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderTerrapin\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.COMB_JELLY\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCombJelly>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCombJelly>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCombJelly\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.COSMIC_COD\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCosmicCod>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCosmicCod>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCosmicCod\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.BUNFUNGUS\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityBunfungus>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityBunfungus>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderBunfungus\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.BISON\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityBison>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityBison>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderBison\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.GIANT_SQUID\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityGiantSquid>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityGiantSquid>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderGiantSquid\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SQUID_GRAPPLE\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySquidGrapple>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySquidGrapple>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSquidGrapple\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SEA_BEAR\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySeaBear>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySeaBear>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSeaBear\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.DEVILS_HOLE_PUPFISH\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityDevilsHolePupfish>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityDevilsHolePupfish>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderDevilsHolePupfish\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CATFISH\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCatfish>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCatfish>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCatfish\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.FLYING_FISH\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityFlyingFish>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityFlyingFish>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderFlyingFish\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SKELEWAG\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySkelewag>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySkelewag>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSkelewag\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.RAIN_FROG\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityRainFrog>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityRainFrog>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderRainFrog\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.POTOO\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityPotoo>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityPotoo>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderPotoo\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MUDSKIPPER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityMudskipper>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityMudskipper>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderMudskipper\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MUD_BALL\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityMudBall>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityMudBall>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderMudBall\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.RHINOCEROS\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityRhinoceros>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityRhinoceros>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderRhinoceros\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SUGAR_GLIDER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySugarGlider>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySugarGlider>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSugarGlider\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.FARSEER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityFarseer>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityFarseer>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderFarseer\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SKREECHER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySkreecher>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySkreecher>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSkreecher\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.UNDERMINER\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityUnderminer>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityUnderminer>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderUnderminer\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MURMUR\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityMurmur>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityMurmur>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderMurmurBody\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.MURMUR_HEAD\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityMurmurHead>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityMurmurHead>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderMurmurHead\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.TENDON_SEGMENT\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityTendonSegment>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityTendonSegment>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderTendonSegment\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.SKUNK\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntitySkunk>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntitySkunk>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderSkunk\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.FART\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityFart>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityFart>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderFart\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.BANANA_SLUG\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityBananaSlug>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityBananaSlug>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderBananaSlug\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.BLUE_JAY\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityBlueJay>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityBlueJay>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderBlueJay\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.CAIMAN\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityCaiman>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityCaiman>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderCaiman\(context\);\ }\ }\);
        EntityRenderers\.register\(AMEntityRegistry\.TRIOPS\.get\(\),\ new\ net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider<com\.github\.alexthe666\.alexsmobs\.entity\.EntityTriops>\(\)\ \{\ public\ net\.minecraft\.client\.renderer\.entity\.EntityRenderer<\?\ super\ com\.github\.alexthe666\.alexsmobs\.entity\.EntityTriops>\ create\(net\.minecraft\.client\.renderer\.entity\.EntityRendererProvider\.Context\ context\)\ \{\ return\ new\ RenderTriops\(context\);\ }\ }\);
        try {
            /* ItemProperties calls for 1.21.4 */
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Could not load item models for weapons");
        }
        BlockEntityRenderers.register(AMTileEntityRegistry.CAPSID.get(), new net.minecraft.client.renderer.entity.EntityRendererProvider() { public net.minecraft.client.renderer.entity.EntityRenderer create(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) { return new RenderCapsid(context); } });
        BlockEntityRenderers.register(AMTileEntityRegistry.VOID_WORM_BEAK.get(), new net.minecraft.client.renderer.entity.EntityRendererProvider() { public net.minecraft.client.renderer.entity.EntityRenderer create(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) { return new RenderVoidWormBeak(context); } });
    }

    public static void setupMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(AMMenuRegistry.TRANSMUTATION_TABLE.get(), GUITransmutationTable::new);
    }

    private void initRainbowBuffers() {
        initializedRainbowBuffers = true;
    }

    private static void onBakingCompleted(final ModelEvent.ModifyBakingResult e) {
    }

    public void openBookGUI(ItemStack itemStackIn) {
        Minecraft.getInstance().setScreenAndShow(new GUIAnimalDictionary(itemStackIn));
    }

    public void openBookGUI(ItemStack itemStackIn, String page) {
        Minecraft.getInstance().setScreenAndShow(new GUIAnimalDictionary(itemStackIn, page));
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    @OnlyIn(Dist.CLIENT)
    public Object getArmorModel(int armorId, LivingEntity entity) {
        switch (armorId) {
            /*
            case 0:
                return ROADRUNNER_BOOTS_MODEL;
            case 1:
                return MOOSE_HEADGEAR_MODEL;
            case 2:
                return FRONTIER_CAP_MODEL.withAnimations(entity);
            case 3:
                return SOMBRERO_MODEL;
            case 4:
                return SPIKED_TURTLE_SHELL_MODEL;
            case 5:
                return FEDORA_MODEL;
            case 6:
                return ELYTRA_MODEL.withAnimations(entity);

             */
            default:
                return null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onEntityStatus(Entity entity, byte updateKind) {
        if (updateKind == 67) {
            if (entity instanceof EntityCockroach && entity.isAlive()) {
                SoundLaCucaracha sound;
                if (COCKROACH_SOUND_MAP.get(entity.getId()) == null) {
                    sound = new SoundLaCucaracha((EntityCockroach) entity);
                    COCKROACH_SOUND_MAP.put(entity.getId(), sound);
                } else {
                    sound = COCKROACH_SOUND_MAP.get(entity.getId());
                }
                if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound() && sound.isOnlyCockroach()) {
                    Minecraft.getInstance().getSoundManager().play(sound);
                }
            } else if (entity instanceof EntityVoidWorm && entity.isAlive()) {
                final float f2 = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);
                if (f2 <= 0) {
                    WORMBOSS_SOUND_MAP.clear();
                } else {
                    SoundWormBoss sound;
                    if (WORMBOSS_SOUND_MAP.get(entity.getId()) == null) {
                        sound = new SoundWormBoss((EntityVoidWorm) entity);
                        WORMBOSS_SOUND_MAP.put(entity.getId(), sound);
                    } else {
                        sound = WORMBOSS_SOUND_MAP.get(entity.getId());
                    }
                    if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.isNearest()) {
                        Minecraft.getInstance().getSoundManager().play(sound);
                    }
                }
            } else if (entity instanceof EntityGrizzlyBear && entity.isAlive()) {
                SoundBearMusicBox sound;
                if (BEAR_MUSIC_BOX_SOUND_MAP.get(entity.getId()) == null) {
                    sound = new SoundBearMusicBox((EntityGrizzlyBear) entity);
                    BEAR_MUSIC_BOX_SOUND_MAP.put(entity.getId(), sound);
                } else {
                    sound = BEAR_MUSIC_BOX_SOUND_MAP.get(entity.getId());
                }
                if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound() && sound.isOnlyMusicBox()) {
                    Minecraft.getInstance().getSoundManager().play(sound);
                }
            } else if (entity instanceof EntityBlueJay && entity.isAlive()) {
                singingBlueJayId = entity.getId();
            }
        }
        if (entity instanceof EntityBlueJay && entity.isAlive() && updateKind == 68) {
            singingBlueJayId = -1;
        }
    }

    public void updateBiomeVisuals(int x, int z) {
    }

    public static void setupParticles(RegisterParticleProvidersEvent registry) {
        AlexsMobs.LOGGER.debug("Registered particle factories");
        registry.registerSpriteSet(AMParticleRegistry.GUSTER_SAND_SPIN.get(), ParticleGusterSandSpin.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.GUSTER_SAND_SHOT.get(), ParticleGusterSandShot.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.GUSTER_SAND_SPIN_RED.get(), ParticleGusterSandSpin.FactoryRed::new);
        registry.registerSpriteSet(AMParticleRegistry.GUSTER_SAND_SHOT_RED.get(), ParticleGusterSandShot.FactoryRed::new);
        registry.registerSpriteSet(AMParticleRegistry.GUSTER_SAND_SPIN_SOUL.get(), ParticleGusterSandSpin.FactorySoul::new);
        registry.registerSpriteSet(AMParticleRegistry.GUSTER_SAND_SHOT_SOUL.get(), ParticleGusterSandShot.FactorySoul::new);
        registry.registerSpriteSet(AMParticleRegistry.HEMOLYMPH.get(), ParticleHemolymph.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.PLATYPUS_SENSE.get(), ParticlePlatypus.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.WHALE_SPLASH.get(), ParticleWhaleSplash.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.DNA.get(), ParticleDna.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.SHOCKED.get(), ParticleSimpleHeart.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.WORM_PORTAL.get(), ParticleWormPortal.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.INVERT_DIG.get(), ParticleInvertDig.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.TEETH_GLINT.get(), ParticleTeethGlint.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.SMELLY.get(), ParticleSmelly.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get(), ParticleBunfungusTransformation.Factory::new);
        registry.registerSpriteSet(AMParticleRegistry.FUNGUS_BUBBLE.get(), ParticleFungusBubble.Factory::new);
        registry.registerSpecial(AMParticleRegistry.BEAR_FREDDY.get(), new ParticleBearFreddy.Factory());
        registry.registerSpriteSet(AMParticleRegistry.SUNBIRD_FEATHER.get(), ParticleSunbirdFeather.Factory::new);
        registry.registerSpecial(AMParticleRegistry.STATIC_SPARK.get(), new ParticleStaticSpark.Factory());
        registry.registerSpecial(AMParticleRegistry.SKULK_BOOM.get(), new ParticleSkulkBoom.Factory());
        registry.registerSpriteSet(AMParticleRegistry.BIRD_SONG.get(), ParticleBirdSong.Factory::new);
    }


    public void setRenderViewEntity(Entity entity) {
        prevPOV = Minecraft.getInstance().options.getCameraType();
        Minecraft.getInstance().setCameraEntity(entity);
        Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
    }

    public void resetRenderViewEntity() {
        Minecraft.getInstance().setCameraEntity(Minecraft.getInstance().player);
    }

    public int getPreviousPOV() {
        return prevPOV.ordinal();
    }

    public boolean isFarFromCamera(double x, double y, double z) {
        Minecraft lvt_1_1_ = Minecraft.getInstance();
        return lvt_1_1_.player != null && lvt_1_1_.player.distanceToSqr(x, y, z) >= 256.0D;
    }

    public void resetVoidPortalCreation(Player player) {

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterEntityRenders(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    @Override
    public Object getISTERProperties() {
        return new AMItemRenderProperties();
    }

    @Override
    public Object getArmorRenderProperties() {
        return new CustomArmorRenderProperties();
    }

    public void spawnSpecialParticle(int type) {
        if (type == 0) {
            Minecraft.getInstance().level.addParticle(AMParticleRegistry.BEAR_FREDDY.get(), Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ(), 0, 0, 0);
        }
    }

    public void processVisualFlag(Entity entity, int flag) {
        if (entity == Minecraft.getInstance().player && flag == 87) {
            ClientEvents.renderStaticScreenFor = 60;
        }
    }

    public void setPupfishChunkForItem(int chunkX, int chunkZ) {
        this.pupfishChunkX = chunkX;
        this.pupfishChunkZ = chunkZ;
    }

    public void setDisplayTransmuteResult(int slot, ItemStack stack){
        transmuteStacks[Mth.clamp(slot, 0, 2)] = stack;
    }

    public ItemStack getDisplayTransmuteResult(int slot){
        ItemStack stack = transmuteStacks[Mth.clamp(slot, 0, 2)];
        return stack == null ? ItemStack.EMPTY : stack;
    }

    public int getSingingBlueJayId() {
        return singingBlueJayId;
    }

}






