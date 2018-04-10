package com.doyd.biz;

import java.util.List;
import java.util.Map;

import com.doyd.model.Files;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;

public interface IFilesService {
	
	/**
	 * 查询附件
	 * @Title: getFiles
	 * @param table
	 * @param id
	 * @param storageTable
	 * @return List<Map<String,Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午1:34:36
	 */
	public List<Map<String, Object>> getFiles(String table, int id, String storageTable);
	
	/**
	 * 删除附件
	 * @Title: deleteFiles
	 * @param table
	 * @param id
	 * @param groupId
	 * @param storageTable
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午2:32:53
	 */
	public boolean deleteFiles(String table, int id, int groupId, String storageTable);
	
	/**
	 * 批量添加附件
	 * @Title: batchAddFiles
	 * @param fileList
	 * @param id
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 上午11:30:18
	 */
	public boolean batchAddFiles(List<Files> fileList, int id);
	
	/**
	 * 批量删除附件
	 * @Title: batchDeleteFiles
	 * @param table
	 * @param storageTable
	 * @param ids
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:28:03
	 */
	public boolean batchDeleteFiles(String table, String storageTable, List<Integer> ids);
	/**
	 * 获得上传签名
	 * 
	 * @param openId 用户openId
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-13 下午2:59:03
	 */
	public ApiMessage getSign(String openId);
	/**
	 * 获得文件最新的签名地址
	 * 
	 * @param openId 用户openId
	 * @param fileId 文件Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-13 下午4:14:52
	 */
	public ApiMessage getFileAddr(String openId, int fileId);
	
	/**
	 * 文件下载
	 * @Title: downloadFile
	 * @param weixinUser
	 * @param ip
	 * @param userAgent
	 * @param file
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-13 下午4:29:24
	 */
	public ApiMessage downloadFile(WeixinUser weixinUser, String ip, String userAgent, Files file, ReqCode reqCode);
	
	/**
	 * 文件预览
	 * @Title: previewFile
	 * @param ip
	 * @param userAgent
	 * @param basePath
	 * @param fileAddr
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-13 下午4:30:44
	 */
	public String previewFile(String ip, String userAgent, String basePath, String fileAddr, ReqCode reqCode);
	
	
	/**
	 * 获得文件
	 * @Title: getFileById
	 * @param fileId
	 * @return Files
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-13 下午4:23:23
	 */
	public Files getFileById(int fileId);
}
