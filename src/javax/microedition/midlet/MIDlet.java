/*
 * Copyright 2012 Kulikov Dmitriy, Naik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.microedition.midlet;

import java.util.Locale;
import java.util.TreeMap;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.util.ContextHolder;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

public class MIDlet {

	private static Context context;
	private static TreeMap<String, String> properties;
	private static Locale locale;

	private static boolean pauseAppCalled = false;
	private static boolean destroyAppCalled = false;

	public void start() {
		// createApp();
		// initApp();
		startApp();
	}

	public static void setMidletContext(Context c) {
		context = c;
		ContextHolder.setContext(context);
	}

	public static Context getMidletContext() {
		return context;
	}

	public static void initProps(TreeMap<String, String> p) {
		properties = p;
	}

	public String getAppProperty(String key) {
		return properties.get(key);
	}

	/**
	 * Сообщить оболочке, что мидлет готов перейти в состояние паузы. При этом
	 * он будет свернут в фон.
	 *
	 * Вызовы этого метода из pauseApp() игнорируются.
	 */
	public final void notifyPaused() {
		if (!pauseAppCalled) {
			ContextHolder.notifyPaused();
		}
	}

	/**
	 * Сообщить оболочке, что мидлет завершил работу. При этом оболочка будет
	 * закрыта.
	 *
	 * Вызовы этого метода из destroyApp() игнорируются.
	 */
	public final void notifyDestroyed() {
		if (!destroyAppCalled) {
			ContextHolder.notifyDestroyed();
		}
	}

	/**
	 * Вызывается в самом начале запуска оболочки, когда создается объект
	 * Application.
	 *
	 * В частности, этот метод вызывается перед созданием Activity.
	 * Соответственно, если конфигурирование оболочки происходит через
	 * ConfigActivity, то в момент вызова этого метода состояние оболочки еще не
	 * определено: виртуальный экран имеет нулевой размер, размеры шрифтов не
	 * скорректированы, ...
	 */
	public void createApp() {
	}

	/**
	 * Вызывается при передаче управления мидлету.
	 *
	 * Этот метод следует использовать вместо конструктора класса мидлета для
	 * его инициализации.
	 *
	 * Если конфигурирование оболочки происходит через ConfigActivity, то в
	 * момент вызова этого метода состояние оболочки полностью определено:
	 * виртуальный экран имеет указанный пользователем размер, размеры шрифтов
	 * скорректированы в соответствии с разрешением экрана, ...
	 */
	public/* abstract */void initApp() {
	}

	/**
	 * Вызывается каждый раз, когда мидлет становится активным: при запуске
	 * после initApp(), при восстановлении из свернутого состояния, ...
	 */
	public/* abstract */void startApp() {
	}

	/**
	 * Вызывается каждый раз, когда мидлет становится на паузу: при сворачивании
	 * в фоновый режим, ...
	 */
	public/* abstract */void pauseApp() {
	}

	/**
	 * Корректно вызвать pauseApp(). Во время выполнения этого метода вызовы
	 * notifyPaused() игнорируются.
	 */
	public final void callPauseApp() {
		pauseAppCalled = true;
		pauseApp();
		pauseAppCalled = false;
	}

	/**
	 * Вызывается при завершении работы приложения.
	 *
	 * @param unconditional
	 *            флаг безусловного завершения, для Android не имеет особого
	 *            смысла
	 */
	public static/* abstract */void destroyApp(boolean unconditional) {
	}

	/**
	 * Корректно вызвать destroyApp(). Во время выполнения этого метода вызовы
	 * notifyDestroyed() игнорируются.
	 *
	 * @param unconditional
	 *            флаг безусловного завершения, для Android не имеет особого
	 *            смысла
	 */
	public static final void callDestroyApp(boolean unconditional) {
		destroyAppCalled = true;
		destroyApp(unconditional);
		destroyAppCalled = false;
	}

	public static void setLocale(String language) {
		locale = new Locale(language);
		updateConfiguration();
	}

	public static String getDefaultLocale() {
		return Locale.getDefault().getCountry();
	}

	public static void updateConfiguration() {
		Resources res = context.getResources();
		Configuration conf = res.getConfiguration();

		if (locale != null) {
			conf.locale = locale;
		}

		res.updateConfiguration(conf, res.getDisplayMetrics());
	}

	// @Override
	// public void onConfigurationChanged(Configuration conf) {
	// updateConfiguration();
	// }

	public void startActivity(Class cls) {
		Intent i = new Intent(context, cls);
		context.startActivity(i);
	}

	public void startActivity(Class cls, Bundle bundle) {
		Intent intent = new Intent(context, cls);

		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (bundle != null) {
			intent.putExtras(bundle);
		}

		context.startActivity(intent);
	}

	public boolean platformRequest(String url)
			throws ConnectionNotFoundException {
		try {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		} catch (ActivityNotFoundException e) {
			throw new ConnectionNotFoundException();
		}

		return true;
	}
}
