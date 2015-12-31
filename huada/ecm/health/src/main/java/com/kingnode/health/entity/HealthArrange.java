package com.kingnode.health.entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingnode.xsimple.entity.IdEntity;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class HealthArrange{
    private long start;//每天开始时间
    private long end;//每天结束时间
    private long amEnd=0;//中午休息开始时间
    private long pmStart=0;//中午休息结束时间
    private int spacing=30*60*1000;//时间间隔
    private long wait=0;//候诊时间
    //private long next=0;//--
    private List<String> restDays=new ArrayList<>();//休假时间
    //private Map<String,List<Long>> consultationMap=new HashMap<>();//复诊集合--
    private Map<String,List<Long>> dayOfArrangeMap=new HashMap<>();//已被排号的记录集合  key:已“天”为单位,格式yyyy-MM-dd。value:已排号有效记录集合
    private Map<String,Integer> maxOrderMap=new HashMap<>();//已被排号的最大号码   key:已“天”为单位,格式yyyy-MM-dd。value:当天已排号记录的最大号码
    private List<Long> arrangeList=new ArrayList<>();//当天已排号有效记录集合,由dayOfArrangeMap中获取
    //private List<Long> consultationList=new ArrayList<>();//复诊时间列表--

    private int number=0;//每天已生成的号码数
    //private boolean first=true;

    public HealthArrange(){

    }
    /**
     * 排号功能的构造函数
     * @param set KnEcmHealthSet设置对象
     * @param restDays 休息日集合
     * @param dayOfArrangeMap 大于指定开始排号时间的已排号对象集合
     * @param maxOrderMap 大于指定开始排号时间的每天已排号对象的最大号码集合(不忽略已失效的排号对象)
     * @param last 指定的开始排号时间
     */
    public HealthArrange(KnEcmHealthSet set,List<String> restDays,Map<String,List<Long>> dayOfArrangeMap,Map<String,Integer> maxOrderMap,long last){
        DateTime now=DateTime.now();//当前时间
        String curr="";
        //初始化开始时间，结束时间
        if(last>now.getMillis()){
            curr=new DateTime(last).toString("yyyy-MM-dd");
        }else{
            curr=now.toString("yyyy-MM-dd");
        }
        DateTimeFormatter format=DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        this.start=DateTime.parse(curr+" "+set.getAmStartDate(),format).getMillis();
        this.amEnd=DateTime.parse(curr+" "+set.getAmEndDate(),format).getMillis();
        if(StringUtils.isNotBlank(set.getPmStartDate())){
            this.pmStart=DateTime.parse(curr+" "+set.getPmStartDate(),format).getMillis();
        }
        if(StringUtils.isNotBlank(set.getPmEndDate())){
            this.end=DateTime.parse(curr+" "+set.getPmEndDate(),format).getMillis();
        }else{
            this.end=DateTime.parse(curr+" "+set.getAmEndDate(),format).getMillis();
        }
        //初始化时间间隔
        this.spacing=set.getSpacing()*60*1000;
        //初始化请假列表
        if(restDays!=null&&restDays.size()>0){
            this.restDays=restDays;
        }
        //初始化插入排号列表
        if(dayOfArrangeMap!=null&&dayOfArrangeMap.size()>0){
            this.dayOfArrangeMap=dayOfArrangeMap;
        }
        //初始化每天最大排序号
        if(maxOrderMap!=null&&maxOrderMap.size()>0){
            this.maxOrderMap=maxOrderMap;
        }
        //初始wait
        if(last<DateTime.now().getMillis()){
            wait=start;
        }else{
            wait=last;
        }
        //设置排号的生效时间
        long action=0;
        if(set.getAction()!=null&&set.getAction()>0){
            action=DateTime.now().plusMinutes(set.getAction()).getMillis();
        }
        if(wait<action){
            wait=start;//保证排号时间是以开始时间+间隔为一个单位
            while(true){
                wait=wait+spacing;
                if(wait>action){
                    break;
                }
            }
        }
        //初始化其他数据
        init();
    }
    /**
     * 读取配置完成或新排号一天的初始数据
     */
    public void init(){
        String key=new DateTime(wait).toString("yyyy-MM-dd");
        arrangeList=dayOfArrangeMap.get(key);
        number=0;
        if(maxOrderMap.containsKey(key)&&maxOrderMap.get(key)!=null){
            number=Integer.parseInt(maxOrderMap.get(key).toString());
        }
    }
    /**
     * 创建排号对象
     * @return KnEcmHealthNumber
     */
    public KnEcmHealthNumber next(){
        //判断下一个排序时间是否已经结束或是休息日
        if(hasDayEnd()||hasRestDay()){
            nextStart();//开始下一天
            wait=start;
            init();
        }

        if(number>0){
            long next=0;
            //判断arrangeList是否有数据
            if(arrangeList!=null&&arrangeList.size()>0){
                //插入排序当天有数据，2种情况:1.刚初始化完成,2.已经常规排序
                if(wait==start){
                    next=wait;
                }else{
                    next=wait+spacing;
                }
                while(true){
                    if(arrangeList.contains(next)){
                        next+=spacing;
                        continue;
                    }
                    if(next+spacing>amEnd&&next<pmStart){
                        next=pmStart;
                        continue;
                    }
                    break;
                }
            }else{
                next=wait+spacing;
                if(next+spacing>amEnd&&next<pmStart){
                    next=pmStart;
                }
            }
            wait=next;
        }
        number++;

        KnEcmHealthNumber obj=new KnEcmHealthNumber();
        obj.setActive(IdEntity.ActiveType.ENABLE);
        obj.setDay(new DateTime(wait).toString("yyyy-MM-dd"));
        obj.setStart(wait);
        obj.setNumber(number);
        obj.setCode(createNumber());
        return obj;
    }


    /*//创建候诊时间
    private void create(){
        //判断下一个排序时间是否已经结束
        if(hasDayEnd()){
            nextStart();//开始下一天
            wait=start;
            init();
        }

        if(number>0){
            long next=0;
            //判断arrangeList是否有数据
            if(arrangeList!=null&&arrangeList.size()>0){
                //插入排序当天有数据，2种情况:1.刚初始化完成,2.已经常规排序
                if(wait==start){
                    next=wait;
                }else{
                    next=wait+spacing;
                }
                while(true){
                    if(arrangeList.contains(next)){
                        next+=spacing;
                        continue;
                    }
                    if(next+spacing>=amEnd&&next<pmStart){
                        next=pmStart;
                        continue;
                    }
                    break;
                }
            }else{
                next=wait+spacing;
                if(next+spacing>=amEnd&&next<pmStart){
                    next=pmStart;
                }
            }
            wait=next;
        }

        number++;

        KnEcmHealthNumber obj=new KnEcmHealthNumber();
        obj.setActive(IdEntity.ActiveType.ENABLE);
        obj.setDay(new DateTime(wait).toString("yyyy-MM-dd"));
        obj.setStart(wait);
        obj.setEnd(wait+spacing);
        //obj.setType(1);
        obj.setNumber(number);
        obj.setCode(createNumber());

        *//*if(first){
            first=false;
            number=0;
            wait=start;
            if(consultationList!=null&&consultationList.size()>0){
                number=consultationList.size();
            }
        }else {
            //有中午休息情况
            if(wait>=amEnd&&wait<pmStart){
                wait=pmStart;
            }else {
                wait+=spacing;
            }
        }

        //判断当前wait是否已有非常规排号安排
        if(consultationList!=null&&consultationList.size()>0){
            while(true){
                if(consultationList.contains(wait)){//复诊列表是否包含当前排号时间
                    wait+=spacing;
                }else {
                    if(wait>=amEnd&&wait<pmStart){
                        wait=pmStart;
                    }else{
                        break;
                    }
                }
            }
        }

        number++;

        //当天被排满的情况下时间自动加1
        if(hasDayEnd()){
            first=true;
            nextStart();
            //判断下一条是否有复诊安排
            consultationList=consultationMap.get(new DateTime(start).toString("yyyy-MM-dd"));
        }*//*
    }*/
    /**
     * 是否一天的排号时间已结束
     * @return boolean
     */
    private boolean hasDayEnd(){
        return (wait+spacing)>=end;
    }
    /**
     * 设置下一个可排号日期的开始时间,结束时间,中间休息时间
     */
    private void nextStart(){
        while(true){
            start=new DateTime(start).plusDays(1).getMillis();
            end=new DateTime(end).plusDays(1).getMillis();
            if(amEnd>0&&pmStart>0){
                amEnd=new DateTime(amEnd).plusDays(1).getMillis();
                pmStart=new DateTime(pmStart).plusDays(1).getMillis();
            }
            //是否休息天
            if(!hasRestDay()){
                break;
            }
        }
    }
    /**
     * 是否休息日
     * @return boolean
     */
    private boolean hasRestDay(){
        return restDays.contains(new DateTime(start).toString("yyyy-MM-dd"));
    }
    /**
     * 创建候诊号
     * @return String
     */
    private String createNumber(){
        return getHead()+getBody()+"-"+getFoot();
    }
    /**
     * 候诊号的头部
     * @return String
     */
    private String getHead(){
        return "D";
    }
    /**
     * 候诊号中间日期表示
     * @return String
     */
    private String getBody(){
        return new DateTime(wait).toString("yyyyMMdd");
    }
    /**
     * 候诊号尾部数字表示
     * @return String
     */
    private String getFoot(){
        if(number<10){
            return "000"+number;
        }else if(number<100){
            return "00"+number;
        }else if(number<1000){
            return "0"+number;
        }
        return number+"";
    }
    /**
     * 获取候诊时间
     * @return Long
     *//*
    public long getWait(){
        return wait;
    }



    public void setStart(long start){
        this.start=start;
    }
    public void setEnd(long end){
        this.end=end;
    }
    public void setAmEnd(long amEnd){
        this.amEnd=amEnd;
    }
    public void setPmStart(long pmStart){
        this.pmStart=pmStart;
    }
    public void setSpacing(int spacing){
        this.spacing=spacing;
    }
    public void setWait(long wait){
        this.wait=wait;
    }
    public void setRestDays(List<String> restDays){
        this.restDays=restDays;
    }
    public void setNumber(int number){
        this.number=number;
    }
    public void setFirst(boolean first){
        this.first=first;
    }
    public void setConsultationMap(Map<String,List<Long>> consultationMap){
        this.consultationMap=consultationMap;
    }*/
}
