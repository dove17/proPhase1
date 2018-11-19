package com.cg.mypaymentapp.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.cg.mypaymentapp.beans.Customer;
import com.cg.mypaymentapp.exception.InvalidInputException;

public class WalletRepoImpl implements WalletRepo {
	private Map<String, Customer> data; 
	private Map<String,ArrayList<String>> trans;
	
	
	
	public WalletRepoImpl(){
		data = new HashMap<String, Customer>();
	}

	public WalletRepoImpl(Map<String, Customer> data) 	{
		super();
		this.data = data;
	}
	
	@Override
	public boolean save(Customer customer) {
		data.put(customer.getMobileNo(), customer);
		return true;
	}

	@Override
	public Customer findOne(String mobileNo) {
		if(data.containsKey(mobileNo)){
			Customer customer=data.get(mobileNo);
			return customer;
		}
		else
			throw new InvalidInputException("Mobile number does not exist");
		}

	public Map<String, ArrayList<String>> getTrans() {
		return trans;
	}

	public void setTrans(Map<String, ArrayList<String>> trans) {
		this.trans = trans;
	}
	
}
