package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashCommandsInit
{
    public static void init(JDA jda)
    {
        //guild commands for testing
//        CommandListUpdateAction commands = jda.getGuildById("695074147071557632").updateCommands();

        //global commands
        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
            new CommandData("add", "Add a song to the Spotify request playlist.")
                .addOption(OptionType.STRING, "url", "Put a track or album URL from Spotify here!", true),
            new CommandData("view", "Look at all the songs that are currently in the request playlist!"),
            new CommandData("recent", "Look at the most recent songs added to the request playlist!"),
            new CommandData("np", "Look at what Angel's currently listening to!")
        );

        commands.queue();
        jda.addEventListener(new SlashHandler());
    }
}
