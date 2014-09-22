/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tn.provincia.osservatorio.verificatoriupdate;


import java.io.*;
import java.net.*;
import java.net.HttpURLConnection;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

//import net.lingala.zip4j.core.ZipFile;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONValue;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;


/**
 *
 * @author pr41103
 */
public class connectrest {
    public static String filename="";
    public static Connection conn=null;
    public static Boolean debug=false;
    
    private static Map toMapMap (Object[] keys, Object[] values, Object[] typeof){
        int keysSize = (keys != null) ? keys.length : 0;
        int valuesSize = (values != null) ? values.length : 0;
        int typeofSize = (typeof != null) ? typeof.length : 0;

        if (keysSize == 0 && valuesSize == 0 && typeofSize == 0) {
            // return mutable map
            return new HashMap();
        } 

        if (keysSize != valuesSize || keysSize!=typeofSize) {
          throw new IllegalArgumentException("The number of keys doesn't match the number of values.");
        }

        Map map = new HashMap();
        Map intmap= new HashMap();
        for (int i = 0; i < keysSize; i++) {
            intmap.put("value",values[i]);
            intmap.put("type",typeof[i]);
            map.put(keys[i], intmap);
        }
        return map;
    }
    
    private static Map toMap(Object[] keys, Object[] values) {
        int keysSize = (keys != null) ? keys.length : 0;
        int valuesSize = (values != null) ? values.length : 0;

        if (keysSize == 0 && valuesSize == 0) {
            // return mutable map
            return new HashMap();
        } 

        if (keysSize != valuesSize) {
            
          throw new IllegalArgumentException("The number of keys doesn't match the number of values.");
        }

        Map map = new HashMap();
        for (int i = 0; i < keysSize; i++) {
            map.put(keys[i], values[i]);
        }

        return map;
    }
    
    private static String dataora (){
        Date now=new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat ("yyyyMMddHHmm");        
        String dataora = dateformat.format(now);
        return dataora;
    }
    
    private static void bakFile(String source) throws IOException{
        String tmpnome= source.substring(0, source.lastIndexOf("."));
        String tmpest= source.substring(source.lastIndexOf("."));
        String destination =tmpnome +"_"+ dataora() + tmpest;
        
        File src = new File(source);
        File dest = new File(destination);
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(src).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (FileNotFoundException ex) {
        Logger.getLogger(connectrest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(connectrest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    private static Connection  conndb(String filename){
        
        Connection conn = null;
        if (conn == null){
            try {
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                String conStr = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ="+filename;
                conn = DriverManager.getConnection(conStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
    
    private static void deltab(String tab){
        Statement delStmt = null;
        try {
            delStmt = conn.createStatement();
            String Sql= "DELETE * FROM "+tab;
            delStmt.execute(Sql);
        } catch (Exception e) {
             e.printStackTrace();
        }finally{
            try {
                delStmt.close();
                //connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        
    }
    
    private static void insdb(String tab,Map Recordset,Map typeofs){
        String numimp =Recordset.get("indice").toString();
        PreparedStatement  insStmt = null;
        List<String> tmpvalue= new ArrayList<>();
        List<String> tmptype=new ArrayList<>();
        List<String> tmpkey=new ArrayList<>();
        
        try {    
            String Sql= "INSERT INTO "+tab+"\n";
            String campi = "(";
            String valori = "(";
            Iterator iterator = Recordset.entrySet().iterator();
            int i = 0;
            
            while (iterator.hasNext()){
                
                Map.Entry mapEntry = (Map.Entry) iterator.next();
                String key=mapEntry.getKey().toString();
                String value=mapEntry.getValue().toString();
                
                String pattern ="/\\s{2,}/g";
                value =value.replaceAll(pattern," ").trim();
                String typeof=typeofs.get(key).toString();
                
                
                if (!value.equals("")){
                    if (i > 0){
                        campi += ",\n";
                        valori += ",\n";
                    }
                    campi+=key;
                    tmpvalue.add(i, value);
                    valori+="?";
                    tmptype.add(i, typeof);
                    i++;
                }
                //Sql+=key+"="+value;
                
                //System.out.println(key+"="+value);
            }
            campi+=")\n";
            valori+=")\n";
            Sql =Sql+campi+" VALUES \n"+valori;
            //System.out.println(Sql);
            insStmt = conn.prepareStatement(Sql);
            String type="";
            if ((tmpvalue!=null||tmptype!=null)&&(tmpvalue.size()==tmptype.size())){
                for (i=0; i< tmpvalue.size(); i++){
                    String value=tmpvalue.get(i);
                    type = tmptype.get(i);
                    switch(tmptype.get(i)){
                        case "1" :  insStmt.setInt(i+1, Integer.parseInt(value));//intero quindi lascio così
                                    break;
                        case "2" :  insStmt.setString(i+1, value);
                                    break;
                        case "3" :  DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                                    Date mindate= new SimpleDateFormat("yyyy-MM-dd").parse("1900-01-01");
                                    if (date.after(mindate)){
                                        insStmt.setDate(i+1, new java.sql.Date(date.getTime()));
                                    } else {
                                        insStmt.setDate(i+1, new java.sql.Date(mindate.getTime()));
                                    }
                                     //insStmt.setString(1, "#"+value+"#");
                                    break;
                        case "4" :  insStmt.setBoolean(i+1, Boolean.getBoolean(value));//boolean lascio perchè sempre vuoto
                                    break;
                        default :   insStmt.setString(i+1, value);
                                    break;
                    }
                }               
            }
            insStmt.executeUpdate();
            insStmt.close();
            /*while (iterator.hasNext()){
                
                Map.Entry mapEntry = (Map.Entry) iterator.next();
                String key=mapEntry.getKey().toString();
                String value=mapEntry.getValue().toString();
                
                String pattern ="/\\s{2,}/g";
                value =value.replaceAll(pattern," ").trim();
                String typeof=typeofs.get(key).toString();
                
                
                if (!value.equals("")){
                    insStmt = conn.prepareStatement(Sql+"("+key+")VALUES(?)");
                    switch(typeof){
                        case "1" : insStmt.setInt(1, Integer.parseInt(value));//intero quindi lascio così
                            break;
                        case "2" : insStmt.setString(1, value);
                            break;
                        case "3" : Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                            insStmt.setDate(1, new java.sql.Date(date.getTime()));
                            //insStmt.setString(1, "#"+value+"#");
                            break;
                        case "4" : insStmt.setBoolean(1, Boolean.getBoolean(value));//boolean lascio perchè sempre vuoto
                            break;
                        default : insStmt.setString(1, value);
                            break;
                    }
                    insStmt.executeUpdate();
                    insStmt.close();
                }
                    
                }*/
        } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
                System.out.println("errore impianto "+numimp);
        }     
    }
    private static String sqlescaping(String campo){
        campo=campo.replaceAll("\'", "\\'");
        campo=campo.replaceAll("\"", "\\\"");
        String pattern ="/\\s{2,}/g";
        campo =campo.replaceAll(pattern," ");
        return campo;
    }
    private static Map parsecsv(Object[]key,Object[]value){
        return toMap(key,value);
    }
    
    private static Map parsecsv (String key,String value, String delimiter){
        return parsecsv (key,value,"",delimiter);
    }
    
    private static Map parsecsv (String key,String value,String typeof, String delimiter){
       String regexpat=";(?=([^\"+]*\"[^\"]*\")*[^\"]*$)\"";
       Pattern regex = Pattern.compile(";(?=([^\"+]*\"[^\"]*\")*[^\"]*$)");
       String[] keys = regex.split(key,-1);
       String[] values=regex.split(value,-1);
       if (typeof==""){
           return toMap(keys,values);
       }
       String[] typeofs= regex.split(typeof,-1);
       return toMapMap(keys,values,typeofs);
    }
    
    private static void parsezip (InputStream is){
        try {
            ZipInputStream zis = new ZipInputStream(is);
            //is.close();
            ZipEntry entry;
            // while there are entries I process them
            while ((entry = zis.getNextEntry()) != null)
            {
                String filename = entry.getName();
                System.out.println("entry: " + entry.getName() + ", " + entry.getSize());
                String impins=filename.substring(0, filename.indexOf("_"));
                System.out.println (impins);
                // consume all the data from this entry
                int read = 0;
                byte[] bytes= new byte [4096];
                //saving to file
                /*outputStream = new FileOutputStream(new File(entry.getName()));
                while ((read = zis.read(bytes)) != -1){
                
                outputStream.write(bytes, 0,read);

                }
                outputStream.close();*/
                //salvarlo in un input stream
                
                ByteArrayOutputStream csvos = new ByteArrayOutputStream();
                while ((read = zis.read(bytes)) != -1) {
                    csvos.write(bytes, 0, read);
                }
                csvos.flush();
                InputStreamReader csvis = new InputStreamReader(new ByteArrayInputStream(csvos.toByteArray()));
                BufferedReader csvbr = new BufferedReader(csvis);
                String key = csvbr.readLine();
                String[] keys=key.split(";", -1);
                String typeof="1;1;2;2;2;3;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;3;2;2;2;2;2;2;3;2;4";
                String csvline;
                Map typeofs=parsecsv(key,typeof,";");
                int i=0;
                while ((csvline = csvbr.readLine()) != null) {
                    Pattern regex = Pattern.compile(";(?=([^\"+]*\"[^\"]*\")*[^\"]*$)");
       
                    String[] values=regex.split(csvline,-1);
                    //String[] values= csvline.split(";",-1);
                    while (values.length<keys.length){
                        csvline+=csvbr.readLine();
                        values=regex.split(csvline,-1);
                    }
                    
                    //if (values[0].toString().equals("100346")){
                    //    System.out.println(csvline);
                    
                        insdb("1_15000",parsecsv(keys,values),typeofs);  
                        
                    //}
                    
                    i++;
                    if (i>2){
                        //break;
                    }
                    
                }
                System.out.println(i+" impianti inseriti");
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
    
    private static ByteArrayOutputStream copyis (InputStream is){
        ByteArrayOutputStream baos = null;
        try {
            //coping InputStream
            baos = new ByteArrayOutputStream();
            
            // Fake code simulating the copy
            // You can generally do better with nio if you need...
            // And please, unlike me, do something about the Exceptions :D
            byte[] buffer = new byte[1024];
            Integer len;
            while ((len = is.read(buffer)) > -1 ) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            is.close();
            //tmpis = new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
                e.printStackTrace();
        }
        return baos;
    }
    
    private static boolean Savefile (String filename,InputStream is) {
        try {
            //saving zip file
            OutputStream outputStream = new FileOutputStream(new File(filename));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();
            is.close();
            return true;
        } catch (Exception e) {
                e.printStackTrace();
                return false;
        }
    }
    public static void base(){
        OutputStream outputStream = null;
        //String rest ="http://localhost/rest_3/example/impianti/csv";
        String rest="http://osservatorio.energia.provincia.tn.it/rest/impianti/zip";
        //String username = "yy777PPP";
        //String password = "yy777PPP";
        //String userpass = "";
        try {
            URL url = new URL(rest);
            HttpURLConnection uc = (HttpURLConnection)url.openConnection();
            //userpass = username + ":" + password;
            //String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            System.out.println("sending request...");
            uc.setRequestMethod("GET");
            uc.setAllowUserInteraction(false);
            uc.setDoOutput(true);
            uc.setRequestProperty("Content-type", "text/xml");
            //uc.setRequestProperty( "Accept", "text/xml" );
            //uc.setRequestProperty ("Authorization", basicAuth);
            System.out.println(uc.getRequestProperties());
            int rspCode = uc.getResponseCode();
            String Content = uc.getContentType();
            System.out.println (rspCode);
            System.out.println(Content);
            
            if (rspCode == 200) {
                InputStream is = uc.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                if (Content.equals("application/zip")){
                    System.out.println("zip file");
                }
                
                // Open new InputStreams using the recorded bytes
                // Can be repeated as many times as you wish
                ByteArrayOutputStream baos =copyis(is);
                InputStream is1 = new ByteArrayInputStream (baos.toByteArray());
                InputStream is2 = new ByteArrayInputStream (baos.toByteArray());
                
                Savefile("Scarico.zip",is1);
                                
                parsezip(is2);
            }

        } catch (Exception e) {
             e.printStackTrace();   
        } 
}

        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            filename="C:\\MioSviluppo\\verificatoriupdate\\controlli.mdb";
            conn =conndb(filename);
            debug =true;
            
            //bakFile(filename);
            
            deltab("1_15000");
            
            base();
            
            //InputStream is = new FileInputStream ("Scarico.zip");
            //parsezip(is);
            //is.close();  
            
            conn.close();
        }  catch (Exception e) {
                    e.printStackTrace();
        }
    }
}
