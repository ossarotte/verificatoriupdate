/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tn.provincia.osservatorio.verificatoriupdate;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVParser;
//import org.apache.commons.csv.CSVRecord;
//import au.com.bytecode.opencsv.CSV;

/**
 *
 * @author pr41103
 */
public class provacsv {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        //    ;(?=([^\"]*\"[^\"]*\")*[^\"]*$) regular expression con ; e " per stringhe;
        //    ;(?![^"].*";)/
        String regularcsv=";(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
        String regularcsv2=";(?![^\"].*\";)";
        Pattern regex = Pattern.compile(";(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        Pattern regex2= Pattern.compile(";(?![^\"].*\";)");
        /*CSV csv = CSV
                .separator(';')
                .quote('"')
                .lineEnd("\r\n")
                .charset("UTF-8")
                .create();
        CSVFormat csv2 = CSVFormat.DEFAULT;
        csv2.withDelimiter(';');
        csv2.withQuote('"');*/
        
        String strcsv="campo1;campo2;\"Campo3;campo3.1;campo3.2;Campo3.4\";campo4;campo5 ppp";
        String strcsv2="\"qualche stringa\";\"un'altra; stringa\" con interno\";stringa normale;\"altra stringa\"";
        String strcsv3="\"qualche stringa\";\"un'altra\"; stringa con interno\";stringa normale;\"altra stringa\"";
        String strcsv4="prova1;prova2;\"prova3; ;prova3.1\"";
        
        ArrayList <Object> campi= new ArrayList<>();
        Integer lungcsv = strcsv.length();
        
        String[]prova =regex.split(strcsv);
        String[]prova2 =regex.split(strcsv2);
        String[]prova3 =regex.split(strcsv3);
        String[]prova4 =regex.split(strcsv4);
        
        String[]prova_1 =regex2.split(strcsv);
        String[]prova2_1 =regex2.split(strcsv2);
        String[]prova3_1 =regex2.split(strcsv3);
        String[]prova4_1 =regex2.split(strcsv4);
        //csv.read(strcsv, null);
         
        /*CSVParser parser = CSVParser.parse(strcsv4, csv2);
        List<CSVRecord> list = parser.getRecords();*/
        System.out.println("prova");
        
        
        
        
        
    }
    
}
