package com.kingnode.redis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class PubSubTest extends JedisPubSub{

    @Override
    public void onMessage(String channel,String message){
        System.out.println("onMessage:channel["+channel+"],messge["+message+"]");
    }
    @Override
    public void onPMessage(String pattern,String channel,String message){
        System.out.println("OnPMessage:pattern["+pattern+"],channel["+channel+"],message["+message+"]");
    }
    @Override
    public void onSubscribe(String channle,int subscribeChannels){
        System.out.println("OnSubscribe:channle["+channle+"],subscribeChannels["+subscribeChannels+"]");
    }
    @Override
    public void onUnsubscribe(String channle,int subscribeChannels){
        System.out.println("onUnsubscribe:channle["+channle+"],subscribeChannels["+subscribeChannels+"]");
    }
    @Override
    public void onPUnsubscribe(String channle,int subscribeChannels){
        System.out.println("onPUnsubscribe:channle["+channle+"],subscribeChannels["+subscribeChannels+"]");
    }
    @Override
    public void onPSubscribe(String channle,int subscribedChannels){
        System.out.println("onPSubscribe:channle["+channle+"],subscribedChannels["+subscribedChannels+"]");
    }

    public static void main(String[] args){
        Jedis jr = null;
        try{
            jr = new Jedis("127.0.0.1",6379,0);
            PubSubTest ps = new PubSubTest();
            ps.proceed(jr.getClient(),"news.share","news.blog");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(jr!=null){
                jr.disconnect();
            }
        }
    }
}
