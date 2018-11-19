package com.cg.mypaymentapp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cg.mypaymentapp.beans.Customer;
import com.cg.mypaymentapp.beans.Transactions;
import com.cg.mypaymentapp.beans.Wallet;
import com.cg.mypaymentapp.exception.InsufficientBalanceException;
import com.cg.mypaymentapp.exception.InvalidInputException;
import com.cg.mypaymentapp.repo.WalletRepo;
import com.cg.mypaymentapp.repo.WalletRepoImpl;

public class WalletServiceImpl implements WalletService {
	private WalletRepo repo;
	private Customer customer;
	private Wallet wallet;
	private Map<String, ArrayList<String>> data;
	
	public WalletServiceImpl() {
		repo = new WalletRepoImpl();
		data=new HashMap<String, ArrayList<String>>();
	}
	public WalletServiceImpl(Map<String, Customer> data){
		repo= new WalletRepoImpl(data);
	}
	public WalletServiceImpl(WalletRepo repo){
		super();
		this.repo = repo;
	}

	@Override
	public Customer createAccount(String name, String mobileNo, BigDecimal amount) throws InvalidInputException	{
		boolean check = false; 
		try {
		wallet = new Wallet(amount);
		customer = new Customer(name, mobileNo, wallet, new Transactions());
		if(isValid(customer))
		check = repo.save(customer); }
		catch(InvalidInputException e)
		{System.out.println(e);
		}
		if (check) {
			return customer;
		} else 
		{
			System.out.println("Data not saved.");
			return null;  
		}	
	} 
		

	@Override
	public Customer showBalance(String mobileNo) {
		
		Customer customer=repo.findOne(mobileNo);
		if(customer!=null)
			return customer;
		else
			throw new InvalidInputException("Invalid mobile no ");
	}

	@Override
	public Customer fundTransfer(String sourceMobileNo, String targetMobileNo, BigDecimal amount) {
		
		if(sourceMobileNo==null||amount.compareTo(BigDecimal.ZERO)<=0||sourceMobileNo.trim().isEmpty()||targetMobileNo.trim().isEmpty()||targetMobileNo==null)
			throw new InvalidInputException("Inputs cannot be empty.");
		
		Customer customer=repo.findOne(sourceMobileNo);
		Customer customer2=repo.findOne(targetMobileNo);
		
		if(customer!=null){
			BigDecimal initialamount=customer.getWallet().getBalance();
			
			if(initialamount.compareTo(amount)>=0){
				BigDecimal finalamount=initialamount.subtract(amount);
				Wallet wallet=new Wallet(finalamount);
				customer.setWallet(wallet);
				customer.getTransaction().getTransaction().add(amount+" was transfered to "+targetMobileNo);
			}
			
			else
				throw new InsufficientBalanceException("Insufficient Balance");
		}
		
		if(customer2!=null){
			BigDecimal initialamount=customer2.getWallet().getBalance();
			BigDecimal finalamount=initialamount.add(amount);
			Wallet wallet=new Wallet(finalamount);
			customer2.setWallet(wallet);
			customer2.getTransaction().getTransaction().add(amount+" was transferred to your account from "+sourceMobileNo);
		}
		
		else
			System.out.println("Target Mobile number is wrong");
		
		return customer;
	}

	@Override
	public Customer depositAmount(String mobileNo, BigDecimal amount) {
		
		if(mobileNo==null||amount.compareTo(BigDecimal.ZERO)<=0||mobileNo.trim().isEmpty())
			throw new InvalidInputException("Inputs cannot be empty.");
		
		Customer customer=null;
		customer=repo.findOne(mobileNo);
		if(customer!=null){
			BigDecimal initialamount=customer.getWallet().getBalance();
			BigDecimal finalamount=initialamount.add(amount);
			Wallet wallet=new Wallet(finalamount);
			customer.setWallet(wallet);
			customer.getTransaction().getTransaction().add(amount+" depositied");
			return customer;
		}
		else
		{
		System.out.println("Deposit failed");	
		return null;
		}
		
	}

	@Override
	public Customer withdrawAmount(String mobileNo, BigDecimal amount) {
		
		if(mobileNo==null||amount.compareTo(BigDecimal.ZERO)<=0||mobileNo.trim().isEmpty())
			throw new InvalidInputException("Inputs cannot be empty.");
		
		Customer customer=repo.findOne(mobileNo);
		
		BigDecimal initialamount=customer.getWallet().getBalance();
		
		if(initialamount.compareTo(amount)>=0){
			BigDecimal finalamount=initialamount.subtract(amount);
			Wallet wallet=new Wallet(finalamount);
			customer.setWallet(wallet);
			customer.getTransaction().getTransaction().add(amount+" withdrawn");
			return customer;
		}
		else
		{	
		throw new InsufficientBalanceException("Insufficient Balance:Withdraw failed");
		
		}
	}
	
	@Override
	public List<String> showTransaction(String mobileNo){
	
		customer=repo.findOne(mobileNo);
		
		return customer.getTransaction().getTransaction();
		
	}
	
	public boolean isValid(Customer customer) throws InvalidInputException, InsufficientBalanceException{
		if(customer.getName() == null || customer.getName() == "")
		{
			throw new InvalidInputException("User Name cannot be null or empty.");
			
		}
		if(customer.getMobileNo() == null || customer.getMobileNo() == "")
			throw new InvalidInputException("User Mobile Number cannot be null or empty.");
		
		BigDecimal value = BigDecimal.ZERO;
		
		if(customer.getWallet().getBalance() == null ||customer.getWallet().getBalance().compareTo(value)==-1)
			throw new InvalidInputException("Wallet Balance cannot be Null.");
		
		if(!(customer.getName().matches("^([A-Z]{1}\\w+)$")))
		{
			throw new InvalidInputException("Invalid Name");
		}
		if(!(customer.getMobileNo().length()==10))
			throw new InvalidInputException("Mobile Number is not 10 digit.");
		
		if(!(customer.getMobileNo().matches("^[7-9]{1}[0-9]{9}$")))
		{
			throw new InvalidInputException("Invalid Number");
		}
		
		return true;
	}	

}
