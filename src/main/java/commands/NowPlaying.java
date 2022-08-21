package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import main.RequestBot;
import methods.SpotifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistRequest;
import se.michaelthelin.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NowPlaying extends Command
{
	public NowPlaying()
	{
		this.name = "np";
		this.help = "Shows you what Angel is currently playing!";
		this.cooldown = 3;
		this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
	}

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
		try
		{
			SpotifyApi api = RequestBot.getSpotifyApi();

			if(api.getAccessToken() == null)
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
				GetInformationAboutUsersCurrentPlaybackRequest getInfoRequest = api.getInformationAboutUsersCurrentPlayback().build();
				CurrentlyPlayingContext np = getInfoRequest.execute();
				if(np.getIs_playing())
				{
					//this is usually a track
					if(np.getItem() != null)
					{
						EmbedBuilder embed = new EmbedBuilder();
						embed.setTitle("Now Playing: ");
						embed.setColor(0x1DB954);
						embed.setFooter(String.format("%s/%s | Shuffle is %s | Repeat state: %s",
								getTime(np.getProgress_ms()), getTime(np.getItem().getDurationMs()),
								np.getShuffle_state() ? "ON" : "OFF", np.getRepeat_state()));

						if(np.getItem().getId() != null)
						{
							GetTrackRequest trackRequest = api.getTrack(np.getItem().getId()).build();
							Track track = trackRequest.execute();
							embed.appendDescription(String.format("**[%s](%s)** by *%s* ",
									track.getName(),track.getExternalUrls().get("spotify"), SpotifyManager.getArtistsString(track.getArtists())));

							Image[] images = track.getAlbum().getImages();
							embed.setThumbnail(images[0].getUrl());
						}

						else
						{
							embed.appendDescription(String.format("**%s** ", np.getItem().getName()));
						}

						//this can be an artist page, playlist, or album page
						if(np.getContext() != null)
						{
							final String PATTERN = "spotify\\.com/a-z]*/(/-z0-9]+)";
							Pattern p = Pattern.compile(PATTERN);
							Matcher m = p.matcher(np.getContext().getExternalUrls().get("spotify"));
							if(m.find())
							{
								String id = m.group(2);

								switch(np.getContext().getType().toString())
								{
									case "ALBUM":
										GetAlbumRequest albumRequest = api.getAlbum(id).build();
										Album album = albumRequest.execute();
										embed.appendDescription(String.format("from the album \"[*%s*](%s)\"", album.getName(), album.getExternalUrls().get("spotify")));
										break;
									case "PLAYLIST":
										GetPlaylistRequest playlistRequest = api.getPlaylist(id).build();
										Playlist playlist = playlistRequest.execute();
										embed.appendDescription(String.format("from the playlist \"[*%s*](%s)\"", playlist.getName(), playlist.getExternalUrls().get("spotify")));
										break;
									case "ARTIST":
										GetArtistRequest artistRequest = api.getArtist(id).build();
										Artist artist = artistRequest.execute();
										embed.appendDescription(String.format("from [*%s*](%s)'s artist page", artist.getName(), artist.getExternalUrls().get("spotify")));
										break;
								}
							}
						}

						if(slashEvent == null)
						{
							textEvent.reply(embed.build());
						}

						else
						{
							slashEvent.replyEmbeds(embed.build()).queue();
						}
					}
				}

				else
				{
					String message = ":x: | Angel is currently not playing anything!";
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

		catch (ParseException | SpotifyWebApiException | IOException e)
		{
			RequestBot.getLogger().error("Spotify API error -- {}: {}", e.getClass().getName(), e.getMessage());

			String message = ":x: | Sorry, there was a problem getting Angel's currently playing track.";
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

	private static String getTime(long ms)
	{
		int seconds = (int) ((ms / 1000) % 60);
		int minutes = (int) ((ms / (1000*60)));
		String secondsString;

		if(seconds < 10)
		{
			secondsString = "0" + seconds;
		}

		else
		{
			secondsString = String.valueOf(seconds);
		}

		return String.format("%d:%s", minutes, secondsString);
	}
}
