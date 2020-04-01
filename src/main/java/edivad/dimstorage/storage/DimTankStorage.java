package edivad.dimstorage.storage;

import edivad.dimstorage.api.AbstractDimStorage;
import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.manager.DimStorageManager;
import edivad.dimstorage.tools.extra.fluid.ExtendedFluidTank;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class DimTankStorage extends AbstractDimStorage implements IFluidHandler {

	private class Tank extends ExtendedFluidTank {

		public Tank(int capacity)
		{
			super(capacity);
		}

		@Override
		public void onLiquidChanged()
		{
			setDirty();
		}
	}

	public static final int CAPACITY = 16000;
	private Tank tank;

	public DimTankStorage(DimStorageManager manager, Frequency freq)
	{
		super(manager, freq);
		tank = new Tank(CAPACITY);
	}

	@Override
	public int getTanks()
	{
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank)
	{
		return this.tank.getFluid();
	}

	@Override
	public int getTankCapacity(int tank)
	{
		return CAPACITY;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack)
	{
		return this.tank.isFluidValid(stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action)
	{
		return tank.fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action)
	{
		return tank.drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action)
	{
		return tank.drain(maxDrain, action);
	}

	@Override
	public void clearStorage()
	{
		tank = new Tank(CAPACITY);
		setDirty();
	}

	@Override
	public String type()
	{
		return "fluid";
	}

	@Override
	public CompoundNBT saveToTag()
	{
		CompoundNBT compound = new CompoundNBT();
		compound.put("tank", tank.toTag());
		return compound;
	}

	@Override
	public void loadFromTag(CompoundNBT tag)
	{
		tank.fromTag(tag.getCompound("tank"));
	}
}
