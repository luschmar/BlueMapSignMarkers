package ch.luschmar.signmarkers.watcher;

import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.World;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Map;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SignWatcherTest {
    @Mock
    Map<World, MarkerSet> markerSet;
    @Mock
    Path webRoot;

    @InjectMocks
    SignWatcher signWatcher;

    @Test
    void onSignWrite_empty() {
        var event = mock(SignChangeEvent.class);

        signWatcher.onSignWrite(event);
    }
}