package menu.matrixdetector;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class MatrixDetectorPlugin extends JavaPlugin implements PluginMessageListener, Listener {

   private static final String CHANNEL = "matrixclient:detect";

   private final Map<UUID, Long> lastAnnounce = new ConcurrentHashMap<>();

   @Override
   public void onEnable() {
      getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);
      getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
      getServer().getPluginManager().registerEvents(this, this);
      getLogger().info("Matrix Detector enabled, listening on " + CHANNEL);
   }

   @Override
   public void onDisable() {
      getServer().getMessenger().unregisterIncomingPluginChannel(this);
      getServer().getMessenger().unregisterOutgoingPluginChannel(this);
   }

   @Override
   public void onPluginMessageReceived(String channel, Player player, byte[] message) {
      if (!CHANNEL.equals(channel) || player == null) {
         return;
      }
      try {
         DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
         String username = readByteString(in);
         String version = readByteString(in);
         if (username == null || username.isBlank() || username.equalsIgnoreCase("NTH_OFF")) {
            return;
         }
         UUID key = player.getUniqueId();
         long now = System.currentTimeMillis();
         Long previous = lastAnnounce.get(key);
         if (previous != null && now - previous < 600000L) {
            return;
         }
         lastAnnounce.put(key, now);
         if (lastAnnounce.size() > 512) {
            lastAnnounce.clear();
         }
         String display = version == null || version.isBlank() || "?".equals(version)
            ? username
            : username + " (v" + version + ")";
         getLogger().warning(display + " is using Matrix Client");
         netBroadcast("\u00a78[\u00a7dMatrix\u00a78] \u00a7e" + display
            + " \u00a77utilise \u00a7d\u00a7lMatrix Client");
      } catch (Exception e) {
         getLogger().warning("Malformed Matrix detection payload from " + player.getName());
      }
   }

   private void netBroadcast(String text) {
      Runnable send = () -> {
         for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(text);
         }
      };
      try {
         Object scheduler = Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler").invoke(Bukkit.getServer());
         scheduler.getClass().getMethod("execute", org.bukkit.plugin.Plugin.class, Runnable.class).invoke(scheduler, this, send);
      } catch (Throwable t) {
         send.run();
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      lastAnnounce.remove(event.getPlayer().getUniqueId());
   }

   private static String readByteString(DataInputStream in) throws IOException {
      int length = readVarInt(in);
      if (length < 0 || length > 1024) {
         throw new EOFException("invalid string length " + length);
      }
      byte[] bytes = new byte[length];
      in.readFully(bytes);
      return new String(bytes, StandardCharsets.UTF_8);
   }

   private static int readVarInt(DataInputStream in) throws IOException {
      int value = 0;
      int position = 0;
      while (position < 32) {
         int current = in.readByte();
         value |= (current & 0x7F) << position;
         if ((current & 0x80) == 0) {
            return value;
         }
         position += 7;
      }
      throw new IOException("varint too big");
   }
}
