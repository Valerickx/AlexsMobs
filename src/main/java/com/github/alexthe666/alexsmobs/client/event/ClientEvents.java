package com.github.alexthe666.alexsmobs.client.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.model.ModelRockyChestplateRolling;
import com.github.alexthe666.alexsmobs.client.model.ModelWanderingVillagerRider;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.LavaVisionFluidRenderer;
import com.github.alexthe666.alexsmobs.client.render.RenderVineLasso;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectPowerDown;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityBlueJay;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.entity.IFalconry;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.github.alexthe666.alexsmobs.message.MessageUpdateEagleControls;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.client.event.EventGetFluidRenderType;
import com.github.alexthe666.citadel.client.event.EventGetOutlineColor;
import com.github.alexthe666.citadel.client.event.EventGetStarBrightness;
import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.EntityHitResult;
import com.github.alexthe666.alexsmobs.message.MessageUpdateEagleControls;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.material.Fluids;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {

    private static final Identifier ROCKY_CHESTPLATE_TEXTURE = Identifier.parse("alexsmobs:textures/armor/rocky_chestplate.png");
    private static final ModelRockyChestplateRolling ROCKY_CHESTPLATE_MODEL = new ModelRockyChestplateRolling();

    private FluidRenderer previousFluidRenderer = null;
    private boolean previousLavaVision = false;
    public long lastStaticTick = -1;
    public static int renderStaticScreenFor = 0;

    @SubscribeEvent
    public void onOutlineEntityColor(EventGetOutlineColor event) {
        if(event.getEntityIn() instanceof Enemy && AlexsMobs.PROXY.getSingingBlueJayId() != -1){
            Entity entity = event.getEntityIn().level().getEntity(AlexsMobs.PROXY.getSingingBlueJayId());
            if(entity instanceof EntityBlueJay jay && jay.isAlive() && jay.isMakingMonstersBlue()){
                event.setColor(0X4B95FE);
            }
        }
        if (event.getEntityIn() instanceof ItemEntity && ((ItemEntity) event.getEntityIn()).getItem().is(AMTagRegistry.VOID_WORM_DROPS)){
            int fromColor = 0;
            int toColor = 0X21E5FF;
            float startR = (float) (fromColor >> 16 & 255) / 255.0F;
            float startG = (float) (fromColor >> 8 & 255) / 255.0F;
            float startB = (float) (fromColor & 255) / 255.0F;
            float endR = (float) (toColor >> 16 & 255) / 255.0F;
            float endG = (float) (toColor >> 8 & 255) / 255.0F;
            float endB = (float) (toColor & 255) / 255.0F;
            float f = (float) (Math.cos(0.4F * (event.getEntityIn().tickCount + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true))) + 1.0F) * 0.5F;
            float r = (endR - startR) * f + startR;
            float g = (endG - startG) * f + startG;
            float b = (endB - startB) * f + startB;
            int j = ((((int) (r * 255)) & 0xFF) << 16) |
                    ((((int) (g * 255)) & 0xFF) << 8) |
                    ((((int) (b * 255)) & 0xFF) << 0);
            event.setColor(j);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGetStarBrightness(EventGetStarBrightness event) {
        if (Minecraft.getInstance().player.hasEffect(AMEffectRegistry.POWER_DOWN)) {
            if (Minecraft.getInstance().player.getEffect(AMEffectRegistry.POWER_DOWN) != null) {
                MobEffectInstance instance = Minecraft.getInstance().player.getEffect(AMEffectRegistry.POWER_DOWN);
                EffectPowerDown powerDown = (EffectPowerDown) instance.getEffect();
                int duration = instance.getDuration();
                float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
                float f = (Math.min(powerDown.getActiveTime(), duration) + partialTicks) * 0.1F;
                event.setBrightness(0);
            }

        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (Minecraft.getInstance().player.hasEffect(AMEffectRegistry.POWER_DOWN)) {
            if (Minecraft.getInstance().player.getEffect(AMEffectRegistry.POWER_DOWN) != null) {
                event.setBlue(0);
                event.setRed(0);
                event.setGreen(0);
            }

        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    @OnlyIn(Dist.CLIENT)
    public void onFogDensity(ViewportEvent.RenderFog event) {
        FogType fogType = event.getCamera().getFluidInCamera();
        if (Minecraft.getInstance().player.hasEffect(AMEffectRegistry.LAVA_VISION) && fogType == FogType.LAVA) {
            event.setNearPlaneDistance(-8.0F);
            event.setFarPlaneDistance(50.0F);
        }
        if (Minecraft.getInstance().player.hasEffect(AMEffectRegistry.POWER_DOWN) && fogType == FogType.NONE) {
            if (Minecraft.getInstance().player.getEffect(AMEffectRegistry.POWER_DOWN) != null) {
                float initEnd = event.getFarPlaneDistance();
                MobEffectInstance instance = Minecraft.getInstance().player.getEffect(AMEffectRegistry.POWER_DOWN);
                EffectPowerDown powerDown = (EffectPowerDown) instance.getEffect();
                int duration = instance.getDuration();
                float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
                float f = Math.min(20, (Math.min(powerDown.getActiveTime() + partialTicks, duration + partialTicks))) * 0.05F;
                event.setNearPlaneDistance(-8.0F);
                float f1 = 8.0F + (1 - f) * Math.max(0, initEnd - 8.0F);
                event.setFarPlaneDistance(f1);
            }

        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPreRenderEntity(RenderLivingEvent.Pre<?, ?, ?> event) {
        var state = event.getRenderState();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPostRenderEntity(RenderLivingEvent.Post<?, ?, ?> event) {
        var state = event.getRenderState();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPoseHand(EventPosePlayerHand event) {
        LivingEntity player = (LivingEntity) event.getEntityIn();
        float f = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        boolean leftHand = false;
        boolean usingLasso = player.isUsingItem() && player.getUseItem().is(AMItemRegistry.VINE_LASSO.get());
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.VINE_LASSO.get()) {
            leftHand = player.getMainArm() == HumanoidArm.LEFT;
        } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.VINE_LASSO.get()) {
            leftHand = player.getMainArm() != HumanoidArm.LEFT;
        }
        if (leftHand && event.isLeftHand() && usingLasso) {
            event.getModel().leftArm.xRot = Maths.rad(-120F) + Mth.sin(player.tickCount + f) * 0.5F;
            event.getModel().leftArm.yRot = Maths.rad(-20F) + Mth.cos(player.tickCount + f) * 0.5F;
        }
        if (!leftHand && !event.isLeftHand() && usingLasso) {
            event.getModel().rightArm.xRot = Maths.rad(-120F) + Mth.sin(player.tickCount + f) * 0.5F;
            event.getModel().rightArm.yRot = Maths.rad(20F) - Mth.cos(player.tickCount + f) * 0.5F;
        }
    }
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderHand(RenderHandEvent event) {
        if (Minecraft.getInstance().getCameraEntity() instanceof IFalconry) {
            event.setCanceled(true);
        }
        if (!Minecraft.getInstance().player.getPassengers().isEmpty() && event.getHand() == InteractionHand.MAIN_HAND) {
            Player player = Minecraft.getInstance().player;
            boolean leftHand = false;
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                leftHand = player.getMainArm() == HumanoidArm.LEFT;
            } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                leftHand = player.getMainArm() != HumanoidArm.LEFT;
            }
            float partialTick = event.getPartialTick();
            for (Entity entity : player.getPassengers()) {
                if (entity instanceof IFalconry falconry) {
                    float yaw = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * partialTick;
                    ClientProxy.currentUnrenderedEntities.remove(entity.getUUID());
                    PoseStack matrixStackIn = event.getPoseStack();
                    matrixStackIn.pushPose();
                    matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                    matrixStackIn.translate(leftHand ? -falconry.getHandOffset() : falconry.getHandOffset(), -0.6F, -1F);
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw));
                    if (leftHand) {
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
                    } else {
                        matrixStackIn.mulPose(Axis.YN.rotationDegrees(90));
                    }
                                        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                                        renderEntity(entity, 0, 0, 0, 0, partialTick, matrixStackIn, bufferSource, event.getPackedLight());
                                        matrixStackIn.popPose();
                    ClientProxy.currentUnrenderedEntities.add(entity.getUUID());
                }
            }
        }
        if (Minecraft.getInstance().player.getUseItem().getItem() instanceof ItemDimensionalCarver && event.getItemStack().getItem() instanceof ItemDimensionalCarver) {
            PoseStack matrixStackIn = event.getPoseStack();
            matrixStackIn.pushPose();
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            InteractionHand hand = MoreObjects.firstNonNull(Minecraft.getInstance().player.swingingArm, InteractionHand.MAIN_HAND);
            float f = Minecraft.getInstance().player.getAttackAnim(event.getPartialTick());
            float f5 = -0.4F * Mth.sin(Mth.sqrt(f) * Mth.PI);
            float f6 = 0.2F * Mth.sin(Mth.sqrt(f) * Mth.TWO_PI);
            float f10 = -0.2F * Mth.sin(f * Mth.PI);
            HumanoidArm handside = hand == InteractionHand.MAIN_HAND ? Minecraft.getInstance().player.getMainArm() : Minecraft.getInstance().player.getMainArm().getOpposite();
            boolean flag3 = handside == HumanoidArm.RIGHT;
            int l = flag3 ? 1 : -1;
            matrixStackIn.translate((float) l * f5, f6, f10);
        }
    }

    public <E extends Entity> void renderEntity(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderNameplate(RenderNameTagEvent event) {
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderWorldLastEvent(RenderLevelStageEvent event) {
        if (true){
            if (!AMConfig.shadersCompat) {
                if (Minecraft.getInstance().player.hasEffect(AMEffectRegistry.LAVA_VISION)) {
                    if (!previousLavaVision) {
                        updateAllChunks();
                    }
                } else {
                    if (previousLavaVision) {
                        updateAllChunks();
                    }
                }
                previousLavaVision = Minecraft.getInstance().player.hasEffect(AMEffectRegistry.LAVA_VISION);
                if (AMConfig.clingingFlipEffect) {
                    if (Minecraft.getInstance().player.hasEffect(AMEffectRegistry.CLINGING) && Minecraft.getInstance().player.getEyeHeight() < Minecraft.getInstance().player.getBbHeight() * 0.45F) {
                        Minecraft.getInstance().gameRenderer.setPostEffect(Identifier.parse("minecraft:shaders/post/flip.json"));
                    }
                }
            }
            if (Minecraft.getInstance().getCameraEntity() instanceof EntityBaldEagle) {
                EntityBaldEagle eagle = (EntityBaldEagle) Minecraft.getInstance().getCameraEntity();
                LocalPlayer playerEntity = Minecraft.getInstance().player;

                if (((EntityBaldEagle) Minecraft.getInstance().getCameraEntity()).shouldHoodedReturn() || eagle.isRemoved()) {
                    Minecraft.getInstance().setCameraEntity(playerEntity);
                    Minecraft.getInstance().options.setCameraType(CameraType.values()[AlexsMobs.PROXY.getPreviousPOV()]);
                } else {
                    float rotX = Mth.wrapDegrees(playerEntity.getYRot() + playerEntity.yHeadRot);
                    float rotY = playerEntity.getXRot();
                    Entity over = null;
                    if (Minecraft.getInstance().hitResult instanceof EntityHitResult) {
                        over = ((EntityHitResult) Minecraft.getInstance().hitResult).getEntity();
                    } else {
                        Minecraft.getInstance().hitResult = null;
                    }
                    boolean loadChunks = playerEntity.level().getGameTime() % 10 == 0;
                    ((EntityBaldEagle) Minecraft.getInstance().getCameraEntity()).directFromPlayer(rotX, rotY, false, over);
                    ClientPacketDistributor.sendToServer((CustomPacketPayload) new MessageUpdateEagleControls(Minecraft.getInstance().getCameraEntity().getId(), rotX, rotY, loadChunks, over == null ? -1 : over.getId()));
                }
            }
        }
    }

    private void updateAllChunks() {
        AlexsMobs.PROXY.updateBiomeVisuals(0, 0);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGetFluidRenderType(EventGetFluidRenderType event) {
        if (Minecraft.getInstance().player.hasEffect(AMEffectRegistry.LAVA_VISION) && (event.getFluidState().is(Fluids.LAVA) || event.getFluidState().is(Fluids.FLOWING_LAVA))) {
        }
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent.Post event) {
        AMItemstackRenderer.incrementTick();
    }

    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        if (Minecraft.getInstance().player.getEffect(AMEffectRegistry.EARTHQUAKE) != null && !Minecraft.getInstance().isPaused()) {
            int duration = Minecraft.getInstance().player.getEffect(AMEffectRegistry.EARTHQUAKE).getDuration();
            float f = (Math.min(10, duration) + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true)) * 0.1F;
            double intensity = f * Minecraft.getInstance().options.screenEffectScale().get();
            RandomSource rng = Minecraft.getInstance().player.getRandom();
            event.setPitch(event.getPitch() + (float)(rng.nextFloat() * 0.1F * intensity));
        }
    }

    @SubscribeEvent
    public void onPostGameOverlay(RenderGuiLayerEvent.Post event) {
        if (renderStaticScreenFor > 0) {
            if (Minecraft.getInstance().player.isAlive() && lastStaticTick != Minecraft.getInstance().level.getGameTime()) {
                renderStaticScreenFor--;
            }
            float staticLevel = (renderStaticScreenFor / 60F);
            if (event.getName().getPath().contains("helmet")) {
                int screenWidth = event.getGuiGraphics().guiWidth();
                int screenHeight = event.getGuiGraphics().guiHeight();

                float ageInTicks = Minecraft.getInstance().level.getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false);
                float staticIndexX = (float) Math.sin(ageInTicks * 0.2F) * 2;
                float staticIndexY = (float) Math.cos(ageInTicks * 0.2F + 3F) * 2;
                float minU = 10 * staticIndexX * 0.125F;
                float maxU = 10 * (0.5F + staticIndexX * 0.125F);
                float minV = 10 * staticIndexY * 0.125F;
                float maxV = 10 * (0.125F + staticIndexY * 0.125F);
                event.getGuiGraphics().blit(AMRenderTypes.STATIC_TEXTURE, 0, 0, screenWidth, screenHeight, minU, minV, maxU - minU, maxV - minV);
            }
            lastStaticTick = Minecraft.getInstance().level.getGameTime();
        }
    }
}



