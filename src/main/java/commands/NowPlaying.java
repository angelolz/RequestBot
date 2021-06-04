package commands;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hc.core5.http.ParseException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.albums.GetAlbumRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;

import main.RequestBot;
import methods.SpotifyManager;
import net.dv8tion.jda.api.EmbedBuilder;

public class NowPlaying extends Command
{
	public NowPlaying()
	{
		this.name = "nowplaying";
		this.help = "Shows you what Angel is currently playing!";
		this.aliases = new String[] {"np"};
		this.cooldown = 3;
	}

	protected void execute(CommandEvent event)
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
						final String PATTERN = "spotify\\.com\\/([a-z]*)\\/([A-Za-z0-9]+)";
						Pattern p = Pattern.compile(PATTERN);
						Matcher m = p.matcher(np.getContext().getExternalUrls().get("spotify"));
						m.find();
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

					event.reply(embed.build());
				}
			}

			else
			{
				event.reply(":x: | Angel is currently not playing anything!");
			}
		} 

		catch (ParseException | SpotifyWebApiException | IOException e)
		{
			event.reply(":x: | Sorry, there was a problem getting Angel's currently playing track.");
		}

	}

	private String getTime(long ms)
	{
		int seconds = (int) ((ms / 1000) % 60);
		int minutes = (int) ((ms / (1000*60)));
		String secondsString = "";

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
