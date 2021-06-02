package edivad.dimstorage.storage;

import java.util.Arrays;

import edivad.dimstorage.api.AbstractDimStorage;
import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.manager.DimStorageManager;
import edivad.dimstorage.network.PacketHandler;
import edivad.dimstorage.network.packet.OpenChest;
import edivad.dimstorage.tools.utils.InventoryUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class DimChestStorage extends AbstractDimStorage implements IInventory {

    private ItemStack[] items;
    private int open;

    public DimChestStorage(DimStorageManager manager, Frequency freq)
    {
        super(manager, freq);
        empty();
    }

    @Override
    public void clearStorage()
    {
        synchronized(this)
        {
            empty();
            setDirty();
        }
    }

    public void loadFromTag(CompoundNBT tag)
    {
        empty();
        InventoryUtils.readItemStacksFromTag(items, tag.getList("Items", 10));
    }

    @Override
    public String type()
    {
        return "item";
    }

    public CompoundNBT saveToTag()
    {
        CompoundNBT compound = new CompoundNBT();
        compound.put("Items", InventoryUtils.writeItemStacksToTag(this.items));
        return compound;
    }

    public ItemStack getItem(int slot)
    {
        synchronized(this)
        {
            return items[slot];
        }
    }

    public ItemStack removeItemNoUpdate(int index)
    {
        synchronized(this)
        {
            return InventoryUtils.removeStackFromSlot(this, index);
        }
    }

    public void setItem(int slot, ItemStack stack)
    {
        synchronized(this)
        {
            items[slot] = stack;
            setChanged();
        }
    }

    public void openInventory()
    {
        if(manager.isServer())
        {
            synchronized(this)
            {
                open++;
                if(open >= 1)
                {
                    PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new OpenChest(freq, true));
                }
            }
        }
    }

    public void closeInventory()
    {
        if(manager.isServer())
        {
            synchronized(this)
            {
                open--;
                if(open <= 0)
                {
                    PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new OpenChest(freq, false));
                }
            }
        }
    }

    public int getNumOpen()
    {
        return open;
    }

    @Override
    public int getContainerSize()
    {
        return 54;
    }

    @Override
    public boolean isEmpty()
    {
        for(ItemStack itemStack : items)
            if(!itemStack.isEmpty())
                return false;
        return true;
    }

    public ItemStack removeItem(int slot, int size)
    {
        synchronized(this)
        {
            return InventoryUtils.decrStackSize(this, slot, size);
        }
    }

    @Override
    public int getMaxStackSize()
    {
        return 64;
    }

    @Override
    public void setChanged()
    {
        setDirty();
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return true;
    }

    public void empty()
    {
        synchronized(this)
        {
            items = new ItemStack [getContainerSize()];
            Arrays.fill(items, ItemStack.EMPTY);
        }
    }

    public void setClientOpen(int i)
    {
        if(!manager.isServer())
            open = i;
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
    }

    @Override
    public void startOpen(PlayerEntity player)
    {
    }

    @Override
    public void stopOpen(PlayerEntity player)
    {
    }
}
