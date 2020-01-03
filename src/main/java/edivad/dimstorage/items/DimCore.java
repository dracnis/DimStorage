package edivad.dimstorage.items;

import edivad.dimstorage.Main;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class DimCore extends Item {

	public DimCore()
	{
		super(new Properties().group(Main.dimStorageTab).maxStackSize(64));
		setRegistryName(new ResourceLocation(Main.MODID, "dim_core"));
	}
}
