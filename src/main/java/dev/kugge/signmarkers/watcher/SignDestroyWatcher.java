package dev.kugge.signmarkers.watcher;

import com.flowpowered.math.vector.Vector3d;
import dev.kugge.signmarkers.SignMarkers;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SignDestroyWatcher implements Listener {

    @EventHandler
    public void onSignDestroy(BlockBreakEvent event) {
        var block = event.getBlock();
        if (!(block.getState() instanceof Sign)) {
            return;
        }
        var set = SignMarkers.markerSet.get(block.getWorld());
        if (set == null) {
            return;
        }

        var pos = new Vector3d(block.getX(), block.getY(), block.getZ());
        var id = "marker-" + pos.getX() + "-" + pos.getY() + "-" + pos.getZ();

        var marker = set.get(id);
        if (marker == null) {
            return;
        }
        set.remove(id);
    }
}
