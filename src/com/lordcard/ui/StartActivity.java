package com.lordcard.ui;

import com.beauty.lord.R;

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import com.lordcard.common.schedule.AutoTask;
import com.lordcard.common.schedule.ScheduledTask;
import com.lordcard.common.upgrade.UpdateUtils;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.AudioPlayUtils;
import com.lordcard.common.util.ChannelUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.view.notification.NotificationService;
import com.sdk.util.PayUtils;
import com.sdk.util.SDKFactory;

public class StartActivity extends BaseActivity {
	View root = null;
	AlphaAnimation alphaAnimation = null;
	private boolean first;
	private SharedPreferences sharedData;
	private ProgressBar loading_progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_main);
		root = findViewById(R.id.root);
		loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
		sharedData = getApplication().getSharedPreferences(Constant.GAME_ACTIVITE, Context.MODE_PRIVATE);
		first = sharedData.getBoolean("first", true);
		// 判断推送服务是否启动
		if (Constant.isPayEnable) {
			Intent newIntent = new Intent(this, NotificationService.class);
			startService(newIntent);
		}
		Constant.startCount = 0;
		if (Constant.isPayEnable) {
			ThreadPool.startWork(new Runnable() {
				@Override
				public void run() {
					HttpRequest.loadJoinRoomTip(); // 加载房间提示信息
					HttpRequest.loadGameNotice(); // 加载游戏公告
				}
			});
		}
		if (Constant.isPayEnable) {
			if (Database.CHECK_VERSION) {
				new Thread() {
					@Override
					public void run() {
						Database.HAS_NEW_VERSION = UpdateUtils.checkNewVersion(HttpURL.CONFIG_SER, HttpURL.APK_INFO);
					};
				}.start();
			}
		}
		HashMap<String, String> playMsg = new HashMap<String, String>();
		playMsg.put(Constant.KEY_COUNT_PLAY_INNINGS, "0");
		playMsg.put(Constant.KEY_COUNT_WIN_INNINGS, "0");
		playMsg.put(Constant.KEY_COUNT_LOSE_INNINGS, "0");
		playMsg.put(Constant.KEY_COUNT_IQ_RISE, "0");
		playMsg.put(Constant.KEY_IQ, "0");
		GameCache.putObj(CacheKey.KEY_PLAY_GAME_MSG, playMsg);
	};

	@Override
	protected void onStart() {
		super.onStart();
		if (Constant.isPayEnable) {
			ThreadPool.startWork(new Runnable() {
				@Override
				public void run() {
					try {
						PayUtils.loadPayInitParam(); // 加载支付初始数据
						PayUtils.loadPaySiteConfig(); // 加载计费点配置数据
						HttpRequest.getComSettingDate(); // 获取所有的共用配置
						/** 获取预充值配置参数 **/
						PrerechargeManager.getPrerechargeParams();
					} catch (Exception e) {
					}
				}
			});
		}
		startLogoAnim();
	}

	public void startLogoAnim() {
		alphaAnimation = new AlphaAnimation(1.0f, 1.0f);
		alphaAnimation.setDuration(1000); // 播放时间
		alphaAnimation.setRepeatMode(Animation.REVERSE); // 反过来执行
		alphaAnimation.setRepeatCount(1); // 重复播放次数 如果需要反过来执行 此数值需要为1
		alphaAnimation.setFillAfter(true);
		root.startAnimation(alphaAnimation);
		ThreadPool.startWork(new Runnable() {
			@Override
			public void run() {
				AudioPlayUtils.getInstance().startLoadResouces();
			}
		});
		ScheduledTask.addRateTask(new AutoTask() {
			@Override
			public void run() {
				int progress = AudioPlayUtils.getInstance().getLoadProgress();
				loading_progress.setProgress(progress);
				if (progress == 100) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					goToLoginActivity();
					stop(false);
				}
			}
		}, 200, 20);
	}

	private void goToLoginActivity() {
		if (first) {
			// 第一次生成快捷方式
			ActivityUtils.createShortCut(R.drawable.icon, R.string.app_name);
			Editor editor = sharedData.edit();
			editor.putBoolean("first", false);
			editor.commit();
		}
		Intent intent = new Intent();
		intent.setClass(StartActivity.this, SDKFactory.getLoginView());
		intent.putExtra("auto_login", true);
		startActivity(intent);
		finishSelf();
	}

	private void initMMChannel() {
		if (Constant.isPayEnable) {
			ThreadPool.startWork(new Runnable() {
				@Override
				public void run() {
					String channelId = ChannelUtils.getMMChannel();
					if (TextUtils.isEmpty(channelId)) {
						GameCache.remove(CacheKey.CHANNEL_MM_ID);
					} else {
						GameCache.putStr(CacheKey.CHANNEL_MM_ID, channelId);
					}
					ChannelUtils.loadChannelCfg();
				}
			});
		}
	}
}
