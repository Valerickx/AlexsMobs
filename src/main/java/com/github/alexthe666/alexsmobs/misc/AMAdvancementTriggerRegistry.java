package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.resources.Identifier;

public class AMAdvancementTriggerRegistry {

    public static final AMAdvancementTrigger MOSQUITO_SICK = new AMAdvancementTrigger(Identifier.parse("alexsmobs:mosquito_sick"));
    public static final AMAdvancementTrigger EMU_DODGE = new AMAdvancementTrigger(Identifier.parse("alexsmobs:emu_dodge"));
    public static final AMAdvancementTrigger STOMP_LEAFCUTTER_ANTHILL = new AMAdvancementTrigger(Identifier.parse("alexsmobs:stomp_leafcutter_anthill"));
    public static final AMAdvancementTrigger BALD_EAGLE_CHALLENGE = new AMAdvancementTrigger(Identifier.parse("alexsmobs:bald_eagle_challenge"));
    public static final AMAdvancementTrigger VOID_WORM_SUMMON = new AMAdvancementTrigger(Identifier.parse("alexsmobs:void_worm_summon"));
    public static final AMAdvancementTrigger VOID_WORM_SPLIT = new AMAdvancementTrigger(Identifier.parse("alexsmobs:void_worm_split"));
    public static final AMAdvancementTrigger VOID_WORM_SLAY_HEAD = new AMAdvancementTrigger(Identifier.parse("alexsmobs:void_worm_kill"));
    public static final AMAdvancementTrigger SEAGULL_STEAL = new AMAdvancementTrigger(Identifier.parse("alexsmobs:seagull_steal"));
    public static final AMAdvancementTrigger LAVIATHAN_FOUR_PASSENGERS = new AMAdvancementTrigger(Identifier.parse("alexsmobs:laviathan_four_passengers"));
    public static final AMAdvancementTrigger TRANSMUTE_1000_ITEMS = new AMAdvancementTrigger(Identifier.parse("alexsmobs:transmute_1000_items"));
    public static final AMAdvancementTrigger UNDERMINE_UNDERMINER = new AMAdvancementTrigger(Identifier.parse("alexsmobs:undermine_underminer"));

    public static final AMAdvancementTrigger ELEPHANT_SWAG = new AMAdvancementTrigger(Identifier.parse("alexsmobs:elephant_swag"));
    public static final AMAdvancementTrigger SKUNK_SPRAY = new AMAdvancementTrigger(Identifier.parse("alexsmobs:skunk_spray"));

    public static void init(){
        CriteriaTriggers.register(MOSQUITO_SICK);
        CriteriaTriggers.register(EMU_DODGE);
        CriteriaTriggers.register(STOMP_LEAFCUTTER_ANTHILL);
        CriteriaTriggers.register(BALD_EAGLE_CHALLENGE);
        CriteriaTriggers.register(VOID_WORM_SUMMON);
        CriteriaTriggers.register(VOID_WORM_SPLIT);
        CriteriaTriggers.register(VOID_WORM_SLAY_HEAD);
        CriteriaTriggers.register(SEAGULL_STEAL);
        CriteriaTriggers.register(LAVIATHAN_FOUR_PASSENGERS);
        CriteriaTriggers.register(TRANSMUTE_1000_ITEMS);
        CriteriaTriggers.register(UNDERMINE_UNDERMINER);
        CriteriaTriggers.register(ELEPHANT_SWAG);
        CriteriaTriggers.register(SKUNK_SPRAY);
    }

}
