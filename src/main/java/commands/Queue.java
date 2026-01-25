package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import main.RequestBot;
import methods.SpotifyLink;
import se.michaelthelin.spotify.requests.data.player.AddItemToUsersPlaybackQueueRequest;

public class Queue extends Command
{
    public Queue() {
        this.name = "queue";
        this.aliases = new String[]{"q"};
    }

    @Override
    protected void execute(CommandEvent commandEvent)
    {
        if(RequestBot.getSpotifyApi().getAccessToken() == null)
        {
            commandEvent.reply("❌ | Sorry, I haven't been set up with a Spotify token yet! Let Angel know about this!");
            return;
        }

        SpotifyLink link = new SpotifyLink(commandEvent.getArgs());
        try {
            if(!link.validLink()) {
                commandEvent.reply("❌ | That's not a valid Spotify track link or album link.");
                return;
            }

            if(!link.isTrack()) {
                commandEvent.reply("❌ | Sorry, I currently only support spotify tracks.");
                return;
            }

            AddItemToUsersPlaybackQueueRequest request = RequestBot.getSpotifyApi().addItemToUsersPlaybackQueue(link.getUri()).build();
            request.execute();

            commandEvent.getMessage().addReaction("✅").queue();
        }

        catch(Exception e) {
            commandEvent.reply("❌ | There was an error adding this song to queue.");
            RequestBot.getLogger().error("Error: {}", e.getMessage());
        }
    }
}
