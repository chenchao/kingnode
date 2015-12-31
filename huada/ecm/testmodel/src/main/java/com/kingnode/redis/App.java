package com.kingnode.redis;
import redis.clients.jedis.Jedis;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class App{
    public static void main(String[] args){
        Jedis jr = null;

        try{
            jr = new Jedis("127.0.0.1",6379);

            String key = "mkey";
            jr.set(key,"hello word");
            String v = jr.get(key);

            String key2 = "count";
            jr.incr(key2);
            jr.incr(key2);

            System.out.println(jr.get(key));
            System.out.println(jr.get(key2));
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(jr != null){
                jr.disconnect();
            }
        }
    }
}
