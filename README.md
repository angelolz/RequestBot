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
    <li>r!help *</li>
    <li>r!ping *</li>
    <li>r!add **</li>
    <li>r!view **</li>
    <li>r!recent *</li>
    <li>r!np **</li>
</ul>

<p>With each song/album submission, the bot keeps track where the submission came from and who
    submitted it, taking their Discord username and tag, Twitter handle, or Twitch username
    and storing it in a database.</p>

<h3>Discord/Twitch</h3>
<p>On <em>Discord/Twitch</em>, send the following command in a Discord server with the bot in it or
    in my own <a href="https://www.twitch.tv/popout/angelolz1/chat">Twitch chat</a>:
<code>r!add https://open.spotify.com/track/2CgOd0Lj5MuvOqzqdaAXtS</code></p>

<p>This will add a song to my playlist, and the bot will send a confirmation message to let you
    know that it has been added, as well as how many songs are in the playlist. </p>

<div style="text-align: center;">
<h4>Examples</h4>
    <img src="https://i.imgur.com/BwNzJjs.png" alt="Discord Bot Usage" width="300" height="199">
    <p>Discord Bot Usage</p>
    <img src="https://i.imgur.com/W0T7DuI.png" alt="Twitch Bot Usage">
    <p>Twitch Bot Usage</p>
</div>

<h3>Twitter</h3>
<p>On Twitter, all you need is to  mention my twitter (<a href="https://twitter.com/angelolz1">@angelolz1</a>) along
    with a Spotify link, optionally leaving a message in the tweet.</p>
<p>Once tweeted, <a href="https://twitter.com/AngelolzBot">my request bot</a> will like your tweet and reply to
    your tweet to confirm that your song has been added to the playlist.</p>

<div style="text-align: center;">
<h4>Example Tweet:</h4>
<img src="https://imgur.com/3nWqnRt.png" alt="Example Tweet" width="423" height="300">
</div>

<h1 id="development-setup">Development Setup</h1>
<ol>
    <li>First off, you'd need to grab a bunch of api keys for this application. 
            This should be pasted in a <code>config.properties</code> file.
        <br>
        <p>
            <h3>Spotify</h3>
            <ul>
                <li>Use the hidden Discord command <code>r!set</code> to get your authorization code.</li>
                <li>The code will be found in the URL address. Use the <code>r!set</code> command again but provide your authorization code.</li>
            </ul>
            <h3>Discord</h3>
            <ul>
                <li>Visit the <a href="https://discord.com/developers/applications">developer dashboard.</a></li>
                <li>Give your app a name, and click <b>Create</b>.</li>
                <li>Click on the <b>Bot</b> menu on the left side and copy your token.</li>
                <li>Paste the token in the <code>d_bot_token</code> field.</li>
            </ul>
            <h3>Twitter</h3>
            <ul>
                <li>Visit the <a href="https://developer.twitter.com/en/portal/projects-and-apps">developer dashboard.</a></li>
                <li>Click on the <b>Create App</b> button and give your app a name.</li>
                <li>Copy the <b>API Key</b> and <b>API Secret Key</b>. Paste the keys in the <code>twt_api_key</code> and <code>twt_api_secret_key</code> fields, respectively.</li>
                <li>Go to the settings of your app and change the app permissions to <b>Read and Write</b>.</li>
                <li>Click on <b>Overview</b> to go to the main list of all your apps. Click on the key icon of the app you've created.</li>
                <li>Click on the <b>Generate</b> button on the <b>Access Token and Secret</b> section.</li>
                <li>Copy the <b>Access Token</b> and <b>Access Token Secret</b>. Paste the keys in the <code>twt_access_token</code> and <code>twt_access_token_secret</code> fields, respectively.</li>
            </ul>
            <h3>Twitch</h3>
            <ul>
                <li>You can use an OAuth Generator <a href="https://twitchapps.com/tmi/">page</a> to get your token. </li>
                <li>Copy and paste the token into the <code>twch_oauth</code> field.</li>
            </ul>
    </li>
    <li>Create the required database and tables needed for the bot, which can be found in the
        <b><a href="https://github.com/angelolz1/RequestBot/blob/master/src/main/resources/tables.sql">tables.sql</a></b> file.
    </li>
    <li>Enter your database credentials in the <b>db.properties</b> file.</li>
    <li>If everything was configured correctly, the app should run normally upon executing it.</li>
    
</ol>
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
