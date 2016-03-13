# Sesquicentennial Android
Android application for the Sesquicentennial Event to happen in 2016 at 
Carleton College.

### Configuration

Get a google maps API key and place it in a file that looks like the following: 

```
<resources>
    <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve">
        Your Key Here
    </string>
</resources>
```

This file should be named google_maps_api.xml and should go in res/values

You will also need to add a server certificate in the form of a file called my.bks in the res/raw folder 
for server authentication.

### Build Process

* `git clone https://github.com/Sesquicentennial/Android`
* open with Android Studio
* Try running the app!
