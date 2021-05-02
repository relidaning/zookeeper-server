# zookeeper-server
## zookeeper服务注册的简单实现
    1. 原理: 在服务启动的时候创建临时节点,将服务请求信息保存于节点内
    2. 当服务断开时,节点自动删除,触发监听,重新加载可用节点列表
    3. 其它服务请求获取该服务的请求信息时,zk从父节点获取所有可用服务
    4. 需要服务自己实现自注册的功能
    5. 该方式旨在理解原理, 代码部分已删除
## springcloud zookeeper
    1. spring封装的zookeeper版本,类似eureka
    2. 简单配置后可用
## zookeeper实现分布式锁
    1. 原理: 还是临时节点, 获取锁的机制是最小节点可获取,所以要求
        节点有序. 其它节点监听排在自己之前的节点,节点变化获取锁.
    2. 存在问题: 当节点尚未完成监听前一节点,而前一节点已完成业务
        并删除,会导致当前节点死锁,这个问题未解决,目前是在执行业务
        时sleep,模拟执行且留出时间让后序节点加锁
## curator framework实现分布锁
    1. apache封装的zookeeper分布锁实现
    2. 简单高效,原理应该与zookeeper实现的一致
