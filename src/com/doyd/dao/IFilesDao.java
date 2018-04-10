package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.model.Files;

public interface IFilesDao {
	
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
	 * 获得文件
	 * @Title: getFileById
	 * @param fileId
	 * @return Files
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-13 下午4:23:23
	 */
	public Files getFileById(int fileId);
	
}
