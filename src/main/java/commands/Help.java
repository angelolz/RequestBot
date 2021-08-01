package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import main.RequestBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class Help extends Command
{
	protected static int commandCount;

	public Help()
	{
		this.name = "help";
		this.help = "Shows this help embed.";
		this.cooldown = 3;
		this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected void execute(CommandEvent event)
	{
		commandCount++;

		//creates new embed
		EmbedBuilder embed = new EmbedBuilder();

		//gets id of bot owner
		String ownerId = event.getClient().getOwnerId();

		//insert info into embed
		event.getJDA().retrieveUserById(ownerId).queue(
				(user) -> {
					embed.setTitle("Angel's Spotify Request bot");
					embed.setColor(0x32CD32);
					embed.setDescription("Here are a list of commands you can use!");
					embed.setFooter("Created by " + user.getAsTag() + " | Version " + RequestBot.getBotVersion(), user.getAvatarUrl());
					embed.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());

					for(Command command : event.getClient().getCommands())
					{
						if(!command.isHidden() && !command.isOwnerCommand())
						{
							String commandName = String.format("%s%s", RequestBot.getBotPrefix(), command.getName());
							if(command.getAliases().length > 0)
							{
								String[] aliases = command.getAliases();
								for (String alias : aliases)
								{
									commandName = commandName.concat("/" + alias);
								}
							}

							embed.addField(commandName, command.getHelp(), true);
						}
					}

					event.reply(embed.build());
				});
	}
}