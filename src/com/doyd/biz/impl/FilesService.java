package com.doyd.biz.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.doyd.ip.IP;
import org.doyd.ip.IpUtil;
import org.doyd.utils.DateUtil;
import org.doyd.utils.EncryptUtils;
import org.doyd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.biz.IFilesService;
import com.doyd.cache.memory.SysCache;
import com.doyd.core.convertor.AppConfig;
import com.doyd.dao.IDownRecordDao;
import com.doyd.dao.IFilesDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.DownRecord;
import com.doyd.model.Files;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.utils.FileUtil;
import com.doyd.module.pc.utils.POIReadExcelToHtml;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import com.doyd.util.FileUploadUtil;
import com.doyd.util.PapersException;

@Service
public class FilesService implements IFilesService {
	@Autowired
	private IFilesDao filesDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IDownRecordDao downRecordDao;
	@Autowired
	private IUserGroupDao userGroupDao;

	@Override
	public boolean deleteFiles(String table, int id, int groupId,
			String storageTable) {
		return filesDao.deleteFiles(table, id, groupId, storageTable);
	}

	@Override
	public List<Map<String, Object>> getFiles(String table, int id,
			String storageTable) {
		List<Map<String, Object>> files = filesDao.getFiles(table, id, storageTable);
		if(files != null && files.size() > 0){
			for (Map<String, Object> map2 : files) {
				String fileAddr = (String) map2.get("fileAddr");
				if(StringUtil.isNotEmpty(fileAddr)){
					try {
						map2.put("fileAddr", FileUploadUtil.getPrivateFileUrl(fileAddr));
					} catch (PapersException e) {
					}
				}
			}
		}
		return files;
	}

	@Override
	public boolean batchAddFiles(List<Files> fileList, int id) {
		return filesDao.batchAddFiles(fileList, id);
	}

	@Override
	public boolean batchDeleteFiles(String table, String storageTable,
			List<Integer> ids) {
		return filesDao.batchDeleteFiles(table, storageTable, ids);
	}

	@Override
	public ApiMessage getSign(String openId) {
		if(StringUtil.isEmpty(openId)){
			return new ApiMessage(ReqCode.GetSign, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.GetSign, ReqState.NoExistUser);
		}
		AppConfig config = SysCache.getCache().getAppConfig();
		long timestamp  = System.currentTimeMillis();
		String sign = EncryptUtils.md5Hex(config.getPapersAppId()+config.getPapersSecret()+timestamp);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("timestamp", timestamp);
		resultMap.put("appId", config.getPapersAppId());
		resultMap.put("sign", sign);
		return new ApiMessage(ReqCode.GetSign, ReqState.Success).setInfo(resultMap);
	}

	@Override
	public ApiMessage downloadFile(WeixinUser weixinUser, String ip, String userAgent, Files file,
			ReqCode reqCode) {
		String fileAddr = file.getFileAddr();
		try{
			fileAddr = FileUploadUtil.getPrivateFileUrl(fileAddr);
		}catch (Exception e) {
			e.printStackTrace();
			return new ApiMessage(reqCode, ReqState.Failure);
		}
		
		IP IP = IpUtil.getIp(ip);
		String city = IP.getCity();
		String province = IP.getProvince();
		
		DownRecord record = new DownRecord();
		record.setFileId(file.getFileId());
		record.setState(1);//无法获取下载状态，默认成功
		record.setWuId(weixinUser.getWuId());
		record.setIp(ip);
		record.setCity(city);
		record.setProvince(province);
		record.setUserAgent(userAgent);
		record.setDownDate(StringUtil.parseInt(DateUtil.today().replaceAll("-", "")));
		record.setDownHour(DateUtil.getHour());
		record.setCtime(DateUtil.now());
		if(!downRecordDao.addRecord(record)){
			return new ApiMessage(reqCode, ReqState.Failure);
		}
		return new ApiMessage(reqCode, ReqState.Success).setInfo(fileAddr);
	}

	@Override
	public String previewFile(String ip, String userAgent, String basePath, 
			String fileAddr, ReqCode reqCode) {
		try {
			fileAddr = FileUploadUtil.getPrivateFileUrl(fileAddr);
			URL url = new URL(fileAddr);
			HttpURLConnection conn = (HttpURLConnection )url.openConnection();
			String fileFormat = FileUtil.getFileFormat(fileAddr);
			String content = "";
			switch (fileFormat) {
			case "doc":
				content = FileUtil.poiWord03ToHtml(conn.getInputStream(), basePath);
				break;
			case "docx":
				content = FileUtil.poiWord07ToHtml(conn.getInputStream(), basePath);
				break;
			case "xls":
			case "xlsx":
				//content = poiExcelToHtml(conn.getInputStream());
				content = POIReadExcelToHtml.readExcelToHtml(conn.getInputStream(), true);
				break;
			case "ppt":
				content = FileUtil.poiPPTToHtml(conn.getInputStream(), basePath);
				break;
			case "pptx":
				content = FileUtil.poiPPTXToHtml(conn.getInputStream(), basePath);
				break;
			default:
				/*ApiMessage msg = new ApiMessage(ReqCode.Preview, ReqState.NoSupport);
				content = msg.toString();*/
				content = "不支持该文件的预览";
				break;
			}
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return "文件预览失败";
		}
	}

	@Override
	public ApiMessage getFileAddr(String openId, int fileId) {
		if(StringUtil.isEmpty(openId) || fileId <= 0){
			return new ApiMessage(ReqCode.GetFileAddr, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.GetFileAddr, ReqState.NoExistUser);
		}
		Files file = filesDao.getFileById(fileId);
		if(file == null){
			return new ApiMessage(ReqCode.GetFileAddr, ReqState.NoExistFile);
		}
		String fileAddr = null;
		//用户不在作业所在班群
		if(!userGroupDao.existUserGroup(wu.getWuId(), file.getGroupId(), 0)){
			return new ApiMessage(ReqCode.GetFileAddr, ReqState.NoPower);
		}
		if(StringUtil.isNotEmpty(file.getFileAddr())){
			try {
				fileAddr = FileUploadUtil.getPrivateFileUrl(file.getFileAddr());
			} catch (PapersException e) {
			}
		}
		return new ApiMessage(ReqCode.GetFileAddr, ReqState.Success).setInfo(fileAddr);
	}

	@Override
	public Files getFileById(int fileId) {
		return filesDao.getFileById(fileId);
	}

}
