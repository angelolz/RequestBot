package methods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyLink
{
	private final String PATTERN = "spotify\\.com\\/(track|album)\\/([A-Za-z0-9]+)";
	private Matcher m;

	public SpotifyLink(String link)
	{
		Pattern p = Pattern.compile(PATTERN);
		m = p.matcher(link);
	}

	public boolean validLink()
	{
		return m.find();
	}

	public boolean isTrack()
	{
		return m.group(1).equalsIgnoreCase("track");
	}
	
	public String getUri()
	{
		return m.group(2);
	}
}
