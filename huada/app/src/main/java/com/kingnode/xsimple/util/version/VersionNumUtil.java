package com.kingnode.xsimple.util.version;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Strings;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.entity.push.KnDeviceInfo;
import com.kingnode.xsimple.util.Utils;
/**
 * 项目名称：knd_xSimple
 * 类名称：VersionNumUtil
 * 类描述：   版本的比较类,兼容1.1.1类版本号
 * 创建人：cici
 * 创建时间：2014-6-26 下午5:07:29
 * 修改时间：2014-6-26 下午5:07:29
 */
public class VersionNumUtil{
    /**
     * 版本号的比较,忽略字母的比较,如果传输进入的是字母,则按照char比较,字母比数字大;
     * 如：1.1.2与1.3.2  后者大; 1.2 与1.4.2 后者大
     * 配合 Collections.sort(aphoneList, new Comparator<Version>() {
     * public int compare(Version o1, Version o2) {
     * return StringOfUtil.versionCompareTo(o1.getNum(), o2.getNum());
     * }
     * });
     * 使用的话就是进行了降序的输出,取出的第一个数据为最高版本的数据
     *
     * @param verStr1 版本号字符串1
     * @param verStr2 版本号字符串2
     *
     * @return >0表示verStr1小,=0表示相等,<0表示verStr2小
     * 即：0--相等;1--后者大,-1--前者大
     */
    public static int versionCompareTo(String verStr1,String verStr2){
        int flag=0;
        //verStr1为空,verStr2小
        if(Strings.isNullOrEmpty(verStr1)){
            return 1;
        }
        //verStr2为空,verStr1小
        if(Strings.isNullOrEmpty(verStr2)){
            return -1;
        }
        try{
            //根据.来进行分割
            String[] verStr1Arr=verStr1.toUpperCase().split("\\.");
            String[] verStr2Arr=verStr2.toUpperCase().split("\\.");
            int vsLength1=verStr1Arr.length;
            int vsLength2=verStr2Arr.length;
            for(int i=0;i<vsLength1;i++){
                if(vsLength2>i){
                    String tempStr1=verStr1Arr[i].trim();
                    String tempStr2=verStr2Arr[i].trim();
                    if(i==0){//第一位数
                        int v1=0, v2=0;
                        if(tempStr1.matches("\\d+")){
                            v1=Integer.parseInt(tempStr1);
                        }
                        if(tempStr2.matches("\\d+")){
                            v2=Integer.parseInt(tempStr2);
                        }
                        if(v1>v2){//v1大
                            return -1;
                        }else if(v1<v2){
                            return 1;
                        }
                    }
                    int t1=tempStr1.length();
                    int t2=tempStr2.length();
                    for(int j=0;j<(t1>t2?t1:t2);j++){
                        char a1='0', a2='0';
                        if(t1>j){
                            a1=tempStr1.charAt(j);
                        }
                        if(t2>j){
                            a2=tempStr2.charAt(j);
                        }
                        if(a1==a2){
                            continue;
                        }else if(a1>a2){//a1大
                            return -1;
                        }else{
                            return 1;
                        }
                    }
                }else{
                    return -1;
                }
            }
        }catch(Exception e){
        }
        return flag;
    }
    /**
     * 根据传入版本的List集合获取比num大或者小的版本的集合
     *
     * @param list  版本的集合,
     * @param num   需要比较的版本号
     * @param getUp 是否获取比num大的数据,ture表示获取比num大的数据,false表示获取比num小的数据
     *
     * @return 经过过滤后的集合信息
     */
    public static List<KnVersionInfo> getVersionByNum(List<KnVersionInfo> list,String num,boolean getUp){
        if(list==null||list.size()==0){
            return null;
        }
        List<KnVersionInfo> tempList=new ArrayList<KnVersionInfo>();
        for(KnVersionInfo version : list){
            if(getUp){
                if(versionCompareTo(version.getNum(),num)==-1){
                    tempList.add(version);
                }
            }else{
                if(versionCompareTo(version.getNum(),num)==1){
                    tempList.add(version);
                }
            }
        }
        return tempList;
    }
    /**
     * 根据传入设备的List集合获取比num大或者小的设备的集合
     *
     * @param list  设备的集合,
     * @param num   需要比较的版本号
     * @param getUp 是否获取比num大的数据,ture表示获取比num大的数据,false表示获取比num小的数据
     *
     * @return 有结果集的list集合
     */
    public static List<KnDeviceInfo> getChannelByNum(List<KnDeviceInfo> list,String num,boolean getUp){
        if(list==null||list.size()==0){
            return null;
        }
        List<KnDeviceInfo> tempList=new ArrayList<KnDeviceInfo>();
        for(KnDeviceInfo c : list){
            if(getUp){
                if(versionCompareTo(c.getChversion(),num)==-1){
                    tempList.add(c);
                }
            }else{
                if(versionCompareTo(c.getChversion(),num)==1){
                    tempList.add(c);
                }
            }
        }
        return tempList;
    }
    /**
     * 根据传入版本的List集合获取此数据中版本号最高的version
     *
     * @param list 版本的集合,
     *
     * @return 版本最高的version
     */
    public static KnVersionInfo getMaxObjectByNum(List<KnVersionInfo> list){
        if(list==null||list.size()==0){
            return null;
        }
        Collections.sort(list,new Comparator<KnVersionInfo>(){
            public int compare(KnVersionInfo o1,KnVersionInfo o2){
                return VersionNumUtil.versionCompareTo(o1.getNum(),o2.getNum());
            }
        });
        return list.get(0);
    }
    /**
     * 根据传入版本的List集合以及版本号获取此数据中版本号最高的version
     *
     * @param list 版本的集合,
     * @param num  版本号
     *
     * @return 版本最高的List<Version>
     */
    public static List<KnVersionInfo> getMaxVerListByNum(List<KnVersionInfo> list,String num){
        if(Utils.isEmpityCollection(list)){
            return list;
        }
        List<KnVersionInfo> verList=new ArrayList<KnVersionInfo>();
        verList.add(getMaxObjectByNum(list));
        return getVersionByNum(verList,num,true);
    }
}
