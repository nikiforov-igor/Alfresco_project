/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.businessjournal.beans.util;

import com.google.common.base.Joiner;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author ikhalikov
 */
public class CustomURLBuilder {

	private StringBuilder result;
	private boolean empty = true;
	private boolean haveFilter = false;
	private String base;
	private StringBuilder query;

	public boolean isHaveFilter() {
		return haveFilter;
	}

	public CustomURLBuilder interval(String field, String begin, String end) {
		empty = false;
		haveFilter = true;
//		result.append("(");
		CustomURLBuilder res = this;
		res.gt(field, begin).and().ls(field, end);
//		result.append(")");
		return res;
	}

	public CustomURLBuilder() {
		this.result = new StringBuilder();
	}

	public boolean isEmpty() {
		return empty;
	}

	public CustomURLBuilder(String result) {
		this.result = new StringBuilder(result);
		this.query = new StringBuilder(result).append("/jdoql?select+from+");
	}

	public CustomURLBuilder path(String path) {
		result.append("/").append(path).append("?");
		query.append(path);
		return this;
	}

	public CustomURLBuilder and() {
		if (empty) {
			return this;
		}
		result.append("&");
		return this;
	}

	public CustomURLBuilder or() {
		if (empty) {
			return this;
		}
		result.append("%7C");
		return this;
	}

	public CustomURLBuilder eq(String param, String value) {
		empty = false;
		haveFilter = true;
		result.append(param).append("==").append(value);
		return this;
	}

	public CustomURLBuilder ls(String param, String value) {
		empty = false;
		haveFilter = true;
		result.append(param).append("%3C").append(value);
		return this;
	}

	public CustomURLBuilder gt(String param, String value) {
		empty = false;
		haveFilter = true;
		result.append(param).append("%3E").append(value);
		return this;
	}

	public CustomURLBuilder in(String param, List values) {
		if (values.isEmpty()) {
			return this;
		}
		empty = false;
		haveFilter = true;
		boolean wrap = false;
		if (values.get(0) instanceof String) {
			wrap = true;
		}
		result.append(listToCQL(values, wrap)).append(".conatins(").append(param).append(")");
		return this;
	}

	public CustomURLBuilder sort(String sortField, boolean asc) {
		result.append("+order+by+").append(sortField);
		query.append("+order+by+").append(sortField);
		if (asc) {
			result.append("+asc");
			query.append("+asc");
		} else {
			result.append("+desc");
			query.append("+desc");
		}
		return this;
	}

	public CustomURLBuilder range(int begin, int end) {
		result.append("+range+").append(begin).append(",").append(end);
		query.append("+range+").append(begin).append(",").append(end);
		return this;
	}

	public CustomURLBuilder andOr(boolean and) {
		if (empty) {
			return this;
		}
		CustomURLBuilder res = this;
		if (and) {
			res.and();
		} else {
			res.or();
		}
		return res;
	}

	@Override
	public String toString() {
		if (empty) {
			return query.toString();
		}
		return result.toString();
	}

	private String listToCQL(List list, boolean wrapElements) {
		StringBuilder builder = new StringBuilder();
		try {
			if (wrapElements) {
				return URLEncoder.encode("'(\"" + Joiner.on("\",\"").join(list) + "\")'", "UTF-8");
			} else {
				return URLEncoder.encode("'(" + Joiner.on(",").join(list) + "')", "UTF-8");
			}
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(CustomURLBuilder.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

}
