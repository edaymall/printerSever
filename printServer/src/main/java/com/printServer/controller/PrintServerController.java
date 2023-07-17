package com.printServer.controller;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/print"})
public class PrintServerController {
	@RequestMapping(value={"/osse"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  String printServerInit(HttpServletRequest request, HttpServletResponse response)
	  {
		String data = request.getParameter("data");
	    String result = "0";
	    
	    System.out.println(data);
	    try
	    {
	      PrintService psZebra = null;
	      String sPrinterName = null;
	      PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
	      for (int i = 0; i < services.length; i++)
	      {
	        sPrinterName = services[i].getName();
	        if (sPrinterName.toLowerCase().indexOf("gx420d") >= 0 || sPrinterName.toLowerCase().indexOf("gk420t") >= 0)
	        {
	          psZebra = services[i];
	          break;
	        }
	      }
	      JSONParser jsonParser = new JSONParser();
	      
	      JSONObject jsonObject = (JSONObject)jsonParser.parse(data);
	      
	      JSONArray dataInfoArray = (JSONArray)jsonObject.get("datas");
	      
	      System.out.println("* datas *");
	      for (int i = 0; i < dataInfoArray.size(); i++)
	      {
	        System.out.println("=datas" + i + " ===========================================");
	        
	        JSONObject bookObject = (JSONObject)dataInfoArray.get(i);
	        
	        System.out.println("dataInfo: data==>" + bookObject.get("data"));
	        System.out.println("dataInfo: count==>" + bookObject.get("count"));
	        try
	        {
	          DocPrintJob job = psZebra.createPrintJob();
	          byte[] by = bookObject.get("data").toString().getBytes();
	          DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
	          Doc doc = new SimpleDoc(by, flavor, null);
	          job.print(doc, null);
	        }
	        catch (PrintException e)
	        {
	          result = "0-print연결후오류";
	          System.out.println("[JWLOG]PrintNetworkFail--" + result);
	          
	          e.printStackTrace();
	        }
	        catch (Exception e)
	        {
	          result = "0-print연결오류";
	          System.out.println("[JWLOG]PrintException--" + result);
	          e.printStackTrace();
	        }
	      }
	    }
	    catch (ParseException e)
	    {
	      System.out.println("[JWLog] result =" + result);
	      result = "0-데이터파싱중오류";
	      e.printStackTrace();
	    }
	    result = "1";
	    System.out.println("[JWLog] result =" + result);
	    System.out.println("프린터인쇄에 성공하였습니다.");
	    return result;
	  }
}
