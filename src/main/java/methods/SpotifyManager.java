package methods;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;

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

public class SpotifyManager 
{	
	public static void addTrackToPlaylist(Track track) throws IOException, ParseException, SpotifyWebApiException
	{
		String[] uris;

		uris = new String[] {track.getUri()};
		AddItemsToPlaylistRequest addRequest = RequestBot.getSpotifyApi().addItemsToPlaylist(RequestBot.getPlaylistId(), uris).build();
		addRequest.execute();
	}

	public static boolean addAlbumToPlaylist(Album album) throws ParseException, SpotifyWebApiException, IOException
	{
		String[] uris;

		Paging<TrackSimplified> pagingTracks = album.getTracks();
		if(pagingTracks.getTotal() > 20)
		{
			return false;
		}

		else
		{
			uris = new String[pagingTracks.getTotal()];

			TrackSimplified[] tracks = pagingTracks.getItems();
			for(int i = 0; i < tracks.length; i++)
			{
				uris[i] = tracks[i].getUri();
			}
		}

		AddItemsToPlaylistRequest addRequest = RequestBot.getSpotifyApi().addItemsToPlaylist(RequestBot.getPlaylistId(), uris).build();
		addRequest.execute();
		return true;
	}

	public static int getPlaylistTrackTotal() throws IOException, ParseException, SpotifyWebApiException
	{
		GetPlaylistRequest getPlaylistRequest = RequestBot.getSpotifyApi().getPlaylist(RequestBot.getPlaylistId()).build();
		Playlist playlist = getPlaylistRequest.execute();
		Paging<PlaylistTrack> playlistSongs = playlist.getTracks();
		return playlistSongs.getTotal();
	}

	public static Track getTrack(String uri) throws IOException, ParseException, SpotifyWebApiException
	{
		GetTrackRequest trackRequest = RequestBot.getSpotifyApi().getTrack(uri).build();
		return trackRequest.execute();
	}

	public static Album getAlbum(String uri) throws IOException, ParseException, SpotifyWebApiException
	{
		GetAlbumRequest albumRequest = RequestBot.getSpotifyApi().getAlbum(uri).build();
		return albumRequest.execute();
	}

	public static String getArtistsString(ArtistSimplified[] artists)
	{
		StringBuilder artistString = new StringBuilder();
		for (ArtistSimplified artist : artists) {
			artistString.append(artist.getName()).append(", ");
		}

		artistString = new StringBuilder(artistString.toString().replaceAll(", $", ""));
		return artistString.toString();
	}
}
