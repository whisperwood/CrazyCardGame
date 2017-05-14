package com.sdk.sms.egame;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.JsonResult;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.ui.view.Assistant;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayPoint;

public class EGameSMSpayUtil {
	static String feeCode = "";

	public static void goPay(final PayPoint point, final String paySiteTag) {
		/*
		 * if(point.getName().equals("30万金豆")) { point.setMoney(30); } else
		 * if(point.getName().equals("25万金豆")) { point.setMoney(25); } else
		 * if(point.getName().equals("20万金豆")) { point.setMoney(20); }
		 */
		// Toast.makeText(JDSMSPayUtil.getContext(),
		// point.getName()+":"+point.getMoney(), Toast.LENGTH_SHORT).show();
		feeCode = "";
		Log.e("smsSender", point.getName() + ":" + point.getNo());
		if (point.getNo().equals("p1")) {
			feeCode = "001";
		} else if (point.getNo().equals("p2")) {
			feeCode = "002";
		} else if (point.getNo().equals("p3")) {
			feeCode = "003";
		} else if (point.getNo().equals("p4")) {
			feeCode = "004";
		} else if (point.getNo().equals("p5")) {
			feeCode = "005";
		} else if (point.getNo().equals("p6")) {
			feeCode = "006";
		} else if (point.getNo().equals("p7")) {
			feeCode = "007";
		} else if (point.getNo().equals("p8")) {
			feeCode = "008";
		} else if (point.getNo().equals("p9"))// 记牌器
		{
			feeCode = "009";
		} else if (point.getNo().equals("p10")) {
			feeCode = "010";
		} else if (point.getNo().equals("p11")) {
			feeCode = "011";
		}
		try {
			if (EGameSMSConfig.PAY_ORDER != null)
				return;
			ThreadPool.startWork(new Runnable() {
				@Override
				public void run() {
					// 先提交充值订单
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("goodsName", point.getName()); // 购买的物品名称
					paramMap.put("money", String.valueOf(point.getMoney()));
					paramMap.put("payFromType", paySiteTag); // 充值的标识位
					// 判断是不是预充值
					if (PaySite.PREPARERECHARGE.equalsIgnoreCase(paySiteTag)
							&& null != PrerechargeManager.mPayRecordOrder
									.getPreOrderNo()) {
						paramMap.put(
								PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERNO,
								PrerechargeManager.mPayRecordOrder
										.getPreOrderNo());
						paramMap.put(
								PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERTYPE,
								"1");
					}
					if (Assistant.ASSID != null && Assistant.BTNCODE != null) {
						paramMap.put("asstId", Assistant.ASSID);
						paramMap.put("btnCode", Assistant.BTNCODE);
						Assistant.ASSID = null;
						Assistant.BTNCODE = null;
					}
					if (Database.JOIN_ROOM != null) {
						paramMap.put("payFromItem",
								Database.JOIN_ROOM.getCode());
					}
					String resultJson = HttpRequest.addPayOrder(
							EGameSMSConfig.PAY_ORDER_URL, paramMap);
					JsonResult result = JsonHelper.fromJson(resultJson,
							JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) {
						EGameOrder mmorder = JsonHelper.fromJson(
								result.getMethodMessage(), EGameOrder.class);
						EGameSMSConfig.PAY_ORDER = mmorder.getOrderNo();
						Database.currentActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								
							}
						});
					} else {
						DialogUtils.mesTip(result.getMethodMessage(), true);
					}
				}
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
