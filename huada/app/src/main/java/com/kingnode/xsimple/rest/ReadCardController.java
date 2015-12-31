package com.kingnode.xsimple.rest;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kingnode.xsimple.util.key.UuidMaker;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
/**
 *  根据图片读取名片信息
 * @author caichune@kingnode.com (cici)
 */
@RestController @RequestMapping({"/api/v1/card"})
public class ReadCardController{
    //请求访问地址
    private String RequestURL = "http://bcr2.intsig.net/BCRService/BCR_VCF2?PIN=1234567&user=liuzhengwei@kingnode.com&pass=AA4GKNYMQY377A8L&lang=15&size=163";

    /**
     * 根据前端传输的流信息获取去二维码扫描云获取到相应的信息进行返回
     * @return
     */
    @RequestMapping(value="read",method={RequestMethod.POST})
    public Map read(HttpServletResponse response,HttpServletRequest request) throws IOException, FileUploadException{
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(4096);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1024 * 1024 * 1024);
        List<FileItem> items = upload.parseRequest(request);
        Map map = new HashMap();
        for(FileItem  item : items){
            InputStream ins = item.getInputStream();
            map.putAll(getMapValueByConn(ins));
        }
        return map;

    }
    /**
     * 根据输入流获取名片里面的信息
     * @param ins
     * @return
     */
    private Map getMapValueByConn( InputStream ins){
        Map map = new HashMap();
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        String BOUNDARY =UuidMaker.getInstance().getUuid(false);
        String PREFIX = "--", LINE_END = "\r\n";
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            sb.append("Content-Disposition: form-data; name=\" fileName0 \"; filename=\"" + "vcard.png" + "\"" + LINE_END);
            sb.append("Content-Type:application/octet-stream; charset=" + "utf-8" + LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = ins.read(bytes)) != -1) {
                dos.write(bytes, 0, len);
            }
            ins.close();
            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            // 获取响应码 200=成功 当响应成功，获取响应的流
            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder resultXml = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    resultXml.append(line);
                    resultXml.append("\n"); // 换行
                }
                String result = resultXml.toString();
                String[] vCard = result.split("\n");
                String isRn = "";
                for (int i = 0; i < vCard.length; i++) {
                    if (vCard[i].contains("X-IS-INFO")) {
                        String firstNum = vCard[i].substring(vCard[i].indexOf(";") + 1);
                        String lastNum = firstNum.substring(0, firstNum.indexOf(","));
                        String keyName = getKey(lastNum);
                        String firstStr = vCard[i - 1].substring(vCard[i - 1].indexOf(":") + 1).replaceAll(";", "");
                        if (isRn.equals(lastNum)) {
                            //                                json.put(keyName, json.getString(keyName) + "()" + firstStr);
                            map.put(keyName,map.containsKey(keyName)? map.get(keyName):"" + "()" + firstStr);
                        } else {
                            //                                json.put(keyName, firstStr);
                            map.put(keyName, firstStr);
                            isRn = lastNum;
                        }

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private  String getKey(String lastNum) {
        String keyName = ""; // 名片标题
        int num = Integer.parseInt(lastNum);
        switch (num) {
        case 0:
            keyName = "name";
            break;
        case 1:
            keyName = "姓";
            break;
        case 2:
            keyName = "名";
            break;
        case 3:
            keyName = "tel";
            break;
        case 4:
            keyName = "家庭电话";
            break;
        case 5:
            keyName = "fax";
            break;
        case 6:
            keyName = "mobile";
            break;
        case 7:
            keyName = "email";
            break;
        case 8:
            keyName = "site";
            break;
        case 9:
            keyName = "duty";
            break;
        case 10:
            keyName = "company";
            break;
        case 11:
            keyName = "address";
            break;
        case 12:
            keyName = "邮政编码";
            break;
        case 13:
            keyName = "备注";
            break;
        case 14:
            keyName = "即时消息";
            break;
        case 15:
            keyName = "dept";
            break;
        case 16:
            keyName = "昵称";
            break;
        default:
            break;
        }
        return keyName;
    }
}
