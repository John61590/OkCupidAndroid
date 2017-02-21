# OkCupidAndroid

A native Java interpretation of the OkCupid Android application (just main screen) without ViewPager and accounts.
This was done for an interview problem in early 2016.

What it is supposed to look like is below.

![ScreenShot](https://raw.github.com/John61590/OkCupidAndroid/master/matchpage.png)

What my interpretation looks like is below. As stated before, no ViewPager, search, action bar/appbar, search, menu, etc.

![ScreenShot](https://raw.github.com/John61590/OkCupidAndroid/master/okcupid-app.png)

# Special Notes

This application uses networking/AsyncTask to download the JSON file at https://okcupid.com/matchSample.json
If this file is removed from the website, the application will not show anything in the grid view of matches.

# Contributor(s)

Developed by John Bohne.

# License

Anyone can do whatever they want with it.

AOSP libraries and Picasso (Square library) are used in this application.
