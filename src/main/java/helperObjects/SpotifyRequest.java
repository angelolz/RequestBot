package helperObjects;

public class SpotifyRequest
{
	private String uri, addedBy, source;
	private boolean isTrack;
	
	public SpotifyRequest(String uri, boolean isTrack, String addedBy, String source)
	{
		this.uri = uri;
		this.isTrack = isTrack;
		this.addedBy = addedBy;
		this.source = source;
	}

	public String getUri() {
		return uri;
	}

	public String getAddedBy() {
		return addedBy;
	}

	public String getSource() {
		return source;
	}

	public boolean isTrack() {
		return isTrack;
	}
}
