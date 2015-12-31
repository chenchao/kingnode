package com.kingnode.xsimple.util.freemark;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import com.kingnode.xsimple.Setting;
import com.kingnode.xsimple.Setting.VersionType;
import com.kingnode.xsimple.Setting.WorkStatusType;
import com.kingnode.xsimple.entity.application.KnApplicationInfo;
import com.kingnode.xsimple.entity.application.KnVersionInfo;
import com.kingnode.xsimple.util.PathUtil;
import com.kingnode.xsimple.util.Utils;
import com.kingnode.xsimple.util.version.VersionNumUtil;
import com.swetake.util.Qrcode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;
/*
*
 * 生成应用的下载文件,根据freemarker模板进行获取
 *
*/
public class WriteToJsp{
    private String freemarker="/WEB-INF/classes/freemarker";
    private final Logger log=LoggerFactory.getLogger(WriteToJsp.class);
    private static WriteToJsp writeToJsp=null;
    private WriteToJsp(){
    }
    public static WriteToJsp getInstall(){
        if(null==writeToJsp){
            synchronized(WriteToJsp.class){
                writeToJsp=new WriteToJsp();
            }
        }
        return writeToJsp;
    }
    /**
     * 创建应用的文件,加载应用的信息模板,根据应用的id
     *
     * @param knApplicationInfo 应用
     * @param oRadio            验证方式标示    1  默认不需要验证  2  用户名  密码    3  验证码  4 邀请码
     *
     * @return
     */
    public Map<String,Object> writeToJsp(KnApplicationInfo knApplicationInfo,String oRadio,List<KnVersionInfo> versionList,String localUrl){
        Map<String,Object> backMap=new HashMap<>();
        boolean flag=false;
        try{
            Map<String,String> map=new HashMap<>();
            String createTime=knApplicationInfo.getCreateTime()+"";
            if(Utils.isEmptyString(createTime)){
                createTime=String.valueOf(System.currentTimeMillis());
            }
            List<KnVersionInfo> iphoneList=new ArrayList<KnVersionInfo>();
            List<KnVersionInfo> ipadList=new ArrayList<KnVersionInfo>();
            List<KnVersionInfo> aphoneList=new ArrayList<KnVersionInfo>();
            List<KnVersionInfo> apadList=new ArrayList<KnVersionInfo>();
            //获取最高纪录的
            for(KnVersionInfo v : versionList){
                String type=v.getType().name().trim();
                if(VersionType.IPHONE.name().equalsIgnoreCase(type)){
                    iphoneList.add(v);
                }else if(VersionType.IPAD.name().equalsIgnoreCase(type)){
                    ipadList.add(v);
                }else if(VersionType.ANDROID_PAD.name().equalsIgnoreCase(type)){
                    apadList.add(v);
                }else if(VersionType.ANDROID.name().equalsIgnoreCase(type)){
                    aphoneList.add(v);
                }
            }
            //根据版本号进行排序,降序输出,取出的第一个数据为最高版本数据
            Collections.sort(iphoneList,new Comparator<KnVersionInfo>(){
                public int compare(KnVersionInfo o1,KnVersionInfo o2){
                    return VersionNumUtil.versionCompareTo(o1.getNum(),o2.getNum());
                }
            });
            Collections.sort(ipadList,new Comparator<KnVersionInfo>(){
                public int compare(KnVersionInfo o1,KnVersionInfo o2){
                    return VersionNumUtil.versionCompareTo(o1.getNum(),o2.getNum());
                }
            });
            Collections.sort(apadList,new Comparator<KnVersionInfo>(){
                public int compare(KnVersionInfo o1,KnVersionInfo o2){
                    return VersionNumUtil.versionCompareTo(o1.getNum(),o2.getNum());
                }
            });
            Collections.sort(aphoneList,new Comparator<KnVersionInfo>(){
                public int compare(KnVersionInfo o1,KnVersionInfo o2){
                    return VersionNumUtil.versionCompareTo(o1.getNum(),o2.getNum());
                }
            });
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(iphoneList.size()>0){
                String address=iphoneList.get(0).getIosHttpsAddress();
                map.put("iphoneAddress",address==null?"":address);
                Long d=iphoneList.get(0).getCreateTime();
                String desc="Ver:"+iphoneList.get(0).getNum()+"   Update:"+(d==null?sdf.format(new Date()):sdf.format(d));
                map.put("iphoneDesc",desc);
                map.put("iphoneId",iphoneList.get(0).getId()+"");
                if(WorkStatusType.prototype.equals(iphoneList.get(0).getWorkStatus())){//原型的数据,不需要验证
                    map.put("iphverify","1");
                }else{
                    map.put("iphverify","0");
                }
            }else{
                map.put("iphoneAddress","");
                map.put("iphoneDesc","暂未发布");
                map.put("iphoneId","");
                map.put("iphverify","1");
            }
            if(ipadList.size()>0){
                String address=ipadList.get(0).getIosHttpsAddress();
                map.put("ipadAddress",address==null?"":address);
                Long d=ipadList.get(0).getCreateTime();
                String desc="Ver:"+ipadList.get(0).getNum()+"   Update:"+(d==null?sdf.format(new Date()):sdf.format(d));
                map.put("ipadDesc",desc);
                map.put("ipadId",ipadList.get(0).getId()+"");
                if(WorkStatusType.prototype.equals(ipadList.get(0).getWorkStatus())){//原型的数据,不需要验证
                    map.put("ipaverify","1");
                }else{
                    map.put("ipaverify","0");
                }
            }else{
                map.put("ipadAddress","");
                map.put("ipadDesc","暂未发布");
                map.put("ipadId","");
                map.put("ipaverify","1");
            }
            if(aphoneList.size()>0){
                String address=aphoneList.get(0).getAddress();
                map.put("androidPhoneAddress",address==null?"":address);
                Long d=aphoneList.get(0).getCreateTime();
                String desc="Ver:"+aphoneList.get(0).getNum()+"   Update:"+(d==null?sdf.format(new Date()):sdf.format(d));
                map.put("androidPhoneDesc",desc);
                map.put("androidId",aphoneList.get(0).getId()+"");
                if(WorkStatusType.prototype.equals(aphoneList.get(0).getWorkStatus())){//原型的数据,不需要验证
                    map.put("aphverify","1");
                }else{
                    map.put("aphverify","0");
                }
            }else{
                map.put("androidPhoneAddress","");
                map.put("androidPhoneDesc","暂未发布");
                map.put("androidId","");
                map.put("aphverify","1");
            }
            if(apadList.size()>0){
                String address=apadList.get(0).getAddress();
                map.put("androidPadAddress",address==null?"":address);
                Long d=apadList.get(0).getCreateTime();
                String desc="Ver:"+apadList.get(0).getNum()+"   Update:"+(d==null?sdf.format(apadList.get(0).getCreateTime()):sdf.format(d));
                map.put("androidPadDesc",desc);
                map.put("androidPadId",apadList.get(0).getId()+"");
                if(WorkStatusType.prototype.equals(ipadList.get(0).getWorkStatus())){//原型的数据,不需要验证
                    map.put("apaverify","1");
                }else{
                    map.put("apaverify","0");
                }
            }else{
                map.put("androidPadAddress","");
                map.put("androidPadDesc","暂未发布");
                map.put("androidPadId","");
                map.put("apaverify","1");
            }
            map.put("appTitle",knApplicationInfo.getTitle()==null?"":knApplicationInfo.getTitle());
            String qrcodeUrl=PathUtil.getRootPath();
            map.put("localUrl",localUrl);
            try{
                String qrcodeimg=Setting.qrcodeimg+"/"+createTime+".png", finqrcodeUrl=qrcodeUrl+qrcodeimg;//二维码图片路径
                File file=new File(finqrcodeUrl);
                if(!file.exists()){
                    file.mkdirs();
                }
                createQRCode(localUrl+knApplicationInfo.getDownLoadUrl(),finqrcodeUrl);
                //new TwoDimensionCode().encoderQRCode(localUrl+knApplicationInfo.getDownLoadUrl(),finqrcodeUrl,"png",7);
                map.put("qrcodeimg",GetImageOfBase64(qrcodeUrl+qrcodeimg));
            }catch(Exception e){
               log.error("生成二维码错误:，错误信息{}",e);
                map.put("qrcodeimg",GetImageOfBase64(qrcodeUrl+"/assets/global/img/default.jpg"));
            }
            //path :E:/myeclipse8.6_work/knd_xSimple/conf/freemarker
            String writeFile=qrcodeUrl+Setting.appDownLoadUrl+"/";
            //修改存放地址,以便用于使用短地址url访问
            //            String jsp=createTime+".jsp";
            Long id=knApplicationInfo.getId();
            File f=new File(writeFile);
            if(!f.exists()){
                f.mkdirs();
            }
            String version_download="version_download.ftl";
            if("1".equals(oRadio)){//默认 不需要验证
                version_download="/verOption/version_download_default.ftl";
            }else if("2".equals(oRadio)){// 需要验证用户名 以及密码
                version_download="/verOption/version_download_userAndPwd.ftl";
            }else if("3".equals(oRadio)){//验证码验证信息
                version_download="/verOption/version_download_numCode.ftl";
            }
            flag=FreeMarker.getInstance().saveToFile(version_download,map,qrcodeUrl+freemarker,writeFile+id+".jsp");
            if(flag){//成功更新applicationInfo的downLoadUrl数据
                log.info("jsp页面生成成功");
            }
            backMap.put("stat",flag);
            backMap.put("downLoadUrl",Setting.appViewsUrl+"/"+id);
        }catch(Exception e){
            backMap.put("stat",flag);
            log.error("jsp页面生成,错误信息，{}",e);
        }
        return backMap;
    }
    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param imgFile 待处理的图片
     *
     * @return 返回base64处理后的字符串
     */
    private String GetImageOfBase64(String imgFile){
        byte[] data=null;
        String base64Image="";
        try{
            InputStream in=new FileInputStream(imgFile);
            data=new byte[in.available()];
            in.read(data);
            in.close();
        }catch(IOException e){
        }
        //对字节数组Base64编码
        if(null!=data){
            BASE64Encoder encoder=new BASE64Encoder();
            base64Image=encoder.encode(data);//返回Base64编码过的字节数组字符串
        }
        return base64Image;
    }

    /**
     * 生成二维码(QRCode)图片
     *
     * @param content
     *            二维码图片的内容
     * @param imgPath
     *            生成二维码图片完整的路径
     */
    public static int createQRCode(String content, String imgPath            ) {
        try {
            Qrcode qrcodeHandler = new Qrcode();
            qrcodeHandler.setQrcodeErrorCorrect('M');
            qrcodeHandler.setQrcodeEncodeMode('B');
            qrcodeHandler.setQrcodeVersion(7);
            byte[] contentBytes = content.getBytes("gb2312");
            BufferedImage bufImg = new BufferedImage(140, 140,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D gs = bufImg.createGraphics();
            gs.setBackground(Color.WHITE);
            gs.clearRect(0, 0, 140, 140);
            gs.setColor(Color.BLACK);// 设定图像颜色 > BLACK
            int pixoff = 2;// 设置偏移量 不设置可能导致解析出错
            if (contentBytes.length > 0 && contentBytes.length < 120) {//输出内容二维码
                boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
                for (int i = 0; i < codeOut.length; i++) {
                    for (int j = 0; j < codeOut.length; j++) {
                        if (codeOut[j][i]) {
                            gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
                        }
                    }
                }
            } else {
                return -1;
            }

			/*Image img = ImageIO.read(new File(ccbPath));
			gs.drawImage(img, 44, 55, 49, 30, null);
			gs.dispose();
			bufImg.flush();*/
            File imgFile = new File(imgPath);
            ImageIO.write(bufImg,"png",imgFile);// 生成二维码QRCode图片
        } catch (Exception e) {
            return -100;
        }
        return 0;
    }
}
