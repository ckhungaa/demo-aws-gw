package com.elephant.common.utils;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

public class ResultList<T> {

	private List<T> items = Lists.newArrayList();
	private int totalNumOfItems;

	public ResultList(){
		
	}
	
	public ResultList(List<T> items, int totalNumOfItems) {
		this.items = items;
		this.totalNumOfItems = totalNumOfItems;
	}

	public List<T> getItems() {
		return items;
	}

	public int getTotalNumOfItems() {
		return totalNumOfItems;
	}

	public Stream<T> stream() {
		return items.stream();
	}

}
