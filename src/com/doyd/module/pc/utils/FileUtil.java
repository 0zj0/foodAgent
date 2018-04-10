/**
 * 
 */
package com.doyd.module.pc.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.doyd.core.action.common.ControllerContext;
import com.doyd.model.Files;
import com.doyd.util.FileUploadUtil;
import org.doyd.utils.DateUtil;
import org.doyd.utils.StringUtil;

/**
 * @author Administrator
 *
 */
public class FileUtil {
	
	private static String image = "jpg|png";
	private static String file="doc|docx|xls|xlsx|pdf|ppt|pptx|mp4";
	private static String compress = "jar|zip";
	
	public static Files createFile(int groupId, String fileAddr, String fileName, String storageTable) throws Exception{
		String fileAddr1 = FileUploadUtil.getPrivateFileUrl(fileAddr);
		URL url = new URL(fileAddr1);
		HttpURLConnection conn = (HttpURLConnection )url.openConnection();
		/*conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);*/
		Files file = new Files();
		file.setGroupId(groupId);
		file.setFileFormat(getFileFormat(fileAddr));
		file.setFileType(getFileType(file.getFileFormat()));
		file.setFileSize(conn.getContentLength());
		file.setFileAddr(fileAddr);
		file.setFileName(fileName);
		file.setStorageTable(storageTable);
		file.setCDate(StringUtil.parseInt(DateUtil.today().replaceAll("-", "")));
		return file;
	}
	
	public static String getFileFormat(String fileAddr){
		if(fileAddr.indexOf("?") > 0){
			fileAddr = fileAddr.substring(0, fileAddr.indexOf("?"));
		}
		return fileAddr.substring(fileAddr.lastIndexOf(".")+1).toLowerCase();
	}
	
	public static String getFileType(String fileFormat){
		if(fileFormat.matches(image)){
			return "image";
		}
		if(fileFormat.matches(file)){
			return "file";
		}
		if(fileFormat.matches(compress)){
			return "compress";
		}
		return null;
	}
	
	public static String getPictureName(){
		String baseStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<4; i++){
			sb.append("-");
			for (int j = 0; j < 6; j++) {
				sb.append(baseStr.charAt(new Random().nextInt(baseStr.length())));
			}
		}
		sb.deleteCharAt(0);
		return sb.toString();
	}
	
	public static String poiPPT03ToHtml(InputStream input, String basePath) throws Exception{
		String folderPath = ControllerContext.getBaseFilePath() + "/img/tmp";
		File folder = new File(folderPath);
        if(!folder.exists()){
        	folder.mkdirs();
        }
		HSLFSlideShow ppt = new HSLFSlideShow(input);
		input.close();
		Dimension pgsize = ppt.getPageSize();
		List<HSLFSlide> slides = ppt.getSlides();
		String imghtml="";
		for (int i=0;i<slides.size();i++) {
			HSLFSlide slide = slides.get(i);
			List<List<HSLFTextParagraph>> truns = slide.getTextParagraphs();
			for(int j=0;j<truns.size();j++){
				List<HSLFTextParagraph> rtruns = truns.get(j);
				for(int k=0;k<rtruns.size();k++){
					HSLFTextParagraph textParagraph = rtruns.get(k);
					List<HSLFTextRun> textRuns = textParagraph.getTextRuns();
					for(int l=0;l<textRuns.size();l++){
						textRuns.get(l).setFontIndex(1);
						textRuns.get(l).setFontFamily("宋体");
					}
				}
			}
			
			BufferedImage img = new BufferedImage(pgsize.width, pgsize.height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = img.createGraphics();
			graphics.setPaint(Color.BLUE);
			graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width,
					pgsize.height));
			slide.draw(graphics);
			String pictureName = FileUtil.getPictureName();
			String filePath = folderPath+"/" + pictureName + ".jpeg";
			File picFile = new File(filePath);
            if(!picFile.exists()){
            	try{
            		picFile.createNewFile();
            	}catch (Exception e) {
					e.printStackTrace();
				}
            }
			FileOutputStream out = new FileOutputStream(filePath); 
			ImageIO.write(img, "jpg", out);
			String imgs="img/tmp/" + pictureName + ".jpeg";    
			imghtml+="<img src=\'"+imgs+"\' style=\'width:1200px;height:830px;vertical-align:text-bottom;\'><br><br><br><br>";
		}
		String content = "<html><head><base href='"+basePath+"'><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>"
				+ imghtml + "</body></html>";
		return content;
	}
	
	public static String poiPPTToHtml(InputStream input, String basePath) throws Exception{
		String folderPath = ControllerContext.getBaseFilePath() + "/img/tmp";
		File folder = new File(folderPath);
        if(!folder.exists()){
        	folder.mkdirs();
        }
        Map<String,Object> map=ConvertPPTFileToImageUtil.converPPTtoImage(input, folderPath+"/");
        String imghtml="";
        boolean converReturnResult=(Boolean) map.get("converReturnResult");
        if(converReturnResult){//如果全部转换成功,则为true;如果有一张转换失败,则为fasle  
            @SuppressWarnings("unchecked")  
            List<String> imgNames=(List<String>) map.get("imgNames");  
            for (String imgName : imgNames) {  
                String img = "img/tmp/" + imgName;
                imghtml+="<img src=\'"+img+"\'><br><br><br><br>";
            }  
              
        }
        String content = "<html><head><base href='"+basePath+"'><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>"
				+ imghtml + "</body></html>";
		return content;
	}
	
	public static String poiPPTXToHtml(InputStream input, String basePath) throws Exception{
		String folderPath = ControllerContext.getBaseFilePath() + "/img/tmp";
		File folder = new File(folderPath);
        if(!folder.exists()){
        	folder.mkdirs();
        }
        Map<String,Object> map=ConvertPPTFileToImageUtil.converPPTXtoImage(input, folderPath+"/");
        String imghtml="";
        boolean converReturnResult=(Boolean) map.get("converReturnResult");
        if(converReturnResult){//如果全部转换成功,则为true;如果有一张转换失败,则为fasle  
            @SuppressWarnings("unchecked")  
            List<String> imgNames=(List<String>) map.get("imgNames");  
            for (String imgName : imgNames) {  
                String img = "img/tmp/" + imgName;
                imghtml+="<img src=\'"+img+"\'><br><br><br><br>";
            }  
              
        }
        String content = "<html><head><base href='"+basePath+"'><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>"
				+ imghtml + "</body></html>";
		return content;
	}
	
	public static String poiPPT07ToHtml(InputStream input, String basePath) throws Exception{
		String folderPath = ControllerContext.getBaseFilePath() + "/img/tmp";
		File folder = new File(folderPath);
        if(!folder.exists()){
        	folder.mkdirs();
        }
		XMLSlideShow ppt = new XMLSlideShow(input);
		input.close();
		Dimension pgsize = ppt.getPageSize();
		List<XSLFSlide> slides = ppt.getSlides();
		String imghtml="";
		for (int i=0;i<slides.size();i++) {
			XSLFSlide slide = slides.get(i);
			List<XSLFShape> shapes = slide.getShapes();
			for(int j=0;j<shapes.size();j++){
				XSLFShape shape = shapes.get(j);
				if(shape instanceof XSLFTextShape){
					XSLFTextShape tsh = (XSLFTextShape)shape; 
                       for(XSLFTextParagraph p : tsh){    
                           for(XSLFTextRun r : p){    
                               r.setFontFamily("宋体");    
                           }    
                       }    
				}
			}
			
			BufferedImage img = new BufferedImage(pgsize.width, pgsize.height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = img.createGraphics();
			
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			
			graphics.setPaint(Color.WHITE);
			graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width,
					pgsize.height));
			slide.draw(graphics);
			String pictureName = FileUtil.getPictureName();
			String filePath = folderPath+"/" + pictureName + ".jpeg";
			File picFile = new File(filePath);
            if(!picFile.exists()){
            	try{
            		picFile.createNewFile();
            	}catch (Exception e) {
					e.printStackTrace();
				}
            }
			FileOutputStream out = new FileOutputStream(filePath); 
			ImageIO.write(img, "jpg", out);
			String imgs="img/tmp/" + pictureName + ".jpeg";    
			imghtml+="<img src=\'"+imgs+"\' style=\'width:1200px;height:830px;vertical-align:text-bottom;\'><br><br><br><br>";
		}
		String content = "<html><head><base href='"+basePath+"'><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>"
				+ imghtml + "</body></html>";
		return content;
	}
	
	public static String poiWord03ToHtml(InputStream input, String basePath) throws Exception{
		String content = "";
		HWPFDocument wordDocument = new HWPFDocument(input);
		input.close();
		WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.newDocument());
		PicturesManager pm = new PicturesManager() {
			public String savePicture(byte[] content, PictureType pictureType,
					String suggestedName, float widthInches, float heightInches) {
				return "img/tmp/" + suggestedName;
			}
		};
		wordToHtmlConverter.setPicturesManager(pm);
		String folderPath = ControllerContext.getBaseFilePath() + "/img/tmp";
		File folder = new File(folderPath);
        if(!folder.exists()){
        	folder.mkdirs();
        }
		List<Picture> pics=wordDocument.getPicturesTable().getAllPictures();  
        if(pics!=null){  
            for(int i=0;i<pics.size();i++){  
                Picture pic = (Picture)pics.get(i);  
                try { 
                	String filePath = folderPath + "/" + pic.suggestFullFileName();
                	File picFile = new File(filePath);
                    if(!picFile.exists()){
                    	try{
                    		picFile.createNewFile();
                    	}catch (Exception e) {
        					e.printStackTrace();
        				}
                    }
                    pic.writeImageContent(new FileOutputStream(filePath));  
                } catch (FileNotFoundException e) {  
                    e.printStackTrace();  
                }    
            }  
        }  
		wordToHtmlConverter.processDocument(wordDocument);
		Document htmlDocument = wordToHtmlConverter.getDocument();
		//设置base路径
		Element base = htmlDocument.createElement("base");
		base.setAttribute("href", basePath);
		/*//添加javascript
		//1、引入jquery
		Element jquery = htmlDocument.createElement("script");
		jquery.setAttribute("src", "js/jquery.min.js");
		
		//2、添加自定义js
		Element js = htmlDocument.createElement("script");
		js.setAttribute("src", "js/preview.js");*/
		
		Element head = (Element) htmlDocument.getElementsByTagName("head").item(0);
		head.appendChild(base);
		//head.appendChild(jquery);
		//head.appendChild(js);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		DOMSource domSource = new DOMSource(htmlDocument);
		StreamResult streamResult = new StreamResult(outStream);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty(OutputKeys.METHOD, "html");
		serializer.transform(domSource, streamResult);
		outStream.close();
		content = new String(outStream.toByteArray()); 
		
		return content;
	}
	
	public static String poiWord07ToHtml(InputStream input, String basePath) throws Exception{
		XWPFDocument wordDocument = new XWPFDocument(input);
		input.close();
		XHTMLOptions options = XHTMLOptions.create(); 
		String folderPath = ControllerContext.getBaseFilePath() + "/img/tmp";
        // 存放图片的文件夹 
        options.setExtractor(new FileImageExtractor(new File(folderPath))); 
        // html中图片的路径 
        options.URIResolver(new BasicURIResolver("img/tmp"));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance(); 
        xhtmlConverter.convert(wordDocument, outStream, options); 
        String content = new String(outStream.toByteArray());
		content = content.replace("<head>", "<head><base href='"+basePath+"'>");
		//content = content.replace("</head>", "<script href='js/jquery.min.js'></script><script href='js/preview.js'></script></head>");
		content = content.replaceAll("\\\\l", "javascript:void(0);");
		return content;
	}
	
	public static String poiExcelToHtml(InputStream input) throws Exception{
		HSSFWorkbook excelBook = new HSSFWorkbook(input);
		input.close();
		ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.newDocument());
		excelToHtmlConverter.processWorkbook(excelBook);
		Document htmlDocument = excelToHtmlConverter.getDocument();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		DOMSource domSource = new DOMSource(htmlDocument);
		StreamResult streamResult = new StreamResult(outStream);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty(OutputKeys.METHOD, "html");
		serializer.transform(domSource, streamResult);
		outStream.close();
		String content = new String(outStream.toByteArray());
		return content;
	}

}
