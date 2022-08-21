package main;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import commands.*;
import listeners.ChatListener;
import listeners.JDAListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schedulers.ScheduledTasks;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class RequestBot
{
	//general
	private static Logger logger;
	private static HikariDataSource ds;

	//spotify
	private static String spPlaylistId, spClientId, spClientSecret;
	private static SpotifyApi spotifyApi;

	//discord
	private static final String prefix = "r!";
	private static final String version = "1.4.2";
	private static String botToken, ownerId;

	//twitter
	private static TwitterClient twitterClient;
	private static String twtAccessToken, twtAccessTokenSecret, twtApiKey, twtApiSecretKey;

	//twitch
	private static TwitchClient twitchClient;
	private static String twchOAuth;

	public static void main(String[] args) throws IOException, IllegalArgumentException {
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
		twchOAuth = prop.getProperty("twch_oauth");

		try 
		{
			//spotify api init
			spotifyApi = new SpotifyApi.Builder()
					.setClientId(spClientId)
					.setClientSecret(spClientSecret)
					.setRedirectUri(SpotifyHttpManager.makeUri("https://angelolz.dev"))
					.build();
			logger.info("Finished loading Spotify API. DON'T FORGET TO SET ACCESS TOKEN USING DISCORD COMMANDS!");

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
					new Recent(),
					new NowPlaying());

			//discord bot builder
			JDABuilder.createDefault(botToken)
			.setStatus(OnlineStatus.DO_NOT_DISTURB)
			.setActivity(Activity.playing("loading!! | h!help"))
			.addEventListeners(client.build(), new JDAListener())
			.build();

			//init timed tasks
			ScheduledTasks.init();
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
