package com.greenandblue.shoppingtrackertwo;

public class Entry {
	
	String paymentMethod;
	String productCategory;
	String productName;
	String remark;
	int sum;
	long date;
	
	public Entry()
	{
		
	}
	public Entry(String paymentMethod, String productCategory,
			String productName, String remark, int sum, long date) {
		this.paymentMethod = paymentMethod;
		this.productCategory = productCategory;
		this.productName = productName;
		this.remark = remark;
		this.sum = sum;
		this.date = date;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	

}
