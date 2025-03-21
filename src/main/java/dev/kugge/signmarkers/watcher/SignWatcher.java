package dev.kugge.signmarkers.watcher;

import de.bluecolored.bluemap.api.markers.POIMarker;
import dev.kugge.signmarkers.SignMarkers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector2i;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class SignWatcher implements Listener {
    private final Path webRoot;

    public SignWatcher(Path webRoot) {
        this.webRoot = webRoot;
    }

    @EventHandler
    public void onSignWrite(SignChangeEvent event) {
        var header = event.line(0);
        if (header == Component.empty() || header == null) {
            return;
        }
        if (!header.toString().contains("[map]")){
            return;
        }

        var cicon = event.line(3);
        if (cicon == Component.empty() || cicon == null){
            return;
        }

        var icon = "./markers/" + LegacyComponentSerializer.legacySection().serialize(cicon) + ".png";
        var iconFile = new File(webRoot + "/" + icon);
        if (!iconFile.exists()) {
            return;
        }

        Vector2i anchor;
        try {
            var image = ImageIO.read(iconFile);
            int width = image.getWidth();
            int height = image.getHeight();
            anchor = new Vector2i(height/2, width/2);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        var clabel1 = event.line(1);
        if (clabel1 == Component.empty() || clabel1 == null){
            return;
        }

        var clabel2 = event.line(2);
        var label = LegacyComponentSerializer.legacySection().serialize(clabel1)
                     + LegacyComponentSerializer.legacySection().serialize(clabel2);

        var block = event.getBlock();
        var pos = new Vector3d(block.getX(), block.getY(), block.getZ());

        var id = "marker-" + pos.getX() + "-" + pos.getY() + "-" + pos.getZ();
        var marker = POIMarker.builder().label(label).position(pos).icon(icon, anchor).maxDistance(1000).build();
        SignMarkers.markerSet.get(block.getWorld()).put(id, marker);

        // Delete [map] and icon lines
        event.line(0, Component.empty());
        event.line(3, Component.empty());
    }
}
