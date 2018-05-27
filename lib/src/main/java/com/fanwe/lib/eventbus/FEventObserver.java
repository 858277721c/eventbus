/*
 * Copyright (C) 2017 zhengjun, fanwe (http://www.fanwe.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanwe.lib.eventbus;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 事件观察者
 */
public abstract class FEventObserver<T>
{
    final Class<T> mEventClass;

    public FEventObserver()
    {
        final Class clazz = findTargetClass();
        final ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        final Type[] types = parameterizedType.getActualTypeArguments();

        if (types != null && types.length == 1)
            mEventClass = (Class<T>) types[0];
        else
            throw new RuntimeException("generic type length must be 1");
    }

    private Class findTargetClass()
    {
        Class clazz = getClass();
        while (true)
        {
            if (clazz.getSuperclass() == FEventObserver.class)
                break;
            else
                clazz = clazz.getSuperclass();
        }
        return clazz;
    }

    /**
     * 收到事件通知
     *
     * @param event
     */
    public abstract void onEvent(T event);

    /**
     * 注册当前对象
     *
     * @return
     */
    public final FEventObserver<T> register()
    {
        FEventBus.getDefault().register(this);
        return this;
    }

    /**
     * 取消注册当前对象
     *
     * @return
     */
    public final FEventObserver<T> unregister()
    {
        FEventBus.getDefault().unregister(this);
        return this;
    }

    //---------- Lifecycle start ----------

    private LifecycleHolder mLifecycleHolder;

    private LifecycleHolder getLifecycleHolder()
    {
        if (mLifecycleHolder == null)
        {
            mLifecycleHolder = new LifecycleHolder(new LifecycleHolder.Callback()
            {
                @Override
                public void onLifecycleStateChanged(boolean enable)
                {
                    if (enable)
                        register();
                    else
                        unregister();
                }
            });
        }
        return mLifecycleHolder;
    }

    /**
     * {@link #setLifecycle(View)}
     *
     * @param activity
     * @return
     */
    public final FEventObserver<T> setLifecycle(Activity activity)
    {
        getLifecycleHolder().setActivity(activity);
        return this;
    }

    /**
     * {@link #setLifecycle(View)}
     *
     * @param dialog
     * @return
     */
    public final FEventObserver<T> setLifecycle(Dialog dialog)
    {
        getLifecycleHolder().setDialog(dialog);
        return this;
    }

    /**
     * 设置View对象
     * <br>
     * 当该View对象Attached的时候会注册当前观察者对象
     * 当该View对象Detached的时候会取消注册当前观察者对象
     *
     * @param view
     * @return
     */
    public final FEventObserver<T> setLifecycle(View view)
    {
        getLifecycleHolder().setView(view);
        return this;
    }

    //---------- Lifecycle end ----------
}
