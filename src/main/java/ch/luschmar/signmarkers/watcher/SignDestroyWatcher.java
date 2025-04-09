package ch.luschmar.signmarkers.watcher;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

public class SignDestroyWatcher implements Listener {
    private final Map<World, MarkerSet> markerSet;

    public SignDestroyWatcher(Map<World, MarkerSet> markerSet) {
        this.markerSet = markerSet;
    }

    @EventHandler
    public void onSignDestroy(BlockBreakEvent event) {
        var block = event.getBlock();
        if (!(block.getState() instanceof Sign)) {
            return;
        }
        var set = markerSet.get(block.getWorld());
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
