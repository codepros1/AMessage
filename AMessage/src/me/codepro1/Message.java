package me.codepro1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Message
  extends JavaPlugin
  implements Listener
{
  public void onEnable()
  {
    Bukkit.getPluginManager().registerEvents(this, this);
    getConfig().addDefault("format.send", "�8? �3�lPRIVATE �8? �6Sent To: �a? �9(target) �8� �7(message)");
    getConfig().addDefault("format.recieve", ChatColor.YELLOW + "�8? �3�lPRIVATE �8? �6Message From: �a? �9(sender) �8� �7(message)");
    getConfig().addDefault("sender", "LEVEL_UP");
    getConfig().addDefault("reciever", "EXPLODE");
    getConfig().options().copyDefaults(true);
    saveConfig();
  }
  
  public String buildConfigMessage(String s, Player target, Player sender, String message)
  {
    if (s.contains("(target")) {
      s = s.replace("(target)", target.getName());
    }
    if (s.contains("(sender")) {
      s = s.replace("(sender)", sender.getName());
    }
    if (s.contains("(message")) {
      s = s.replace("(message)", message);
    }
    return s;
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    getConfig().set(event.getPlayer().getUniqueId() + ".lastPlayer", "");
    saveConfig();
  }
  
  public String buildMessage(String[] args)
  {
    String message = "";
    for (int i = 1; i < args.length; i++) {
      message = message + " " + args[i];
    }
    return message;
  }
  
  public String buildReplyMessage(String[] args)
  {
    String message = "";
    for (int i = 0; i < args.length; i++) {
      message = message + " " + args[i];
    }
    return message;
  }
  
  public void sendSound(Player player, String s)
  {
    Sound[] arrayOfSound;
    int j = (arrayOfSound = Sound.values()).length;
    for (int i = 0; i < j; i++)
    {
      Sound sounValue = arrayOfSound[i];
      if (sounValue.toString().equals(s))
      {
        Sound sound = Sound.valueOf(s);
        player.getWorld().playSound(player.getLocation(), sound, 1.0F, 1.0F);
        break;
      }
    }
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((sender instanceof Player))
    {
      Player player = (Player)sender;
      if (cmd.getName().equalsIgnoreCase("msg"))
      {
        if (args.length > 1)
        {
          Player target = Bukkit.getServer().getPlayer(args[0]);
          if (target != null)
          {
            String message = buildMessage(args);
            String senderM = buildConfigMessage(getConfig().getString("format.send"), target, player, message);
            String targetM = buildConfigMessage(getConfig().getString("format.recieve"), target, player, message);
            player.sendMessage(senderM);
            target.sendMessage(targetM);
            getConfig().set(player.getUniqueId() + ".lastPlayer", target.getName());
            getConfig().set(target.getUniqueId() + ".lastPlayer", player.getName());
            sendSound(target, getConfig().getString("reciever"));
            sendSound(target, getConfig().getString("sender"));
            return true;
          }
          player.sendMessage("ERROR: The player " + args[0] + " does not exist or is not online!");
          return true;
        }
        player.sendMessage(ChatColor.YELLOW + "USAGE: /msg <Player> <Message>");
        return true;
      }
      if (cmd.getName().equalsIgnoreCase("reply")) {
        if (args.length > 0)
        {
          Player target = Bukkit.getServer().getPlayer(getConfig().getString(player.getUniqueId() + ".lastPlayer"));
          if (target != null)
          {
            String message = buildReplyMessage(args);
            String senderM = buildConfigMessage(getConfig().getString("format.send"), target, player, message);
            String targetM = buildConfigMessage(getConfig().getString("format.recieve"), target, player, message);
            player.sendMessage(senderM);
            target.sendMessage(targetM);
            getConfig().set(player.getUniqueId() + ".lastPlayer", target.getName());
            getConfig().set(target.getUniqueId() + ".lastPlayer", player.getName());
            sendSound(target, getConfig().getString("reciever"));
            sendSound(target, getConfig().getString("sender"));
            return true;
          }
        }
        else
        {
          player.sendMessage(ChatColor.YELLOW + "USAGE: /r <Message>");
          return true;
        }
      }
    }
    return true;
  }
}
