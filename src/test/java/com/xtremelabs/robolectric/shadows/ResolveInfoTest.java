package com.xtremelabs.robolectric.shadows;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import com.xtremelabs.robolectric.TestRunners;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.pm.ResolveInfo;

import com.xtremelabs.robolectric.Robolectric;

@RunWith(TestRunners.WithDefaults.class)
public class ResolveInfoTest {

    private ResolveInfo mResolveInfo;
    private ShadowResolveInfo mShadowInfo;

    @Before
    public void setup() {
    	mResolveInfo = ShadowResolveInfo.newResolveInfo("name", "package", "fragmentActivity");
        mShadowInfo = Robolectric.shadowOf(mResolveInfo);
    }

    @Test
    public void testLoadLabel() {
        mShadowInfo.setLabel("test");
        assertThat("test", equalTo(mResolveInfo.loadLabel(null)));
    }
    
    @Test
    public void testNewResolveInfoWithActivity() {
        assertThat(mResolveInfo.loadLabel(null).toString(), equalTo("name"));
        assertThat(mResolveInfo.activityInfo.packageName, equalTo("package"));
        assertThat(mResolveInfo.activityInfo.applicationInfo.packageName, equalTo("package"));
        assertThat(mResolveInfo.activityInfo.name, equalTo("fragmentActivity"));
    }
}
