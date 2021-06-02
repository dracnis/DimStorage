package edivad.dimstorage.container;

import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.manager.DimStorageManager;
import edivad.dimstorage.setup.Registration;
import edivad.dimstorage.storage.DimChestStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ContainerDimTablet extends Container {

    private DimChestStorage chestInv;

    public ContainerDimTablet(int windowId, PlayerInventory playerInventory, World world)
    {
        super(Registration.DIMTABLET_CONTAINER.get(), windowId);

        ItemStack item = playerInventory.player.getItemInHand(Hand.MAIN_HAND);
        Frequency frequency = new Frequency(item.getOrCreateTag().getCompound("frequency"));

        this.chestInv = (DimChestStorage) DimStorageManager.instance(world.isClientSide).getStorage(frequency, "item");
        this.chestInv.openInventory();

        addOwnSlots();
        addPlayerSlots(playerInventory);
    }

    private void addOwnSlots()
    {
        for(int y = 0; y < 6; y++)
            for(int x = 0; x < 9; x++)
                this.addSlot(new Slot(chestInv, x + y * 9, 8 + x * 18, 18 + y * 18));
    }

    private void addPlayerSlots(IInventory playerInventory)
    {
        // Main Inventory
        for(int y = 0; y < 3; y++)
            for(int x = 0; x < 9; x++)
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
        // Hotbar
        for(int x = 0; x < 9; x++)
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 198));
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int position)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(position);

        if(slot != null && slot.hasItem())
        {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // chest to inventory
            if(position < 54)
            {
                if(!this.moveItemStackTo(itemstack1, 54, this.slots.size(), true))
                    return ItemStack.EMPTY;
            }
            // inventory to chest
            else if(!this.moveItemStackTo(itemstack1, 0, 54, false))
                return ItemStack.EMPTY;

            if(itemstack1.getCount() == 0)
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
        }
        return itemstack;
    }

    @Override
    public void removed(PlayerEntity entityplayer)
    {
        super.removed(entityplayer);
        chestInv.closeInventory();
    }
}
