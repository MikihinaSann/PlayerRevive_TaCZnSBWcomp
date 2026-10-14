package dev.baechka.playerrevive_compat.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Mixin плагин для условной загрузки миксинов TACZ
 * Загружает миксины только если TACZ присутствует
 */
public class TaczMixinPlugin implements IMixinConfigPlugin {

    private static final boolean TACZ_LOADED;

    static {
        TACZ_LOADED = FabricLoader.getInstance().isModLoaded("tacz");
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return TACZ_LOADED;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        // Если TACZ не загружен, возвращаем пустой список - миксины не будут даже пытаться загружаться
        if (!TACZ_LOADED) {
            return Collections.emptyList();
        }
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
