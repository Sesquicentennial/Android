package carleton150.edu.carleton.carleton150;

import android.app.Activity;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowToast;

import android.content.Intent;
import android.widget.Button;


/**
 * Created by haleyhinze on 10/26/15.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class MainActivityTest {

    Activity activity;

    @Before
    public void setup()  {
        activity = Robolectric.buildActivity(MainActivity.class)
                .create().get();
    }
    /*@Test
    public void checkActivityNotNull() throws Exception {
        assertNotNull(activity);
    }*/

    @Test
    public void shouldPass() throws Exception {
        assertTrue(true);

    }

}