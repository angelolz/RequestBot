# Angel's Request Bot
This bot allows users to give me (Angel) songs to listen to from Spotify and add them in a single playlist, utilizing the Spotify Web API. I wanted to make this bot because I wanted to experiment with multiple APIs: Twitch API, Spotify Web API, and Twitter API.

## Bot Usage
The only valid links RequestBot can accept are either **Spotify tracks** or **albums**. The bot will only accept the album link if it has ***20 tracks or less*** in it.

The available commands are listed below:
`*` = Discord only, `**` = Discord and Twitch

- r!add **
- r!view **
- r!help *
- r!ping *

With each song/album submission, the bot keeps track where the submission came from and who submitted it, taking their Discord username and tag, Twitter handle, or Twitch username and storing it in a database.

### Discord/Twitch
On *Discord/Twitch*, send the following command in a Discord server with the bot in it or in my own [Twitch chat](https://www.twitch.tv/popout/angelolz1/chat):
`r!add https://open.spotify.com/track/2CgOd0Lj5MuvOqzqdaAXtS?si=a65f4df98aeb4b80`

This will add a song to my playlist, and the bot will send a confirmation message to let you know that it has been added, as well as how many songs are in the playlist. 

Examples:
![Discord Bot Usage](https://i.imgur.com/BwNzJjs.png)
![Twitch Bot Usage](https://i.imgur.com/W0T7DuI.png)

### Twitter
On Twitter, you'd want to mention my twitter [(@angelolz1)](https://twitter.com/angelolz1) along with a Spotify link, optionally leaving a message in the tweet.

Once tweeted, [My request bot](https://twitter.com/AngelolzBot) will like your tweet and reply to your tweet to confirm that your song has been added to the playlist.

Example Tweet:
![Example Tweet](https://imgur.com/3nWqnRt.png)

# Dependencies Used:
- [JDA](https://github.com/DV8FromTheWorld/JDA/)/[jda-utilities](https://github.com/JDA-Applications/JDA-Utilities)
- [twittered](https://github.com/redouane59/twittered)
- [Twitch4J](https://github.com/twitch4j/twitch4j)
- [spotify-web-api](https://github.com/thelinmichael/spotify-web-api-java)
- *as well as other backend stuff (jdbc, hikariCP, jackson-core, logback-classic)*

# Support
Feel free to let me know of any bugs that you discover through my twitter [(@angelolz1)](https://twitter.com/angelolz1), which is the best way to reach me!

# Contributing
This is my first time releasing open-source stuff, so my code could probably use some improvements from brilliant people like you! Feel free to open a pull request and describe the changes that you've made. I'll try my best to come back to it as soon as possible!

# License
This project uses the MIT license.
