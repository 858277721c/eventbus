package com.fanwe.event;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.fanwe.lib.eventbus.FEventBus;
import com.fanwe.lib.eventbus.FEventObserver;
import com.fanwe.lib.eventbus.FEventObserverGroup;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FEventBus.getDefault().post(new TestEvent()); //发送事件
            }
        });

        /**
         * 设置观察者所在的Activity
         *
         * 如果调用此方法设置一个Activity对象，则会在该Activity生命周期onDestroy()的时候自动取消注册观察者
         * 如果不调用此方法，则要在适当的地方调用取消注册观察者(mEventObserver.unregister())，否则会内存泄漏
         */
        mEventObserver.setActivity(this);
        mEventObserverGroup.setActivity(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        FEventBus.getDefault().post(new OnResumeEvent()); //发送事件
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FEventBus.getDefault().post(new OnStopEvent()); //发送事件
    }

    /**
     * 接收事件方式一
     */
    private FEventObserverGroup mEventObserverGroup = new FEventObserverGroup()
    {
        /**
         * 定义public权限，无返回值，参数长度为1个的方法
         * @param event
         */
        public void onEvent(OnResumeEvent event)
        {
            Log.i(TAG, "group:" + String.valueOf(event));
        }

        /**
         * 定义public权限，无返回值，参数长度为1个的方法
         * @param event
         */
        public void onEvent(OnStopEvent event)
        {
            Log.i(TAG, "group:" + String.valueOf(event));
        }
    };

    /**
     * 接收事件方式二
     */
    private FEventObserver<TestEvent> mEventObserver = new FEventObserver<TestEvent>()
    {
        @Override
        public void onEvent(TestEvent event)
        {
            Log.i(TAG, String.valueOf(event));
        }
    };
}
