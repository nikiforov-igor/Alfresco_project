/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package ru.it.lecm.businessjournal.remote;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 *
 * @author ikhalikov
 */
@PersistenceCapable
@Cacheable("false")
public class RecordsCount {

	private Long count = 0L;
	@PrimaryKey
	private Long id = 0L;

	public Long getRecordsCount() {
		return count;
	}

	public void setRecordsCount(Long recordsCount) {
		this.count = recordsCount;
	}

	public Long incr() {
		return this.count++;
	}

}
