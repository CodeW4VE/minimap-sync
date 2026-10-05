package net.earthcomputer.minimapsync.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.earthcomputer.minimapsync.MinimapSync;
import net.earthcomputer.minimapsync.model.Model;
import net.earthcomputer.minimapsync.model.Waypoint;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.sdl.SDLDialog;
import org.lwjgl.sdl.SDL_DialogFileCallback;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/** @noinspection ConstantConditions */ // @Nullable on this.minecraft is annoying
public class ChooseIconScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Component TITLE = Component.nullToEmpty("Choose Icon");

    private final Screen parent;
    @Nullable
    private final String initialSelection;
    private final Consumer<@Nullable String> resultConsumer;
    private final Function<String, IconSelectionList.@Nullable IconRenderer> iconRendererSupplier;
    private IconSelectionList selectionList;
    private Button doneButton;
    private Button deleteButton;

    public ChooseIconScreen(
        Screen parent,
        @Nullable String initialSelection,
        Consumer<@Nullable String> resultConsumer,
        Function<String, IconSelectionList.@Nullable IconRenderer> iconRendererSupplier
    ) {
        super(TITLE);
        this.parent = parent;
        this.initialSelection = initialSelection;
        this.resultConsumer = resultConsumer;
        this.iconRendererSupplier = iconRendererSupplier;
    }

    @Override
    protected void init() {
        boolean firstInit = selectionList == null;
        selectionList = new IconSelectionList(
            minecraft,
            this::onDone,
            this,
            width, height - 64 - 48,
            48,
            36,
            selectionList
        ) {
            @Override
            public void setSelected(@Nullable IconSelectionList.Entry selected) {
                super.setSelected(selected);
                doneButton.active = selected != null;
                deleteButton.active = selected != null;
            }
        };
        if (firstInit) {
            ClientPacketListener connection = minecraft.getConnection();
            if (connection != null) {
                Model model = Model.get(connection);
                for (String icon : (Iterable<String>) model.icons().names().stream().sorted(String.CASE_INSENSITIVE_ORDER)::iterator) {
                    selectionList.addInitialEntry(icon, Objects.requireNonNullElse(iconRendererSupplier.apply(icon), IconSelectionList.IconRenderer.NOOP));
                }
            }
        }
        addWidget(selectionList);
        doneButton = addRenderableWidget(Button.builder(MinimapSync.createComponent(minecraft.level.registryAccess(), "{\"translate\": \"gui.done\"}"), button -> onDone())
            .bounds(width / 2 - 154, height - 52, 150, 20)
            .build());
        addRenderableWidget(Button.builder(MinimapSync.createComponent(minecraft.level.registryAccess(), "{\"translate\": \"gui.cancel\"}"), button -> onClose())
            .bounds(width / 2 + 4, height - 52, 150, 20)
            .build());
        addRenderableWidget(Button.builder(Component.nullToEmpty("Add New Icon"), button -> onAdd())
            .bounds(width / 2 - 154, height - 28, 150, 20)
            .build());
        deleteButton = addRenderableWidget(Button.builder(Component.nullToEmpty("Delete"), button -> selectionList.removeSelectedEntry())
            .bounds(width / 2 + 4, height - 28, 150, 20)
            .build());
        setInitialFocus(selectionList);
        if (firstInit) {
            for (IconSelectionList.Entry child : selectionList.children()) {
                if (child.getName().equals(initialSelection)) {
                    selectionList.setSelected(child);
                    break;
                }
            }
        }
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.centeredText(font, title, width / 2, 8, 0xffffffff);
        selectionList.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void onDone() {
        for (String toRemove : selectionList.getIconsToRemove()) {
            MinimapSyncClient.onRemoveIcon(null, toRemove);
        }
        selectionList.getIconsToAdd().forEach((name, path) -> {
            // Make sure the image hasn't been swapped out from beneath us, and convert to PNG while we're at it
            try (InputStream in = Files.newInputStream(path);
                 NativeImage image = NativeImage.read(in)
            ) {
                if (image.getWidth() != image.getHeight()
                    || image.getWidth() < Waypoint.MIN_ICON_DIMENSIONS
                    || image.getWidth() > Waypoint.MAX_ICON_DIMENSIONS
                    || !Mth.isPowerOfTwo(image.getWidth())
                ) {
                    return;
                }
                Path encoded = Files.createTempFile("minimapsync-icon-", ".png");
                try { image.writeToFile(encoded); MinimapSyncClient.onAddIcon(null, name, Files.readAllBytes(encoded)); }
                finally { Files.deleteIfExists(encoded); } // asByteArray() is PNG format
            } catch (IOException e) {
                LOGGER.error("Failed to read icon", e);
            }
        });

        IconSelectionList.Entry selected = selectionList.getSelected();
        if (selected != null) {
            resultConsumer.accept(selected.getName());
        }

        onClose();
    }

    private void onAdd() {
        final SDL_DialogFileCallback[] holder = new SDL_DialogFileCallback[1];
        holder[0] = SDL_DialogFileCallback.create((userdata, filelist, filter) -> {
            java.util.List<String> paths = new java.util.ArrayList<>();
            if (filelist != 0) {
                for (long offset = 0; ; offset += org.lwjgl.system.Pointer.POINTER_SIZE) {
                    long address = MemoryUtil.memGetAddress(filelist + offset);
                    if (address == 0) break;
                    paths.add(MemoryUtil.memUTF8(address));
                }
            }
            minecraft.execute(() -> {
                pendingDialogs.remove(holder[0]);
                holder[0].free();
                if (!paths.isEmpty()) addChosenFiles(paths.toArray(String[]::new));
            });
        });
        pendingDialogs.add(holder[0]);
        SDLDialog.SDL_ShowOpenFileDialog(holder[0], 0, minecraft.getWindow().handle(), null, (String)null, true);
    }

    private static final Set<SDL_DialogFileCallback> pendingDialogs = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private void addChosenFiles(String[] chosenFiles) {
        Set<String> errors = new LinkedHashSet<>();
        for (String file : chosenFiles) {
            // Normalize file separators
            if (!"/".equals(File.separator)) {
                file = file.replace("/", File.separator);
            }

            String iconName = file.substring(file.lastIndexOf(File.separator) + File.separator.length());
            int dotIndex = iconName.lastIndexOf('.');
            if (dotIndex > 0) {
                iconName = iconName.substring(0, dotIndex);
            }
            if (iconName.isBlank()) {
                iconName = "icon";
            } else {
                iconName = iconName.toLowerCase(Locale.ROOT);
            }

            String error = selectionList.addEntry(iconName, Path.of(file));
            if (error != null) {
                errors.add(error);
            }
        }

        if (!errors.isEmpty()) {
            minecraft.setScreenAndShow(new ConfirmScreen(
                result -> minecraft.setScreenAndShow(this),
                Component.nullToEmpty("Some errors occurred importing icon(s)"),
                Component.nullToEmpty(String.join("\n", errors))
            , Component.literal("OK"), Component.literal("Back")));
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreenAndShow(parent);
    }

    @Override
    public void removed() {
        for (IconSelectionList.Entry entry : selectionList.children()) {
            entry.close();
        }
        super.removed();
    }
}
