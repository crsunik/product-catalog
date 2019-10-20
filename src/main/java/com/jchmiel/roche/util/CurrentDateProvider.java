package com.jchmiel.roche.util;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CurrentDateProvider {

	public LocalDate currentDate() {
		return LocalDate.now();
	}

}
