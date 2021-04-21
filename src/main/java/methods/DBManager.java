package methods;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.RequestBot;

public class DBManager
{
	//keeps track of the add song requests from multiple sources
	public static void addRequest(String uri, boolean isTrack, String addedBy, String source) throws SQLException
	{
		String sql = "INSERT INTO Requests (uri, isTrack, addedBy, source) VALUES (?,?,?,?);";

		try(Connection con = RequestBot.getDataSource().getConnection();
				PreparedStatement pst = con.prepareStatement(sql);)
		{
			pst.setString(1, uri);
			pst.setBoolean(2, isTrack);
			pst.setString(3, addedBy);
			pst.setString(4, source);
			pst.executeUpdate();
		}
	}

	public static void addTweet(String id) throws SQLException
	{
		String sql = "INSERT INTO Tweets (tweet_id) VALUES (?);";

		try(Connection con = RequestBot.getDataSource().getConnection();
				PreparedStatement pst = con.prepareStatement(sql);)
		{
			pst.setString(1, id);
			pst.executeUpdate();
		}
	}
	
	//makes sure that the tweet does not get processed again by the bot to avoid duplicates to the playlist
	public static boolean tweetExists(String id) throws SQLException
	{
		String sql = "SELECT * FROM Tweets WHERE tweet_id = ?;";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try
		{
			con = RequestBot.getDataSource().getConnection();
			pst = con.prepareStatement(sql);
			pst.setString(1, id);
			rs = pst.executeQuery();
			
			return rs.next();
		}
		
		finally
		{
			if(con != null) con.close();
			if(pst != null) pst.close();
			if(rs != null) rs.close();
		}
	}
}
