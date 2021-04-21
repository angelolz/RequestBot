package commands;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.hc.core5.http.ParseException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Track;

import main.RequestBot;
import methods.DBManager;
import methods.*;

public class Add extends Command
{
	public Add()
	{
		this.name = "Add";
		this.help = "Add a song to the request playlist.";
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event)
	{
		SpotifyLink link = new SpotifyLink(event.getArgs());

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

					event.reply(String.format("Successfully added the track **%s** by *%s* to the playlist! "
							+ "There are **%s** songs in the playlist right now. "
							+ "Use `" + RequestBot.getBotPrefix() + "view` to see the playlist!",
							track.getName(), artists, numOfTracks > 100 ? "more than 100" : String.valueOf(numOfTracks)));

					DBManager.addRequest(link.getUri(), true, event.getAuthor().getAsTag(), "discord");
				}

				else
				{
					Album album = SpotifyManager.getAlbum(link.getUri());

					if(SpotifyManager.addAlbumToPlaylist(album))
					{
						String artists = SpotifyManager.getArtistsString(album.getArtists());
						
						int numOfTracks = SpotifyManager.getPlaylistTrackTotal();

						event.reply(String.format("Successfully added the album **%s** by *%s* to the playlist! "
								+ "There are **%s** songs in the playlist right now. "
								+ "Use `" + RequestBot.getBotPrefix() + "view` to see the playlist!",
								album.getName(), artists, numOfTracks > 100 ? "more than 100" : String.valueOf(numOfTracks)));

						DBManager.addRequest(link.getUri(), false, event.getAuthor().getAsTag(), "discord");
					}

					else
					{
						event.reply(":x: | ***SHEEEESH*** You have an album with more than 20 songs! "
								+ "How about you just pick a handful that you think would be best to listen to?");
					}
				}
			}

			else
			{
				event.reply(":x: | That's not a valid Spotify track link or album link.");
			}
		}

		catch (IOException | ParseException e) 
		{
			RequestBot.getLogger().error("IOException/ParseException Error: " + e.toString());
			if(link.isTrack())
			{
				event.reply(":x: | There was an error in adding your track.");
			}

			else
			{
				event.reply(":x: | There was an error in adding your album.");
			}
		}

		catch (SpotifyWebApiException e)
		{
			RequestBot.getLogger().error("Spotify API Error: " + e.toString());

			if(link.isTrack())
			{
				event.reply(":x: | That's not a valid track.");
			}

			else
			{
				event.reply(":x: | That's not a valid album.");
			}
		}

		catch (SQLException e)
		{
			RequestBot.getLogger().error("SQLException: " + e.toString());
		}
	}
}