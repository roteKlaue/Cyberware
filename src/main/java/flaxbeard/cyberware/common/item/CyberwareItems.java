package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.client.render.TrenchCoatModel;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.item.equipment.CyberwareArmorItem;
import flaxbeard.cyberware.common.item.equipment.CyberwareArmorMaterials;
import flaxbeard.cyberware.common.item.equipment.CyberwareItemTiers;
import flaxbeard.cyberware.common.item.equipment.CyberwareSwordItem;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CyberwareItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, OverclockedOrgans.MOD_ID);

    public static final List<RegistryObject<Item>> COMPONENTS = Collections.unmodifiableList(
            Stream.of("actuator", "reactor", "titanium", "ssc", "plating", "fiberoptics", "fullerene", "synthnerves", "storage", "microelectric")
                    .map(CyberwareItems::registerItem)
                    .collect(Collectors.toList())
    );

    public static final RegistryObject<CybereyesItem> CYBER_EYES_MANUFACTURED =
            ITEMS.register("cybereyes_manufactured", CybereyesItem::makeManufactured);
    public static final RegistryObject<EyeUpgradeItem> HUDLENS_MANUFACTURED =
            ITEMS.register("hudlens_manufactured", EyeUpgradeItem::makeManufactured);
    public static final RegistryObject<CybereyesItem> CYBER_EYES_SALVAGED =
            ITEMS.register("cybereyes_salvaged", () -> CybereyesItem.makeSalvaged(CYBER_EYES_MANUFACTURED));
    public static final RegistryObject<EyeUpgradeItem> HUDLENS_SALVAGED =
            ITEMS.register("hudlens_salvaged", () -> EyeUpgradeItem.makeSalvaged(HUDLENS_MANUFACTURED));
    public static final RegistryObject<Item> CYBER_LEG_LEFT = registerItem("cyber_leg_left");
    public static final RegistryObject<Item> CYBER_LEG_RIGHT = registerItem("cyber_leg_right");
    public static final RegistryObject<Item> CYBER_ARM_RIGHT = registerItem("cyber_arm_right");
    public static final RegistryObject<Item> CYBER_ARM_LEFT = registerItem("cyber_arm_left");

    public static final RegistryObject<BodyPartItem> BODY_PART_EYES =
            ITEMS.register("body_part_eyes", () -> new BodyPartItem(BodyPartItem.Variant.EYES));
    public static final RegistryObject<BodyPartItem> BODY_PART_BRAIN =
            ITEMS.register("body_part_brain", () -> new BodyPartItem(BodyPartItem.Variant.BRAIN));
    public static final RegistryObject<BodyPartItem> BODY_PART_HEART =
            ITEMS.register("body_part_heart", () -> new BodyPartItem(BodyPartItem.Variant.HEART));
    public static final RegistryObject<BodyPartItem> BODY_PART_LUNGS =
            ITEMS.register("body_part_lungs", () -> new BodyPartItem(BodyPartItem.Variant.LUNGS));
    public static final RegistryObject<BodyPartItem> BODY_PART_STOMACH =
            ITEMS.register("body_part_stomach", () -> new BodyPartItem(BodyPartItem.Variant.STOMACH));
    public static final RegistryObject<BodyPartItem> BODY_PART_SKIN =
            ITEMS.register("body_part_skin", () -> new BodyPartItem(BodyPartItem.Variant.SKIN));
    public static final RegistryObject<BodyPartItem> BODY_PART_MUSCLES =
            ITEMS.register("body_part_muscles", () -> new BodyPartItem(BodyPartItem.Variant.MUSCLES));
    public static final RegistryObject<BodyPartItem> BODY_PART_BONES =
            ITEMS.register("body_part_bones", () -> new BodyPartItem(BodyPartItem.Variant.BONES));
    public static final RegistryObject<BodyPartItem> BODY_PART_ARM_LEFT =
            ITEMS.register("body_part_arm_left", () -> new BodyPartItem(BodyPartItem.Variant.ARM_LEFT));
    public static final RegistryObject<BodyPartItem> BODY_PART_ARM_RIGHT =
            ITEMS.register("body_part_arm_right", () -> new BodyPartItem(BodyPartItem.Variant.ARM_RIGHT));
    public static final RegistryObject<BodyPartItem> BODY_PART_LEG_LEFT =
            ITEMS.register("body_part_leg_left", () -> new BodyPartItem(BodyPartItem.Variant.LEG_LEFT));
    public static final RegistryObject<BodyPartItem> BODY_PART_LEG_RIGHT =
            ITEMS.register("body_part_leg_right", () -> new BodyPartItem(BodyPartItem.Variant.LEG_RIGHT));

    public static RegistryObject<CyberwareSwordItem> KATANA;
    public static RegistryObject<CyberwareArmorItem> SHADES;
    public static RegistryObject<CyberwareArmorItem> SHADES2;
    public static RegistryObject<CyberwareArmorItem> JACKET;
    public static RegistryObject<CyberwareArmorItem> TRENCHCOAT;
    public static RegistryObject<NeuropozyneItem> NEUROPOZYNE;

    public static final RegistryObject<BlueprintItem> BLUEPRINT = ITEMS.register("blueprint",
            BlueprintItem::new);

    static {
        boolean katana = CyberwareConfig.ENABLE_KATANA.get();
        if (katana) {
            KATANA = ITEMS.register("katana",
                    () -> new CyberwareSwordItem(CyberwareItemTiers.KATANA,3,-2.4f, new Item.Properties()));
        }

        boolean clothes = CyberwareConfig.ENABLE_CLOTHES.get();
        if (clothes) {
            SHADES = registerArmor("shades",
                    CyberwareArmorMaterials.SHADES, EquipmentSlotType.HEAD);
            SHADES2 = registerArmor("shades2",
                    CyberwareArmorMaterials.SHADES2, EquipmentSlotType.HEAD);
            JACKET = registerArmor("jacket",
                    CyberwareArmorMaterials.JACKET, EquipmentSlotType.CHEST);
            TRENCHCOAT = registerArmor("trenchcoat",
                    CyberwareArmorMaterials.TRENCHCOAT, EquipmentSlotType.CHEST,
                    () -> new TrenchCoatModel(1.0f));
        }

        NEUROPOZYNE = registerItem("neuropozyne", () -> new NeuropozyneItem(
                (katana || clothes) ? CreativeModeTabs.EQUIPMENT_GROUP : CreativeModeTabs.MANUFACTURED_GROUP
        ));
    }

    private static RegistryObject<Item> registerItem(@Nonnull String name) {
        return registerItem(name, () -> new Item(new Item.Properties()));
    }

    private static <T extends Item> RegistryObject<T> registerItem(@Nonnull String name, Supplier<T> supplier) {
        return ITEMS.register(name.toLowerCase(), supplier);
    }

    public static <T extends Block> RegistryObject<BlockItem> registerBlockItem(String name, RegistryObject<T> block) {
        return registerBlockItem(name, block, BlockItem::new);
    }

    public static <T extends Block, Z extends BlockItem> RegistryObject<BlockItem> registerBlockItem(String name, RegistryObject<T> block, BiFunction<Block, Item.Properties, Z> supplier) {
        return ITEMS.register(name, () -> supplier.apply(block.get(),
                new Item.Properties().tab(CreativeModeTabs.BLOCK_GROUP)));
    }

    public static RegistryObject<CyberwareArmorItem> registerArmor(String name, IArmorMaterial material, EquipmentSlotType slot) {
        return registerArmor(name, material, slot, null);
    }

    public static RegistryObject<CyberwareArmorItem> registerArmor(String name, IArmorMaterial material, EquipmentSlotType slot, Supplier<BipedModel<LivingEntity>> modelSupplier) {
        return ITEMS.register(name, () -> new CyberwareArmorItem(material, slot, new Item.Properties(), modelSupplier));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
