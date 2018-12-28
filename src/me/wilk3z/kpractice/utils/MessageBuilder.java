package me.wilk3z.kpractice.utils;

import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

public class MessageBuilder
{
    public ComponentBuilder builder;
    public BaseComponent[] result;

    public MessageBuilder()
    {
        builder = new ComponentBuilder("");
    }

    public void addMessage(String message)
    {
        builder.append(message);
    }

    public void addCommand(String command)
    {
        builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
    }

    public void addHover(String message)
    {
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(message)));
    }

    public void send(Player p)
    {
        if(result == null) result = builder.create();
        p.spigot().sendMessage(result);
    }
}
