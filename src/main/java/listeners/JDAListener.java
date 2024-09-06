package listeners;

import commands.Set;
import main.SlashCommandsInit;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JDAListener extends ListenerAdapter
{
    private final String ownerId;

    public JDAListener(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public void onReady(ReadyEvent event)
    {
        SlashCommandsInit.init(event.getJDA());
        Set.sendAuthorizationEmbed(event.getJDA(), ownerId);
    }
}
