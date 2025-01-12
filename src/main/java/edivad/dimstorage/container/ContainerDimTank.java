package edivad.dimstorage.container;

import edivad.dimstorage.setup.Registration;
import edivad.dimstorage.blockentities.BlockEntityDimTank;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class ContainerDimTank extends AbstractContainerMenu {

    public BlockEntityDimTank owner;
    public boolean isOpen;

    public ContainerDimTank(int windowId, Inventory inventory, BlockEntityDimTank owner, boolean isOpen) {
        super(Registration.DIMTANK_CONTAINER.get(), windowId);
        this.owner = owner;
        this.isOpen = isOpen;

        addPlayerSlots(inventory);
    }

    private void addPlayerSlots(Container playerInventory) {
        // Main Inventory
        for(int y = 0; y < 3; y++)
            for(int x = 0; x < 9; x++)
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
        // Hotbar
        for(int x = 0; x < 9; x++)
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 198));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
