package commands;

import java.io.IOException;
import java.sql.SQLException;

import main.SlashCommandsInit;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.apache.hc.core5.http.ParseException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Track;

import main.RequestBot;
import methods.*;

public class Add extends Command
{
	public Add()
	{
		this.name = "add";
		this.help = "Add a song to the request playlist.";
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
		if(RequestBot.getSpotifyApi().getAccessToken() == null)
		{
			String message = ":x: | Sorry, I haven't been set up with a Spotify token yet! Let Angel know about this!";
			if(slashEvent == null)
			{
				textEvent.reply(message);
			}

			else
			{
				slashEvent.reply(message).queue();
			}
		}

		else
		{
			SpotifyLink link = new SpotifyLink(slashEvent == null ? textEvent.getArgs() : slashEvent.getOption("url").getAsString());
			try
			{
				if(link.validLink())
				{
					if(link.isTrack())
					{
						Track track = SpotifyManager.getTrack(link.getUri());
						String artists = SpotifyManager.getArtistsString(track.getArtists());
						SpotifyManager.addTrackToPlaylist(track);

						int numOfTracks = SpotifyManager.getPlaylistTrackTotal();

						String message = String.format("Successfully added the track **%s** by *%s* to the playlist! "
										+ "There are **%s** songs in the playlist right now. "
										+ "Use `" + RequestBot.getBotPrefix() + "view` to see the playlist!",
								track.getName(), artists, numOfTracks > 100 ? "more than 100" : String.valueOf(numOfTracks));

						if(slashEvent == null)
						{
							textEvent.reply(message);
							DBManager.addRequest(link.getUri(), true, textEvent.getAuthor().getAsTag(), "discord");
						}

						else
						{
							slashEvent.reply(message).queue();
							DBManager.addRequest(link.getUri(), true, slashEvent.getUser().getAsTag(), "discord");
						}
					}

					else
					{
						Album album = SpotifyManager.getAlbum(link.getUri());

						if(SpotifyManager.addAlbumToPlaylist(album))
						{
							String artists = SpotifyManager.getArtistsString(album.getArtists());

							int numOfTracks = SpotifyManager.getPlaylistTrackTotal();

							String message = String.format("Successfully added the album **%s** by *%s* to the playlist! "
											+ "There are **%s** songs in the playlist right now. "
											+ "Use `" + RequestBot.getBotPrefix() + "view` to see the playlist!",
									album.getName(), artists, numOfTracks > 100 ? "more than 100" : String.valueOf(numOfTracks));

							if(slashEvent == null)
							{
								textEvent.reply(message);
								DBManager.addRequest(link.getUri(), false, textEvent.getAuthor().getAsTag(), "discord");
							}

							else
							{
								slashEvent.reply(message).queue();
								DBManager.addRequest(link.getUri(), false, slashEvent.getUser().getAsTag(), "discord");
							}
						}

						else
						{
							String message = ":x: | You have an album with more than 20 songs! How about you just pick a handful from them instead?";
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
				}

				else
				{
					String message = ":x: | That's not a valid Spotify track link or album link.";
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

			catch (IOException | ParseException e)
			{
				RequestBot.getLogger().error("IOException/ParseException Error -- {}: {}", e.getClass().getName(), e.getMessage());

				String message = ":x: | There was an error in adding your " + (link.isTrack() ? "track." : "album.");
				if(slashEvent == null)
				{
					textEvent.reply(message);
				}

				else
				{
					slashEvent.reply(message).queue();
				}
			}

			catch (SpotifyWebApiException e)
			{
				//if someone puts an invalid spotify track/album
				if(e.toString().contains("invalid id"))
				{
					String message = ":x: | That's not a valid " + (link.isTrack() ? "track." : "album.");
					if(slashEvent == null)
					{
						textEvent.reply(message);
					}

					else
					{
						slashEvent.reply(message).queue();
					}
				}

				//for other spotify api exceptions
				else
				{
					RequestBot.getLogger().error("Spotify API Error: {}", e.getMessage());

					String message = ":x: | Sorry, there was a problem in adding this to the playlist!";
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

			catch (SQLException e)
			{
				RequestBot.getLogger().error("SQLException: {}", e.getMessage());
			}
		}
	}
}