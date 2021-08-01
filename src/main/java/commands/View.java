package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import main.RequestBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class View extends Command 
{
	public View()
	{
		this.name = "view";
		this.help = "View the requested songs playlist.";
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event)
	{
		executeAction(event, null);
	}

	public static void executeSlash(SlashCommandEvent event)
	{
		executeAction(null, event);
	}

	private static void executeAction(CommandEvent textEvent, SlashCommandEvent slashEvent)
	{
		String message = "Here is the current playlist:\n" +
				"https://open.spotify.com/playlist/" + RequestBot.getPlaylistId();

		if(slashEvent == null)
		{
			textEvent.reply(message);
		}

		else
		{
			slashEvent.reply(message).queue();
		}
	}
}
