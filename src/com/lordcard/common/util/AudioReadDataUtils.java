package com.lordcard.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;

import com.crazy.shui.R;

public class AudioReadDataUtils {
	/**
	 * 开启或关闭背景音乐
	 */
	public static void playOrStopBgMusic(final Context ctx) {
		if (AudioPlayUtils.isPlay)
			return;
		new Thread() {
			@Override
			public void run() {
				AudioPlayUtils.isPlay = true;
				SharedPreferences sharedPreferences = PreferenceHelper
						.getMyPreference().getSetting();
				if (!sharedPreferences.getBoolean("bgmusic", true)) {
					AudioPlayUtils.getInstance().stopBgMusic();
				} else {
					AudioManager audiomanage = (AudioManager) ctx
							.getSystemService(Context.AUDIO_SERVICE);
					int currentVolume = audiomanage
							.getStreamVolume(AudioManager.STREAM_MUSIC);
					if (currentVolume > 70)
						currentVolume = 70;
					AudioPlayUtils.getInstance().SetVoice(
							sharedPreferences.getInt("music", currentVolume));// 如果没有设置过音量，就获取系统的音量
					AudioPlayUtils.getInstance().playBgMusic(R.raw.mg_bg);
				}
			};
		}.start();
	};

}
