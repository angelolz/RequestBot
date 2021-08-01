package listeners;

import main.SlashCommandsInit;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JDAListener extends ListenerAdapter
{
    @Override
    public void onReady(ReadyEvent event)
    {
        SlashCommandsInit.init(event.getJDA());
    }
}
