package com.github.alexthe666.alexsmobs.client.render.misc;

import com.google.gson.JsonObject;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.GsonHelper;

public class VoidWormMetadataSection {
    public static final MetadataSectionType<VoidWormMetadataSection> TYPE = MetadataSectionType.fromCodec("void_worm", com.mojang.serialization.Codec.unit(VoidWormMetadataSection::new));
    private final boolean hasEndPortalTexture;

    public VoidWormMetadataSection() {
        this.hasEndPortalTexture = false;
    }

    public VoidWormMetadataSection(boolean hasEndPortalTexture) {
        this.hasEndPortalTexture = hasEndPortalTexture;
    }

    public boolean isEndPortalTexture() {
        return this.hasEndPortalTexture;
    }

    public static VoidWormMetadataSection fromJson(JsonObject json) {
        return new VoidWormMetadataSection(GsonHelper.getAsBoolean(json, "end_portal_texture"));
    }
}
