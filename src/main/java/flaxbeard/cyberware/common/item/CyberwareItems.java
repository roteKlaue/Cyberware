package flaxbeard.cyberware.common.item;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.item.equipment.CyberwareArmorItem;
import flaxbeard.cyberware.common.item.equipment.CyberwareArmorMaterials;
import flaxbeard.cyberware.common.item.equipment.CyberwareSwordItem;
import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CyberwareItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, OverclockedOrgans.MOD_ID);

    public static final List<RegistryObject<? extends Item>> COMPONENT = Collections.unmodifiableList(
            Stream.of("actuator", "reactor", "titanium", "ssc", "plating", "fiberoptics", "fullerene", "synthnerves", "storage", "microelectric")
                    .map(item -> ITEMS.register(item, CyberwareBaseItem::new))
                    .collect(Collectors.toList())
    );

    public static final RegistryObject<CyberwareSwordItem> KATANA = ITEMS.register("katana",
            () -> new CyberwareSwordItem(ItemTier.DIAMOND,3,-2.4f, new Item.Properties().durability(100)));

    public static final RegistryObject<? extends Item> CYBER_EYES_MANUFACTURED = ITEMS.register("cybereyes_manufactured",
            CyberwareBaseItem::new);
    public static final RegistryObject<? extends Item> CYBER_EYES_SALVAGED = ITEMS.register("cybereyes_salvaged",
            CyberwareBaseItem::makeSalvaged);

    public static final RegistryObject<Item> SHADES = ITEMS.register("shades",
            () -> new CyberwareArmorItem(CyberwareArmorMaterials.SHADES, EquipmentSlotType.HEAD,
                    new Item.Properties()));

    public static final RegistryObject<Item> SHADES2 = ITEMS.register("shades2",
            () -> new CyberwareArmorItem(CyberwareArmorMaterials.SHADES2, EquipmentSlotType.HEAD,
                    new Item.Properties()));

    public static final RegistryObject<Item> JACKET = ITEMS.register("jacket",
            () -> new CyberwareArmorItem(CyberwareArmorMaterials.JACKET, EquipmentSlotType.CHEST,
                    new Item.Properties()));

    public static final RegistryObject<CyberwareArmorItem> TRENCHCOAT = registerArmor("trenchcoat",
            CyberwareArmorMaterials.TRENCHCOAT, EquipmentSlotType.CHEST);


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


