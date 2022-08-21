package listeners;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.hc.core5.http.ParseException;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import main.RequestBot;
import methods.DBManager;
import methods.SpotifyLink;
import methods.SpotifyManager;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

public class ChatListener
{
	private final HashMap<String, Long> cooldown;

	//**CHANGE THIS FOR YOUR OWN CHANNEL**//
	private static final String CHANNEL = "angelolz1";

	public ChatListener(SimpleEventHandler eventHandler)
	{
		eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
		this.cooldown = new HashMap<>();
	}

	public void onChannelMessage(ChannelMessageEvent event)
	{
		//pretty much similar structure as the discord add command
		String[] args = event.getMessage().split("\\s+");
		TwitchChat chat = RequestBot.getTwitchClient().getChat();

		if(args[0].equalsIgnoreCase("r!add"))
		{
			if(canUse(event.getUser().getName()))
			{
				cooldown.put(event.getUser().getName(), System.currentTimeMillis());

				if(args.length == 1)
				{
					String message = String.format("@%s, you have to send a spotify song or album link!", event.getUser().getName());
					RequestBot.getTwitchClient().getChat().sendMessage(CHANNEL, message);
				}

				else
				{
					SpotifyLink link = new SpotifyLink(event.getMessage());
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

								String message = String.format("@%s, successfully added the track %s by %s to the playlist! "
												+ "There are %s songs in the playlist right now.", event.getUser().getName(), track.getName(),
										artists, numOfTracks > 100 ? "more than 100" : String.valueOf(numOfTracks));
								chat.sendMessage(CHANNEL, message);

								DBManager.addRequest(link.getUri(), true, event.getChannel().getName(), "twitch");
							}

							else
							{
								Album album = SpotifyManager.getAlbum(link.getUri());

								if(SpotifyManager.addAlbumToPlaylist(album))
								{
									String artists = SpotifyManager.getArtistsString(album.getArtists());
									int numOfTracks = SpotifyManager.getPlaylistTrackTotal();

									String message = String.format("@%s, successfully added the album %s by %s to the playlist! "
													+ "There are %s songs in the playlist right now. "
													+ "Use `r!view` to see the playlist!",
											event.getUser().getName(), album.getName(), artists, numOfTracks > 100 ? "more than 100" : String.valueOf(numOfTracks));
									chat.sendMessage(CHANNEL, message);

									DBManager.addRequest(link.getUri(), false, event.getUser().getName(), "twitch");
								}

								else
								{
									chat.sendMessage(CHANNEL, "@" + event.getUser().getName() + ", you have an album with more than 20 songs! "
											+ "How about you just pick a handful that you think would be best to listen to?");
								}
							}
						}

						else
						{
							chat.sendMessage(CHANNEL, "@" + event.getUser().getName() + ", that's not a valid Spotify track link or album link.");
						}
					}

					catch (IOException | ParseException e)
					{
						RequestBot.getLogger().error("IOException/ParseException Error: " + e);
						if(link.isTrack())
						{
							chat.sendMessage(CHANNEL, "@" + event.getUser().getName() + ", there was an error in adding your track.");
						}

						else
						{
							chat.sendMessage(CHANNEL, "@" + event.getUser().getName() + ", there was an error in adding your album.");
						}
					}

					catch (SpotifyWebApiException e)
					{
						//if someone puts an invalid spotify track/album
						if(e.toString().contains("invalid id"))
						{
							if(link.isTrack())
							{
								chat.sendMessage(CHANNEL, "@" + event.getUser().getName() + ", that's not a valid track.");
							}

							else
							{
								chat.sendMessage(CHANNEL, "@" + event.getUser().getName() + ", that's not a valid album.");
							}
						}

						//for other spotify api exceptions
						else
						{
							RequestBot.getLogger().error("Spotify API Error: " + e);
							chat.sendMessage(CHANNEL, "Sorry, there was an error in adding this to the playlist!");
						}
					}

					catch (SQLException e)
					{
						RequestBot.getLogger().error("SQLException: " + e);
					}
				}
			}
		}

		else if(args[0].equalsIgnoreCase("r!view"))
		{
			if(canUse(event.getUser().getName()))
			{
				cooldown.put(event.getUser().getName(), System.currentTimeMillis());
				try
				{
					String message = String.format("@%s, there are %s songs in the playlist. You can view the playlist here: https://open.spotify.com/playlist/%s",
							event.getUser().getName(), SpotifyManager.getPlaylistTrackTotal(), RequestBot.getPlaylistId());
					RequestBot.getTwitchClient().getChat().sendMessage(CHANNEL, message);
				}

				catch (ParseException | SpotifyWebApiException | IOException e)
				{
					RequestBot.getLogger().error("Error when sending playlist on twitch: {} - {}", e.getClass().getName(), e.getMessage());
				}
			}
		}

		else if(args[0].equalsIgnoreCase("r!np"))
		{
			try
			{
				SpotifyApi api = RequestBot.getSpotifyApi();

				GetInformationAboutUsersCurrentPlaybackRequest getInfoRequest = api.getInformationAboutUsersCurrentPlayback().build();
				CurrentlyPlayingContext np = getInfoRequest.execute();
				if(np.getIs_playing())
				{
					//this is usually a track
					if(np.getItem() != null)
					{
						GetTrackRequest trackRequest = api.getTrack(np.getItem().getId()).build();
						Track track = trackRequest.execute();
						chat.sendMessage(CHANNEL,String.format("%s by %s: %s", track.getName(),
								SpotifyManager.getArtistsString(track.getArtists()), track.getExternalUrls().get("spotify")));
					}
				}

				else
				{
					chat.sendMessage(CHANNEL, "Angel is currently not playing anything!");
				}
			}

			catch (ParseException | SpotifyWebApiException | IOException e)
			{
				RequestBot.getLogger().error("Spotify API error -- {}: {}", e.getClass().getName(), e.getMessage());
				chat.sendMessage(CHANNEL, "Sorry, there was a problem getting Angel's " +
						"currently playing track! Try again later!");
			}
		}
	}

	//puts a 5-second cooldown on twitch commands
	private boolean canUse(String userName)
	{
		Long time = cooldown.get(userName);
		if(time == null)
		{
			return true;
		}

		else if((System.currentTimeMillis() - time) / 1000 >= 5)
		{
			cooldown.remove(userName);
			return true;
		}

		else
		{
			return false;
		}
	}
}
