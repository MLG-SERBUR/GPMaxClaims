package com.github.gpaddons.gpmaxclaims;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.Messages;
import me.ryanhamshire.GriefPrevention.PlayerData;
import me.ryanhamshire.GriefPrevention.TextMode;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Enforces per-rank total top-level claim counts via {@code griefprevention.maxclaims.<N>}.
 *
 * <p>Fallback when no permission is present is {@code default-max-claims} in this addon's
 * config, or GriefPrevention's {@code MaximumNumberOfClaimsPerPlayer} when that is negative.
 * A value of zero means unlimited, matching GriefPrevention's convention.
 */
public class GPMaxClaims extends JavaPlugin implements Listener
{

    static final String PREFIX = "griefprevention.maxclaims.";
    static final String BYPASS = "griefprevention.overrideclaimcountlimit";

    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args)
    {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload"))
        {
            reloadConfig();
            sender.sendMessage("GPMaxClaims config reloaded.");
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClaimCreated(ClaimCreatedEvent event)
    {
        if (event.getClaim() == null) return;
        // Only limit top-level claims. Subdivisions live under a parent and are not counted
        // by GriefPrevention's own MaximumNumberOfClaimsPerPlayer check.
        if (event.getClaim().parent != null) return;
        if (event.getClaim().isAdminClaim()) return;

        Player player = null;
        if (event.getCreator() instanceof Player)
        {
            player = (Player) event.getCreator();
        }
        else
        {
            UUID owner = event.getClaim().getOwnerID();
            if (owner != null) player = Bukkit.getPlayer(owner);
        }
        if (player == null) return;
        if (player.hasPermission(BYPASS)) return;

        int max = resolveMaxClaims(player);
        if (max < 0) return; // unlimited

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        int count = playerData.getClaims().size();
        if (count >= max)
        {
            event.setCancelled(true);
            GriefPrevention.sendMessage(player, TextMode.Err, Messages.ClaimCreationFailedOverClaimCountLimit);
        }
    }

    /**
     * Resolve the maximum number of top-level claims a player may own.
     *
     * @param player the player to check
     * @return the limit, or a negative value for unlimited
     */
    int resolveMaxClaims(Player player)
    {
        int best = -1;
        boolean found = false;

        for (PermissionAttachmentInfo info : player.getEffectivePermissions())
        {
            if (!info.getValue()) continue;
            String permission = info.getPermission().toLowerCase();
            if (!permission.startsWith(PREFIX)) continue;

            String suffix = permission.substring(PREFIX.length());
            if (suffix.equals("*")) return -1;
            int value;
            try
            {
                value = Integer.parseInt(suffix);
            }
            catch (NumberFormatException e)
            {
                continue;
            }
            if (value < 0) continue;
            if (value == 0) return -1;
            if (!found || value > best)
            {
                best = value;
                found = true;
            }
        }

        if (found) return best;

        int configured = getConfig().getInt("default-max-claims", -1);
        if (configured == 0) return -1;
        if (configured > 0) return configured;

        int global = GriefPrevention.instance.config_claims_maxClaimsPerPlayer;
        if (global > 0) return global;

        return -1;
    }

}
