package edivad.dimstorage.tools.extra;

import javax.annotation.Nonnull;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NumberNBT;

public class InventoryUtils {

	/**
	 * NBT item loading function with support for stack sizes > 32K
	 */
	public static void readItemStacksFromTag(ItemStack[] items, ListNBT tagList)
	{
		for(int i = 0; i < tagList.size(); i++)
		{
			CompoundNBT tag = tagList.getCompound(i);
			int b = tag.getShort("Slot");
			items[b] = ItemStack.read(tag);
			if(tag.contains("Quantity"))
			{
				items[b].setCount(((NumberNBT) tag.get("Quantity")).getInt());
			}
		}
	}

	/**
	 * NBT item saving function
	 */
	public static ListNBT writeItemStacksToTag(ItemStack[] items)
	{
		return writeItemStacksToTag(items, 64);
	}

	/**
	 * NBT item saving function with support for stack sizes > 32K
	 */
	public static ListNBT writeItemStacksToTag(ItemStack[] items, int maxQuantity)
	{
		ListNBT tagList = new ListNBT();
		for(int i = 0; i < items.length; i++)
		{
			CompoundNBT tag = new CompoundNBT();
			tag.putShort("Slot", (short) i);
			items[i].setTag(tag);

			if(maxQuantity > Short.MAX_VALUE)
			{
				tag.putInt("Quantity", items[i].getCount());
			}
			else if(maxQuantity > Byte.MAX_VALUE)
			{
				tag.putShort("Quantity", (short) items[i].getCount());
			}
			tagList.add(tag);
		}
		return tagList;
	}

	/**
	 * Static default implementation for IInventory method
	 */
	public static ItemStack removeStackFromSlot(IInventory inv, int slot)
	{
		ItemStack stack = inv.getStackInSlot(slot);
		inv.setInventorySlotContents(slot, ItemStack.EMPTY);
		return stack;
	}

	/**
	 * Static default implementation for IInventory method
	 */
	@Nonnull
	public static ItemStack decrStackSize(IInventory inv, int slot, int size)
	{
		ItemStack item = inv.getStackInSlot(slot);

		if(!item.isEmpty())
		{
			if(item.getCount() <= size)
			{
				inv.setInventorySlotContents(slot, ItemStack.EMPTY);
				inv.markDirty();
				return item;
			}
			ItemStack itemstack1 = item.split(size);
			if(item.getCount() == 0)
			{
				inv.setInventorySlotContents(slot, ItemStack.EMPTY);
			}
			else
			{
				inv.setInventorySlotContents(slot, item);
			}

			inv.markDirty();
			return itemstack1;
		}
		return ItemStack.EMPTY;
	}
}
