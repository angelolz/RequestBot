package commands;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hc.core5.http.ParseException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.data.albums.GetAlbumRequest;
import com.wrapper.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;

import main.RequestBot;

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
		String link = event.getArgs().trim();
		String pattern = "https:\\/\\/[a-z0-9]+[.]?spotify[.]com\\/(track|album)\\/([A-Za-z0-9]+)\\??.+";

		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(link);
		if(m.find())
		{
			SpotifyApi api = RequestBot.getSpotifyApi();

			if(m.group(1).equalsIgnoreCase("track"))
			{
				try
				{
					String trackName = "", artistNames = "";
					GetTrackRequest trackRequest = api.getTrack(m.group(2)).build();
					Track track = trackRequest.execute();
					trackName = track.getName();
					ArtistSimplified[] artists = track.getArtists();

					for(int i = 0; i < artists.length; i++)
					{
						artistNames += artists[i].getName() + ", ";
					}
					artistNames = artistNames.replaceAll(", $", "");

					String[] uris = new String[] {track.getUri()};
					AddItemsToPlaylistRequest addRequest = api.addItemsToPlaylist(RequestBot.getPlaylistId(), uris).build();
					addRequest.execute();

					GetPlaylistRequest getPlaylistRequest = api.getPlaylist(RequestBot.getPlaylistId()).build();
					Playlist playlist = getPlaylistRequest.execute();
					Paging<PlaylistTrack> playlistSongs = playlist.getTracks();
					int num = playlistSongs.getTotal();
					event.reply(String.format("Successfully added the track **%s** by *%s* to the playlist! "
							+ "There are **%s** songs in the playlist right now. "
							+ "Use `" + RequestBot.getPrefix() + "view` to see the playlist!",
							trackName, artistNames, num > 100 ? "more than 100" : String.valueOf(num)));
				}

				catch (IOException | ParseException e) 
				{
					RequestBot.getLogger().error("Error: " + e.toString());
					event.reply(":x: | There was an error in adding your track.");
				}

				catch (SpotifyWebApiException e)
				{
					RequestBot.getLogger().error("Error: " + e.toString());
					event.reply(":x: | That's not a valid track.");
				}
			}

			else
			{
				try
				{
					GetAlbumRequest albumRequest = api.getAlbum(m.group(2)).build();
					Album album = albumRequest.execute();

					Paging<TrackSimplified> pagingTracks = album.getTracks();
					if(pagingTracks.getTotal() > 20)
					{
						event.reply(":x: | Sheesh! You have an album with more than 20 songs! How about you just pick a handful that you think would be best to listen to?");
					}

					else
					{
						String albumName = "", artistNames = "";
						TrackSimplified[] tracks = pagingTracks.getItems();
						String[] trackUris = new String[tracks.length];
						for(int i = 0; i < tracks.length; i++)
						{
							trackUris[i] = tracks[i].getUri();
						}

						albumName = album.getName();
						ArtistSimplified[] artists = album.getArtists();
						for(int i = 0; i < artists.length; i++)
						{
							artistNames += artists[i].getName() + ", ";
						}
						artistNames = artistNames.replaceAll(", $", "");

						AddItemsToPlaylistRequest addRequest = api.addItemsToPlaylist(RequestBot.getPlaylistId(), trackUris).build();
						addRequest.execute();
						
						GetPlaylistRequest getPlaylistRequest = api.getPlaylist(RequestBot.getPlaylistId()).build();
						Playlist playlist = getPlaylistRequest.execute();
						Paging<PlaylistTrack> playlistSongs = playlist.getTracks();
						int num = playlistSongs.getTotal();
						event.reply(String.format("Successfully added the album **%s** by *%s* to the playlist! "
								+ "There are **%s** songs in the playlist right now. "
								+ "Use `" + RequestBot.getPrefix() + "view` to see the playlist!",
								albumName, artistNames, num > 100 ? "more than 100" : String.valueOf(num)));
					}
				}

				catch (IOException | ParseException e) 
				{
					RequestBot.getLogger().error("Error: " + e.toString());
					event.reply(":x: | There was an error in adding your album.");
				}

				catch (SpotifyWebApiException e)
				{
					RequestBot.getLogger().error("Error: " + e.toString());
					event.reply(":x: | That's not a valid album.");
				}
			}
		}
		
		else
		{
			event.reply(":x: | That's not a valid Spotify track link or album link.");
		}
	}

}
