package org.mryan.eventnice.core;

import org.mryan.eventnice.utils.LoggerUtils;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

/**
 * @description： 默认事件调度器
 * @Author MRyan
 * @Date 2021/10/9 00:46
 * @Version 1.0
 */
public class DefaultEventDispatcher extends EventDispatcher {

    /**
     * 执行器
     */
    private Executor executor;

    public DefaultEventDispatcher(Executor executor) {
        this.executor = executor;
    }

    /**
     * 事件调度
     *
     * @param event
     * @param registry
     */
    @Override
    public void post(Object event, ReceiverRegistry registry) {
        //捕获指定匹配事件接收器
        CopyOnWriteArraySet<EventReceiver> eventReceivers = registry.huntingMatchedEventReceivers(event);
        for (EventReceiver eventReceiver : eventReceivers) {
            //匹配事件 在进行调度，同一个事件接收器中匹配多个EventReceiver 进行事件调度时所属event若不同并不会调度给所有EventReceiver执行事件 避免执行异常
            if (eventReceiver.methodInfo.getMethod().getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                executor.execute(() -> {
                    try {
                        eventReceiver.execute(eventReceiver, event);
                    } catch (Exception e) {
                        LoggerUtils.error(LoggerFactory.getLogger(getClass()), "error: " + event, e);
                    }
                });
            }
        }
    }

}
