package schedulers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hc.core5.http.ParseException;

import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.github.redouane59.twitter.dto.user.User;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import main.RequestBot;
import methods.DBManager;
import methods.SpotifyLink;
import methods.SpotifyManager;

public class ScheduledTasks
{
	private final ScheduledExecutorService refreshTokenScheduler = Executors.newSingleThreadScheduledExecutor();
	private final ScheduledExecutorService tweetsCheckScheduler = Executors.newSingleThreadScheduledExecutor();

	Runnable refreshToken = () -> refreshToken();
	Runnable checkTweets = () -> checkTweets();

	ScheduledFuture<?> refreshTokenUpdater = refreshTokenScheduler.scheduleAtFixedRate(refreshToken, 15, 30, TimeUnit.MINUTES);;
	ScheduledFuture<?> tweetsCheckUpdater = tweetsCheckScheduler.scheduleAtFixedRate(checkTweets, 0, 5, TimeUnit.MINUTES);;

	//gets a new refresh and access token every hour
	private void refreshToken()
	{
		try
		{
			SpotifyApi api = RequestBot.getSpotifyApi();
			AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = api.authorizationCodeRefresh().build();
			AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

			api.setAccessToken(authorizationCodeCredentials.getAccessToken());
		}

		catch(Exception e)
		{
			RequestBot.getLogger().error("Failed to get new token: ", e.toString());
		}

	}

	//checks mentions from @angelolz1 every minute
	private void checkTweets()
	{
		try
		{
			List<Tweet> result = RequestBot.getTwitterClient().getUserMentions("416230081", 150);
			for(Tweet tweet : result)
			{
				boolean exists = DBManager.tweetExists(tweet.getId());

				//if it is not in the db and the request bot isn't the author of the tweet
				if(!exists && !tweet.getAuthorId().equals("1384677926851911702"))
				{
					String realURL = "";

					//checks for shortened twitter URL links
					final String PATTERN = "http(?:s)?:\\/\\/(?:www)?t\\.co\\/([a-zA-Z0-9_]+)";
					Pattern p = Pattern.compile(PATTERN);
					Matcher m = p.matcher(tweet.getText());

					if(m.find())
					{
						//get the real URL
						HttpURLConnection con = (HttpURLConnection) new URL(m.group()).openConnection();
						con.setInstanceFollowRedirects(false);
						con.connect();
						realURL = con.getHeaderField("Location").toString();
					}

					SpotifyLink link = new SpotifyLink(realURL);
					if(link.validLink())
					{
						if(link.isTrack())
						{
							User user = RequestBot.getTwitterClient().getUserFromUserId(tweet.getAuthorId());
							Track track = SpotifyManager.getTrack(link.getUri());

							String message = String.format("@%s Added the track %s to the playlist! Thank you!", user.getName(), 
									track.getName().length() > 210 ? track.getName().substring(0, 206) + "..." : track.getName());

							SpotifyManager.addTrackToPlaylist(track);

							RequestBot.getTwitterClient().postTweet(message, tweet.getId());
							DBManager.addRequest(link.getUri(), true, user.getName(), "twitter");
							DBManager.addTweet(tweet.getId());
						}

						else
						{
							User user = RequestBot.getTwitterClient().getUserFromUserId(tweet.getAuthorId());
							Album album = SpotifyManager.getAlbum(link.getUri());

							String message = "";
							if(SpotifyManager.addAlbumToPlaylist(album))
							{
								message = String.format("@%s Added the album %s to the playlist! Thank you!", user.getName(),
										album.getName().length() > 210 ? album.getName().substring(0, 206) + "..." : album.getName());

							}

							else
							{
								message = String.format("@%s Sorry, couldn't add this album to the playlist!"
										+ "I only acccept albums that are 20 songs or less.",
										user.getName());
							}

							RequestBot.getTwitterClient().postTweet(message, tweet.getId());
							DBManager.addRequest(link.getUri(), false, user.getName(), "twitter");
							DBManager.addTweet(tweet.getId());
						}

						//like the tweet for confirmation that it has been added
						RequestBot.getTwitterClient().likeTweet(tweet.getId());
					}
				}
			}
		}

		catch(SQLException e)
		{
			RequestBot.getLogger().error("Error when checking tweets in database: {}", e.getMessage());
		}

		catch(SpotifyWebApiException e)
		{
			RequestBot.getLogger().error("Spotify API Error (adding from twitter): {}", e.getMessage());
		}

		catch(ParseException | IOException e)
		{
			RequestBot.getLogger().error("ParseException/IOException error (adding from twitter): {}", e.getMessage());
			e.printStackTrace();
		}
	}
}
