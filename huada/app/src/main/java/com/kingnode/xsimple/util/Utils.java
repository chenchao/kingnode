package com.kingnode.xsimple.util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import com.kingnode.diva.security.utils.Digests;
import com.kingnode.diva.utils.Encodes;
import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.api.common.DataTable;
import org.springframework.data.domain.Sort;
/**
 * Created by xushuangyong on 14-5-29.
 */
public class Utils{
    /**
     * 判断一个字符串是否为空
     *
     * @param str
     *
     * @return
     */
    public static boolean isEmptyStr(String str){
        if(str==null){
            return true;
        }
        str=str.trim();
        return str.isEmpty();
    }
    public static Sort getSort(DataTable dt,ServletRequest request){
        Sort sort=null;
        String iSortCol_0=dt.getiSortCol_0();
        String sSortDir_0=dt.getsSortDir_0();
        String isSort_="bSortable_"+iSortCol_0;
        String orderParamter="mDataProp_"+iSortCol_0;
        boolean isSort=Boolean.parseBoolean(request.getParameter(isSort_));
        String orderName=null;
        if(isSort==true){
            orderName=request.getParameter(orderParamter);
            if(orderName!=null&&!orderName.equals("")&&sSortDir_0!=null){
                if(sSortDir_0.equalsIgnoreCase("asc")){
                    sort=new Sort(Sort.Direction.ASC,new String[]{orderName});
                }else if(sSortDir_0.equalsIgnoreCase("desc")){
                    sort=new Sort(Sort.Direction.DESC,new String[]{orderName});
                }
                return sort;
            }
        }
        return null;
    }
    /**
     * 对集合去重 （无顺序）
     *
     * @param arlList 带去重的集合
     *
     * @return 返回去重后的集合
     */
    public static List removeDuplicate(List arlList){
        if(Utils.isEmpityCollection(arlList)){
            return new ArrayList();
        }
        HashSet h=new HashSet(arlList);
        arlList.clear();
        arlList.addAll(h);
        return arlList;
    }
    /**
     * 判断对象是否为空
     *
     * @param obj
     *
     * @return
     */
    public static boolean isNotNull(Object obj){
        if(obj!=null){
            return true;
        }
        return false;
    }
    /**
     * 判断集合是否为空
     *
     * @param c 待判断的集合
     *
     * @return集合为空,返回true ,反之 false
     */
    public static boolean isEmpityCollection(Collection c){
        if(c==null||c.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    public static boolean isEmptyStr(Object s){
        return (s==null)||(s.toString().trim().length()==0);
    }
    /**
     * 判断传入参数是否为空,空字符串""或"null"或"<null> 为了兼容ios的空获取到<null>字符串
     *
     * @param s 待判断参数
     *
     * @return true 空 <br>
     * false 非空
     */
    public static boolean isEmptyString(Object s){
        return (s==null)||(s.toString().trim().length()==0)||s.toString().trim().equalsIgnoreCase("null")||s.toString().trim().equalsIgnoreCase("<null>");
    }
    /**
     * 对传入参数进行判断是否为空,为空则返回"",反之返回传入参数
     *
     * @param v 传入参数
     *
     * @return 处理后的参数
     */
    public static String filterNullValue(String v){
        return isEmptyString(v)?"":v;
    }
    /**
     * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
     * 当用户提供明文密码是
     *
     * @param password 用户输入密码
     */
    public static Map<String,String> entryptPassword(String password){
        Map<String,String> map=new HashMap<>();
        password=Utils.isEmptyString(password)?Setting.PASSWORD:password;
        byte[] salt=Digests.generateSalt(Setting.SALT_SIZE);
        byte[] hashPassword=Digests.sha1(password.getBytes(),salt,Setting.HASH_INTERATIONS);
        password=Encodes.encodeHex(hashPassword);
        map.put("password",password);
        map.put("enCodeSalt",Encodes.encodeHex(salt));
        return map;
    }
}
