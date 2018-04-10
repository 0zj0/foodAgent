package com.doyd.core.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.Statement;

@Repository
public class DaoSupport {
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbc;
	
	protected NamedParameterJdbcTemplate getJdbc(){
		return namedParameterJdbc;
	}
	
	public <T> T queryForObject(String sql, Object[] params, RowMapper<T> rowMapper){
		try{
			List<T> list = getJdbc().getJdbcOperations().query(sql, params, rowMapper);
			if(list==null || list.size()<1){
				return null;
			}else{
				return list.get(0);
			}
		}catch (Exception e) {
			error(e, sql, params);
			return null;
		}
	}
	
	public int update(final String sql, final Object... params){
		try{
			return getJdbc().getJdbcOperations().update(sql, params);
		}catch (Exception e) {
			error(e, sql, params);
			return -1;
		}
	}
	
	public int[] buntchInsert(final String sql, final Object... params){
		try{
			String[] sqls = new String[]{sql};
			return getJdbc().getJdbcOperations().batchUpdate(sqls);
		}catch (Exception e) {
			error(e, sql, params);
			return null;
		}
	}
	
	
	public int insert(final String sql, final Object... params){
		try{
			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbc().getJdbcOperations().update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					if (params != null && params.length > 0) {
						for (int i = 0; i < params.length; i++) {
							pstmt.setObject(i + 1, params[i]);
						}
					}
					return pstmt;
				}
			}, keyHolder);
			return keyHolder.getKey().intValue();
		}catch (Exception e) {
			error(e, sql, params);
			return -1;
		}
	}
	
	public String queryForString(String sql, Object... params){
		try{
			SqlRowSet rs = getJdbc().getJdbcOperations().queryForRowSet(sql, params);
			if(rs.first()){
				String str = rs.getString(1);
				return str;
			}
			return null;
		}catch (Exception e) {
			error(e, sql, params);
			return null;
		}
	}
	
	public int queryForInt(String sql, Object... params){
		try{
			SqlRowSet rs = getJdbc().getJdbcOperations().queryForRowSet(sql, params);
			if(rs.first()){
				int value = rs.getInt(1);
				return value;
			}
			return 0;
		}catch (Exception e) {
			error(e, sql, params);
			return -1;
		}
	}
	
	public boolean queryForExist(String sql, Object[] params){
		try{
			SqlRowSet rs = getJdbc().getJdbcOperations().queryForRowSet(sql, params);
			if(rs.first()){
				return true;
			}
			return false;
		}catch (Exception e) {
			error(e, sql, params);
			return false;
		}
	}
	public <T> List<T> query(String sql, Object[] params, RowMapper<T> rowMapper){
		try{
			return getJdbc().getJdbcOperations().query(sql, params, rowMapper);
		}catch (Exception e) {
			error(e, sql, params);
			return null;
		}
	}
	
	public List<Map<String, Object>> queryForList(String sql, Object... params){
		try{
			List<Map<String, Object>> list = getJdbc().getJdbcOperations().queryForList(sql, params);
			return list;
		}catch (Exception e) {
			error(e, sql, params);
			return null;
		}
	}
	/*
	 * 查询一列String类型的数据，以列表形式返回
	 * author ww
	 * 2014-10-23
	 */
	public List<String> queryForStringList(String sql, Object... params){
		try{
			SqlRowSet rs = getJdbc().getJdbcOperations().queryForRowSet(sql, params);
			List<String> list = new ArrayList<String>();
			while(rs.next()){
				list.add(rs.getString(1));
			}
			return list;
		}catch (Exception e) {
			error(e, sql, params);
			return null;
		}
	}
	
	/*
	 * 查询一列Integer类型的数据，以列表形式返回
	 * author ww
	 * 2014-10-23
	 */
	public List<Integer> queryForIntList(String sql, Object... params){
		try{
			SqlRowSet rs = getJdbc().getJdbcOperations().queryForRowSet(sql, params);
			List<Integer> list = new ArrayList<Integer>();
			while(rs.next()){
				list.add(rs.getInt(1));
			}
			return list;
		}catch (Exception e) {
			error(e, sql, params);
			return null;
		}
	}
	
	public Map<String, Object> queryForMap(String sql, Object... params){
		try{
			List<Map<String, Object>> list = getJdbc().getJdbcOperations().queryForList(sql, params);
			if(list==null || list.size()<=0){
				return null;
			}else{
				return list.get(0);
			}
		}catch (Exception e) {
			error(e, sql, params);
			return null;
		}
	}
	
	public <T> T call(final String sql, final Object[] params, final RowMapper<T> mapper){
		T t = getJdbc().getJdbcOperations().execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection conn) throws SQLException {
				CallableStatement stmt = conn.prepareCall(sql);
				if (params != null && params.length > 0) {
					for (int i = 0; i < params.length; i++) {
						stmt.setObject(i + 1, params[i]);
					}
				}
				return stmt;
			}
			
		}, new CallableStatementCallback<T>() {
			@Override
			public T doInCallableStatement(CallableStatement stmt) throws SQLException, DataAccessException {
				ResultSet rs = stmt.getResultSet();
				int rowNum = 0;
				if(rs.getRow()<=0){
					return null;
				}
				if(rs.next()){
					T t = mapper.mapRow(rs, rowNum++);
					return t;
				}
				return null;
			}
		});
		return t;
	}
	
	public <T> List<T> callList(final String sql, final Object[] params, final RowMapper<T> mapper){
		List<T> list = getJdbc().getJdbcOperations().execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection conn) throws SQLException {
				CallableStatement stmt = conn.prepareCall(sql);
				if (params != null && params.length > 0) {
					for (int i = 0; i < params.length; i++) {
						stmt.setObject(i + 1, params[i]);
					}
				}
				return stmt;
			}
			
		}, new CallableStatementCallback<List<T>>() {
			@Override
			public List<T> doInCallableStatement(CallableStatement stmt) throws SQLException, DataAccessException {
				ResultSet rs = stmt.getResultSet();
				int rowNum = 0;
				if(rs.getRow()<=0){
					return null;
				}
				List<T> list = new ArrayList<T>(rs.getRow());
				while(rs.next()){
					T t = mapper.mapRow(rs, rowNum++);
					list.add(t);
				}
				return list;
			}
		});
		return list;
	}
	
	private static final Logger logger = Logger.getLogger(DaoSupport.class);
	private void error(Exception e, String sql, Object... params){
		logger.error("执行出错-sql:" + sql + "\r\n参数" + Arrays.toString(params)+"\r\n"+e);
		e.printStackTrace();
	}
}
