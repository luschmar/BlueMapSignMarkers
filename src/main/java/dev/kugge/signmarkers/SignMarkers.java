package dev.kugge.signmarkers;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.gson.MarkerGson;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import dev.kugge.signmarkers.watcher.SignDestroyWatcher;
import dev.kugge.signmarkers.watcher.SignWatcher;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SignMarkers extends JavaPlugin {
    private final Map<World, MarkerSet> markerSet = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        createFiles();
        for (World world : Bukkit.getWorlds()) {
            loadWorldMarkerSet(world);
            registerWorld(world);
        }
        BlueMapAPI.onEnable(api -> {
            var webRoot = api.getWebApp().getWebRoot();
            Bukkit.getPluginManager().registerEvents(new SignWatcher(markerSet, webRoot), this);
            Bukkit.getPluginManager().registerEvents(new SignDestroyWatcher(markerSet), this);
        });
    }

    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) {
            saveWorldMarkerSet(world);
        }
    }

    private void createFiles() {
        for (var world : Bukkit.getWorlds()) {
            var name = "marker-set-" + world.getName() + ".json";
            var file = new File(this.getDataFolder(), name);
            try {
                var folder = this.getDataFolder();
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, "cannot load json", ex);
            }
        }
    }

    private void saveWorldMarkerSet(World world) {
        var name = "marker-set-" + world.getName() + ".json";
        var file = new File(this.getDataFolder(), name);
        try (FileWriter writer = new FileWriter(file)) {
            MarkerGson.INSTANCE.toJson(markerSet.get(world), writer);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "saveWorldMarkerSet failed", ex);
        }
    }

    private void loadWorldMarkerSet(World world) {
        var name = "marker-set-" + world.getName() + ".json";
        var file = new File(this.getDataFolder(), name);
        try (FileReader reader = new FileReader(file)) {
            var set = MarkerGson.INSTANCE.fromJson(reader, MarkerSet.class);
            if (set != null) {
                markerSet.put(world, set);
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "loadWorldMarkerSet failed", ex);
        }
    }

    private void registerWorld(World world) {
        BlueMapAPI.onEnable(api ->
                api.getWorld(world).ifPresent(blueWorld -> {
                    for (var map : blueWorld.getMaps()) {
                        var label = "sign-markers-" + world.getName();
                        var set = markerSet.get(world);
                        if (set == null) {
                            set = MarkerSet.builder().label(label).build();
                        }
                        map.getMarkerSets().put(label, set);
                        markerSet.put(world, set);
                    }
                })
        );
    }
}
