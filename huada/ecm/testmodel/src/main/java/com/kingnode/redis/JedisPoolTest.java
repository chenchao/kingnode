package com.kingnode.redis;
import java.util.concurrent.CountDownLatch;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class JedisPoolTest{
    private static JedisPoolConfig config;//jedis客户端池配置
    private static JedisPool pool;//jedis客户端池

    private static String NO_POOL_INCR = "noPoolTest2";
    private static String WITH_POOL_INCR = "withPoolTest2";

    static{
        config = new JedisPoolConfig();
        config.setMaxActive(60000);
        config.setMaxIdle(1000);
        config.setMaxWait(10000);
        config.setTestOnBorrow(true);

        pool = new JedisPool(config,"127.0.0.1",6379);
    }


    public static void testNoPool(int count){
        Jedis jr = null;
        jr = new Jedis("127.0.0.1",6379);

        for(int i=0;i<count;i++){
            try{
                testOnce(jr,NO_POOL_INCR);
            }catch(Exception e){
                e.printStackTrace();
            }finally{

            }
        }
        if( jr != null ){
            jr.disconnect();
        }
    }

    public static void testWithPool(int count){
        Jedis jr = null;
        jr = pool.getResource();
        for(int i = 0; i < count ; i ++ ){
            try{

                testOnce(jr,WITH_POOL_INCR);
            }catch(Exception e){

            }finally{

            }
        }
        if(jr != null){
            pool.returnResource(jr);
        }
    }

    public static void paiallelTestNoPool(int paialle,int count){

        long start = System.currentTimeMillis();

        Thread[] ts = new Thread[paialle];

        //该对象保证所有线程都完成主线程菜退出
        CountDownLatch cdl = new CountDownLatch(paialle);

        for(int i=0; i<paialle; i++){
            ts[i] = new Thread(new WorkerNoPool(cdl , count));
            ts[i].start();
        }

        try{
            cdl.await();
        }catch(Exception e){
            e.printStackTrace();
        }

        long last = System.currentTimeMillis();

        System.out.println("No pool user time :"+(last - start) + "ms");
    }

    public static void paiallelTestWithPool(int paialle, int count){

        long  start = System.currentTimeMillis();

        CountDownLatch cdl = new CountDownLatch(paialle);

        Thread[] ths = new Thread[paialle];

        for(int i=0; i<paialle; i++){
            ths[i] = new Thread( new WorkerWithPool(cdl,count));

            ths[i].start();
        }

        try{
            cdl.await();
        }catch(Exception e){
            e.printStackTrace();
        }

        long last = System.currentTimeMillis();
        System.out.println("With pool user time:"+(last - start) + "ms");
    }

    public static void testOnce(Jedis js,String key){
        js.incr(key);
    }

    public static class WorkerNoPool implements Runnable{

        private CountDownLatch cdl ;
        private int count;

        public WorkerNoPool(CountDownLatch cdl,int count){
            this.cdl = cdl;
            this.count = count;
        }

        @Override
        public void run(){
            try{
                testNoPool(this.count);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                cdl.countDown();
            }
        }
    }

    public static class WorkerWithPool implements Runnable{

        private CountDownLatch cdl;
        private int count;

        public WorkerWithPool(CountDownLatch cdl, int count){
            this.cdl = cdl;
            this.count = count;
        }

        @Override
        public void run(){
            try{
                testWithPool(this.count);
            }catch(Exception e){
                e.printStackTrace();
            }
            finally{
                cdl.countDown();
            }
        }
    }

    public static void main(String[] args){
        paiallelTestWithPool(200,10000);
    }
}
