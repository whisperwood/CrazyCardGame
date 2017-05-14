package com.lordcard.ui.view;
//
//import com.beauty.lord.BuildConfig;
//import com.beauty.lord.R;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//
import android.content.Context;
//import android.os.CountDownTimer;
//import android.os.Debug;
//import android.util.Log;
import android.view.View;
//
public class GoogleAdsHelper {
//	private InterstitialAd mInterstitialAd;
//	private CountDownTimer mCountDownTimer;
	private static Context context;
	private static GoogleAdsHelper instance;
//	private final static String TAG = "GoogleAdsHelper";
//	private boolean isLoading;
//
	public static GoogleAdsHelper getInstance() {
		return instance;
	}
	public static void initConext(Context ctx) {
		context = ctx;
		if (instance == null) {
			synchronized (GoogleAdsHelper.class) {
				if (instance == null) {
					instance = new GoogleAdsHelper();
				}
			}
		}
	}
//	private GoogleAdsHelper() {
//		mInterstitialAd = new InterstitialAd(context);
//		mInterstitialAd.setAdUnitId(context.getString(R.string.ad_unit_id));
//		mCountDownTimer = new CountDownTimer(3000, 50) {
//			@Override
//			public void onTick(long millisUntilFinished) {
//			}
//			@Override
//			public void onFinish() {
//				Log.i(TAG, "~onFinish~");
//			}
//		};
//		mInterstitialAd.setAdListener(new AdListener() {
//			@Override
//			public void onAdClosed() {
//				super.onAdClosed();
//				Log.i(TAG, "~onAdClosed~");
//				isLoading = false;
//			}
//			@Override
//			public void onAdLoaded() {
//				super.onAdLoaded();
//				Log.i(TAG, "~onAdLoaded~");
//				mCountDownTimer.cancel();
//				mInterstitialAd.show();
//			}
//			@Override
//			public void onAdOpened() {
//				super.onAdOpened();
//				Log.i(TAG, "~onAdOpened~");
//			}
//		});
//		showInterstitial();
//	}
	public void showInterstitial() {
		// Show the ad if it's ready. Otherwise toast and restart the game.
//		if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
//			// mCountDownTimer.cancel();
//			// mInterstitialAd.show();
//			// isLoading = false;
//		} else {
//			startGame();
//		}
	}
//	private void startGame() {
//		if (isLoading)
//			return;
//		isLoading = true;
//		Log.i(TAG, "startGame...");
//		AdRequest adRequest = new AdRequest.Builder().build();
//		mInterstitialAd.loadAd(adRequest);
//		mCountDownTimer.start();
//	}
	public void showBanner(View parentView) {
//		AdView mAdView = (AdView) parentView.findViewById(R.id.adView);
//		AdRequest adRequest = new AdRequest.Builder().build();
//		mAdView.loadAd(adRequest);
	}
}
