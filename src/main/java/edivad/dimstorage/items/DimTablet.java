package edivad.dimstorage.items;

import edivad.dimstorage.Main;
import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.container.ContainerDimTablet;
import edivad.dimstorage.manager.DimStorageManager;
import edivad.dimstorage.setup.ModSetup;
import edivad.dimstorage.storage.DimChestStorage;
import edivad.dimstorage.blockentities.BlockEntityDimChest;
import edivad.dimstorage.tools.Config;
import edivad.dimstorage.tools.CustomTranslate;
import edivad.dimstorage.tools.utils.InventoryUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.List;

public class DimTablet extends Item implements MenuProvider {

    public DimTablet() {
        super(new Properties().tab(ModSetup.dimStorageTab).stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        if(!level.isClientSide) {
            ItemStack device = player.getItemInHand(context.getHand());
            BlockEntity blockentity = level.getBlockEntity(pos);
            if(player.isCrouching()) {
                if(blockentity instanceof BlockEntityDimChest dimChest) {
                    if(dimChest.canAccess(player)) {
                        CompoundTag tag = new CompoundTag();
                        tag.put("frequency", dimChest.getFrequency().serializeNBT());
                        tag.putBoolean("bound", true);
                        tag.putBoolean("autocollect", false);
                        device.setTag(tag);

                        player.displayClientMessage(new TextComponent(ChatFormatting.GREEN + "Linked to chest"), false);
                        return InteractionResult.SUCCESS;
                    }
                    player.displayClientMessage(new TextComponent(ChatFormatting.RED + "Access Denied!"), false);
                }
                else {
                    stack.getTag().putBoolean("autocollect", !stack.getTag().getBoolean("autocollect"));
                    if(stack.getTag().getBoolean("autocollect"))
                        player.displayClientMessage(new TextComponent(ChatFormatting.GREEN + "Enabled autocollect"), false);
                    else
                        player.displayClientMessage(new TextComponent(ChatFormatting.RED + "Disabled autocollect"), false);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide && hand.compareTo(InteractionHand.MAIN_HAND) == 0) {
            if(player.isCrouching())
                return super.use(level, player, hand);
            if(!stack.getOrCreateTag().getBoolean("bound")) {
                player.displayClientMessage(new TextComponent(ChatFormatting.RED + "Dimensional Tablet not connected to any DimChest"), false);
                return new InteractionResultHolder<>(InteractionResult.PASS, stack);
            }
            Frequency f = new Frequency(stack.getOrCreateTag().getCompound("frequency"));
            if(!f.canAccess(player))
                return new InteractionResultHolder<>(InteractionResult.PASS, stack);

            NetworkHooks.openGui((ServerPlayer) player, this);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if(!level.isClientSide) {
            if(stack.getOrCreateTag().getBoolean("autocollect") && stack.getOrCreateTag().getBoolean("bound")) {
                if(entity instanceof Player player) {
                    Frequency f = new Frequency(stack.getOrCreateTag().getCompound("frequency"));
                    InvWrapper chestInventory = new InvWrapper(getStorage(level, f));

                    for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        Item item = player.getInventory().getItem(i).getItem();
                        if(Config.DIMTABLET_LIST.get().contains(item.getRegistryName().toString())) {
                            InventoryUtils.mergeItemStack(player.getInventory().getItem(i), 0, getStorage(level, f).getContainerSize(), chestInventory);
                        }
                    }
                }
            }
        }
    }

    private DimChestStorage getStorage(Level level, Frequency frequency) {
        return (DimChestStorage) DimStorageManager.instance(level.isClientSide).getStorage(frequency, "item");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flagIn) {
        if(level != null) {
            if(!stack.hasTag() || !stack.getTag().getBoolean("bound")) {
                tooltip.add(CustomTranslate.translateToLocal("message." + Main.MODID + ".adviceToLink"));
                return;
            }

            CompoundTag tag = stack.getTag();
            if(Screen.hasShiftDown()) {
                Frequency f = new Frequency(tag.getCompound("frequency"));
                tooltip.add(new TranslatableComponent("gui." + Main.MODID + ".frequency").append(" " + f.getChannel()).withStyle(ChatFormatting.GRAY));
                if(f.hasOwner())
                    tooltip.add(new TranslatableComponent("gui." + Main.MODID + ".owner").append(" " + f.getOwner()).withStyle(ChatFormatting.GRAY));

                String yes = new TranslatableComponent("gui." + Main.MODID + ".yes").getString();
                String no = new TranslatableComponent("gui." + Main.MODID + ".no").getString();
                tooltip.add(new TranslatableComponent("gui." + Main.MODID + ".collecting").append(": " + (tag.getBoolean("autocollect") ? yes : no)).withStyle(ChatFormatting.GRAY));
            }
            else
                tooltip.add(CustomTranslate.translateToLocal("message." + Main.MODID + ".holdShift"));

            tooltip.add(CustomTranslate.translateToLocal("message." + Main.MODID + ".changeAutoCollect"));
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ContainerDimTablet(id, inventory, player.level);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(this.getDescriptionId());
    }
}