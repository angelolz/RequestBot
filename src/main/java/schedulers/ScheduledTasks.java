package schedulers;

import main.RequestBot;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScheduledTasks
{
	public static void init()
	{
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(ScheduledTasks::refreshToken, 15, 30, TimeUnit.MINUTES);
	}

	//gets a new refresh and access token every hour
	private static void refreshToken()
	{
		try
		{
			SpotifyApi api = RequestBot.getSpotifyApi();
			if(!api.getAccessToken().isEmpty())
			{
				AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = api.authorizationCodeRefresh().build();
				AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
				api.setAccessToken(authorizationCodeCredentials.getAccessToken());
			}
		}

		catch(Exception e)
		{
			RequestBot.getLogger().error("Failed to get new token: {}", e.toString());
		}

	}
}
