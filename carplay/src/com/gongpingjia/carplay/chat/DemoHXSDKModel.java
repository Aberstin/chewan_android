/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gongpingjia.carplay.chat;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.chat.bean.ChatUser;
import com.gongpingjia.carplay.chat.bean.RobotUser;
import com.gongpingjia.carplay.chat.db.DemoDBManager;
import com.gongpingjia.carplay.chat.db.UserDao;
import com.gongpingjia.carplay.chat.model.DefaultHXSDKModel;

import android.content.Context;

public class DemoHXSDKModel extends DefaultHXSDKModel {

	public DemoHXSDKModel(Context ctx) {
		super(ctx);
	}

	public boolean getUseHXRoster() {
		return true;
	}

	// demo will switch on debug mode
	public boolean isDebugMode() {
		return true;
	}

	public boolean saveContactList(List<ChatUser> contactList) {
		UserDao dao = new UserDao(context);
		dao.saveContactList(contactList);
		return true;
	}

	public Map<String, ChatUser> getContactList() {
		UserDao dao = new UserDao(context);
		return dao.getContactList();
	}

	public void saveContact(ChatUser user) {
		UserDao dao = new UserDao(context);
		dao.saveContact(user);
	}

	public Map<String, RobotUser> getRobotList() {
		UserDao dao = new UserDao(context);
		return dao.getRobotUser();
	}

	public boolean saveRobotList(List<RobotUser> robotList) {
		UserDao dao = new UserDao(context);
		dao.saveRobotUser(robotList);
		return true;
	}

	public void closeDB() {
		DemoDBManager.getInstance().closeDB();
	}

	@Override
	public String getAppProcessName() {
		return context.getPackageName();
	}
}
