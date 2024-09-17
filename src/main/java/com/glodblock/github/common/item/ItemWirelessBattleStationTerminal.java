package com.glodblock.github.common.item;

import static net.minecraft.client.gui.GuiScreen.isShiftKeyDown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.glodblock.github.FluidCraft;
import com.glodblock.github.common.tabs.FluidCraftingTabs;
import com.glodblock.github.inventory.InventoryHandler;
import com.glodblock.github.inventory.gui.GuiType;
import com.glodblock.github.inventory.item.WirelessCraftingTerminalInventory;
import com.glodblock.github.inventory.item.WirelessFluidTerminalInventory;
import com.glodblock.github.inventory.item.WirelessInterfaceTerminalInventory;
import com.glodblock.github.inventory.item.WirelessLevelTerminalInventory;
import com.glodblock.github.inventory.item.WirelessPatternTerminalExInventory;
import com.glodblock.github.inventory.item.WirelessPatternTerminalInventory;
import com.glodblock.github.inventory.item.WirelessBattleStationInventory;
import com.glodblock.github.loader.IRegister;
import com.glodblock.github.network.CPacketSwitchGuis;
import com.glodblock.github.util.BlockPos;
import com.glodblock.github.util.ModAndClassUtil;
import com.glodblock.github.util.NameConst;
import com.glodblock.github.util.Util;

import appeng.api.AEApi;
import appeng.api.networking.IGridNode;
import appeng.core.features.AEFeature;

import appeng.core.localization.PlayerMessages;
import appeng.util.Platform;
import baubles.api.BaubleType;
import baubles.api.IBauble;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class ItemWirelessBattleStationTerminal extends ItemBaseWirelessTerminal
        implements IBauble, IRegister<ItemWirelessBattleStationTerminal> {

    public final static String MODE = "mode_main";
    private final static List<GuiType> guis = new ArrayList<>();

    public ItemWirelessBattleStationTerminal() {
        super(null);
        AEApi.instance().registries().wireless().registerWirelessHandler(this);
        this.setFeature(EnumSet.of(AEFeature.WirelessAccessTerminal, AEFeature.PoweredTools));
        setUnlocalizedName(NameConst.ITEM_WIRELESS_ULTRA_TERMINAL);
        setTextureName(FluidCraft.resource(NameConst.ITEM_WIRELESS_ULTRA_TERMINAL).toString());
        guis.add(GuiType.WIRELESS_CRAFTING_TERMINAL);
        guis.add(GuiType.WIRELESS_FLUID_PATTERN_TERMINAL);
        guis.add(GuiType.WIRELESS_FLUID_PATTERN_TERMINAL_EX);
        guis.add(GuiType.WIRELESS_FLUID_TERMINAL);
        guis.add(GuiType.WIRELESS_INTERFACE_TERMINAL);
        guis.add(GuiType.WIRELESS_LEVEL_TERMINAL);
        if (ModAndClassUtil.ThE) {
            guis.add(GuiType.WIRELESS_ESSENTIA_TERMINAL);
        }
    }

    @Override
    public ItemWirelessBattleStationTerminal register() {
        GameRegistry.registerItem(this, NameConst.ITEM_WIRELESS_BATTLE_STATION_TERMINAL, FluidCraft.MODID);
        setCreativeTab(FluidCraftingTabs.INSTANCE);
        return this;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addCheckedInformation(ItemStack stack, EntityPlayer player, List<String> lines,
            boolean displayMoreInfo) {
        super.addCheckedInformation(stack, player, lines, displayMoreInfo);
        if (isShiftKeyDown()) {
            lines.add(StatCollector.translateToLocal(NameConst.TT_ULTRA_TERMINAL));
            lines.add(StatCollector.translateToLocal(NameConst.TT_ULTRA_TERMINAL + "." + guiGuiType(stack)));
            lines.add(NameConst.i18n(NameConst.TT_ULTRA_TERMINAL_TIPS));
            lines.addAll(Arrays.asList(NameConst.i18n(NameConst.TT_ULTRA_TERMINAL_TIPS_DESC).split("\\\\n")));
        } else {
            lines.add(NameConst.i18n(NameConst.TT_SHIFT_FOR_MORE));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return StatCollector.translateToLocalFormatted("item.wireless_battle_station_terminal.name");
    }

    @Override
    public Object getInventory(ItemStack stack, World world, int x, int y, int z, EntityPlayer player) {
        try {
            IGridNode gridNode = Util.getWirelessGrid(stack);

            return new WirelessBattleStationInventory(stack, x, gridNode, player);
        } catch (Exception e) {
            player.addChatMessage(PlayerMessages.OutOfRange.get());
        }
        return null;
    }

    @Override
    public ItemStack onItemRightClick(final ItemStack item, final World w, final EntityPlayer player) {
        if (player.isSneaking()) {
            setNext(readMode(item), item);
            return item;
        }
        readMode(item);
        return super.onItemRightClick(item, w, player);
    }

    public static GuiType readMode(ItemStack stack) {
//        NBTTagCompound data = Platform.openNbtData(stack);
//        if (data.hasKey(MODE)) {
//            String GUI = data.getString(MODE);
//            try {
//                return GuiType.valueOf(GUI);
//            } catch (IllegalArgumentException e) {
//                return GuiType.WIRELESS_BATTLE_STATION_MAIN_TERMINAL;
//            }
//        } else {
        return GuiType.WIRELESS_BATTLE_STATION_MAIN_TERMINAL;
//        }
    }

    public void setNext(GuiType type, ItemStack stack) {
        boolean f = false;
        for (GuiType g : guis) {
            if (f) {
                setMode(g.toString(), stack);
                return;
            }
            if (g == type) {
                f = true;
            }
        }
        setMode(guis.get(0).toString(), stack);
    }

    public void setMode(String mode, ItemStack stack) {
        NBTTagCompound data = Platform.openNbtData(stack);
        data.setString(MODE, mode);
    }

    public void setMode(GuiType mode, ItemStack stack) {
        this.setMode(mode.toString(), stack);
    }

    public static void switchTerminal(EntityPlayer player, GuiType guiType) {
        ImmutablePair<Integer, ItemStack> term = Util.getUltraWirelessTerm(player);
        if (term == null) return;
        if (term.getRight().getItem() instanceof ItemWirelessBattleStationTerminal) {
            ((ItemWirelessBattleStationTerminal) term.getRight().getItem()).setMode(guiType, term.getRight());
        }
        if (Platform.isClient()) {
            FluidCraft.proxy.netHandler.sendToServer(new CPacketSwitchGuis(guiType, true));
        } else {
            InventoryHandler.openGui(
                    player,
                    player.worldObj,
                    new BlockPos(
                            term.getLeft(),
                            Util.GuiHelper.encodeType(
                                    guis.indexOf(GuiType.valueOf(guiType.toString())),
                                    Util.GuiHelper.GuiType.ITEM),
                            1),
                    ForgeDirection.UNKNOWN,
                    guiType);
        }
    }

    public static boolean hasInfinityBoosterCard(EntityPlayer player) {
        ImmutablePair<Integer, ItemStack> term = Util.getUltraWirelessTerm(player);
        if (term == null) return false;
        if (term.getRight().getItem() instanceof ItemWirelessBattleStationTerminal) {
            return Util.hasInfinityBoosterCard(term.getRight());
        }
        return false;
    }

    public static List<GuiType> getGuis() {
        return guis;
    }

    @Override
    public GuiType guiGuiType(ItemStack stack) {
        return readMode(stack);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack itemStack, EntityLivingBase entityLivingBase) {

    }

    @Override
    public void onEquipped(ItemStack itemStack, EntityLivingBase entityLivingBase) {

    }

    @Override
    public void onUnequipped(ItemStack itemStack, EntityLivingBase entityLivingBase) {

    }

    @Override
    public boolean canEquip(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return true;
    }
}
