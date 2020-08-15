package studio.trc.bukkit.crazyauctionsplus.command.commands.admin;

import studio.trc.bukkit.crazyauctionsplus.Main;
import studio.trc.bukkit.crazyauctionsplus.command.CommandType;
import studio.trc.bukkit.crazyauctionsplus.command.VCommand;
import studio.trc.bukkit.crazyauctionsplus.util.PluginControl;
import studio.trc.bukkit.crazyauctionsplus.util.enums.Messages;

public class CommandAuctionAdminPrintStackTrace extends VCommand {
    public CommandAuctionAdminPrintStackTrace() {
        this.setPermission("Admin.SubCommands.PrintStackTrace");
        this.addSubCommand("printstacktrace");
    }

    @Override
    protected CommandType perform(Main plugin) {
        if (PluginControl.stackTraceVisible.containsKey(sender)) {
            if (PluginControl.stackTraceVisible.get(sender)) {
                PluginControl.stackTraceVisible.put(sender, false);
                Messages.sendMessage(sender, "Admin-Command.PrintStackTrace.Turn-Off");
            } else {
                PluginControl.stackTraceVisible.put(sender, true);
                Messages.sendMessage(sender, "Admin-Command.PrintStackTrace.Turn-On");
            }
        } else {
            PluginControl.stackTraceVisible.put(sender, true);
            Messages.sendMessage(sender, "Admin-Command.PrintStackTrace.Turn-On");
        }
        return CommandType.SUCCESS;
    }
}
