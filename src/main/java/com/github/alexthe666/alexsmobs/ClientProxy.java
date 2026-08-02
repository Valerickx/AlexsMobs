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
        EntityRenderers.register(AMEntityRegistry.GRIZZLY_BEAR.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderGrizzlyBear(context));
        EntityRenderers.register(AMEntityRegistry.ROADRUNNER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderRoadrunner(context));
        EntityRenderers.register(AMEntityRegistry.BONE_SERPENT.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderBoneSerpent(context));
        EntityRenderers.register(AMEntityRegistry.BONE_SERPENT_PART.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderBoneSerpentPart(context));
        EntityRenderers.register(AMEntityRegistry.GAZELLE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderGazelle(context));
        EntityRenderers.register(AMEntityRegistry.CROCODILE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCrocodile(context));
        EntityRenderers.register(AMEntityRegistry.FLY.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderFly(context));
        EntityRenderers.register(AMEntityRegistry.HUMMINGBIRD.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderHummingbird(context));
        EntityRenderers.register(AMEntityRegistry.ORCA.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderOrca(context));
        EntityRenderers.register(AMEntityRegistry.SUNBIRD.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSunbird(context));
        EntityRenderers.register(AMEntityRegistry.GORILLA.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderGorilla(context));
        EntityRenderers.register(AMEntityRegistry.CRIMSON_MOSQUITO.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCrimsonMosquito(context));
        EntityRenderers.register(AMEntityRegistry.MOSQUITO_SPIT.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderMosquitoSpit(context));
        EntityRenderers.register(AMEntityRegistry.RATTLESNAKE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderRattlesnake(context));
        EntityRenderers.register(AMEntityRegistry.ENDERGRADE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderEndergrade(context));
        EntityRenderers.register(AMEntityRegistry.HAMMERHEAD_SHARK.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderHammerheadShark(context));
        EntityRenderers.register(AMEntityRegistry.SHARK_TOOTH_ARROW.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSharkToothArrow(context));
        EntityRenderers.register(AMEntityRegistry.LOBSTER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderLobster(context));
        EntityRenderers.register(AMEntityRegistry.KOMODO_DRAGON.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderKomodoDragon(context));
        EntityRenderers.register(AMEntityRegistry.CAPUCHIN_MONKEY.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCapuchinMonkey(context));
        EntityRenderers.register(AMEntityRegistry.TOSSED_ITEM.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderTossedItem(context));
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_HEAD.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCentipedeHead(context));
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_BODY.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCentipedeBody(context));
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_TAIL.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCentipedeTail(context));
        EntityRenderers.register(AMEntityRegistry.WARPED_TOAD.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderWarpedToad(context));
        EntityRenderers.register(AMEntityRegistry.MOOSE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderMoose(context));
        EntityRenderers.register(AMEntityRegistry.MIMICUBE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderMimicube(context));
        EntityRenderers.register(AMEntityRegistry.RACCOON.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderRaccoon(context));
        EntityRenderers.register(AMEntityRegistry.BLOBFISH.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderBlobfish(context));
        EntityRenderers.register(AMEntityRegistry.SEAL.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSeal(context));
        EntityRenderers.register(AMEntityRegistry.COCKROACH.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCockroach(context));
        EntityRenderers.register(AMEntityRegistry.COCKROACH_EGG.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.SHOEBILL.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderShoebill(context));
        EntityRenderers.register(AMEntityRegistry.ELEPHANT.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderElephant(context));
        EntityRenderers.register(AMEntityRegistry.SOUL_VULTURE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSoulVulture(context));
        EntityRenderers.register(AMEntityRegistry.SNOW_LEOPARD.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSnowLeopard(context));
        EntityRenderers.register(AMEntityRegistry.SPECTRE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSpectre(context));
        EntityRenderers.register(AMEntityRegistry.CROW.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCrow(context));
        EntityRenderers.register(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderAlligatorSnappingTurtle(context));
        EntityRenderers.register(AMEntityRegistry.MUNGUS.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderMungus(context));
        EntityRenderers.register(AMEntityRegistry.MANTIS_SHRIMP.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderMantisShrimp(context));
        EntityRenderers.register(AMEntityRegistry.GUSTER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderGuster(context));
        EntityRenderers.register(AMEntityRegistry.SAND_SHOT.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSandShot(context));
        EntityRenderers.register(AMEntityRegistry.GUST.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderGust(context));
        EntityRenderers.register(AMEntityRegistry.WARPED_MOSCO.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderWarpedMosco(context));
        EntityRenderers.register(AMEntityRegistry.HEMOLYMPH.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderHemolymph(context));
        EntityRenderers.register(AMEntityRegistry.STRADDLER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderStraddler(context));
        EntityRenderers.register(AMEntityRegistry.STRADPOLE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderStradpole(context));
        EntityRenderers.register(AMEntityRegistry.STRADDLEBOARD.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderStraddleboard(context));
        EntityRenderers.register(AMEntityRegistry.EMU.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderEmu(context));
        EntityRenderers.register(AMEntityRegistry.EMU_EGG.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.PLATYPUS.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderPlatypus(context));
        EntityRenderers.register(AMEntityRegistry.DROPBEAR.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderDropBear(context));
        EntityRenderers.register(AMEntityRegistry.TASMANIAN_DEVIL.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderTasmanianDevil(context));
        EntityRenderers.register(AMEntityRegistry.KANGAROO.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderKangaroo(context));
        EntityRenderers.register(AMEntityRegistry.CACHALOT_WHALE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCachalotWhale(context));
        EntityRenderers.register(AMEntityRegistry.CACHALOT_ECHO.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCachalotEcho(context));
        EntityRenderers.register(AMEntityRegistry.LEAFCUTTER_ANT.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderLeafcutterAnt(context));
        EntityRenderers.register(AMEntityRegistry.ENDERIOPHAGE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderEnderiophage(context));
        EntityRenderers.register(AMEntityRegistry.ENDERIOPHAGE_ROCKET.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.BALD_EAGLE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderBaldEagle(context));
        EntityRenderers.register(AMEntityRegistry.TIGER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderTiger(context));
        EntityRenderers.register(AMEntityRegistry.TARANTULA_HAWK.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderTarantulaHawk(context));
        EntityRenderers.register(AMEntityRegistry.VOID_WORM.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderVoidWormHead(context));
        EntityRenderers.register(AMEntityRegistry.VOID_WORM_PART.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderVoidWormBody(context));
        EntityRenderers.register(AMEntityRegistry.VOID_WORM_SHOT.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderVoidWormShot(context));
        EntityRenderers.register(AMEntityRegistry.VOID_PORTAL.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderVoidPortal(context));
        EntityRenderers.register(AMEntityRegistry.FRILLED_SHARK.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderFrilledShark(context));
        EntityRenderers.register(AMEntityRegistry.MIMIC_OCTOPUS.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderMimicOctopus(context));
        EntityRenderers.register(AMEntityRegistry.SEAGULL.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSeagull(context));
        EntityRenderers.register(AMEntityRegistry.FROSTSTALKER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderFroststalker(context));
        EntityRenderers.register(AMEntityRegistry.ICE_SHARD.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderIceShard(context));
        EntityRenderers.register(AMEntityRegistry.TUSKLIN.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderTusklin(context));
        EntityRenderers.register(AMEntityRegistry.LAVIATHAN.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderLaviathan(context));
        EntityRenderers.register(AMEntityRegistry.COSMAW.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCosmaw(context));
        EntityRenderers.register(AMEntityRegistry.TOUCAN.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderToucan(context));
        EntityRenderers.register(AMEntityRegistry.MANED_WOLF.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderManedWolf(context));
        EntityRenderers.register(AMEntityRegistry.ANACONDA.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderAnaconda(context));
        EntityRenderers.register(AMEntityRegistry.ANACONDA_PART.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderAnacondaPart(context));
        EntityRenderers.register(AMEntityRegistry.VINE_LASSO.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderVineLasso(context));
        EntityRenderers.register(AMEntityRegistry.ANTEATER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderAnteater(context));
        EntityRenderers.register(AMEntityRegistry.ROCKY_ROLLER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderRockyRoller(context));
        EntityRenderers.register(AMEntityRegistry.FLUTTER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderFlutter(context));
        EntityRenderers.register(AMEntityRegistry.POLLEN_BALL.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderPollenBall(context));
        EntityRenderers.register(AMEntityRegistry.GELADA_MONKEY.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderGeladaMonkey(context));
        EntityRenderers.register(AMEntityRegistry.JERBOA.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderJerboa(context));
        EntityRenderers.register(AMEntityRegistry.TERRAPIN.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderTerrapin(context));
        EntityRenderers.register(AMEntityRegistry.COMB_JELLY.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCombJelly(context));
        EntityRenderers.register(AMEntityRegistry.COSMIC_COD.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCosmicCod(context));
        EntityRenderers.register(AMEntityRegistry.BUNFUNGUS.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderBunfungus(context));
        EntityRenderers.register(AMEntityRegistry.BISON.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderBison(context));
        EntityRenderers.register(AMEntityRegistry.GIANT_SQUID.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderGiantSquid(context));
        EntityRenderers.register(AMEntityRegistry.SQUID_GRAPPLE.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSquidGrapple(context));
        EntityRenderers.register(AMEntityRegistry.SEA_BEAR.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSeaBear(context));
        EntityRenderers.register(AMEntityRegistry.DEVILS_HOLE_PUPFISH.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderDevilsHolePupfish(context));
        EntityRenderers.register(AMEntityRegistry.CATFISH.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCatfish(context));
        EntityRenderers.register(AMEntityRegistry.FLYING_FISH.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderFlyingFish(context));
        EntityRenderers.register(AMEntityRegistry.SKELEWAG.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSkelewag(context));
        EntityRenderers.register(AMEntityRegistry.RAIN_FROG.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderRainFrog(context));
        EntityRenderers.register(AMEntityRegistry.POTOO.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderPotoo(context));
        EntityRenderers.register(AMEntityRegistry.MUDSKIPPER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderMudskipper(context));
        EntityRenderers.register(AMEntityRegistry.MUD_BALL.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderMudBall(context));
        EntityRenderers.register(AMEntityRegistry.RHINOCEROS.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderRhinoceros(context));
        EntityRenderers.register(AMEntityRegistry.SUGAR_GLIDER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSugarGlider(context));
        EntityRenderers.register(AMEntityRegistry.FARSEER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderFarseer(context));
        EntityRenderers.register(AMEntityRegistry.SKREECHER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSkreecher(context));
        EntityRenderers.register(AMEntityRegistry.UNDERMINER.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderUnderminer(context));
        EntityRenderers.register(AMEntityRegistry.MURMUR.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderMurmurBody(context));
        EntityRenderers.register(AMEntityRegistry.MURMUR_HEAD.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderMurmurHead(context));
        EntityRenderers.register(AMEntityRegistry.TENDON_SEGMENT.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderTendonSegment(context));
        EntityRenderers.register(AMEntityRegistry.SKUNK.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderSkunk(context));
        EntityRenderers.register(AMEntityRegistry.FART.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderFart(context));
        EntityRenderers.register(AMEntityRegistry.BANANA_SLUG.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderBananaSlug(context));
        EntityRenderers.register(AMEntityRegistry.BLUE_JAY.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderBlueJay(context));
        EntityRenderers.register(AMEntityRegistry.CAIMAN.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCaiman(context));
        EntityRenderers.register(AMEntityRegistry.TRIOPS.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderTriops(context));
        try {
            /* ItemProperties calls for 1.21.4 */
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Could not load item models for weapons");
        }
        BlockEntityRenderers.register(AMTileEntityRegistry.CAPSID.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderCapsid(context));
        BlockEntityRenderers.register(AMTileEntityRegistry.VOID_WORM_BEAK.get(), (net.minecraft.client.renderer.entity.EntityRendererProvider) (context) -> new RenderVoidWormBeak(context));
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



