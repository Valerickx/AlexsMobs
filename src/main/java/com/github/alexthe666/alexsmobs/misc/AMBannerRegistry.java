package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMBannerRegistry {

    public static final DeferredRegister<BannerPattern> DEF_REG = DeferredRegister.create(Registries.BANNER_PATTERN, AlexsMobs.MODID);

    static{
        DEF_REG.register("bear", () ->  new BannerPattern(Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "bear"), "alexsmobs.banner.bear"));
        DEF_REG.register("australia_0", () ->  new BannerPattern(Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "australia_0"), "alexsmobs.banner.australia_0"));
        DEF_REG.register("australia_1", () ->  new BannerPattern(Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "australia_1"), "alexsmobs.banner.australia_1"));
        DEF_REG.register("new_mexico", () ->  new BannerPattern(Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "new_mexico"), "alexsmobs.banner.new_mexico"));
        DEF_REG.register("brazil", () ->  new BannerPattern(Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "brazil"), "alexsmobs.banner.brazil"));
    }
}



