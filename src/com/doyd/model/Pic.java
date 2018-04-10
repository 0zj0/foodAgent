package com.doyd.model;
/**
 * @author ylb
 * @version 创建时间：2018-1-8 下午5:07:13
 */
public class Pic {

	private String picUrl; 
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Pic(String picUrl, int x, int y, int width, int height){
		this.picUrl = picUrl;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
