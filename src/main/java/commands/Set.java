package commands;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.JDA;
import org.apache.hc.core5.http.ParseException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import main.RequestBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import schedulers.ScheduledTasks;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

public class Set extends Command
{
    public Set()
    {
        this.name = "set";
        this.help = "Sets a new access and refresh token **(ADMIN ONLY)**";
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
	        sendAuthorizationEmbed(event.getJDA(), event.getClient().getOwnerId());

            event
                .getChannel()
                .sendMessage(":white_check_mark: | A DM has been sent for further instructions!")
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        }

        else
        {
            try
            {
                if(!event.getChannel().getType().toString().equals("PRIVATE"))
                    event.getMessage().delete().queue();

                SpotifyApi api = RequestBot.getSpotifyApi();

                AuthorizationCodeRequest authorizationCodeRequest = api.authorizationCode(event.getArgs()).build();
                AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
                String access = authorizationCodeCredentials.getAccessToken();
                String refresh = authorizationCodeCredentials.getRefreshToken();

                api.setAccessToken(access);
                api.setRefreshToken(refresh);

                event.getChannel().sendMessage(":white_check_mark: | Successfully set your tokens.")
                     .delay(5, TimeUnit.SECONDS)
                     .flatMap(Message::delete)
                     .queue();

                ScheduledTasks.startSpotifyRefresh();
            }

            catch(ParseException | SpotifyWebApiException | IOException e)
            {
                RequestBot.getLogger().error("Exception caught -- {}: {}", e.getClass().getName(), e.getMessage());
                event.reply(":x: | There was an error in getting your tokens.");
            }
        }
    }

    public static void sendAuthorizationEmbed(JDA jda, String ownerId)
    {
        jda.openPrivateChannelById(ownerId).queue(
            dm ->
            {
                AuthorizationCodeUriRequest authorizationCodeUriRequest = RequestBot.getSpotifyApi()
	                .authorizationCodeUri()
	                .scope("playlist-modify-private playlist-modify-public playlist-read-private " +
	                    "playlist-read-collaborative " +
	                    "user-read-currently-playing user-read-playback-state")
	                .build();

                URI uri = authorizationCodeUriRequest.execute();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(0x1DB954);
                embed.setTitle("Get your authorization code here!");
                embed.setDescription("Use [**this**](" + uri.toString() + ") link to get your refresh code set :)");
                dm.sendMessageEmbeds(embed.build()).queue();
            });
    }
}