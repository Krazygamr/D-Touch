package com.beloko.doom;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;

import com.beloko.idtech.AppSettings;
import com.beloko.idtech.CustomUncaughtExceptionHandler;
import com.beloko.idtech.GD;
import com.beloko.idtech.GD.IDGame;
import com.beloko.idtech.GamePadFragment;
import com.beloko.idtech.IntroDialog;
import com.beloko.idtech.OnlineFragment;
import com.beloko.idtech.OptionsFragment;
import com.beloko.license.License;
import com.beloko.serial.Serial;

public class EntryActivity extends FragmentActivity  {

	public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnHyH9No9rqA4yPnEJ+TwBOLtqZAt+5rT5+5hztkQIiUedum1gF3rAKZzk6Tx/XoDMh8dAt9RDgVligJJMb3v9Hby1OxCDaRo+I7MnksTm+me7nCkLOSU0XCo8n39CP3Z7Pqlk2hxiibjv4vJlfbENrunF9n1qCn5B9OLDsxi96jBep1Zjo3hN7DEpc5pSFocihRLIRZ2GOvx12sLnCxBIXDn8buN5tkuiWVRj3gAm3GWiGxVtW3wQjgYdffaaFDLqvM6Dz4itdAyeouBuzbUFr712JtCHacLVyJPnNQ6jukfszfT/Wl+ofUZXhcjYQqPzxua5+sGfYCWi53vSeXCmQIDAQAB";

	final static int LAUNCH_FRAG = 0;
	final static int MODS_FRAG = 1;

	GamePadFragment gamePadFrag;

	static License licCheck;         

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler("Doom_crash"));

		licCheck = new License(this,Serial.DOOM,BASE64_PUBLIC_KEY);             
		licCheck.doCheck();

		GD.init(this);
		//Utils.expired();

		setContentView(R.layout.activity_quake);

		// Set up the action bar to show tabs.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		AppSettings.setGame(IDGame.Doom);
		AppSettings.reloadSettings(getApplication());

		//Tab test = actionBar.newTab().setText("Gzdoom").setTabListener(new TabListener<LaunchFragmentGZdoom>(this, "Gzdoom", LaunchFragmentGZdoom.class));

		actionBar.addTab(actionBar.newTab().setText("Gzdoom").setTabListener(new TabListener<LaunchFragmentGZdoom>(this, "Gzdoom", LaunchFragmentGZdoom.class)));
		actionBar.addTab(actionBar.newTab().setText("prboom").setTabListener(new TabListener<LaunchFragment>(this, "prboom", LaunchFragment.class)));
		actionBar.addTab(actionBar.newTab().setText("Choc").setTabListener(new TabListener<LaunchFragmentChoc>(this, "Choc", LaunchFragmentChoc.class)));
		actionBar.addTab(actionBar.newTab().setText("gamepad").setTabListener(new TabListener<GamePadFragment>(this, "gamepad", GamePadFragment.class)));
		actionBar.addTab(actionBar.newTab().setText("options").setTabListener(new TabListener<OptionsFragment>(this, "options", OptionsFragment.class)));
		actionBar.addTab(actionBar.newTab().setText("help").setTabListener(new TabListener<HelpFragment>(this, "help", HelpFragment.class)));
		actionBar.addTab(actionBar.newTab().setText("social").setTabListener(new TabListener<OnlineFragment>(this, "social", OnlineFragment.class)));
		//actionBar.addTab(actionBar.newTab().setText("Cloud Save").setTabListener(new TabListener<CloudSaveFragment>(this, "cloud_save", CloudSaveFragment.class)));

		
		String last_tab = AppSettings.getStringOption(getApplicationContext(), "last_tab", "");
		if (last_tab.contentEquals("gzdoom"))
			actionBar.setSelectedNavigationItem(0);
		else if  (last_tab.contentEquals("prboom"))
			actionBar.setSelectedNavigationItem(1);
		else if (last_tab.contentEquals("choc"))
			actionBar.setSelectedNavigationItem(2);

		gamePadFrag = (GamePadFragment)getFragmentManager().findFragmentByTag("gamepad");

		AppSettings.setStringOption(getApplicationContext(), "altGraphics", "Default,Fancy (By Ilia)");
		AppSettings.setStringOption(getApplicationContext(), "altGraphicsPrefix", ",a_");


		if (IntroDialog.showIntro(this))
		{
			IntroDialog.show(this,"Doom Touch",R.raw.intro);
		}
		else
		{
			if (AboutDialog.showAbout(this))
				AboutDialog.show(this);
		}

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_quake, menu);
		return true;
	}


	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (gamePadFrag == null)
			gamePadFrag = (GamePadFragment)getFragmentManager().findFragmentByTag("gamepad");

		gamePadFrag.onGenericMotionEvent(event);
		return super.onGenericMotionEvent(event);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (gamePadFrag == null)
			gamePadFrag = (GamePadFragment)getFragmentManager().findFragmentByTag("gamepad");

		if (gamePadFrag.onKeyDown(keyCode, event))
			return true;
		else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if (gamePadFrag == null)
			gamePadFrag = (GamePadFragment)getFragmentManager().findFragmentByTag("gamepad");

		if ( gamePadFrag.onKeyUp(keyCode, event))
			return true;
		else
			return super.onKeyUp(keyCode, event);
	} 


	


	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
		private final FragmentActivity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		private final Bundle mArgs;
		private Fragment mFragment;

		public TabListener(FragmentActivity activity, String tag, Class<T> clz) {
			this(activity, tag, clz, null);
		}

		public TabListener(FragmentActivity activity, String tag, Class<T> clz, Bundle args) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			mArgs = args;

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state.  If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);

			if (mFragment == null) //Actually create all fragments NOW
			{
				mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
				FragmentTransaction ft =  mActivity.getFragmentManager().beginTransaction();
				ft.add(android.R.id.content, mFragment, mTag);	
				ft.commit();
			}


			//if (mFragment != null && !mFragment.isDetached()) {
			if (mFragment != null && !mFragment.isHidden()) {
				FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
				//ft.detach(mFragment);
				ft.hide(mFragment);
				ft.commit();
			}
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				//ft.attach(mFragment);
				//ft.setCustomAnimations(R., R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
				ft.show(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				//ft.detach(mFragment);
				ft.hide(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			//Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
		}
	}

}
