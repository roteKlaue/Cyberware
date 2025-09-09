package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.item.equipment.CyberwareArmorItem;
import flaxbeard.cyberware.common.item.equipment.CyberwareArmorMaterials;
import flaxbeard.cyberware.common.item.equipment.CyberwareItemTiers;
import flaxbeard.cyberware.common.item.equipment.CyberwareSwordItem;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CyberwareItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, OverclockedOrgans.MOD_ID);

    public static final List<RegistryObject<CyberwareBaseItem>> COMPONENTS = Collections.unmodifiableList(
            Stream.of("actuator", "reactor", "titanium", "ssc", "plating", "fiberoptics", "fullerene", "synthnerves", "storage", "microelectric")
                    .map(item -> ITEMS.register(item, CyberwareBaseItem::new))
                    .collect(Collectors.toList())
    );

    public static final RegistryObject<CyberwareBaseItem> CYBER_EYES_MANUFACTURED = ITEMS.register("cybereyes_manufactured",
            EyeUpgradeItem::new);
    public static final RegistryObject<? extends Item> CYBER_EYES_SALVAGED = ITEMS.register("cybereyes_salvaged",
            TestCyberwareItem::makeSalvaged);

    public static RegistryObject<CyberwareSwordItem> KATANA;
    public static RegistryObject<CyberwareArmorItem> SHADES;
    public static RegistryObject<CyberwareArmorItem> SHADES2;
    public static RegistryObject<CyberwareArmorItem> JACKET;
    public static RegistryObject<CyberwareArmorItem> TRENCHCOAT;

    public static final RegistryObject<BlueprintItem> BLUEPRINT = ITEMS.register("blueprint",
            BlueprintItem::new);

    public static final List<RegistryObject<? extends Item>> MANUFACTURED_ITEMS = Arrays.asList(
//            BLUEPRINT,
            CYBER_EYES_MANUFACTURED,
            SHADES,
            SHADES2,
            JACKET,
            TRENCHCOAT
    );


    static {
        if (CyberwareConfig.ENABLE_KATANA.get()) {
            KATANA = ITEMS.register("katana",
                    () -> new CyberwareSwordItem(CyberwareItemTiers.KATANA,3,-2.4f, new Item.Properties()));
        }

        if (CyberwareConfig.ENABLE_CLOTHES.get()) {
            SHADES = registerArmor("shades",
                    CyberwareArmorMaterials.SHADES, EquipmentSlotType.HEAD);
            SHADES2 = registerArmor("shades2",
                    CyberwareArmorMaterials.SHADES2, EquipmentSlotType.HEAD);
            JACKET = registerArmor("jacket",
                    CyberwareArmorMaterials.JACKET, EquipmentSlotType.CHEST);
            TRENCHCOAT = registerArmor("trenchcoat",
                    CyberwareArmorMaterials.TRENCHCOAT, EquipmentSlotType.CHEST);
        }
    }

    public static <T extends Block> RegistryObject<BlockItem> registerBlockItem(String name, RegistryObject<T> block) {
        return registerBlockItem(name, block, BlockItem::new);
    }

    public static <T extends Block, Z extends BlockItem> RegistryObject<BlockItem> registerBlockItem(String name, RegistryObject<T> block, BiFunction<Block, Item.Properties, Z> supplier) {
        return ITEMS.register(name, () -> supplier.apply(block.get(),
                new Item.Properties().tab(CreativeModeTabs.BLOCK_GROUP)));
    }

    public static RegistryObject<CyberwareArmorItem> registerArmor(String name, IArmorMaterial material, EquipmentSlotType slot) {
        return ITEMS.register(name, () -> new CyberwareArmorItem(material, slot, new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
