package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import commands.*;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import schedulers.ScheduledTasks;

public class RequestBot
{
	private static String prefix = "r!";
	private static String version = "1.0";
	private static String token, ownerId, playlistId;
	private static Logger logger;
	private static SpotifyApi spotifyApi;
	private static AuthorizationCodeUriRequest authorizationCodeUriRequest;
	private static AuthorizationCodeRequest authorizationCodeRequest;
	private static AuthorizationCodeCredentials authorizationCodeCredentials;
	
	public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException, RateLimitedException
	{

		//logger
		logger = LoggerFactory.getLogger(RequestBot.class);

		//gets bot properties
		Properties prop = new Properties();
		FileInputStream propFile = new FileInputStream("config.properties");
		prop.load(propFile);
		token = prop.getProperty("bot_token");
		ownerId = prop.getProperty("owner_id");
		playlistId = prop.getProperty("playlist_id");

		//create command builders and listeners
		EventWaiter waiter = new EventWaiter();
		CommandClientBuilder client = new CommandClientBuilder();

		//bot client config
		client.useHelpBuilder(false);
		client.setActivity(Activity.playing("give angel music to listen to lol | r!help"));
		client.setOwnerId(ownerId);
		client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
		client.setPrefix(prefix);

		//non-hidden commands
		client.addCommands(
			new Help(),
			new Ping(),
			new Add(),
			new View());

		//shutdown command

		try 
		{
			spotifyApi = new SpotifyApi.Builder()
					.setClientId("8a4f9398b0c6474caeafa4b7b49c0b23")
					.setClientSecret("d747f9c2d994413daad93efff0b0e6ca")
					.setRedirectUri(SpotifyHttpManager.makeUri("https://angelolz.dev"))
					.build();

			authorizationCodeUriRequest = spotifyApi.authorizationCodeUri().scope("playlist-modify-private playlist-read-private").build();
			URI uri = authorizationCodeUriRequest.execute();
			
			Scanner kbd = new Scanner(System.in);
			System.out.println("Open this link in your browser: " + uri.toString());
			System.out.println();
			System.out.print("Enter auth code: ");
			String code = kbd.nextLine();
			kbd.close();
			
			authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
			authorizationCodeCredentials = authorizationCodeRequest.execute();
			String access = authorizationCodeCredentials.getAccessToken();
			String refresh = authorizationCodeCredentials.getRefreshToken();
			
			spotifyApi.setAccessToken(access);
			spotifyApi.setRefreshToken(refresh);
			
			logger.info("Access token expires in: " + authorizationCodeCredentials.getExpiresIn());
			
			JDABuilder.createDefault(token)
			.setStatus(OnlineStatus.DO_NOT_DISTURB)
			.setActivity(Activity.playing("loading!! | h!help"))
			.addEventListeners(waiter, client.build(), new ScheduledTasks())
			.build();
		}

		catch(Exception e)
		{
			System.out.println("Unable to login with bot token.");
			e.printStackTrace();
		}
	}

	public static String getVersion() {
		return version;
	}

	public static String getPrefix() {
		return prefix;
	}

	public static Logger getLogger() {
		return logger;
	}
	
	public static SpotifyApi getSpotifyApi()
	{
		return spotifyApi;
	}
	
	public static String getPlaylistId()
	{
		return playlistId;
	}
}
