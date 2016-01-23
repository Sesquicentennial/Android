package carleton150.edu.carleton.carleton150;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import java.io.InputStream;
import carleton150.edu.carleton.carleton150.CertificateManagement.ExtHttpClientStack;
import carleton150.edu.carleton.carleton150.CertificateManagement.SslHttpClient;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by haleyhinze on 10/28/15.
 *
 * Overrides the Application superclass. This makes it possible to use Volley (a
 * library to handle server requests)
 * to communicate with the server
 */
public class MyApplication extends Application{

    private static MyApplication sInstance;

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Arsenal-Regular.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        InputStream keyStore = getResources().openRawResource(R.raw.my);
        mRequestQueue = Volley.newRequestQueue(this,
                new ExtHttpClientStack(new SslHttpClient(keyStore, "mysecret", 80)));
        sInstance = this;
    }

    public synchronized static MyApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

}
