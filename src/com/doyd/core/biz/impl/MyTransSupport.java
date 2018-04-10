package com.doyd.core.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


/**

 * @author Administrator
 * @eg.
 * public boolean create {
		TransactionStatus status = getTxStatus();
		boolean r = true;
		try{
			r = create();
		}catch(Exception e){
			r = false;
		}finally{
			commit(status, r);
		}
		return r;
	}
 *
 */
public class MyTransSupport {
	@Autowired
	public DataSourceTransactionManager txManager;
	
	private TransactionDefinition getTxDef(){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName(this.getClass().getName()+System.currentTimeMillis());
		def.setReadOnly(false);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		return def;
	}
	
	private DataSourceTransactionManager getTxManager() {
		return txManager;
	}
	
	public TransactionStatus getTxStatus(){
		return getTxManager().getTransaction(getTxDef());
	}
	
	public boolean commit(TransactionStatus status, boolean r){
		if(r){
			getTxManager().commit(status);
		}else{
			getTxManager().rollback(status);
		}
		return r;
	}
}
