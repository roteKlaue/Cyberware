package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.BlueprintArchiveBlockEntity;
import flaxbeard.cyberware.common.block.entities.CyberwareBlockEntities;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class BlueprintArchiveBlock extends NamedContainerBlock<BlueprintArchiveBlockEntity> {
    public BlueprintArchiveBlock() {
        super(Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.STONE),
                CyberwareBlockEntities.BLUEPRINT_ARCHIVE,
                BlueprintArchiveBlockEntity.class,
                new BlockEntityNameAccessor<BlueprintArchiveBlockEntity>() {
                    @Override
                    public void setName(BlueprintArchiveBlockEntity te, ITextComponent name) {
                        te.setCustomName(name);
                    }
                    @Nonnull
                    @Override
                    public ITextComponent getName(BlueprintArchiveBlockEntity te) {
                        return te.getDisplayName();
                    }
                }, (t) -> t.items);
    }
}
