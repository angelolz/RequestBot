<h1>Angel's Request Bot</h1>
<p>This bot allows users to give me (Angel) songs to listen to from Spotify and
    add them in a single playlist, utilizing the Spotify Web API. I wanted to make
    this bot because I wanted to experiment with multiple APIs: Twitch API, Spotify Web API,
    and Twitter API.</p>

<h2 id="bot-usage">Bot Usage</h2>
<p>The only valid links RequestBot can accept are either <strong>Spotify tracks</strong>
    or <strong>albums</strong>. The bot will only accept the album link if it has <strong><em>20 tracks
    or less</em></strong> in it.</p>

<p>The available commands are listed below:
<code>*</code> = Discord only, <code>**</code> = Discord and Twitch</p>
<ul>
    <li>r!add **</li>
    <li>r!view **</li>
    <li>r!help *</li>
    <li>r!ping *</li>
</ul>

<p>With each song/album submission, the bot keeps track where the submission came from and who
    submitted it, taking their Discord username and tag, Twitter handle, or Twitch username
    and storing it in a database.</p>

<h3>Discord/Twitch</h3>
<p>On <em>Discord/Twitch</em>, send the following command in a Discord server with the bot in it or
    in my own <a href="https://www.twitch.tv/popout/angelolz1/chat">Twitch chat</a>:
<code>r!add https://open.spotify.com/track/2CgOd0Lj5MuvOqzqdaAXtS?si=a65f4df98aeb4b80</code></p>

<p>This will add a song to my playlist, and the bot will send a confirmation message to let you
    know that it has been added, as well as how many songs are in the playlist. </p>

<center>
<h4>Examples</h4>
    <img src="https://i.imgur.com/BwNzJjs.png" alt="Discord Bot Usage" width="300" height="199">
    <p>Discord Bot Usage</p>
    <img src="https://i.imgur.com/W0T7DuI.png" alt="Twitch Bot Usage">
    <p>Twitch Bot Usage</p>
</center>

<h3>Twitter</h3>
<p>On Twitter, you&#39;d want to mention my twitter <a href="https://twitter.com/angelolz1">(@angelolz1)</a> along
    with a Spotify link, optionally leaving a message in the tweet.</p>
<p>Once tweeted, <a href="https://twitter.com/AngelolzBot">My request bot</a> will like your tweet and reply to
    your tweet to confirm that your song has been added to the playlist.</p>

<center>
<h4>Example Tweet:</h4>
<img src="https://imgur.com/3nWqnRt.png" alt="Example Tweet" width="423" height="300">
</center>

<h1 id="dependencies-used-">Dependencies Used:</h1>
<ul>
<li><a href="https://github.com/DV8FromTheWorld/JDA/">JDA</a>/<a href="https://github.com/JDA-Applications/JDA-Utilities">jda-utilities</a></li>
<li><a href="https://github.com/redouane59/twittered">twittered</a></li>
<li><a href="https://github.com/twitch4j/twitch4j">Twitch4J</a></li>
<li><a href="https://github.com/thelinmichael/spotify-web-api-java">spotify-web-api</a></li>
<li><em>as well as other backend stuff (jdbc, hikariCP, jackson-core, logback-classic)</em></li>
</ul>

<h1>Support</h1>
<p>Feel free to let me know of any bugs that you discover through my twitter <a href="https://twitter.com/angelolz1">(@angelolz1)</a>, which is the best way to reach me!</p>

<h1>Contributing</h1>
<p>This is my first time releasing open-source stuff, so my code could probably use some improvements from brilliant people like you! Feel free to open a pull request and describe the changes that you&#39;ve made. I&#39;ll try my best to come back to it as soon as possible!</p>

<h1>License</h1>
<p>This project uses the MIT license.</p>
