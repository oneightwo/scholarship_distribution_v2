package com.oneightwo.scholarship_distribution_v2.tools

import org.slf4j.LoggerFactory


fun <T> logger(clazz: Class<T>) = LoggerFactory.getLogger(clazz)