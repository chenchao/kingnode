package com.kingnode.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class PipelineTest{
    public static void main(String[] args){
        long t1 = System.currentTimeMillis();
        withoutPipeline(10000);
        long t2 = System.currentTimeMillis();
        System.out.println("without pipeline user time:" + (t2-t1));

        long t3 = System.currentTimeMillis();
        userPipeline(10000);
        long t4 = System.currentTimeMillis();
        System.out.println("User pipeline user time:" + (t4-t3));
    }

    private static void withoutPipeline(int count){
        Jedis jr = null;
        try{
            jr = new Jedis("127.0.0.1",6379);
            for(int i=0;i<count;i++){
                jr.incr("noPipeline");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(jr != null){
                jr.disconnect();
            }
        }
    }

    private static void userPipeline(int count){
        Jedis jr = null;
        try{
            jr = new Jedis("127.0.0.1",6379);
            Pipeline pp = jr.pipelined();
            for(int i=0;i<count;i++){
                pp.incr("userPipeline");
            }
            pp.sync();
        }catch(Exception e){

        }finally{
            if(jr != null){
                jr.disconnect();
            }
        }
    }
}
