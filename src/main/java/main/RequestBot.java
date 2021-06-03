package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import commands.*;
import listeners.ChatListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import schedulers.ScheduledTasks;

public class RequestBot
{
	//general
	private static Logger logger;
	private static HikariDataSource ds;

	//spotify
	private static String spPlaylistId, spClientId, spClientSecret;
	private static SpotifyApi spotifyApi;
	private static AuthorizationCodeUriRequest authorizationCodeUriRequest;
	private static AuthorizationCodeRequest authorizationCodeRequest;
	private static AuthorizationCodeCredentials authorizationCodeCredentials;

	//discord
	private static String prefix = "r!";
	private static String version = "1.2.1";
	private static String botToken, ownerId;

	//twitter
	private static TwitterClient twitterClient;
	private static String twtAccessToken, twtAccessTokenSecret, twtApiKey, twtApiSecretKey;

	//twitch
	private static TwitchClient twitchClient;
	private static String twchClientId, twchClientSecret, twchOAuth;

	public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException, RateLimitedException
	{
		//logger
		logger = LoggerFactory.getLogger(RequestBot.class);

		//gets bot properties
		Properties prop = new Properties();
		FileInputStream propFile = new FileInputStream("config.properties");

		prop.load(propFile);

		//spotify api properties
		spPlaylistId = prop.getProperty("sp_playlist_id");
		spClientId = prop.getProperty("sp_client_id");
		spClientSecret = prop.getProperty("sp_client_secret");

		//discord bot properties
		botToken = prop.getProperty("d_bot_token");
		ownerId = prop.getProperty("d_owner_id");

		//twitter api properties
		twtAccessToken = prop.getProperty("twt_access_token");
		twtAccessTokenSecret = prop.getProperty("twt_access_token_secret");
		twtApiKey = prop.getProperty("twt_api_key");
		twtApiSecretKey = prop.getProperty("twt_api_secret_key");

		//twitch api properties
		twchClientId = prop.getProperty("twch_client_id");
		twchClientId = prop.getProperty("twch_client_secret");
		twchOAuth = prop.getProperty("twch_oauth");

		try 
		{

			//spotify api init
			Scanner kbd = new Scanner(System.in);

			spotifyApi = new SpotifyApi.Builder()
					.setClientId(spClientId)
					.setClientSecret(spClientSecret)
					.setRedirectUri(SpotifyHttpManager.makeUri("https://angelolz.dev"))
					.build();

			authorizationCodeUriRequest = spotifyApi.authorizationCodeUri().scope("playlist-modify-private playlist-modify-public playlist-read-private playlist-read-collaborative").build();
			URI uri = authorizationCodeUriRequest.execute();

			//asks to authorize spotify account and input auth code to get access and refresh tokens
			System.out.println("Open auth link in browser:\n" + uri.toString());
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
			logger.info("Finished loading Spotify API with access token.");

			//database init
			String configFile = "db.properties";
			HikariConfig cfg = new HikariConfig(configFile);
			ds = new HikariDataSource(cfg);
			logger.info("Connected to database successfully!");

			/*   twitter bot   */
			TwitterCredentials twtCred = TwitterCredentials.builder()
					.accessToken(twtAccessToken)
					.accessTokenSecret(twtAccessTokenSecret)
					.apiKey(twtApiKey)
					.apiSecretKey(twtApiSecretKey)
					.build();

			twitterClient = new TwitterClient(twtCred);
			logger.info("Finished loading Twitter API.");

			/*   twitch bot   */
			OAuth2Credential credential = new OAuth2Credential("twitch", twchOAuth);
			twitchClient = TwitchClientBuilder.builder()
					.withClientId(twchClientId)
					.withClientSecret(twchClientSecret)
					.withEnableHelix(true)
					.withEnableChat(true)
					.withChatAccount(credential)
					.build();

			SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);
			new ChatListener(eventHandler);

			logger.info("Finished loading Twitch API and joined angelolz1's channel.");

			/*   discord bot   */
			//create command builders and listeners
			CommandClientBuilder client = new CommandClientBuilder();

			//discord client config
			client.useHelpBuilder(false);
			client.setActivity(Activity.playing("give angel music to listen to lol | r!help"));
			client.setOwnerId(ownerId);
			client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
			client.setPrefix(prefix);

			//discord bot commands
			client.addCommands( new Help(),
					new Ping(),
					new Add(),
					new View(),
					new Set(),
					new Recent());

			//discord bot builder
			JDABuilder.createDefault(botToken)
			.setStatus(OnlineStatus.DO_NOT_DISTURB)
			.setActivity(Activity.playing("loading!! | h!help"))
			.addEventListeners(client.build())
			.build();

			//init timed tasks
			new ScheduledTasks();
		}

		catch(Exception e)
		{
			System.out.println("There was a problem when initializing the APIs.");
			e.printStackTrace();
		}
	}

	public static String getBotVersion() 
	{
		return version;
	}

	public static String getBotPrefix() 
	{
		return prefix;
	}

	public static Logger getLogger() 
	{
		return logger;
	}

	public static SpotifyApi getSpotifyApi()
	{
		return spotifyApi;
	}

	public static String getPlaylistId()
	{
		return spPlaylistId;
	}

	public static TwitterClient getTwitterClient()
	{
		return twitterClient;
	}

	public static TwitchClient getTwitchClient()
	{
		return twitchClient;
	}

	public static HikariDataSource getDataSource()
	{
		return ds;
	}
}
