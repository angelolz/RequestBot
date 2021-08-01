package commands;

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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Recent extends Command
{
	public Recent()
	{
		this.name = "recent";
		this.help = "Shows the most recent songs given.";
		this.cooldown = 3;
		this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
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
			List<SpotifyRequest> list = new ArrayList<>();
			List<String> tracks = new ArrayList<>();
			List<String> albums = new ArrayList<>();

			EmbedBuilder embed = new EmbedBuilder();

			String sql = "SELECT * FROM Requests ORDER BY time_added DESC LIMIT 10;";
			try(Connection con = RequestBot.getDataSource().getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery())
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
					String message = ":x: | Sorry, there hasn't been any songs given to me yet! You can do so by using `" + RequestBot.getBotPrefix() + "add` followed by a Spotify track/album!";
					if(slashEvent == null)
					{
						textEvent.reply(message);
					}

					else
					{
						slashEvent.reply(message).queue();
					}
				}

				else {
					String[] trackArray = new String[tracks.size()];
					String[] albumArray = new String[albums.size()];
					trackArray = tracks.toArray(trackArray);
					albumArray = albums.toArray(albumArray);

					Track[] spTrack = null;
					Album[] spAlbum = null;
					if (trackArray.length > 0)
					{
						GetSeveralTracksRequest trackRequest = api.getSeveralTracks(trackArray).build();
						spTrack = trackRequest.execute();
					}

					if(albumArray.length > 0)
					{
						GetSeveralAlbumsRequest albumRequest = api.getSeveralAlbums(albumArray).build();
						spAlbum = albumRequest.execute();
					}

					embed.setTitle("Here are the 10 most recent added requests:");
					embed.setColor(0x1DB954);

					int index = 1, trackIndex = 0, albumIndex = 0;
					for(SpotifyRequest request: list)
					{
						if(request.isTrack())
						{
							Track track = spTrack[trackIndex];
							ArtistSimplified[] artists = track.getArtists();
							String text = String.format("%d. **[%s](https://open.spotify.com/track/%s)** by *%s*\nTrack added by: **%s** from *%s*\n\n",
									index, track.getName(), request.getUri(), artists[0].getName(), request.getAddedBy(), request.getSource());

							embed.appendDescription(text);

							trackIndex++;
						}

						else
						{
							Album album = spAlbum[albumIndex];
							ArtistSimplified[] artists = album.getArtists();
							String text = String.format("%d. **[%s](https://open.spotify.com/album/%s)** by *%s*\nAlbum added by: **%s** from *%s*\n\n",
									index, album.getName(), request.getUri(), artists[0].getName(), request.getAddedBy(), request.getSource());

							embed.appendDescription(text);

							albumIndex++;
						}

						index++;
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

			catch(Exception e)
			{
				String message = ":x: | Sorry, something went wrong. I've notified Angel of the error.";
				if(slashEvent == null)
				{
					textEvent.reply(message);
				}

				else
				{
					slashEvent.reply(message).queue();
				}
				RequestBot.getLogger().error("{}: {}", e.getClass().getName(), e.getMessage());
			}
		}
	}
}
