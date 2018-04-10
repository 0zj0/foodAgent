package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.core.action.common.Page;
import com.doyd.model.ShareFiles;

public interface IShareFilesDao {
	
	/**
	 * 获得共享文件
	 * @Title: getShareFilesById
	 * @param shareId
	 * @return ShareFiles
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午3:36:21
	 */
	public ShareFiles getShareFilesById(int shareId);
	
	/**
	 * 分页查询共享文件
	 * @Title: queryShareFilesByPage
	 * @param page
	 * @param groupId
	 * @param wuId
	 * @param key
	 * @param type
	 * @return List<Map<String,Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 上午11:03:43
	 */
	public List<Map<String, Object>> queryShareFilesByPage(Page page, int groupId, int wuId, String key, int type);
	public List<Map<String, Object>> queryShareFilesByPage(int page, int groupId, String key);
	
	/**
	 * 添加共享文件
	 * @Title: addShareFiles
	 * @param shareFiles
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 上午11:49:18
	 */
	public boolean addShareFiles(ShareFiles shareFiles);
	
	/**
	 * 删除/批量删除共享文件
	 * @Title: deleteShareFiles
	 * @param shareIds
	 * @param groupId
	 * @param flag
	 * @param wuId
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午2:47:44
	 */
	public boolean deleteShareFiles(int[] shareIds, int groupId, boolean flag, int wuId);
	
	/**
	 * 删除/批量删除共享文件
	 * @Title: deleteShareFiles
	 * @param shareIds
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午4:27:40
	 */
	public boolean deleteShareFiles(int[] shareIds);
	
	
	/**
	 * 修改共享文件（备注）
	 * @Title: updateShareFiles
	 * @param shareFiles
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 上午11:05:01
	 */
	public boolean updateShareFiles(ShareFiles shareFiles);
	
	/**
	 * 查询共享文件数量
	 * @Title: getShareFilesCnt
	 * @param groupId
	 * @param wuId
	 * @param type
	 * @return int
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:48:23
	 */
	public int getShareFilesCnt(int groupId, int wuId, int type);
	
	/**
	 * 批量查询共享文件
	 * @Title: getShareFilesList
	 * @param shareIds
	 * @return List<ShareFiles>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午3:47:24
	 */
	public List<ShareFiles> getShareFilesList(int[] shareIds);
	
	/**
	 * 
	 * @Title: getShareFilesList
	 * @param flag
	 * @param wuId
	 * @param shareIds
	 * @return List<ShareFiles>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午4:04:03
	 */
	public List<ShareFiles> getShareFilesList(boolean flag, int wuId, int[] shareIds);
}
