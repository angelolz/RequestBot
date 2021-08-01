package main;

import commands.Add;
import commands.NowPlaying;
import commands.Recent;
import commands.View;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashHandler extends ListenerAdapter
{
    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        if(event.getGuild() == null) return;

        switch(event.getName())
        {
            case "add":
                Add.executeSlash(event);
                break;
            case "view":
                View.executeSlash(event);
                break;
            case "recent":
                Recent.executeSlash(event);
                break;
            case "np":
                NowPlaying.executeSlash(event);
                break;
        }
    }
}
