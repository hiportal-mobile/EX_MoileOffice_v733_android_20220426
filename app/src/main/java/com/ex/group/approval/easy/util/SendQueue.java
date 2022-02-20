package com.ex.group.approval.easy.util;

public class SendQueue
{
	public String key;
	public String value;
	
	public SendQueue(String _key, String _value)
	{
		this.key = _key;
		this.value = _value;
	}

	@Override
	public String toString() {
		return "key:"+this.key+" value:"+this.value;
	}
	
}
