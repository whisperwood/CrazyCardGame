package com.lordcard.common.task;

import com.lordcard.common.task.base.TaskListener;
import com.lordcard.common.task.base.TaskResult;

/**
 * 任务事件监听抽象类 common.task.TaskAdapter
 * 
 * @author yinhb <br/>
 *         create at 2013 2013-2-5 下午4:11:49
 */
public abstract class TaskAdapter implements TaskListener {
	@Override
	public abstract String getName();

	@Override
	public void onPreExecute(GenericTask task) {
	};

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
	};

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
	};

	@Override
	public void onCancelled(GenericTask task) {
	};
}
