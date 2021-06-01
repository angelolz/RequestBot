package commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.albums.GetSeveralAlbumsRequest;
import com.wrapper.spotify.requests.data.tracks.GetSeveralTracksRequest;

import helperObjects.SpotifyRequest;
import main.RequestBot;
import net.dv8tion.jda.api.EmbedBuilder;

public class Recent extends Command 
{
	public Recent()
	{
		this.name = "recent";
		this.help = "Shows the most recent songs given.";
		this.cooldown = 3;
	}

	@Override
	protected void execute(CommandEvent event)
	{
		List<SpotifyRequest> list = new ArrayList<SpotifyRequest>();
		List<String> tracks = new ArrayList<String>();
		List<String> albums = new ArrayList<String>();

		EmbedBuilder embed = new EmbedBuilder();

		String sql = "SELECT * FROM Requests ORDER BY time_added DESC LIMIT 10;";
		try(Connection con = RequestBot.getDataSource().getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery();)
		{
			while(rs.next())
			{
				String uri = rs.getString("uri");
				String addedBy = rs.getString("addedBy");
				String source = rs.getString("source");
				boolean isTrack = rs.getBoolean("isTrack");

				SpotifyRequest request = new SpotifyRequest(uri, isTrack, addedBy, source);
				list.add(request);

				if(isTrack)
				{
					tracks.add(uri);
				}

				else
				{
					albums.add(uri);
				}
			}

			if(list.size() == 0)
			{
				event.reply(":x: | Sorry, there hasn't been any songs given to me yet! You can do so by using `r!add` followed by a Spotify track/album!");
			}

			else
			{
				String[] trackArray = new String[tracks.size()];
				String[] albumArray = new String[albums.size()];
				trackArray = tracks.toArray(trackArray);
				albumArray = albums.toArray(albumArray);

				SpotifyApi api = RequestBot.getSpotifyApi();

				GetSeveralTracksRequest trackRequest = api.getSeveralTracks(trackArray).build();
				Track[] spTrack = trackRequest.execute();

				GetSeveralAlbumsRequest albumRequest = api.getSeveralAlbums(albumArray).build();
				Album[] spAlbum = albumRequest.execute();

				embed.setTitle("Here are the 10 most recent added requests:");
				embed.setColor(0x1DB954);

				int index = 1, trackIndex = 0, albumIndex = 0;
				for(SpotifyRequest request: list)
				{
					if(request.isTrack())
					{
						Track track = spTrack[trackIndex];
						ArtistSimplified[] artists = track.getArtists();
						String text = String.format("%d. **%s** by *%s*\nTrack added by: **%s** from *%s*\n\n", 
								index, track.getName(), artists[0].getName(), request.getAddedBy(), request.getSource());

						embed.appendDescription(text);

						trackIndex++;
					}

					else
					{
						Album album = spAlbum[albumIndex];
						ArtistSimplified[] artists = album.getArtists();
						String text = String.format("%d. **%s** by *%s*\nAlbum added by: **%s** from *%s*\n\n", 
								index, album.getName(), artists[0].getName(), request.getAddedBy(), request.getSource());

						embed.appendDescription(text);

						albumIndex++;
					}

					index++;
				}
				
				event.reply(embed.build());
			}
		}

		catch(SQLException e)
		{
			event.reply(":x: | Sorry, there was an error trying to retrieve the recent list. Try again in a bit!");
			RequestBot.getLogger().error(e.toString());
		}

		catch(Exception e)
		{
			event.reply(":x: | Sorry, something went wrong. I've notified Angel of the error.");
			e.printStackTrace();
		}
	}
}
