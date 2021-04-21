package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

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
		String link = "https://open.spotify.com/playlist/6N6FvUk4YionGIa6xaUBx5?si=yFp085xsQbaIoomP0hg15A";
		event.reply("Here is the current request playlist:\n" + link);
	}
}
