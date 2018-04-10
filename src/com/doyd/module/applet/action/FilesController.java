package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IFilesService;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2018-1-13 下午2:41:26
 */
@Controller
@RequestMapping(value = {"applet"})
public class FilesController {

	@Autowired
	private IFilesService filesService;
	
	@RequestMapping(value="/getSign")
	@ResponseBody
	public ApiMessage getSign(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		return filesService.getSign(openId);
	}
	
	@RequestMapping(value="/getFileAddr")
	@ResponseBody
	public ApiMessage getFileAddr(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int fileId = StringUtil.parseInt(request.getParameter("fileId"));
		return filesService.getFileAddr(openId, fileId);
	}
}
