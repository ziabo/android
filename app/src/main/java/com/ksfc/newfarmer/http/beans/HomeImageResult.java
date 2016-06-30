package com.ksfc.newfarmer.http.beans;

import java.util.List;

import com.ksfc.newfarmer.http.ResponseResult;

public class HomeImageResult extends ResponseResult {

	public UserRollImage datas;

	public static class UserRollImage {
		public int total;
		public List<Rows> rows;
		public String locationUserId;
	}

	public static class Rows {
		public String imgUrl;
		public String id;
		public String url;
	}
}