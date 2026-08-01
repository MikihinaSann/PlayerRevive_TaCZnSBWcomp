package dev.baechka.playerrevive_compat.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mixin плагин для условной загрузки миксинов SuperbWarfare
 * Загружает миксины только если SuperbWarfare присутствует
 */
public class SbwMixinPlugin implements IMixinConfigPlugin {

    private static final boolean SBW_LOADED;
    private static final boolean SBW_089_OR_NEWER;
    private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)");

    static {
        var modFile = LoadingModList.get().getModFileById("superbwarfare");
        SBW_LOADED = modFile != null;
        SBW_089_OR_NEWER = SBW_LOADED && modFile.getMods().stream()
                .filter(mod -> "superbwarfare".equals(mod.getModId()))
                .findFirst()
                .map(mod -> is089OrNewer(mod.getVersion().toString()))
                .orElse(false);
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
        if (!SBW_LOADED) {
            return false;
        }

        boolean is089Mixin = mixinClassName.contains(".v089.");
        return is089Mixin == SBW_089_OR_NEWER;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        // Если SBW не загружен, возвращаем пустой список - миксины не будут даже пытаться загружаться
        if (!SBW_LOADED) {
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

    private static boolean is089OrNewer(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (!matcher.find()) {
            return false;
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));
        return major > 0 || minor > 8 || (minor == 8 && patch >= 9);
    }
}
