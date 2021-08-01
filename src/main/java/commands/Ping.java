package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.MessageChannel;

public class Ping extends Command
{
	public Ping()
	{
		this.name = "ping";
		this.aliases = new String[] {"pong"};
		this.help = ":ping_pong: Pong!";
		this.cooldown = 3;
	}

	@Override
	protected void execute(CommandEvent event)
	{
		MessageChannel channel = event.getChannel();
		String author = event.getAuthor().getAsMention();
		long time = System.currentTimeMillis();

		if(event.getMessage().getContentRaw().contains("ping"))
		{
			channel.sendMessage(":ping_pong: | " + author + ", Pong! ...").queue(
					response -> response.editMessageFormat(":ping_pong: | " + author + ", Pong! %d ms", System.currentTimeMillis() - time).queue());
		}

		else if(event.getMessage().getContentRaw().contains("pong"))
		{
			channel.sendMessage(":ping_pong: | " + author + ", Ping! ...").queue(
					response -> response.editMessageFormat(":ping_pong: | " + author + ", Ping! %d ms", System.currentTimeMillis() - time).queue());
		}
	}
}
