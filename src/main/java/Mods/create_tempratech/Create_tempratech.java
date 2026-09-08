package Mods.create_tempratech;

import Mods.create_tempratech.Client.ClientRegister;
import Mods.create_tempratech.Network.ModPayloads;
import Mods.create_tempratech.Regs.ModAttachments;
import Mods.create_tempratech.Regs.modBlockEntities;
import Mods.create_tempratech.Regs.modBlocks;
import Mods.create_tempratech.Regs.modFluids;
import Mods.create_tempratech.Regs.modItems;
import Mods.create_tempratech.ThermalSystem.Simulation.ThermalHazardHandler;
import Mods.create_tempratech.ThermalSystem.Simulation.ThermalServerTickHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(Create_tempratech.MODID)
public class Create_tempratech {
    public static final String MODID = "create_tempratech";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB =
            CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.create_tempratech"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> modItems.HEAT_HARVESTER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(modItems.HEAT_HARVESTER);
                        output.accept(modItems.TITANIUM_INGOT);
                        output.accept(modItems.HEAT_PIPE);
                        output.accept(modItems.THERMOMETER);
                        output.accept(modItems.THERMAL_GOGGLES);
                        modFluids.all().forEach(fluid -> output.accept(fluid.bucket()));
                    })
                    .build());

    public Create_tempratech(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModPayloads::register);

        modFluids.register(modEventBus);
        modBlocks.BLOCKS.register(modEventBus);
        modItems.ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModAttachments.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(ThermalServerTickHandler.class);
        NeoForge.EVENT_BUS.register(ThermalHazardHandler.class);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(ClientRegister::registerRenderers);
        modEventBus.addListener(ClientRegister::registerFluidExtensions);
        modEventBus.addListener(ClientRegister::registerReloadListeners);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
        Config.items.forEach(item -> LOGGER.info("ITEM >> {}", item));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
