/**
 * 
 */
package com.doyd.module.pc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IFilesService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.model.Files;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import com.doyd.util.FileUploadUtil;
import com.doyd.util.PapersException;
import org.doyd.utils.StringUtil;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = {"pc"})
public class PCFileController {
	
	@Autowired
	private IWeixinUserService weixinUserService;
	@Autowired
	private IFilesService filesService;
	
	@RequestMapping(value="/files/getSign")
	@ResponseBody
	public ApiMessage getSign(HttpServletRequest request){
		ReqCode reqCode = ReqCode.GetSign;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		return filesService.getSign(weixinUser.getOpenId());
	}
	
	@RequestMapping(value="/files/download",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ApiMessage download(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.DownLoad;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		int fileId = StringUtil.parseInt(request.getParameter("fileId"));
		if(fileId<=0){
			return new ApiMessage(reqCode, ReqState.LackFileId);
		}
		Files file = filesService.getFileById(fileId);
		if(file==null){
			return new ApiMessage(reqCode, ReqState.NoExistFile);
		}
		String ip = ControllerContext.getIp(request);
		String userAgent = request.getHeader("user-agent");
		String fileAddr = "";
		try {
			fileAddr = FileUploadUtil.getPrivateFileUrl(file.getFileAddr());
			fileAddr += "&download=true&fileName="+file.getFileName();
		} catch (PapersException e) {
			e.printStackTrace();
		}
		return filesService.downloadFile(weixinUser, ip, userAgent, file, reqCode).setInfo(fileAddr);
	}
	
	@RequestMapping(value="/files/preview",method={RequestMethod.GET,RequestMethod.POST})
	public void preview(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.Preview;
		String ip = ControllerContext.getIp(request);
		String userAgent = request.getHeader("user-agent");
		String basePath = ControllerContext.getBasePath(request);
		String fileAddr = StringUtil.trim(request.getParameter("fileAddr"));
		String content = "文件地址为空";
		if(StringUtil.isNotEmpty(fileAddr)){
			content = filesService.previewFile(ip, userAgent, basePath, fileAddr, reqCode);
		}
		ControllerContext.print(response, content, "text/html;charset=GB2312");
	}
	
}
