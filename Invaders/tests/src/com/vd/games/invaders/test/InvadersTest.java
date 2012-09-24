package com.vd.games.invaders.test;

import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.vd.games.invaders.Invaders;

public class InvadersTest extends ActivityUnitTestCase<Invaders> {

	private Invaders invadersActivity;

	public InvadersTest(Class<Invaders> activityClass) {
		super(activityClass);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	protected void setUp() throws Exception {
		Log.d("InvadersTest", "setup");
		invadersActivity = getActivity();
		super.setUp();
	}
	
	@SmallTest
	public void testOnCreateBundle() {
		invadersActivity.onCreate(new Bundle());
		
	}

	@SmallTest
	public void testOnOptionsItemSelectedMenuItem() {
		fail("Not yet implemented");
	}

}
