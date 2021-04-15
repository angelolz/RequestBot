package schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import main.RequestBot;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ScheduledTasks extends ListenerAdapter
{
	private final ScheduledExecutorService refreshTokenScheduler = Executors.newSingleThreadScheduledExecutor();
	
	ScheduledFuture<?> refreshTokenUpdater = null;

	@Override
	public void onReady(ReadyEvent event)
	{
		Runnable refreshToken = () -> refreshToken();
		
		refreshTokenUpdater = refreshTokenScheduler.scheduleAtFixedRate(refreshToken, 1, 1, TimeUnit.HOURS);
	}
	
	public void onShutdown(ShutdownEvent event)
	{
		refreshTokenScheduler.shutdown();
	}

	private void refreshToken()
	{
		try
		{
			SpotifyApi api = RequestBot.getSpotifyApi();
			AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = api.authorizationCodeRefresh().build();
			AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

			api.setAccessToken(authorizationCodeCredentials.getAccessToken());
			RequestBot.getLogger().info("New refresh token acquired: " + authorizationCodeCredentials.getExpiresIn());
		}
		
		catch(Exception e)
		{
			RequestBot.getLogger().error("Failed to get new token: ", e.toString());
		}

	}
}
