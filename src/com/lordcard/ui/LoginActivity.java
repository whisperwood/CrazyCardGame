package com.lordcard.ui;

import com.beauty.lord.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.mydb.DBHelper;
import com.lordcard.common.schedule.AutoTask;
import com.lordcard.common.schedule.ScheduledTask;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.upgrade.UpdateUtils;
import com.lordcard.common.util.ActivityPool;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.AudioPlayUtils;
import com.lordcard.common.util.AudioReadDataUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.NoticesVo;
import com.lordcard.network.http.GameCache;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.base.FastJoinTask;
import com.lordcard.ui.base.ILoginView;
import com.lordcard.ui.dizhu.DoudizhuRoomListActivity;
import com.lordcard.ui.personal.PersonnalDoudizhuActivity;
import com.lordcard.ui.view.GoogleAdsHelper;
import com.lordcard.ui.view.NumberIconView;
import com.lordcard.ui.view.dialog.AccountBindDialog;
import com.lordcard.ui.view.dialog.ChangeAccountDialog;
import com.lordcard.ui.view.dialog.GameDialog;
import com.lordcard.ui.view.dialog.SettingDialog;

@SuppressLint({ "HandlerLeak", "DefaultLocale", "SimpleDateFormat", "WorldReadableFiles" })
public class LoginActivity extends BaseActivity implements ILoginView {
	private TextView accountTv, goldTv; // 账号，金豆
	private ImageView headIv;// 头像
	private Button loginBtn, changeAccountBtn, bindAccountBtn, quickMatch, quickLogin, updateBtn;
	private AccountBindDialog mAccountBindDialog;// 绑定账号对话框
	private SharedPreferences sharedPrefrences;
	// private SharedPreferences sharedViewfiper;
	private Editor editor;
	private GameDialog netWorkDialog = null;
	public static final String LOGIN_VIEW_FLIPPER = "login_view_flipper";
	// private boolean isBackState =
	// false;//退出状态，标记当前是否准备退出的状态（点击两次退出，两次间隔时间超过10秒表示重新开始标记）
	public static final String KEY_USER = "user_key";
	/** 更新界面账号 */
	public static final int HANDLER_WHAT_LOGIN_UPDATE_USER = 1000;
	/** 注册账号 */
	public static final int HANDLER_WHAT_LOGIN_RESIGSTER_USER = 1001;
	/** 公告打开 */
	public static final int HANDLER_WHAT_LOGIN_ANNOUNCEMENT_OPEN = 1003;
	/** 公告关闭 */
	public static final int HANDLER_WHAT_LOGIN_ANNOUNCEMENT_CLOSE = 1004;
	/** 公告显示 */
	public static final int HANDLER_WHAT_LOGIN_ANNOUNCEMENT_VISIBLE = 1005;
	/** 公告隐藏 */
	public static final int HANDLER_WHAT_LOGIN_ANNOUNCEMENT_GONE = 1006;
	// 登录的时候需要的几个常量
	public final static String ACCOUNT = "account";
	public final static String PASSWORD = "userPwd";
	private RelativeLayout gameBg = null;
	private RelativeLayout katong, loginBg = null;
	private ChangeAccountDialog mChangeAccountDialog = null;
	// private TextView zhezhao;
	// 是否更新支付数据
	private RelativeLayout ggdetaiLayout;
	private TextView titleView, contentView, timeView, textName, textTeam;
	private static Boolean boolean1;
	private static int i;
	/** 像素增加 */
	private static int PXZ;
	/** 像素最大值 */
	private static int PX_MST;
	/** 像素最大值 */
	private static int PX_LAST_MST;
	/** 是否有公告内容 */
	private static boolean HAS_GONGGAO = false;
	private TextView t1;
	private ScrollView scrollView;
	private Button gonggao;
	private GenericTask rjoinTask;
	public static DBHelper dbHelper;
	private boolean isShown;
	private int[] imageId = new int[] {/*
										 * R.drawable.login_guide1,
										 * R.drawable.login_guide2,
										 * R.drawable.login_guide3 xs_del
										 */};
	private AutoTask autoTask; // 定时任务
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case HANDLER_WHAT_LOGIN_UPDATE_USER:
					setUserInfo();
					break;
				case HANDLER_WHAT_LOGIN_RESIGSTER_USER:// 注册账号
					GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
					if (gameUser != null) {
						DialogUtils.mesToastTip("当前已有账号登录,无需再注册!");
						return;
					}
					break;
				case HANDLER_WHAT_LOGIN_ANNOUNCEMENT_OPEN:// 公告展开
					PXZ = PXZ + PX_MST;
					LayoutParams lp = t1.getLayoutParams();
					lp.height = PXZ;
					t1.setLayoutParams(lp);
					i = i + 1;
					break;
				case HANDLER_WHAT_LOGIN_ANNOUNCEMENT_CLOSE:// 公告收拢
					PXZ = PXZ - PX_MST;
					LayoutParams lp2 = t1.getLayoutParams();
					lp2.height = PXZ;
					t1.setLayoutParams(lp2);
					i = i - 1;
					break;
				case HANDLER_WHAT_LOGIN_ANNOUNCEMENT_VISIBLE:// 公告显示
					PXZ = PX_LAST_MST;
					i = 20;
					if (autoTask != null) {
						autoTask.stop(true);
						autoTask = null;
					}
					scrollView.setVisibility(View.VISIBLE);
					gonggao.setClickable(true);
					break;
				case HANDLER_WHAT_LOGIN_ANNOUNCEMENT_GONE:// 公告隐藏
					PXZ = 0;
					i = 1;
					if (autoTask != null) {
						autoTask.stop(true);
						autoTask = null;
					}
					ggdetaiLayout.setVisibility(View.GONE);
					gonggao.setClickable(true);
					break;
				default:
					break;
			}
		};
	};
	private ImageButton game_set;
	private NumberIconView goldbean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置标题栏不显示
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.game_login2);
		initView();
		i = 1;
		PXZ = 0;
		PX_MST = mst.adjustYIgnoreDensity(13);// 公告每段高度
		PX_LAST_MST = PX_MST * 20;// 高度总高度
		t1 = (TextView) findViewById(R.id.t1);
		boolean1 = true; // 公告标志
		dbHelper = new DBHelper(CrashApplication.getInstance());
		GenericTask gameNoticeTask = new GameNoticeTask();
		gameNoticeTask.execute();
		taskManager.addTask(gameNoticeTask);
		mst.adjustView(gameBg);
		GoogleAdsHelper.getInstance().showBanner(gameBg);
		GameCache.putStr(Constant.GAME_NAME_CACHE, "武则天");
	}

	@Override
	public void onBackPressed() {
		String appName = getResources().getString(R.string.app_name);
		GameDialog gameDialog = new GameDialog(Database.currentActivity) {
			@Override
			public void okClick() {
				ActivityPool.exitApp();
			}

			@Override
			public void cancelClick() {
				dismiss();
			};
		};
		gameDialog.show();
		gameDialog.setText("是否退出   " + appName + " ?");
	}

	/**
	 * 初始化view控件
	 */
	private void initView() {
		String isShow = GameCache.getStr(LOGIN_VIEW_FLIPPER);
		mChangeAccountDialog = new ChangeAccountDialog(this, handler);
		mAccountBindDialog = new AccountBindDialog(this, handler);
		updateBtn = (Button) findViewById(R.id.update);
		updateBtn.setOnClickListener(mOnClickListener);
		loginBtn = (Button) findViewById(R.id.game_login_in);
		loginBtn.setOnClickListener(mOnClickListener);
		quickMatch = (Button) findViewById(R.id.game_quick_match);
		quickMatch.setOnClickListener(mOnClickListener);
		quickLogin = (Button) findViewById(R.id.game_quick_login);
		quickLogin.setOnClickListener(mOnClickListener);
		changeAccountBtn = (Button) findViewById(R.id.game_login_change_account);
		changeAccountBtn.setOnClickListener(mOnClickListener);
		bindAccountBtn = (Button) findViewById(R.id.game_login_bind_account);
		// 移动不允许账号绑定
		bindAccountBtn.setEnabled(false);
		bindAccountBtn.setVisibility(View.INVISIBLE);
		bindAccountBtn.setText("");
		// 切换账号
		changeAccountBtn.setEnabled(false);
		changeAccountBtn.setVisibility(View.INVISIBLE);
		bindAccountBtn.setOnClickListener(mOnClickListener);
		accountTv = (TextView) findViewById(R.id.game_login_id);
		goldTv = (TextView) findViewById(R.id.game_login_gold);
		headIv = (ImageView) findViewById(R.id.game_login_img);
		gameBg = (RelativeLayout) findViewById(R.id.layout);
		gameBg.setBackgroundResource(R.drawable.login_bg);
		katong = (RelativeLayout) findViewById(R.id.katong);
		katong.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.katong, true));
		titleView = (TextView) findViewById(R.id.gg_title);
		contentView = (TextView) findViewById(R.id.gg_content);
		timeView = (TextView) findViewById(R.id.gg_time);
		textName = (TextView) findViewById(R.id.gg_name);
		textTeam = (TextView) findViewById(R.id.gg_team);
		ggdetaiLayout = (RelativeLayout) findViewById(R.id.gg_detail);
		scrollView = (ScrollView) findViewById(R.id.room_list_scrollView);
		gonggao = (Button) findViewById(R.id.gonggao);
		gonggao.setOnClickListener(mOnClickListener);
		loginBg = (RelativeLayout) findViewById(R.id.login_bg);
		// loginBg.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.loginbj,
		// true));
		game_set = (ImageButton) findViewById(R.id.game_set);
		game_set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (settingDialog != null && settingDialog.isShowing()) {
					settingDialog.dismiss();
				}
				settingDialog = new SettingDialog(LoginActivity.this) {
					@Override
					public void setDismiss() {
						super.setDismiss();
						settingDialog.dismiss();
					}
				};
				settingDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						settingDialog = null;
					}
				});
				settingDialog.show();
				settingDialog.setPro();
			}
		});
		goldbean = (NumberIconView) findViewById(R.id.goldbean);
	}

	private SettingDialog settingDialog;

	@Override
	public void onResume() {
		super.onResume();
		accountTv.setText(GameCache.getStr(Constant.GAME_NAME_CACHE)); // 账号
		goldbean.setNum(Integer.parseInt(checkBeans()));//金豆
		AudioReadDataUtils.playOrStopBgMusic(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		AudioPlayUtils.getInstance().stopBgMusic();
		AudioPlayUtils.getInstance().stopMusic();
	}

	/**
	 * 设置用户信息
	 * @param gameUser
	 */
	private void setUserInfo() {
		GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (gameUser != null) {
			String account = gameUser.getAccount();
			if (!TextUtils.isEmpty(gameUser.getRelaAccount())) {
				account = gameUser.getRelaAccount();
			}
			// accountTv.setText(account); // 账号
			accountTv.setText(GameCache.getStr(Constant.GAME_NAME_CACHE)); // 账号
			goldTv.setText(PatternUtils.changeZhidou(0 > gameUser.getBean() ? 0 : gameUser.getBean())); // 金豆
			String localBean = GameCache.getStr(Constant.GAME_BEAN_CACHE);
			goldTv.setText(localBean);
			goldbean.setNum(Integer.parseInt(localBean));
			if (!TextUtils.isEmpty(gameUser.getGender()) && "1".equals(gameUser.getGender())) {// 女性
				headIv.setBackgroundResource(R.drawable.nv_user_img);
			} else {
				headIv.setBackgroundResource(R.drawable.nan_user_img);
			}
		}
	}

	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.gonggao:
					gonggao.setClickable(false);
					if (boolean1 == false) {
						scrollView.setVisibility(View.GONE);
						if (autoTask != null) {
							autoTask.stop(true);
							autoTask = null;
						}
						/** 画卷收拢 */
						autoTask = new AutoTask() {
							@Override
							public void run() {
								if (i >= 1) {
									handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_CLOSE);
								} else {
									PXZ = 0;
									i = 1;
									stop(true);
									handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_GONE);
								}
							}
						};
						ScheduledTask.addRateTask(autoTask, 30);
						boolean1 = true;
					} else {
						if (HAS_GONGGAO) {
							gonggao.setClickable(false);
							ggdetaiLayout.setVisibility(View.VISIBLE);
							if (autoTask != null) {
								autoTask.stop(true);
								autoTask = null;
							}
							/** 画卷展开 */
							autoTask = new AutoTask() {
								@Override
								public void run() {
									if (i >= 0 && i <= 20) {
										handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_OPEN);
									} else {
										PXZ = PX_LAST_MST;
										i = 20;
										handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_VISIBLE);
										stop(true);
									}
								}
							};
							ScheduledTask.addRateTask(autoTask, 30);
							boolean1 = false;
						} else {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									DialogUtils.mesToastTip("亲，暂时没有公告哟~");
									gonggao.setClickable(true);
								}
							});
						}
					}
					break;
				case R.id.game_login_in:// 游戏大厅
					if (!ActivityUtils.isNetworkAvailable()) {
						showNetWorkDialog();
					} else {
						GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
						if (gameUser == null) {
							// login(true);
							showLoginDialog();
						} else {
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this, DoudizhuRoomListActivity.class);
							startActivity(intent);
						}
					}
					break;
				case R.id.game_login_change_account:// 切换账号
					if (!ActivityUtils.isNetworkAvailable()) {
						showNetWorkDialog();
					} else {
						showLoginDialog();
					}
					break;
				case R.id.game_login_bind_account:// 绑定账号
					if (!ActivityUtils.isNetworkAvailable()) {
						showNetWorkDialog();
					} else {
						GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
						if (gameUser == null) {
							// login(true);
							showLoginDialog();
						} else {
							if (!mAccountBindDialog.isShowing()) {
								mAccountBindDialog.show();
								mAccountBindDialog.initView();
							}
						}
					}
					break;
				case R.id.game_quick_match:// 单机
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, PersonnalDoudizhuActivity.class);
					startActivity(intent);
					break;
				case R.id.game_quick_login:// 快速游戏
					if (!ActivityUtils.isNetworkAvailable()) {
						showNetWorkDialog();
					} else {
						GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
						if (gu == null) {
							showLoginDialog();
						} else {
							FastJoinTask.fastJoin();
						}
					}
					break;
				case R.id.update:// 升级游戏
					if (Database.UPDATED) {
						UpdateUtils.newVersionTip();
					} else {
						if (Math.abs(System.currentTimeMillis() - Constant.CLICK_TIME) >= Constant.SPACING_TIME) {
							Constant.CLICK_TIME = System.currentTimeMillis();
							DialogUtils.mesToastTip("您当前已经是最新版本，暂时无需更新！");
						}
					}
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 游戏公告信息
	 */
	private class GameNoticeTask extends GenericTask {
		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				final NoticesVo notices = (NoticesVo) GameCache.getObj(CacheKey.GAME_NOTICE);
				if (notices == null)
					return TaskResult.FAILED;
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				Date dt = format.parse(notices.getCtime());
				format = new SimpleDateFormat("yyyy-MM-dd");
				final String time1 = format.format(dt);
				final String content1 = notices.getContent();// 获取的公告内容
				sharedPrefrences = getSharedPreferences("account_ban", MODE_WORLD_READABLE);
				final String time2 = sharedPrefrences.getString("time", null);
				// final String content2 = sharedPrefrences.getString("content",
				// null);// 保存的公告内容
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ggdetaiLayout.setOnClickListener(null);
						ggdetaiLayout.setVisibility(View.VISIBLE);
						if (time1.equals(time2)) {
							ggdetaiLayout.setVisibility(View.GONE);
							titleView.setText(notices.getTitle());
							contentView.setText("    " + notices.getContent());
							timeView.setText(time1);
							textName.setText("尊敬的玩家：");
							textTeam.setText("掌中游斗地主运营团队");
							boolean1 = true;
						} else {
							boolean1 = false;
							ggdetaiLayout.setVisibility(View.GONE);
							titleView.setText(notices.getTitle());
							contentView.setText("    " + notices.getContent());
							timeView.setText(time1);
							textName.setText("尊敬的玩家：");
							textTeam.setText("掌中游斗地主运营团队");
							editor = sharedPrefrences.edit();
							editor.putString("content", content1);
							editor.putString("time", time1);
							editor.commit();
							if (autoTask != null) {
								autoTask.stop(true);
								autoTask = null;
							}
							/** 画卷展开 */
							autoTask = new AutoTask() {
								@Override
								public void run() {
									if (i <= 20 && i >= 0) {
										handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_OPEN);
									} else {
										i = 20;
										PXZ = PX_LAST_MST;
										handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_VISIBLE);
										cancel(true);
									}
								}
							};
							ScheduledTask.addRateTask(autoTask, 2000, 30);
						}
						if (!TextUtils.isEmpty(notices.getTitle().trim())) {
							HAS_GONGGAO = true;
						}
					}
				});
			} catch (Exception e) {
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		recyleDrawable();
		if (autoTask != null) {
			autoTask.stop(true);
		}
		if (rjoinTask != null) {
			rjoinTask.cancel(true);
			rjoinTask = null;
		}
	}

	public void recyleDrawable() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
		layout.removeAllViews();
	}

	private void mesTip(final String msg, final boolean showCancel, final boolean isFinish) {
		try {
			Database.currentActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					GameDialog gameDialog = new GameDialog(Database.currentActivity, showCancel) {
						@Override
						public void okClick() {
							if (isFinish) {
								ActivityUtils.finishAcitivity();
							}
							if (null != mChangeAccountDialog && !mChangeAccountDialog.isShowing()) {
								mChangeAccountDialog = new ChangeAccountDialog(LoginActivity.this, handler);
								mChangeAccountDialog.show();
							}
						}
					};
					gameDialog.show();
					gameDialog.setText(msg);
				}
			});
		} catch (Exception e) {
		}
	}

	/**
	 * 提示没有网络
	 * @Title: showNetWorkDialog
	 * @param
	 * @return void
	 * @throws
	 */
	public void showNetWorkDialog() {
		if (netWorkDialog == null) {
			netWorkDialog = DialogUtils.getNetWorkDialog();
		} else if (!netWorkDialog.isShowing()) {
			netWorkDialog.show();
		}
	}

	/**
	 * 展示登录或账号切换窗口
	 * @Title: showLoginDialog
	 * @param
	 * @return void
	 * @throws
	 */
	public void showLoginDialog() {
		if (mChangeAccountDialog == null) {
			mChangeAccountDialog = new ChangeAccountDialog(LoginActivity.this, handler);
		}
		if (!mChangeAccountDialog.isShowing()) {
			mChangeAccountDialog.show();
		}
		// if (null != mChangeAccountDialog &&
		// !mChangeAccountDialog.isShowing()) {
		// mChangeAccountDialog = new ChangeAccountDialog(LoginActivity.this,
		// handler);
		// mChangeAccountDialog.show();
		// }
	}

	/**
	 * 检查金豆然后发牌开始
	 */
	private String checkBeans() {
		String localBean = GameCache.getStr(Constant.GAME_BEAN_CACHE);
		if (!TextUtils.isEmpty(localBean)) {
			int allMybeans = Integer.parseInt(localBean);
			if (allMybeans <= 0) {
				if (!TextUtils.isEmpty(GameCache.getStr(Constant.GET_BEAN_CACHE))
						&& GameCache.getStr(Constant.GET_BEAN_CACHE).trim().equals(ActivityUtils.getNowDate().trim())) {
					int getBeanCount = Integer.parseInt(GameCache.getStr(Constant.GET_BEAN_COUNT).trim());
					if (getBeanCount < 5) {
						getBeanCount++;
						DialogUtils.mesToastTip("您的金豆不足，系统赠送1000金豆，每天最多5次,还剩"+(5-getBeanCount)+"次！");
						localBean = "1000";
						GameCache.putStr(Constant.GAME_BEAN_CACHE, localBean);
						GameCache.putStr(Constant.GET_BEAN_COUNT, getBeanCount + "");
					} else {
						// 提示不能进去了
						// DialogUtils.mesTip(getString(R.string.link_server_fail),
						// true);
						//DialogUtils.mesTip("您的金豆不足，赠送次数用完，明天继续！", false, false);
						localBean = "0";
					}
				} else {
					GameCache.putStr(Constant.GET_BEAN_CACHE, ActivityUtils.getNowDate());
					localBean = "1000";
					DialogUtils.mesToastTip("您的金豆不足，系统赠送1000金豆，每天最多5次,还剩4次！");
					GameCache.putStr(Constant.GAME_BEAN_CACHE, localBean);
					GameCache.putStr(Constant.GET_BEAN_COUNT, "1");
				}
			}
		} else {
			ActivityUtils.getNowDate();
			localBean = "20000";
			GameCache.putStr(Constant.GAME_BEAN_CACHE, localBean);
		}
		return localBean;
	}
}
