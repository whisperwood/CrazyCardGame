package com.sdk.jdpay;

import com.lordcard.constant.Database;
import com.lordcard.network.base.ThreadPool;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.mmpay.sms.util.MMSMSPayUtil;
import com.sdk.util.ISDKFactory;
import com.sdk.util.PaySite;
import com.sdk.util.vo.PayInit;
import com.sdk.util.vo.PayPoint;

/**
 * 移动ＭＭ弱联网支付
 * 
 * @author yinhb 2013-12-9 下午2:00:41
 */
public class JDSMSPayFactory extends ISDKFactory {
	/**
	 * 支付初始化
	 */
	@Override
	public void loadPay(PayInit payInit) {
	}

	/**
	 * 支付
	 * 
	 * @Title: goPay
	 * @param payPoint
	 *            具体的充值计费点
	 * @return void
	 * @throws
	 */
	@Override
	public void goPay(PayPoint payPoint, String paySiteTag) {
		if (payPoint == null)
			return;
		// MMSMSPayUtil.goPay(payPoint,paySiteTag);
		JDSMSPayUtil.goPay(payPoint, paySiteTag);
	}

	@Override
	public String getPayCode() {
		return JDSMSConfig.PAY_CODE;
	}

	@Override
	public void localPay(final PayPoint point, String paySiteTag) {
		try {
			ThreadPool.startWork(new Runnable() {
				@Override
				public void run() {
					Database.currentActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MMSMSPayUtil.mListener.setPayTo(PaySite.OFF_LINE);
							MMSMSPayUtil.mListener.setPayPoint(point);
							MMSMSPayUtil.purchase.smsOrder(
									Database.currentActivity, point.getValue(),
									MMSMSPayUtil.mListener);
						}
					});
				}
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
